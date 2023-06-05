package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    /**
     * Find all performances that belong to an event.
     *
     * @param id the id of the event
     * @return all performances in a List.
     */
    List<Performance> findByEvent_IdOrderByDateAsc(Long id);

}
