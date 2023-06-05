package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NegativeBonusPointException;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.BookingServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.BookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class BookingServiceImplTest implements TestData {

    ApplicationUser user = ApplicationUser.builder()
        .id(5L)
        .email("user@email.com")
        .firstName("First")
        .lastName("Last")
        .build();


    Booking booking1 = Booking.builder()
        .id(1L)
        .date(LocalDate.of(2023, 10, 10))
        .isPaid(true)
        .isCanceled(false)
        .user(user)
        .build();

    Booking booking2 = Booking.builder()
        .id(2L)
        .date(LocalDate.of(2023, 11, 10))
        .isPaid(false)
        .isCanceled(false)
        .user(user)
        .build();

    Booking booking3 = Booking.builder()
        .id(3L)
        .date(LocalDate.of(2023, 12, 10))
        .isPaid(false)
        .isCanceled(false)
        .user(user)
        .build();


    Ticket ticket1 = Ticket.builder()
        .id(10L)
        .booking(booking1)
        .isStandingTicket(true)
        .price(100.00)
        .build();

    Ticket ticket2 = Ticket.builder()
        .id(20L)
        .booking(booking2)
        .isStandingTicket(true)
        .price(100.00)
        .build();


    BookingDto bookingDto1 = BookingDto.builder()
        .id(1L)
        .date(LocalDate.of(2023, 10, 10))
        .isPaid(true)
        .isCanceled(false)
        .tickets(List.of(ticket1))
        .userId(user.getId())
        .build();

    BookingDto bookingDto2 = BookingDto.builder()
        .id(2L)
        .date(LocalDate.of(2023, 11, 10))
        .isPaid(false)
        .isCanceled(false)
        .tickets(List.of(ticket1))
        .userId(user.getId())
        .build();

    BookingDto bookingDto3 = BookingDto.builder()
        .id(2L)
        .date(LocalDate.of(2023, 12, 10))
        .isPaid(false)
        .isCanceled(false)
        .tickets(List.of(ticket2))
        .userId(user.getId())
        .build();

    private BookingServiceImpl service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingValidator bookingValidator;
    @Mock
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        service = new BookingServiceImpl(
            performanceRepository,
            seatRepository,
            ticketRepository,
            bookingRepository,
            userRepository,
            bookingValidator,
            emailService
        );
    }

    @Test
    void cancelBookings() throws NegativeBonusPointException {
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(ticket1);
        booking1.setTickets(ticketList);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking1);

        List<BookingDto> bookingDtoList = new ArrayList<>();
        bookingDtoList.add(bookingDto1);

        Mockito.when(bookingRepository.findByUserAndIsPaidAndIsCanceled(user, booking1.getIsPaid(), booking1.getIsCanceled())).thenReturn(bookingList);
        service.cancelBookings(bookingDtoList);
        Mockito.verify(bookingRepository, Mockito.times(1)).updateBooking(Mockito.any());

    }

    @Test
    void buyReservedBookings() {
        List<Ticket> ticketList1 = new ArrayList<>();
        ticketList1.add(ticket1);
        List<Ticket> ticketList2 = new ArrayList<>();
        ticketList2.add(ticket2);
        booking3.setTickets(ticketList2);
        booking2.setTickets(ticketList1);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking2);
        bookingList.add(booking3);

        List<BookingDto> bookingDtoList = new ArrayList<>();
        bookingDtoList.add(bookingDto2);
        bookingDtoList.add(bookingDto3);

        Mockito.when(bookingRepository.findByUserAndIsPaidAndIsCanceled(user, booking2.getIsPaid(), booking3.getIsCanceled())).thenReturn(bookingList);
        service.buyReservedBookings(bookingDtoList);
        Mockito.verify(bookingRepository, Mockito.times(2)).buyReservedBooking(Mockito.any());
    }

}