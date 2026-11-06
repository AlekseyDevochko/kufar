package by.inno.kufar.repository;

import by.inno.kufar.model.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query("""
           SELECT a FROM Advertisement a
           LEFT JOIN a.seller s
           WHERE (:keyword IS NULL OR :keyword = ''
                  OR LOWER(a.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(a.city)        LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(s.fullName)    LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND (:minPrice IS NULL OR a.price >= :minPrice)
             AND (:maxPrice IS NULL OR a.price <= :maxPrice)
             AND (:city IS NULL OR :city = '' OR LOWER(a.city) = LOWER(:city))
           """)
    Page<Advertisement> search(@Param("keyword") String keyword,
                               @Param("minPrice") BigDecimal minPrice,
                               @Param("maxPrice") BigDecimal maxPrice,
                               @Param("city") String city,
                               Pageable pageable);

    @Query("SELECT DISTINCT a.city FROM Advertisement a WHERE a.city IS NOT NULL ORDER BY a.city")
    List<String> findAllCities();

    List<Advertisement> findBySeller_Id(Long sellerId);

}

