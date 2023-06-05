package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageValidator {

    public void validateForCreate(CreateMessageDto toCreate) throws ValidationException {
        log.trace("Validating user to add: ({})", toCreate);
        List<String> validationErrors = new ArrayList<>();

        if (toCreate.getTitle() == null) {
            validationErrors.add("Title is null");
        } else if (toCreate.getTitle().equals("")) {
            validationErrors.add("Title is empty");
        } else if (toCreate.getTitle().length() > 200) {
            validationErrors.add("Title is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getShortDescription() == null) {
            validationErrors.add("Short description is null");
        } else if (toCreate.getShortDescription().equals("")) {
            validationErrors.add("Short description is empty");
        } else if (toCreate.getShortDescription().length() > 1000) {
            validationErrors.add("Short description is too long, only up to 1000 characters are allowed");
        }

        if (toCreate.getDescription() == null) {
            validationErrors.add("Description is null");
        } else if (toCreate.getDescription().equals("")) {
            validationErrors.add("Description is empty");
        } else if (toCreate.getDescription().length() > 10000) {
            validationErrors.add("Description is too long, only up to 10000 characters are allowed");
        }

        if (toCreate.getPicture() == null) {
            validationErrors.add("Picture is null");
        } else {
            if (!toCreate.getPicture().getContentType().equals("image/jpg") && !toCreate.getPicture().getContentType().equals("image/jpeg")) {
                validationErrors.add("Picture does not have the required format 'jpg', instead it has: " + toCreate.getPicture().getContentType());
            }
            if (toCreate.getPicture().getSize() > 5000000) {
                validationErrors.add("Picture file size is too large. Only files up to 5MB are allowed");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("There are validation errors: ", validationErrors);
        }
    }

    public void validateForUpdate(UpdateMessageDto toUpdate) throws ValidationException {
        log.trace("Validating user to update: ({})", toUpdate);
        List<String> validationErrors = new ArrayList<>();

        if (toUpdate.getTitle() == null) {
            validationErrors.add("Title is null");
        } else if (toUpdate.getTitle().equals("")) {
            validationErrors.add("Title is empty");
        } else if (toUpdate.getTitle().length() > 200) {
            validationErrors.add("Title is too long, only up to 200 characters are allowed");
        }

        if (toUpdate.getShortDescription() == null) {
            validationErrors.add("Short description is null");
        } else if (toUpdate.getShortDescription().equals("")) {
            validationErrors.add("Short description is empty");
        } else if (toUpdate.getShortDescription().length() > 1000) {
            validationErrors.add("Short description is too long, only up to 1000 characters are allowed");
        }

        if (toUpdate.getDescription() == null) {
            validationErrors.add("Description is null");
        } else if (toUpdate.getDescription().equals("")) {
            validationErrors.add("Description is empty");
        } else if (toUpdate.getDescription().length() > 10000) {
            validationErrors.add("Description is too long, only up to 10000 characters are allowed");
        }

        if (toUpdate.getPicture() != null) {
            if (!toUpdate.getPicture().getContentType().equals("image/jpg")  && !toUpdate.getPicture().getContentType().equals("image/jpeg")) {
                validationErrors.add("Picture does not have the required format 'jpg'");
            }
            if (toUpdate.getPicture().getSize() > 5000000) {
                validationErrors.add("Picture file size is too large. Only files up to 5MB are allowed");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("There are validation errors", validationErrors);
        }
    }
}
