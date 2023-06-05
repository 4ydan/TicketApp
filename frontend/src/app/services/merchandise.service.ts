import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';

import {Merchandise, MerchandiseRedeem} from '../dtos/merchandise';

@Injectable({
  providedIn: 'root'
})
export class MerchandiseService {

  private merchandiseBaseUri: string = this.globals.backendUri + '/merchandises';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all messages from the backend
   */
  getMerchandise(page: number, filter: string, sort: string): Observable<Merchandise[]> {
      return this.httpClient.get<Merchandise[]>(this.merchandiseBaseUri + '?page=' + page + '&filter=' + filter + '&sort=' + sort);
    }

    /**
     * Exchanges item for bonus points
     */
  redeemMerchandise(merchandise: MerchandiseRedeem): Observable<any> {
    console.log(merchandise);
    return this.httpClient.put<any>(this.merchandiseBaseUri+'/bonusPoints',merchandise);
  }
}
