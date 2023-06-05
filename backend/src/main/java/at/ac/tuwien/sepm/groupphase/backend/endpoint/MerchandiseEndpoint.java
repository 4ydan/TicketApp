package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseRedeemDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchandiseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/merchandises")
public class MerchandiseEndpoint {

    private final MerchandiseService merchandiseService;

    @Autowired
    public MerchandiseEndpoint(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get list of merchandises concerning the page")
    public List<MerchandiseDto> findAll(@RequestParam(name = "page") int page, @RequestParam(name = "filter", required = false) String filter,
                                        @RequestParam(name = "sort", required = false) String sort) {
        log.info("GET /api/v1/merchandises?page={}&filter={}&sort={}", page, filter, sort);
        return merchandiseService.findAll(page, filter, sort);
    }

    @Secured("ROLE_USER")
    @PutMapping("/bonusPoints")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Redeem merchandise")
    public void redeemMerchandise(@RequestBody MerchandiseRedeemDto merchandise) throws ValidationException {
        log.info("PUT /api/v1/merchandises/bonusPoints/{}", merchandise);
        this.merchandiseService.redeem(merchandise);
    }

}
