package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class PerformanceValidator {
    private final EventRepository eventRepository;

    public PerformanceValidator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void validate(PerformanceDto toCreate, Long eventId) throws ValidationException {
        log.trace("Validating performance to create: ({})", toCreate);
        List<String> validationErrors = new ArrayList<>();

        if (toCreate.getName().equals("")) {
            validationErrors.add("Name is empty");
        } else if (toCreate.getName().length() > 200) {
            validationErrors.add("Name is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getCity().equals("")) {
            validationErrors.add("City is empty");
        } else if (toCreate.getCity().length() > 200) {
            validationErrors.add("City is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getCountry().equals("")) {
            validationErrors.add("Country is empty");
        } else if (toCreate.getCountry().length() > 200) {
            validationErrors.add("Country is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getStreet().equals("")) {
            validationErrors.add("Street is empty");
        } else if (toCreate.getStreet().length() > 200) {
            validationErrors.add("Street is too long, only up to 200 characters are allowed");
        }

        if (toCreate.getPrice() < 0) {
            validationErrors.add("Price has a negative value");
        } else if (toCreate.getPrice() > 999999) {
            validationErrors.add("Price is too high, only prices lower than one million are allowed");
        }

        try {
            LocalDate date = LocalDate.parse(toCreate.getDate());
            if (date.isBefore(LocalDate.now())) {
                validationErrors.add("Performance date is in the past");
            }
            try {
                Event event = eventRepository.findById(eventId).get();
                if (date.isAfter(event.getEndDate())) {
                    validationErrors.add("Performance date is after the event end date");
                }
                if (date.isBefore(event.getStartDate())) {
                    validationErrors.add("Performance date is before the event start date");
                }
            } catch (NoSuchElementException e) {
                validationErrors.add("Provided event does not exist");
            }
        } catch (DateTimeParseException e) {
            validationErrors.add("Performance date is not of the type LocalDate");
        }

        try {
            LocalTime.parse(toCreate.getStartTime());
        } catch (DateTimeParseException e) {
            validationErrors.add("Performance start time is not of the type LocalTime");
        }


        if (!validationErrors.isEmpty()) {
            throw new ValidationException("There are validation errors: ", validationErrors);
        }
    }
}
