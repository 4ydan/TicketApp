import { Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {ToastrService} from 'ngx-toastr';
import {ConfirmationDialogService} from '../../services/confirm-delete.service';
import {BookingService} from '../../services/booking.service';
import {BookingToCreate} from '../../dtos/event/booking';

@Component({
  selector: 'app-order-overview',
  templateUrl: './order-overview.component.html',
  styleUrls: ['./order-overview.component.scss']
})
export class OrderOverviewComponent implements OnInit {

  canceledBookings: BookingToCreate[] = [];
  boughtBookings: BookingToCreate[] = [];
  reservedBookings: BookingToCreate[] = [];

  reservedBookingsToBuy: BookingToCreate[] = [];
  bookingsToCancel: BookingToCreate[] = [];

  canceled: boolean;
  bought: boolean;
  reserved: boolean;

  canceledEmpty: boolean;
  reservedEmpty: boolean;
  boughtEmpty: boolean;

  canceledError: boolean;
  reservedError: boolean;
  boughtError: boolean;

  p = 1;
  count = 5;

  user: any;

  constructor(
    private userService: UserService,
    private notification: ToastrService,
    private confirmation: ConfirmationDialogService,
    private bookingService: BookingService,
  ) {
  }

  ngOnInit() {
    this.user = JSON.parse(localStorage.getItem('user')).id;
    this.bought = true;
    this.canceled = false;
    this.reserved = false;
    this.bookingService.getBookingsBought(this.user).subscribe({
      next: value => {
        this.boughtBookings = value;
        this.boughtEmpty = this.boughtBookings.length === 0;
      },
      error: err => {
        console.log('ERROR: ', err.message);
      }
    });
  }

  getBoughtBookings() {
    this.bookingsToCancel = [];
    this.p = 1;
    this.canceled = false;
    this.reserved = false;
    this.bought = true;
    const id = JSON.parse(localStorage.getItem('user')).id;
    this.bookingService.getBookingsBought(id).subscribe({
      next: value => {
        this.boughtBookings = value;
        this.boughtEmpty = this.boughtBookings.length === 0;
        console.log(this.boughtBookings);
      },
      error: err => {
        console.log('ERROR: ', err.message);
        this.boughtError = true;
      }
    });
  }

  getCanceledBookings() {
    this.canceledBookings = [];
    this.p = 1;
    this.canceled = true;
    this.bought = false;
    this.reserved = false;
    const id = JSON.parse(localStorage.getItem('user')).id;
    this.bookingService.getBookingsCanceled(id).subscribe({
      next: value => {
        if (value[0].length !== 0) {
          value[0].forEach(item => {
            this.canceledBookings.push(item);
          });
        }
        if (value[1].length !== 0) {
          value[1].forEach(item => {
            this.canceledBookings.push(item);
          });
        }
        this.canceledEmpty = this.canceledBookings.length === 0;
      },
      error: err => {
        console.log('ERROR: ', err.message);
        this.canceledError = true;
      }
    });
  }

  getReservedBookings() {
    this.bookingsToCancel = [];
    this.reservedBookingsToBuy = [];
    this.p = 1;
    this.canceled = false;
    this.reserved = true;
    this.bought = false;
    const id = JSON.parse(localStorage.getItem('user')).id;
    this.bookingService.getBookingsReserved(id).subscribe({
      next: value => {
        this.reservedBookings = value;
        this.reservedEmpty = this.reservedBookings.length === 0;
      },
      error: err => {
        console.log('ERROR: ', err.message);
        this.reservedError = true;
      }
    });
  }

  getChecked(ev: Event, booking: BookingToCreate) {
    if (ev.target['checked']) {
      if (booking.isPaid) {
        this.bookingsToCancel.push(booking);
      }
      if (!booking.isPaid) {
        this.reservedBookingsToBuy.push(booking);
        this.bookingsToCancel.push(booking);
      }
    } else {
      if (booking.isPaid) {
        const index = this.bookingsToCancel.indexOf(booking, 0);
        if (index > -1) {
          this.bookingsToCancel.splice(index, 1);
        }
      }
      if (!booking.isPaid) {
        const index = this.reservedBookingsToBuy.indexOf(booking, 0);
        if (index > -1) {
          this.bookingsToCancel.splice(index, 1);
        }
        const ind = this.bookingsToCancel.indexOf(booking, 0);
        if (ind > -1) {
          this.bookingsToCancel.splice(ind, 1);
        }
      }
    }
  }

  openCancelTicketDialog() {
    this.confirmation.confirm('Are you sure you want to cancel tickets?')
      .then((confirmed) => {
        if (confirmed) {
          this.cancelTickets();
        }
      });
  }

  openBuyReservedBookingsDialog() {
    this.confirmation.confirm('Are you sure you want to proceed with purchase?')
      .then((confirmed) => {
        if (confirmed) {
          this.buyReservedBookings();
        }
      });
  }

  buyReservedBookings() {
    this.bookingService.buyReservedBookings(this.reservedBookingsToBuy).subscribe({
      next: value => {
        this.notification.success('You successfully bought reserved booking!');
      },
      error: err => {
        console.log('ERROR CANCEL: ', err.message);
        this.notification.error('Something went wrong! Purchase was not successful!');
      }
    });
    this.reservedBookingsToBuy.forEach(item => {
      const index = this.reservedBookings.indexOf(item, 0);
      if (index > -1) {
        this.reservedBookings.splice(index, 1);
      }
    });
    if (this.reservedBookingsToBuy.length === 0) {
      this.reservedEmpty = true;
    }
    this.reservedBookingsToBuy = [];
  }

  cancelTickets() {
    if (this.reserved) {
      this.bookingService.cancelBookings(this.bookingsToCancel).subscribe({
        next: value => {
          console.log('CANCEL VALUE: ', value);
          if (this.bookingsToCancel.length === 1) {
            this.notification.success('Reserved booking successfully canceled');
          }
          if (this.bookingsToCancel.length > 1) {
            this.notification.success('Reserved bookings successfully canceled');
          }
        },
        error: err => {
          console.log('ERROR CANCEL: ', err.message);
          if (this.bookingsToCancel.length === 1) {
            this.notification.error('Something went wrong! Cancellation of booking was not successful!');
          }
          if (this.bookingsToCancel.length > 1) {
            this.notification.error('Something went wrong! Cancellation of bookings was not successful!');
          }
        }
      });
      this.reservedBookingsToBuy.forEach(item => {
        const index = this.reservedBookings.indexOf(item, 0);
        if (index > -1) {
          this.reservedBookings.splice(index, 1);
        }
      });
      if (this.reservedBookings.length === 0) {
        this.reservedEmpty = true;
      }
    }

    if (this.bought) {
      this.bookingService.cancelBookings(this.bookingsToCancel).subscribe({
        next: value => {
          if (this.bookingsToCancel.length === 1) {
            this.notification.success('Bought booking successfully canceled');
          }
          if (this.bookingsToCancel.length > 1) {
            this.notification.success('Bought bookings successfully canceled');
          }
        },
        error: err => {
          console.log('ERROR CANCEL: ', err.message);
          if (err.status === 406) {
            this.notification.error('Cannot cancel booking as you already spent the bonus points for it!');
          } else {
            if (this.bookingsToCancel.length === 1) {
              this.notification.error('Something went wrong! Cancellation of booking was not successful!');
            }
            if (this.bookingsToCancel.length > 1) {
              this.notification.error('Something went wrong! Cancellation of bookings was not successful!');
            }
          }
        }
      });
      this.bookingsToCancel.forEach(item => {
        const index = this.boughtBookings.indexOf(item, 0);
        if (index > -1) {
          this.boughtBookings.splice(index, 1);
        }
      });
      if (this.boughtBookings.length === 0) {
        this.boughtEmpty = true;
      }
    }
  }

}
