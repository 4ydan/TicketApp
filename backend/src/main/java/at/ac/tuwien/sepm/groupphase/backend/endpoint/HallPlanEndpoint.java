package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SectorCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/hallplans")
public class HallPlanEndpoint {
    private final HallPlanService hallPlanService;

    @Autowired
    public HallPlanEndpoint(HallPlanService hallPlanService) {
        this.hallPlanService = hallPlanService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("{id}/seatingplan")
    @Operation(summary = "Get a list of all seats that belong to a hall plan")
    public List<SeatDto> getSeatingPlanByHallPlan_Id(@PathVariable long id) {
        log.info("Get /api/v1/hallplan/{}/seatingplan", id);
        try {
            return this.hallPlanService.getSeatingPlanByHallPlanId(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("{id}/sectorcategories")
    @Operation(summary = "Get a list of all seats that belong to a hall plan")
    public List<SectorCategoryDto> getSectorCategoryByHallPlanId(@PathVariable long id) {
        log.info("Get /api/v1/hallplan/{}/sectorcategories/", id);
        try {
            return this.hallPlanService.getSectorCategoryByHallPlanId(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping
    @Operation(summary = "Get a list of all hall-plans matching to the search criteria", security = @SecurityRequirement(name = "apiKey"))
    public List<HallPlanDto> findAll(HallPlanSearchDto searchDto) {
        log.info("GET /api/v1/hallplan");
        return hallPlanService.find(searchDto);
    }
}
