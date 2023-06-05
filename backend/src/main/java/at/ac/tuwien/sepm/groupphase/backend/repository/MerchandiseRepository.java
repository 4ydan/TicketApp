package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long> {

    /**
     * Find all merchandises on page.
     *
     * @param page number
     * @return list of merchandises on page
     */
    @Query(
        value = "SELECT * FROM MERCHANDISES m WHERE (m.costs_points IS NOT NULL OR ?2 = 'false') AND (m.costs_points IS NULL OR ?3 = 'false') AND"
            + " m.id NOT IN (SELECT m.id from MERCHANDISES m WHERE (m.costs_points IS NOT NULL OR ?2 = 'false') AND (m.costs_points IS NULL OR ?3 = 'false') LIMIT 16*?1)"
            + " LIMIT 17",
        nativeQuery = true
    )
    List<Merchandise> findAllOnPage(int page, String filter1, String filter2);

    /**
     * Find all merchandises on page sorted by price ascending.
     *
     * @param page number
     * @return list of sorted merchandises on page
     */
    @Query(
        value = "SELECT * FROM MERCHANDISES m WHERE (m.costs_points IS NOT NULL OR ?2 = 'false') AND (m.costs_points IS NULL OR ?3 = 'false') AND"
            + " m.id NOT IN (SELECT m.id from MERCHANDISES m WHERE (m.costs_points IS NOT NULL OR ?2 = 'false') AND (m.costs_points IS NULL OR ?3 = 'false') ORDER BY m.price ASC LIMIT 16*?1)"
            + " ORDER BY m.price ASC LIMIT 17",
        nativeQuery = true
    )
    List<Merchandise> findAllOnPageSortByPriceAsc(int page, String filter1, String filter2);

    /**
     * Find all merchandises on page sorted by price descending.
     *
     * @param page number
     * @return list of sorted merchandises on page
     */
    @Query(
        value = "SELECT * FROM MERCHANDISES m WHERE(m.costs_points IS NOT NULL OR ?2 = 'false') AND (m.costs_points IS NULL OR ?3 = 'false') AND"
            + " m.id NOT IN (SELECT m.id from MERCHANDISES m WHERE (m.costs_points IS NOT NULL OR ?2 = 'false') AND (m.costs_points IS NULL OR ?3 = 'false') ORDER BY m.price DESC LIMIT 16*?1)"
            + " ORDER BY m.price DESC LIMIT 17",
        nativeQuery = true
    )
    List<Merchandise> findAllOnPageSortByPriceDesc(int page, String filter1, String filter2);
}

