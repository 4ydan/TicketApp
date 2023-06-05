package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/purchases")
public class PurchaseEndpoint {

    private final PurchaseService purchaseService;

    public PurchaseEndpoint(PurchaseService purchaseseService) {
        this.purchaseService = purchaseseService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Purchase merchandises and/or buy tickets")
    public void purchase(@RequestBody PurchaseDto purchaseDto) throws ValidationException, ConflictException {
        log.info("POST /api/v1/purchases/{}", purchaseDto);
        purchaseService.purchase(purchaseDto);
    }

    @Secured("ROLE_VENDOR")
    @PostMapping("{email}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Purchase merchandises and/or buy tickets")
    public void purchase(@RequestBody PurchaseDto purchaseDto, @Parameter(required = true) String email) throws ValidationException, ConflictException {
        log.info("POST /api/v1/purchases?{}", email);
        purchaseService.purchaseByVendorForCustomer(purchaseDto, email);
    }
}
