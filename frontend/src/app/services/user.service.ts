import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {UserDetailed} from '../dtos/user-detailed';
import {Injectable} from '@angular/core';
import jwt_decode from 'jwt-decode';
import {ChangePassword} from '../dtos/change-password';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private authBaseUri: string = this.globals.backendUri;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Persists Customer to the backend
   *
   * @param user to persist
   */
  createCustomer(user: UserDetailed): Observable<string> {
    console.log('Create user with email ' + user.email);
    return this.httpClient.post(this.authBaseUri + '/registrations', user, {responseType: 'text'});
  }

  /**
   * Get user by email
   *
   * @param string user email
   */
  getUserByEmail(string: string): Observable<any> {
    console.log('Get user with email ' + string);
    return this.httpClient.get(this.authBaseUri + '/users/' + string);
  }

  /**
   * Activate user
   *
   * @param string user email
   */
  getActivatedUserByEmail(string: string): Observable<UserDetailed> {
    console.log('Get user with email ' + string);
    return this.httpClient.get<UserDetailed>(this.authBaseUri + '/users/activated/' + string);
  }

  /**
   * Get an activated customer by his email address
   *
   * @param email of the customer
   */
  getActivatedCustomerByEmail(email: string): Observable<UserDetailed> {
    console.log('Get customer with email ' + email);
    return this.httpClient.get<UserDetailed>(this.authBaseUri + '/users/activated/customers/' + email);
  }

  getLoggedInUser() {
    const x = localStorage.getItem('authToken');
    const s = x.split(' ')[1];
    const email = this.getDecodedAccessToken(s).sub;
    return this.getUserByEmail(email);
  }

  /**
   * Get decoded token
   *
   * @param token to decode
   */
  getDecodedAccessToken(token: string): any {
    try {
      return jwt_decode(token);
    } catch (Error) {
      return null;
    }
  }

  /**
   * Get all users
   */
  getAllUsers(): Observable<UserDetailed[]> {
    console.log('Get all users');
    return this.httpClient.get<UserDetailed[]>(this.authBaseUri + '/users');
  }

  /**
   * Edit existing user
   *
   * @param user to edit
   */
  editUser(user: UserDetailed): Observable<UserDetailed> {
    console.log('Edit user: ', user);
    return this.httpClient.put<UserDetailed>(this.authBaseUri + '/users', user);
  }

  /**
   * Change user password
   *
   * @param password to change
   */
  changePassword(password: ChangePassword): Observable<ChangePassword> {
    console.log('Change password ', password);
    return this.httpClient.put<ChangePassword>(this.authBaseUri + '/users/changePassword', password);
  }

  /**
   * Delete existing user
   *
   * @param email of user to be deleted
   */
  deleteUser(email: string) {
    console.log('Delete user with email: ', email);
    return this.httpClient.delete(this.authBaseUri + '/users/' + email);
  }

  /**
   * Block user account
   *
   * @param email of a user to be blocked
   */
  blockUser(email: string) {
    console.log('Block user with email: ', email);
    return this.httpClient.put(this.authBaseUri + '/users/' + email + '?block=true', email);
  }

  /**
   * Unblock user account
   *
   * @param email of a user to be unblocked
   */
  unblockUser(email: string) {
    console.log('Unblock user with email: ', email);
    return this.httpClient.put(this.authBaseUri + '/users/' + email + '?block=false', email);
  }
}
