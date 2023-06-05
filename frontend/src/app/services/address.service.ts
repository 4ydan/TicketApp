import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Address} from '../dtos/Address';


@Injectable({
  providedIn: 'root'
})
export class AddressService {

  private authBaseUri: string = this.globals.backendUri + '/addresses';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Persists Customer to the backend
   *
   * @param address address to add
   */
  createAddress(address: Address): Observable<Address> {
    console.log('Create address ' + address.toString());
    return this.httpClient.post(this.authBaseUri, address);
  }

  /**
   * Get all unique countries from the backend
   *
   */
  getUniqueCountries(): Observable<Address> {
    console.log('Get unique countries ');
    return this.httpClient.get(this.authBaseUri + '/countries');
  }

  /**
   * Get all unique postal codes from the backend
   *
   */
  getUniquePostalCodes(): Observable<Address> {
    console.log('Get unique postal codes ');
    return this.httpClient.get(this.authBaseUri + '/codes');
  }

  /**
   * Get all unique cities from the backend
   *
   */
  getUniqueCities(): Observable<Address> {
    console.log('Get unique cities');
    return this.httpClient.get(this.authBaseUri + '/cities');
  }
}
