package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandisePurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseRedeemDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MerchandiseValidator {

    private final MerchandiseRepository merchandiseRepository;

    private final UserRepository userRepository;


    public MerchandiseValidator(MerchandiseRepository merchandiseRepository, UserRepository userRepository) {
        this.merchandiseRepository = merchandiseRepository;
        this.userRepository = userRepository;
    }

    public void validateForPurchase(List<MerchandisePurchaseDto> merchandises) throws ValidationException {
        log.trace("Validate merchandises for purchase {}", merchandises);
        List<String> validationErrors = new ArrayList<>();

        for (MerchandisePurchaseDto merchandisePurchaseDto : merchandises) {
            Optional<Merchandise> merchandise = merchandiseRepository.findById(merchandisePurchaseDto.getId());
            if (merchandise.isEmpty()) {
                validationErrors.add("Merchandise given does not exist");
            } else if (merchandise.get().getInStock() < merchandisePurchaseDto.getAmount()) {
                validationErrors.add("Merchandise " + merchandise.get().getName() + " is not available in given amount");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of merchandise purchase is invalid", validationErrors);
        }
    }

    public void validateForRedeeming(MerchandiseRedeemDto merchandise) throws ValidationException {
        log.trace("Validate merchandise for redeeming {}", merchandise);
        List<String> validationErrors = new ArrayList<>();

        if (merchandise.getUserId() == null || !this.userRepository.existsById(merchandise.getUserId())) {
            validationErrors.add("UserId is null or assigned user does not exist");
        } else {
            if (merchandiseRepository.existsById(merchandise.getItemId())) {
                if (userRepository.getById(merchandise.getUserId()).getBonusPoints() < merchandiseRepository.findById(merchandise.getItemId()).get().getCostsPoints() * merchandise.getAmount()) {
                    validationErrors.add("User has insufficient bonus points");
                }
            }
        }
        if (merchandise.getItemId() == null || !this.merchandiseRepository.existsById(merchandise.getItemId())) {
            validationErrors.add("ItemId is null or assigned item does not exist");
        } else {
            if (this.merchandiseRepository.findById(merchandise.getItemId()).get().getInStock() < merchandise.getAmount()) {
                validationErrors.add("Requested amount is higher than stock amount");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation for redeeming of merchandise is invalid", validationErrors);
        }
    }
}
