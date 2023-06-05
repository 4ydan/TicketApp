import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Injectable} from '@angular/core';
import {BookingToCreate} from '../dtos/event/booking';
import {forkJoin, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private bookingBaseUri: string = this.globals.backendUri + '/bookings';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Send a new booking to persist.
   *
   * @param bookingInfo {@link BookingToCreate} data for creation of a new booking
   * @return the booking number
   */
  sendBookingToCreate(bookingInfo: BookingToCreate): Observable<any> {
    console.log('INFO: ' + bookingInfo);
    console.log('POST: ' + this.bookingBaseUri);
    return this.httpClient.post<any>(this.bookingBaseUri, bookingInfo);
  }

  /**
   * Get bought bookings
   *
   * @param id booking id
   */
  getBookingsBought(id: any): Observable<any> {
    const param = new HttpParams().set('userId', id).set('isPaid', true).set('isCanceled', false);
    return this.httpClient.get<BookingToCreate[]>(this.bookingBaseUri, {params: param});
  }

  /**
   * Get reserved bookings
   *
   * @param id booking id
   */
  getBookingsReserved(id: any): Observable<any> {
    const param = new HttpParams().set('userId', id).set('isPaid', false).set('isCanceled', false);
    return this.httpClient.get<BookingToCreate>(this.bookingBaseUri, {params: param});
  }

  /**
   * Get cancelled bookings
   *
   * @param id cancelled booking id
   */
  getBookingsCanceled(id: any): Observable<any[]> {
    const param1 = new HttpParams().set('userId', id).set('isPaid', true).set('isCanceled', true);
    const param2 = new HttpParams().set('userId', id).set('isPaid', false).set('isCanceled', true);
    const response1 = this.httpClient.get<BookingToCreate[]>(this.bookingBaseUri, {params: param1});
    const response2 = this.httpClient.get<BookingToCreate[]>(this.bookingBaseUri, {params: param2});
    return forkJoin([response1, response2]);
  }


  /**
   * Cancel booking
   *
   * @param bookingsList list of bookings to be cancelled
   */
  cancelBookings(bookingsList: any[]) {
    return this.httpClient.post<any[]>(this.bookingBaseUri + '/canceled', bookingsList);
  }

  /**
   * Buy reserved bookings
   *
   * @param bookingList list of reserved bookings to be bought
   */
  buyReservedBookings(bookingList: any[]) {
    return this.httpClient.post<any[]>(this.bookingBaseUri + '/reserved', bookingList);
  }

}
