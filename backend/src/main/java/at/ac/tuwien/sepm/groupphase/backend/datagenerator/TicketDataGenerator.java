package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
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
public class TicketDataGenerator {
    private static final Faker FAKER = new Faker();
    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;


    private final BookingRepository bookingRepository;

    public TicketDataGenerator(TicketRepository ticketRepository, PerformanceRepository performanceRepository,
                               BookingRepository bookingRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostConstruct
    public void generateTickets() {
        if (ticketRepository.findAll().size() > 0) {
            log.debug("tickets already generated");
        } else {
            List<Performance> performanceList = performanceRepository.findAll();
            log.debug("generating between 30 to 60 performances for {} event", performanceList.size());
            log.debug("saving new tickets");
            for (Performance performance : performanceList) {
                List<Ticket> ticketList = new ArrayList<>();
                for (int k = 0; k < FAKER.random().nextInt(50, 60); k++) {
                    Ticket ticket = new Ticket();
                    ticket.setPrice(Math.round(FAKER.random().nextDouble(50.0, 150.0) * 100.0) / 100.0);
                    ticket.setPerformance(performance);
                    ticket.setStandingTicket(true);
                    ticketList.add(ticket);
                }

                ticketRepository.saveAll(ticketList);
            }
        }
    }

}
