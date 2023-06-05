import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
// @ts-ignore
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {UserDetailed} from '../dtos/user-detailed';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private registBaseUri: string = this.globals.backendUri + '/registrations';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * confirm Account with specified token
   *
   * @param token of user
   */
  confirmAccount(token: string): Observable<string>  {
    console.log('Activate account with token ' + token);
    const params = new HttpParams().set('token', token);
    return this.httpClient.get(this.registBaseUri + '/confirmation', {params, responseType: 'text'});
  }

  /**
   * Sign up User
   *
   * @param user User data
   */
  signUpUser(user: UserDetailed): Observable<string> {
    console.log('Create user with email ' + user.email);
    if (user.address.country?.trim() === '' || undefined) {
      user.address.country = undefined;
    }
    if (user.address.city?.trim() === '' || undefined) {
      user.address.city = undefined;
    }
    if (user.address.street?.trim() === '' || undefined) {
      user.address.street = undefined;
    }
    console.log('USER TO REGISTER: ', user);
    return this.httpClient.post(this.registBaseUri, user, {responseType: 'text'});
  }

}
