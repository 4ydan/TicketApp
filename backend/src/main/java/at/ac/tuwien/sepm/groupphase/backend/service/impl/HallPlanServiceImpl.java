package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SectorCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorCategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorCategory;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanService;
import at.ac.tuwien.sepm.groupphase.backend.type.SectorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HallPlanServiceImpl implements HallPlanService {

    private final SeatRepository seatRepository;
    private final SectorCategoryRepository sectorCategoryRepository;
    private final HallPlanRepository hallPlanRepository;
    private final SectorCategoryMapper sectorCategoryMapper;
    private final HallPlanMapper hallPlanMapper;

    public HallPlanServiceImpl(SeatRepository seatRepository,
                           SectorCategoryRepository sectorCategoryRepository,
                           HallPlanRepository hallPlanRepository,
                           HallPlanMapper hallPlanMapper,
                           SectorCategoryMapper sectorCategoryMapper) {
        this.seatRepository = seatRepository;
        this.sectorCategoryRepository = sectorCategoryRepository;
        this.hallPlanRepository = hallPlanRepository;
        this.sectorCategoryMapper = sectorCategoryMapper;
        this.hallPlanMapper = hallPlanMapper;
    }

    @Override
    public List<SeatDto> getSeatingPlanByHallPlanId(long id) {
        log.trace("Get seating plan by hall plan id {}", id);
        log.debug("Seats Size: " + this.seatRepository.findSeatsByHallPlanId(1L).size());
        return this.seatRepository.findSeatsByHallPlanId(id);

    }

    @Override
    public List<SectorCategoryDto> getSectorCategoryByHallPlanId(Long id) {
        log.trace("Get sector categories by hall plan id {}", id);
        List<SectorCategory> sectorCategoryDaoList = this.seatRepository.findSectorCategoryByHallPlanId(id);
        if (this.hallPlanRepository.findHallPlanById(id).getMaxStandingCapacity() > 0) {
            sectorCategoryDaoList.add(this.sectorCategoryRepository.findSectorCategoryBySectorType(SectorType.STANDING));
        }
        return this.sectorCategoryMapper.entityToListDto(sectorCategoryDaoList);
    }

    @Override
    public List<HallPlanDto> find(HallPlanSearchDto searchDto) {
        log.trace("Find all hall-plans by term {}", searchDto.getTerm());
        return this.hallPlanMapper.entityToListDto(
            this.hallPlanRepository.findByNameContainingIgnoreCase(
                searchDto.getTerm(),
                PageRequest.of(0, searchDto.getMaxResults())
            )
        );
    }
}
