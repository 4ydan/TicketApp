package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Profile("generateData")
@Component
public class PerformanceDataGenerator {
    private static final Faker FAKER = new Faker();
    private final EventRepository eventRepository;
    private final PerformanceRepository performanceRepository;
    private final AddressRepository addressRepository;
    private final HallPlanRepository hallPlanRepository;

    private final List<String> testCities = new ArrayList<>();
    private final List<String> testStreets = new ArrayList<>();

    public PerformanceDataGenerator(EventRepository eventRepository,
                                    PerformanceRepository performanceRepository,
                                    HallPlanRepository hallPlanRepository,
                                    AddressRepository addressRepository) {
        this.eventRepository = eventRepository;
        this.performanceRepository = performanceRepository;
        this.hallPlanRepository = hallPlanRepository;
        this.addressRepository = addressRepository;
        testCities.add("Wien");
        testCities.add("Berlin");
        testCities.add("Amsterdam");
        testCities.add("Linz");
        testCities.add("Cologne");
        testCities.add("Munich");
        testCities.add("London");
        testCities.add("Sofia");
        testCities.add("Bern");
        testCities.add("Milano");
        testCities.add("Venice");
        testStreets.add("Abdella Way");
        testStreets.add("Houdek Avenue");
        testStreets.add("Hudson Lane ");
        testStreets.add("Abney Avenue");
        testStreets.add("Illehaw Place");
        testStreets.add("Indale Place");
        testStreets.add("Memory Avenue");
    }

    @PostConstruct
    public void generatePerformances() {
        Artist artist = Artist.builder().name("Test").build();
        if (performanceRepository.findAll().size() > 0) {
            log.debug("performances already generated");
        } else {
            List<Event> eventList = eventRepository.findAll();
            List<HallPlan> hallPlanList = hallPlanRepository.findAll();
            log.debug("generating between 2 to 4 performances for {} event", eventList.size());
            log.debug("saving new performances");
            for (Event event : eventList) {
                ArrayList<Performance> performanceList = new ArrayList<>();
                List<LocalDate> dates = event.getStartDate().datesUntil(event.getEndDate()).toList();
                if (!dates.isEmpty()) {
                    for (int j = 0; j < FAKER.random().nextInt(2, 4); j++) {
                        Address address = Address.builder()
                            .country(FAKER.country().name())
                            .city(testCities.get(FAKER.random().nextInt(testCities.size() - 1)))
                            .postalCode(1040L)
                            .street(testStreets.get(FAKER.random().nextInt(testStreets.size() - 1)))
                            .streetNr(23L)
                            .build();
                        addressRepository.save(address);
                        LocalTime startTime = LocalTime.of(FAKER.random().nextInt(8, 13), 30);
                        Performance performance = Performance.builder()
                            .event(event)
                            .name("TEST-PERFORMANCE-EVENT-ID-" + event.getTitle())
                            .address(address)
                            .date(dates.get(FAKER.random().nextInt(dates.size())))
                            .price(Math.round(FAKER.random().nextDouble(50.0, 650.0) * 100.0) / 100.0)
                            .startTime(startTime)
                            .endTime(startTime.plusMinutes(event.getDurationInMinutes()))
                            .hallPlan(hallPlanList.get(FAKER.number().numberBetween(1, HallPlanDataGenerator.NUMBER_OF_HALLPLANS_TO_GENERATE)))
                            .build();
                        performanceList.add(performance);
                    }
                    performanceRepository.saveAll(performanceList);
                }
            }
        }
    }
}
