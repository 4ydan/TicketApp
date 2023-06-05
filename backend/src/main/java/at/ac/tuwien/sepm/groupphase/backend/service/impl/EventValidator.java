package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventValidator {

    public void validateForCreate(EventCreateDto toCreate) throws ValidationException {
        log.trace("Validating event to add: ({})", toCreate);
        List<String> validationErrors = new ArrayList<>();

        if (toCreate.getTitle().equals("")) {
            validationErrors.add("Title is empty");
        } else if (toCreate.getTitle().length() > 200) {
            validationErrors.add("Title is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getDescription().equals("")) {
            validationErrors.add("Description is empty");
        } else if (toCreate.getDescription().length() > 10000) {
            validationErrors.add("Description is too long, only up to 10000 characters are allowed");
        }

        try {
            LocalDate startDate = LocalDate.parse(toCreate.getStartDate());
            LocalDate endDate = LocalDate.parse(toCreate.getEndDate());
            if (startDate.isAfter(endDate)) {
                validationErrors.add("Event end date is before event start date");
            }
        } catch (DateTimeParseException e) {
            validationErrors.add("Dates have to be of the type LocalDate");
        }

        if (toCreate.getDurationInMinutes() < 0) {
            validationErrors.add("Duration is negative");
        } else if (toCreate.getDurationInMinutes() > 1440) {
            validationErrors.add("Event cannot take longer than 24 hours");
        }

        if (toCreate.getArtistName().equals("")) {
            validationErrors.add("Artist is empty");
        } else if (toCreate.getArtistName().length() > 200) {
            validationErrors.add("Artist name is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getImage() == null) {
            validationErrors.add("No image provided");
        } else {
            if (!toCreate.getImage().getContentType().equals("image/jpg") && !toCreate.getImage().getContentType().equals("image/jpeg")) {
                validationErrors.add("Image does not have the required format 'jpg', instead it has: " + toCreate.getImage().getContentType());
            }
            if (toCreate.getImage().getSize() > 5000000) {
                validationErrors.add("Image file size is too large. Only files up to 5MB are allowed");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("There are validation errors: ", validationErrors);
        }
    }
}
