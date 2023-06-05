package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.AddressServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/addresses")
public class AddressEndpoint {

    private final AddressServiceImpl addressService;

    @Autowired
    public AddressEndpoint(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("/countries")
    @Operation(summary = "Get all unique countries", security = @SecurityRequirement(name = "apiKey"))
    public List<String> getCountries() {
        log.info("GET /api/v1/addresses/countries");
        try {
            return addressService.getUniqueCountries();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("/codes")
    @Operation(summary = "Get all unique postal codes", security = @SecurityRequirement(name = "apiKey"))
    public List<String> getPostalCodes() {
        log.info("GET /api/v1/addresses/codes");
        try {
            return addressService.getUniquePostalCodes();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("/cities")
    @Operation(summary = "Get all unique cities", security = @SecurityRequirement(name = "apiKey"))
    public List<String> getCities() {
        log.info("GET /api/v1/addresses/cities");
        try {
            return addressService.getUniqueCities();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
