import {Injectable} from '@angular/core';
import {Merchandise, MerchandisePurchase} from '../dtos/merchandise';
import {MerchandiseService} from './merchandise.service';
import {ToastrService} from 'ngx-toastr';
import {TicketShoppingCart, TicketToCreate} from '../dtos/event/ticket';
import {PurchaseService} from './purchase.service';
import {Purchase} from '../dtos/purchase';

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {

  private merchandiseItems: Map<Merchandise, number> = new Map(JSON.parse(localStorage.getItem('merchandiseItems')));

  private ticketItems: TicketShoppingCart[] = JSON.parse(localStorage.getItem('ticketItems'));

  constructor(private merchandiseService: MerchandiseService,
              private notification: ToastrService,
              private purchaseService: PurchaseService) {
  }

  /**
   * Set merchandise items
   *
   * @param merchandise item
   * @param amount of items
   */
  setMerchandiseItem(merchandise: Merchandise, amount: number) {
    this.merchandiseItems.set(merchandise, amount);
    this.syncMerchandiseItems();
  }

  /**
   * Set ticket items
   *
   * @param ticket to add to cart
   */
  setTicketItem(ticket: TicketShoppingCart) {
    let present = false;
    if (this.ticketItems === null) {
      this.ticketItems = [];
    }
    this.ticketItems.forEach(e => {
      if (e !== null && e.seat !== undefined && ticket.seat !== undefined && ticket.seat.id === e.seat.id) {
        present = true;
      }
    });
    if (present === false) {
      this.ticketItems.push(ticket);
      this.syncTicketItems();
    }
  }

  /**
   * Remove merchandise from cart
   *
   * @param merchandise to remove
   */
  removeMerchandiseItem(merchandise: Merchandise) {
    this.merchandiseItems.delete(merchandise);
    this.syncMerchandiseItems();
  }

  /**
   * Remove ticket from cart
   *
   * @param ticket to remove
   */
  removeTicketItem(ticket: TicketToCreate) {
    this.ticketItems.forEach((e, i) => {
      if (ticket === e) {
        this.ticketItems.splice(i, 1);
      }
    });
    this.syncTicketItems();
  }

  /**
   * Get total number of items
   */
  getTotalItems(): number {
    let sum = 0;
    this.merchandiseItems.forEach((value: number) => {
      sum = +sum + +value;
    });
    if (this.ticketItems !== null) {
      this.ticketItems.forEach(() => {
        sum = +sum + 1;
      });
    }
    return sum;
  }

  /**
   * Get total sum
   */
  getTotalSum(): number {
    let sum = 0;
    this.merchandiseItems.forEach((value: number, key: Merchandise) => {
      sum += value * key.price;
    });
    if (this.ticketItems !== null) {
      this.ticketItems.forEach((t) => {
        sum = sum + t.price;
      });
    }
    return sum;
  }

  /**
   * get merchandise items
   */
  getMerchandiseItems(): Map<Merchandise, number> {
    return this.merchandiseItems;
  }

  /**
   * Get ticket items
   */
  getTicketItems(): TicketShoppingCart[] {
    return this.ticketItems;
  }

  length(): number {
    return this.merchandiseItems.size;
  }

  syncMerchandiseItems() {
    localStorage.setItem('merchandiseItems', JSON.stringify(Array.from(this.merchandiseItems.entries())));
  }

  syncTicketItems() {
    localStorage.setItem('ticketItems', JSON.stringify(this.ticketItems));
  }

  /**
   * Purchase items
   */
  purchase() {
    const merchandises = [];
    this.merchandiseItems.forEach((value, key) => {
      const merchandise = new MerchandisePurchase();
      merchandise.id = key.id;
      merchandise.amount = value;
      merchandises.push(merchandise);
    });
    const tickets = [];
    this.ticketItems.forEach(ticket => {
      const ticketToCreate = new TicketToCreate(ticket.performance.id, +ticket.price.toFixed(2), ticket.seat, ticket.isStandingTicket);
      tickets.push(ticketToCreate);
    });
    const purchase = new Purchase();
    purchase.merchandises = merchandises;
    purchase.tickets = tickets;
    this.purchaseService.purchase(purchase).subscribe({
      next: () => {
        this.notification.success('The purchase was successful!');
        this.merchandiseItems.clear();
        this.syncMerchandiseItems();
        this.ticketItems.splice(0, this.ticketItems.length);
        this.syncTicketItems();
      },
      error: error => {
        const errorMessage = error.error;
        this.notification.error(errorMessage, 'Purchase cannot be performed');
      }
    });
    setTimeout(() => {
      window.scrollTo(0, 0);
    }, 300);
  }

  purchaseFromVendor(email: string) {
    const merchandises = [];
    this.merchandiseItems.forEach((value, key) => {
      const merchandise = new MerchandisePurchase();
      merchandise.id = key.id;
      merchandise.amount = value;
      merchandises.push(merchandise);
    });
    const tickets = [];
    this.ticketItems.forEach(ticket => {
      const ticketToCreate = new TicketToCreate(ticket.performance.id, +ticket.price.toFixed(2), ticket.seat, ticket.isStandingTicket);
      tickets.push(ticketToCreate);
    });
    const purchase = new Purchase();
    purchase.merchandises = merchandises;
    purchase.tickets = tickets;
    this.purchaseService.purchaseFromVendor(purchase, email).subscribe({
      next: () => {
        this.notification.success('The purchase was successful!');
        this.merchandiseItems.clear();
        this.syncMerchandiseItems();
        this.ticketItems.splice(0, this.ticketItems.length);
        this.syncTicketItems();
      },
      error: error => {
        const errorMessage = error.error;
        this.notification.error(errorMessage, 'Purchase cannot be performed');
      }
    });
    setTimeout(() => {
      window.scrollTo(0, 0);
    }, 300);
  }
}
