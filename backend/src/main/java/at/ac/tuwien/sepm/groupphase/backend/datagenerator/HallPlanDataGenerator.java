package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorCategory;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.SectorType;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("generateData")
@Component
public class HallPlanDataGenerator {
    public static final int NUMBER_OF_HALLPLANS_TO_GENERATE = 40;
    private static final Faker FAKER = new Faker();
    private final HallPlanRepository hallPlanRepository;
    private final SectorCategoryRepository sectorCategoryRepository;
    private final SeatRepository seatRepository;

    private final PerformanceRepository performanceRepository;

    public HallPlanDataGenerator(HallPlanRepository hallPlanRepository, SectorCategoryRepository sectorCategoryRepository,
                                 SeatRepository seatRepository, PerformanceRepository performanceRepository) {
        this.hallPlanRepository = hallPlanRepository;
        this.sectorCategoryRepository = sectorCategoryRepository;
        this.seatRepository = seatRepository;
        this.performanceRepository = performanceRepository;
    }

    @PostConstruct
    public void generateSeats() {
        this.generateSeatCategories();
        this.generateHallPlans();

        if (this.seatRepository.count() > 0) {
            log.debug("seats already exist");
            return;
        }

        List<Seat> seatList = new ArrayList<>();
        List<HallPlan> hallPlanList = this.hallPlanRepository.findAll();
        List<SectorCategory> sectorCategoryNoStandingList = this.sectorCategoryRepository.findAll();
        sectorCategoryNoStandingList.remove(this.sectorCategoryRepository.findSectorCategoryBySectorType(SectorType.STANDING));

        for (int i = 0; i < NUMBER_OF_HALLPLANS_TO_GENERATE; i++) {
            int rowNum = FAKER.number().numberBetween(6, 18);
            HallPlan tempHallPlan = hallPlanList.get(i);
            for (int rowCount = 0; rowCount < rowNum; rowCount++) {
                int colNum = FAKER.number().numberBetween(11, 21);
                for (int colCount = 0; colCount < colNum; colCount++) {
                    int indexOfSectorCategory;
                    if (rowCount <= (rowNum / 10 + 1)) {
                        indexOfSectorCategory = 0;
                    } else if (rowCount <= (rowNum / 6 + 1)) {
                        indexOfSectorCategory = 1;
                    } else if (rowCount <= rowNum / 3) {
                        indexOfSectorCategory = 2;
                    } else {
                        indexOfSectorCategory = 3;
                    }

                    SectorCategory tempSectorCategory = sectorCategoryNoStandingList.get(indexOfSectorCategory);
                    Seat seat = new Seat();
                    seat.setSeatNumber(colCount);
                    seat.setSeatRow(rowCount);
                    seat.setBooked(FAKER.random().nextBoolean());
                    seat.setHallPlan(tempHallPlan);
                    seat.setSectorCategory(tempSectorCategory);
                    seatList.add(seat);
                }
            }
        }

        log.debug("saving new seats");
        this.seatRepository.saveAll(seatList);
    }

    private void generateSeatCategories() {
        if (this.sectorCategoryRepository.count() > 0) {
            log.debug("sector categories already exist");
            return;
        }

        List<SectorCategory> sectorCategoryList = new ArrayList<>();
        int count = 4;
        for (SectorType type : SectorType.values()) {
            SectorCategory sectorCategory = new SectorCategory();
            sectorCategory.setSectorType(type);
            sectorCategory.setSurcharge(Double.valueOf(FAKER.number().numberBetween(10, 12)) * count--);
            sectorCategoryList.add(sectorCategory);
        }
        log.debug("saving new sector categories");
        this.sectorCategoryRepository.saveAll(sectorCategoryList);
    }

    private List<HallPlan> generateHallPlans() {
        if (this.hallPlanRepository.count() > 0) {
            log.debug("hall plans already exist");
            return null;
        }

        List<HallPlan> hallPlanList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HALLPLANS_TO_GENERATE; i++) {
            HallPlan hallPlan = new HallPlan();
            hallPlan.setName("Hall" + (i + 1));
            hallPlan.setDescription(FAKER.lorem().maxLengthSentence(50));
            int maxCapacity = FAKER.number().numberBetween(20, 50);
            hallPlan.setMaxStandingCapacity(maxCapacity);
            hallPlan.setBookedNumOfStandingTickets(FAKER.number().numberBetween(0, maxCapacity));
            hallPlanList.add(hallPlan);
        }
        log.debug("saving new hall plans");
        this.hallPlanRepository.saveAll(hallPlanList);
        return hallPlanList;
    }
}