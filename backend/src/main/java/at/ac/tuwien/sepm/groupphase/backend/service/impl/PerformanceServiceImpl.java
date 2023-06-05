package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Service
public class PerformanceServiceImpl implements PerformanceService {
    private final HallPlanRepository hallPlanRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;
    private final EventRepository eventRepository;

    private final AddressRepository addressRepository;
    private final PerformanceValidator performanceValidator;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository,
                                  PerformanceMapper performanceMapper,
                                  EventRepository eventRepository,
                                  PerformanceValidator performanceValidator,
                                  HallPlanRepository hallPlanRepository,
                                  AddressRepository addressRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceMapper = performanceMapper;
        this.eventRepository = eventRepository;
        this.performanceValidator = performanceValidator;
        this.hallPlanRepository = hallPlanRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public PerformanceDto getById(long id) {
        log.trace("Get performance by id {}", id);
        Optional<Performance> performance = this.performanceRepository.findById(id);
        if (performance.isEmpty()) {
            throw new NotFoundException("Performance not found");
        }
        return this.performanceMapper.entityToDto(performance.get());
    }

    @Override
    public PerformanceDto create(PerformanceDto toCreate, Long eventId, Long hallPlanId) throws ValidationException {
        log.trace("Create performance {}", toCreate);

        performanceValidator.validate(toCreate, eventId);
        Address address = Address.builder()
            .street(toCreate.getStreet())
            .city(toCreate.getCity())
            .country(toCreate.getCountry())
            .postalCode(toCreate.getPostalCode())
            .streetNr(toCreate.getStreetNr())
            .build();
        addressRepository.save(address);
        Performance performance = Performance.builder()
            .name(toCreate.getName())
            .address(address)
            .date(LocalDate.parse(toCreate.getDate()))
            .startTime(LocalTime.parse(toCreate.getStartTime()))
            .endTime(null)
            .price(toCreate.getPrice())
            .build();

        Optional<HallPlan> dbHallPlan = hallPlanRepository.findById(hallPlanId);
        if (dbHallPlan.isPresent()) {
            HallPlan hallPlan = dbHallPlan.get();
            performance.setHallPlan(hallPlan);
        } else {
            throw new NotFoundException("HallPlan with ID " + hallPlanId + " not found");
        }

        Optional<Event> dbEvent = eventRepository.findById(eventId);
        if (dbEvent.isPresent()) {
            Event event = dbEvent.get();
            performance.setEvent(event);
            performance.setEndTime(performance.getStartTime().plusMinutes(event.getDurationInMinutes()));
        } else {
            throw new NotFoundException("Event with ID " + eventId + " not found");
        }
        performanceRepository.save(performance);
        return performanceMapper.entityToDto(performance);
    }
}
