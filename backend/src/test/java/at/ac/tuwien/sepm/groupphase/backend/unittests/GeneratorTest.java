package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.EventDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.HallPlanDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.MerchandiseDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.MessageDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.PerformanceDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.TicketDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class GeneratorTest implements TestData {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SectorCategoryRepository sectorCategoryRepository;

    @Mock
    private EntityManager entityManager;

    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 50;

    private static final int NUMBER_OF_MERCHANDISES_TO_GENERATE = 50;


    @Test
    public void testEventGeneratorWorksAndDoesNotGenerateAdditionalData() throws IOException {
        HallPlanDataGenerator gen0 = new HallPlanDataGenerator(hallPlanRepository, sectorCategoryRepository, seatRepository, performanceRepository);
        gen0.generateSeats();
        EventDataGenerator gen = new EventDataGenerator(eventRepository, artistRepository);
        gen.generateEvents();
        PerformanceDataGenerator gen2 = new PerformanceDataGenerator(eventRepository, performanceRepository, hallPlanRepository, addressRepository);
        gen2.generatePerformances();
        TicketDataGenerator gen3 = new TicketDataGenerator(ticketRepository, performanceRepository, bookingRepository);
        gen3.generateTickets();
        List<Performance> performanceList = performanceRepository.findAll();
        List<Ticket> ticketList = ticketRepository.findAll();
        int performanceListSize = performanceList.size();
        int ticketListSize = ticketList.size();
        //Test generators does not generate additional data
        gen.generateEvents();
        gen2.generatePerformances();
        gen3.generateTickets();

        List<Event> eventList = eventRepository.findAll();
        performanceList = performanceRepository.findAll();
        ticketList = ticketRepository.findAll();

        assertEquals(200, eventList.size());
        assertEquals(performanceListSize, performanceList.size());
        assertEquals(ticketListSize, ticketList.size());
    }

    @Test
    public void testMessageGeneratorWorksAndSavesPictures() throws Exception {
        MessageDataGenerator gen = new MessageDataGenerator(messageRepository);
        gen.setManager(entityManager);
        doNothing().when(entityManager).persist(Message.class);

        gen.generateMessage();

        verify(entityManager, times(NUMBER_OF_MESSAGES_TO_GENERATE)).persist(any());
        for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
            assertTrue(new PathResource(FOLDER_PATH + "test_message_img" + i + ".jpg").getFile().exists());
        }
    }

    @Test
    public void testMerchandiseGeneratorWorksAndSavesPictures() throws Exception {
        MerchandiseDataGenerator gen = new MerchandiseDataGenerator(merchandiseRepository);
        gen.setManager(entityManager);
        doNothing().when(entityManager).persist(Merchandise.class);

        gen.generateMerchandises();

        verify(entityManager, times(NUMBER_OF_MERCHANDISES_TO_GENERATE)).persist(any());
        for (int i = 0; i < NUMBER_OF_MERCHANDISES_TO_GENERATE; i++) {
            assertTrue(new PathResource(FOLDER_PATH + "test_merchandise_img" + i + ".jpg").getFile().exists());
        }
    }
}
