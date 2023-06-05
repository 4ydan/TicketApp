package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallPlanRepository extends JpaRepository<HallPlan, Long> {

    /**
     * Finds the HallPlan by Id.
     *
     * @param id the hall plan id
     * @return the matching hall plan.
     */
    HallPlan findHallPlanById(Long id);

    /**
     * Find all halls by term and ignore case.
     *
     * @param term searching term
     * @return list of all halls that satisfy search param
     */
    List<HallPlan> findByNameContainingIgnoreCase(String term, Pageable pageable);
}
