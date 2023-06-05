import {Seat} from '../seat';
import {Performance} from './performance';

export class TicketToCreate {
  performanceId?: number;
  price: number;
  seat?: Seat;
  isStandingTicket: boolean;

  constructor(performanceId: number, price: number, seat: Seat, isStandingTicket: boolean) {
    this.performanceId = performanceId;
    this.price = price;
    if (seat) {
      this.seat = seat;
    }
    this.isStandingTicket = isStandingTicket;
  }

}

export class TicketShoppingCart {
  performance: Performance;
  price: number;
  seat?: Seat;
  isStandingTicket: boolean;

  constructor(performance: Performance, price: number, seat: Seat, isStandingTicket: boolean) {
    this.performance = performance;
    this.price = price;
    if (seat) {
      this.seat = seat;
    }
    this.isStandingTicket = isStandingTicket;
  }
}
