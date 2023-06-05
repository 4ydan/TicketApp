import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Seat} from '../../dtos/seat';
import {SectorCategory} from '../../dtos/sectorCategory';
import {Performance} from '../../dtos/event/performance';
import {PerformanceService} from '../../services/performance.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HallPlanService} from '../../services/hall-plan.service';
import {SectorType} from '../../dtos/sectorType';
import {TicketShoppingCart, TicketToCreate} from '../../dtos/event/ticket';
import {ToastrService} from 'ngx-toastr';
import {BookingToCreate} from '../../dtos/event/booking';
import {BookingService} from '../../services/booking.service';
import {ShoppingCartService} from '../../services/shopping-cart.service';
import {AuthService} from '../../services/auth.service';
import {AbstractControl, AsyncValidatorFn, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {debounceTime, distinctUntilChanged, first, map, switchMap} from 'rxjs';
import {UserDetailed} from '../../dtos/user-detailed';
import {EventDetail} from '../../dtos/event/event-detail';
import {EventService} from '../../services/event.service';


@Component({
  selector: 'app-hall-plan',
  templateUrl: './hall-plan.component.html',
  styleUrls: ['./hall-plan.component.scss']
})
export class HallPlanComponent implements OnInit {

  @ViewChild('canvasScreenline', {static: true})
  canvasSL: ElementRef<HTMLCanvasElement>;
  @ViewChild('canvasStandingArea', {static: true})
  canvasSA: ElementRef<HTMLCanvasElement>;
  eid;
  pid;
  hid;
  calculatedPriceOfSeatTickets;
  calculatedPriceOfStandingTickets;
  selectedStandingTicketNumber;
  standingTicketPrice;
  event: EventDetail;
  performance: Performance;
  rows;
  seats: Seat[];
  tickets: TicketToCreate[];
  sectorCategories: SectorCategory[];
  sectorCategoryColors: string[] = ['#D353E7', '#E7536E', '#53E785', '#53E7CE', '#212CFC'];
  confirmationButtonText = '';
  confirmationInfoText = '';
  isLoggedIn = false;
  isVendor: boolean;
  form: UntypedFormGroup;
  submitted = false;
  userId: number;


  private ctx: CanvasRenderingContext2D;
  private newBookingToCreate: BookingToCreate;

  constructor(
    private eventService: EventService,
    private performanceService: PerformanceService,
    private hallPlanService: HallPlanService,
    private bookingService: BookingService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private shoppingCart: ShoppingCartService,
    private formBuilder: UntypedFormBuilder
  ) {
    this.calculatedPriceOfSeatTickets = 0;
    this.calculatedPriceOfStandingTickets = 0;
    this.selectedStandingTicketNumber = 0;
    this.tickets = [];
    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'),
        this.noWhitespaceValidator], this.emailExistValidation()]
    },);
  }

  get hEventImage() {
    return (this.event && this.event.image) ? this.event.image : null;
  }

  get hPerformanceName() {
    return (this.performance && this.performance.name) ? this.performance.name : null;
  }

  get hPerformancePlace() {
    return (this.performance && this.performance.city && this.performance.street && this.performance.streetNr)
      ? this.performance.city + ', ' + this.performance.street + ' ' + this.performance.streetNr : null;
  }

  get hPerformancePrice() {
    return (this.performance && this.performance.price) ? this.performance.price : null;
  }

  get hPerformanceDate() {
    return (this.performance && this.performance.date) ? this.performance.date : null;
  }

  get hPerformanceStartTime() {
    return (this.performance && this.performance.startTime) ? this.performance.startTime : null;
  }

  get hPerformanceEndTime() {
    return (this.performance && this.performance.endTime) ? this.performance.endTime : null;
  }

  get hPerformanceHallPlanAvailableTicketNum() {
    return (this.performance && this.performance.hallPlan && this.performance.hallPlan.maxStandingCapacity
      && this.performance.hallPlan.bookedNumOfStandingTickets)
      ? this.performance.hallPlan.maxStandingCapacity - this.performance.hallPlan.bookedNumOfStandingTickets : 0;
  }

  ngOnInit(): void {
    this.isVendor = this.authService.getUserRole() === 'VENDOR';

    this.ctx = this.canvasSL.nativeElement.getContext('2d');
    this.ctx.beginPath();
    this.ctx.moveTo(0, 30);
    this.ctx.bezierCurveTo(100, 0, 300, 0, 400, 30);
    this.ctx.strokeStyle = '#C4C4C4';
    this.ctx.stroke();
    this.ctx = this.canvasSA.nativeElement.getContext('2d');
    this.ctx.beginPath();
    this.ctx.rect(5, 5, 390, 70);
    this.ctx.textAlign = 'center';
    this.ctx.fillStyle = '#C4C4C4';
    this.ctx.fillText('standing area', 195, 45);
    this.ctx.strokeStyle = '#C4C4C4';
    this.ctx.stroke();
    this.route.params.subscribe(_ => {
      this.eid = +Number(this.route.snapshot.paramMap.get('eid'));
      this.pid = +Number(this.route.snapshot.paramMap.get('pid'));
      this.hid = +Number(this.route.snapshot.paramMap.get('hid'));
      this.getEvent(this.eid);
      this.getPerformance(this.pid);
      this.getSectorCategories(this.hid);
      this.getHallPlan(this.hid);
    });
  }

  getEvent(id: number): void {
    console.log(id);
    this.eventService.get(id).subscribe(event => this.event = event);
  }

  getPerformance(id: number) {
    this.performanceService.get(id).subscribe(performance => this.performance = performance);
  }

  getHallPlan(id: number) {
    this.hallPlanService.getHallPlan(id).subscribe({
      next: data => {
        this.seats = data;
        this.rows = Array.from({length: data[data.length - 1].seatRow}, (_, i) => i + 1);
      },
      error: error => {
        console.error('Error fetching hall plan', error);
      }
    });
  }

  getSectorCategories(id: number) {
    console.log(id);
    this.hallPlanService.getSectorCategories(id).subscribe({
      next: data => {
        this.sectorCategories = data;
        this.sectorCategories.forEach((element) => {
          if (element.sectorType !== SectorType.standing) {
            element.color = this.sectorCategoryColors.pop();
          }
        });
        this.standingTicketPrice = this.getSurchargeBySectorType(SectorType.standing) + this.performance.price;
      },
      error: error => {
        console.error('Error fetching hall plan', error);
      }
    });
  }

  selectSeat(clickedSeat) {
    this.seats = this.seats.map(object => {
      if (object.seatRow === clickedSeat.seatRow && object.seatNumber === clickedSeat.seatNumber) {
        const category = this.sectorCategories.find(obj => obj.sectorType === clickedSeat.sectorType);
        const ticketPrice = (category.surcharge + this.performance.price);
        if (clickedSeat.isSelected) {
          this.calculatedPriceOfSeatTickets -= ticketPrice;
          this.tickets = this.tickets.filter(ticket => {
            if (ticket.seat.id !== object.id) {
              return ticket;
            }
          });
        } else {
          this.calculatedPriceOfSeatTickets += ticketPrice;
          const tempTicket = new TicketToCreate(this.performance.id, ticketPrice, object, false);
          this.tickets.push(tempTicket);
        }

        return {...object, isSelected: !object.isSelected};
      }
      return object;
    });
  }

  standingTicketOnChange() {
    this.calculatedPriceOfStandingTickets = this.selectedStandingTicketNumber * this.standingTicketPrice;
  }

  getSurchargeBySectorType(type: SectorType) {
    return this.sectorCategories.find(obj => obj.sectorType === type).surcharge;
  }

  getSeatColor(isBooked, isSelected, sectorType) {
    if (isBooked) {
      return '#C4C4C4';
    } else if (isSelected) {
      return '#07571B';
    }
    if (this.sectorCategories) {
      const category = this.sectorCategories.find(obj => obj.sectorType === sectorType);
      return category.color;
    }
    return null;
  }

  async onClickSelectionConfirmation() {
    if (!this.isVendor || this.form.valid) {
      if (!localStorage.getItem('user')) {
        this.router.navigate(['/login']);
        return;
      }

      if (this.tickets.length + this.selectedStandingTicketNumber > 10) {
        this.notification.error('Tickets can be selected up to maximum 10 of a performance at a time',
          'Maximum 10 tickets for a reservation or purchase');
        return;
      }

      for (let i = 0; i < this.selectedStandingTicketNumber; i++) {
        const tempTicket = new TicketToCreate(this.performance.id,
          this.performance.price + this.getSurchargeBySectorType(SectorType.standing),
          null, true);
        this.tickets.push(tempTicket);
      }

      let isPaid = false;
      let bookingMethodText = 'Reservation';
      let reservationReminder = 'Don\'t forget to pick up the tickets 30 minutes before the performance starts,' +
        ' otherwise the ticket will expire.';
      if (this.confirmationButtonText === 'Buy') {
        isPaid = true;
        bookingMethodText = 'Payment';
        reservationReminder = '';
      }
      if (this.isVendor) {
        const temp = await this.userService.getActivatedUserByEmail(this.form.controls.email.value).toPromise();
        this.userId = temp.id;
        this.form.controls.email.setValue('');
      } else {
        this.userId = JSON.parse(localStorage.getItem('user')).id;
      }

      this.newBookingToCreate = new BookingToCreate(null, isPaid, this.tickets, new Date(), this.userId, false);

      console.log(this.newBookingToCreate);
      this.bookingService.sendBookingToCreate(this.newBookingToCreate).subscribe({
        next: data => {
          if (reservationReminder.length > 0) {
            reservationReminder += (' Your reservation number is ' + data + '.');
          }
          this.notification.success('Your ' + bookingMethodText + ' was successful.' + reservationReminder, 'Booking confirmed');
          this.getPerformance(this.pid);
          this.getHallPlan(this.hid);
        },
        error: error => {
          console.error('Error creating booking', error);
          this.notification.error(error.error, 'Error creating booking');
        }
      });
      setTimeout(() => {
        window.scrollTo(0, 0);
      }, 600);

      this.tickets = [];
      this.calculatedPriceOfStandingTickets = 0;
      this.calculatedPriceOfSeatTickets = 0;
      this.selectedStandingTicketNumber = 0;
      this.seats.forEach(obj => obj.isSelected = false);
      this.isLoggedIn = false;
    } else {
      console.log('Invalid input');
    }
  }

  validateSelection(tryToReserve: boolean
  ) {
    if (!localStorage.getItem('user')) {
      this.confirmationInfoText = 'Please login before you want to buy or reserve tickets!';
      this.confirmationButtonText = 'Login';
      return;
    } else if ((this.tickets.length + this.selectedStandingTicketNumber) < 1) {
      this.notification.error('Please select at least one seat or standing place', 'No Seat Selected');
      this.confirmationInfoText = 'Please select at least one seat or standing place!';
    } else {
      this.confirmationInfoText = 'Are you sure you want to book the following seats/standing tickets?';
    }
    this.isLoggedIn = true;
    if (tryToReserve) {
      this.confirmationButtonText = 'Reserve';
    } else {
      this.confirmationButtonText = 'Buy';
    }
  }

  addToShoppingCart() {
    for (let i = 0; i < this.selectedStandingTicketNumber; i++) {
      const tempTicket = new TicketToCreate(this.performance.id,
        this.performance.price + this.getSurchargeBySectorType(SectorType.standing),
        null, true);
      this.tickets.push(tempTicket);
    }
    this.tickets.forEach(t => {
      this.shoppingCart.setTicketItem(new TicketShoppingCart(this.performance, t.price, t.seat, t.isStandingTicket));
    });
    this.calculatedPriceOfSeatTickets = 0;
    this.calculatedPriceOfStandingTickets = 0;
    this.selectedStandingTicketNumber = 0;
    this.seats.forEach(s => {
      s.isSelected = false;
    });
    let message;
    if (this.tickets.length > 1) {
      message = 'Tickets successfully added to the shopping cart!';
    } else {
      message = 'Ticket successfully added to the shopping cart!';
    }
    this.notification.success(message);
    this.tickets = [];
  }

  checkSectorType(sectorType: SectorType) {
    if (sectorType === SectorType.standing) {
      return '€  (Standing)';
    }
    return '€';
  }

  noWhitespaceValidator(control: AbstractControl
  ) {
    const isWhitespace = (control && control.value && control.value.toString() || '').trim().length === 0 && control.value !== '';
    const isValid = !isWhitespace;
    return isValid ? null : {whitespace: true};
  }

  emailExistValidation(): AsyncValidatorFn {
    return control => control.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(value => this.userService.getActivatedCustomerByEmail(value)),
        map((unique: UserDetailed) => (unique ? null : {emailTaken: true})),
        first());
  }

}
