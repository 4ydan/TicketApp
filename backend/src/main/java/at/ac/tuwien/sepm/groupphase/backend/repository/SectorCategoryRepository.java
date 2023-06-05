package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.SectorCategory;
import at.ac.tuwien.sepm.groupphase.backend.type.SectorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorCategoryRepository  extends JpaRepository<SectorCategory, Long> {

    /**
     * Finds the SectorCategory by SectorType.
     *
     * @param sectorType the sector type
     * @return the matching sector category.
     */
    SectorCategory findSectorCategoryBySectorType(SectorType sectorType);
}
