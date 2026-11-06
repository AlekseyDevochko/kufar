package by.inno.kufar.service;

import by.inno.kufar.model.Advertisement;
import by.inno.kufar.model.EntityMapper;
import by.inno.kufar.model.Image;
import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.AdvertisementCreateDto;
import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.model.dto.AdvertisementFilterDto;
import by.inno.kufar.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertisementService {
    private AdvertisementRepository advertisementRepository;
    private EntityMapper entityMapper;

    @Autowired
    public AdvertisementService(
            AdvertisementRepository advertisementRepository,
            EntityMapper entityMapper
    ) {
        this.advertisementRepository = advertisementRepository;
        this.entityMapper = entityMapper;
    }

    @Transactional(readOnly = true)
    public Page<AdvertisementDto> search(AdvertisementFilterDto filter) {
        Sort.Direction dir = "asc".equalsIgnoreCase(filter.getSortDirection())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = switch (filter.getSortBy() == null ? "createdDate" : filter.getSortBy()) {
            case "price" -> Sort.by(dir, "price");
            case "title" -> Sort.by(dir, "title");
            default       -> Sort.by(dir, "createdDate");
        };

        Pageable pageable = PageRequest.of(
                Math.max(filter.getPage(), 0),
                Math.max(filter.getSize(), 1),
                sort);

        return advertisementRepository.search(
                        filter.getKeyword(),
                        filter.getMinPrice(),
                        filter.getMaxPrice(),
                        filter.getCity(),
                        pageable)
                .map(entityMapper::toAdvertisementDto);
    }

    @Transactional(readOnly = true)
    public AdvertisementDto findById(Long id) {
        return advertisementRepository.findById(id)
                .map(entityMapper::toAdvertisementDto)
                .orElseThrow(() -> new IllegalArgumentException("Объявление не найдено: " + id));
    }

    @Transactional(readOnly = true)
    public List<String> findAllCities() {
        return advertisementRepository.findAllCities();
    }

    @Transactional
    public AdvertisementDto create(AdvertisementCreateDto dto, User seller) {
        Advertisement ad = new Advertisement();
        applyFields(ad, dto);
        ad.setSeller(seller);
        ad.setCreatedDate(LocalDateTime.now());
        ad.setImages(new ArrayList<>());
        appendImages(ad, dto.getImages());
        return entityMapper.toAdvertisementDto(advertisementRepository.save(ad));
    }

    @Transactional
    public AdvertisementDto update(Long id, AdvertisementCreateDto dto, User owner) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Объявление не найдено"));
        if (!ad.getSeller().getId().equals(owner.getId())) {
            throw new SecurityException("Нет прав на редактирование");
        }
        applyFields(ad, dto);
        appendImages(ad, dto.getImages());
        return entityMapper.toAdvertisementDto(ad);
    }

    @Transactional
    public void delete(Long id, User owner) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Объявление не найдено"));
        if (!ad.getSeller().getId().equals(owner.getId())) {
            throw new SecurityException("Нет прав на удаление");
        }
        advertisementRepository.delete(ad);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementDto> findBySeller(Long sellerId) {
        return advertisementRepository.findAll().stream()
                .filter(a -> a.getSeller() != null && a.getSeller().getId().equals(sellerId))
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
                .map(entityMapper::toAdvertisementDto)
                .toList();
    }

    private void applyFields(Advertisement ad, AdvertisementCreateDto dto) {
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setCity(dto.getCity());
    }

    private void appendImages(Advertisement ad, List<MultipartFile> files) {
        if (files == null) return;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            try {
                Image img = new Image();
                img.setFileName(file.getOriginalFilename());
                img.setContentType(file.getContentType());
                img.setData(file.getBytes());
                img.setAdvertisement(ad);
                ad.getImages().add(img);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось прочитать файл: " + file.getOriginalFilename(), e);
            }
        }
    }


}
