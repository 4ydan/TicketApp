import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Purchase} from '../dtos/purchase';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private purchaseBaseUri: string = this.globals.backendUri + '/purchases';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Performs purchase
   */
  purchase(purchase: Purchase): Observable<Purchase> {
    return this.httpClient.post<Purchase>(this.purchaseBaseUri, purchase);
  }

  /**
   * Performs purchase for Customer by Vendor
   */
  purchaseFromVendor(purchase: Purchase, email: string) {

    return this.httpClient.post<Purchase>(this.purchaseBaseUri+'/'+email, purchase);
  }
}
