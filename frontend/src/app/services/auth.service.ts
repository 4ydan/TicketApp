import {Injectable} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
// @ts-ignore
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {UserService} from './user.service';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string = this.globals.backendUri + '/authentications';

  constructor(private httpClient: HttpClient, private globals: Globals, private userService: UserService) {
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   *
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
      .pipe(tap(
          (authResponse: string) => {
            this.setToken(authResponse);
            const decoded: any = jwt_decode(this.getToken());
            const email: string = decoded.sub;
            this.userService.getUserByEmail(email).subscribe(
              {
                next: (data) => {
                  console.log('received user', data);
                  localStorage.setItem('user', JSON.stringify(data));
                  JSON.parse(localStorage.getItem('user'));
                },
                error: (error) => {
                  console.log('Error getting user', error);
                  localStorage.setItem('user', null);
                  JSON.parse(localStorage.getItem('user'));
                }
              });
          },
        )
      );
  }


  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  /**
   * Log out user and remove token and user from local storage
   */
  logoutUser() {
    console.log('Logout');
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  /**
   * Get authentication token
   */
  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole() {
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ROLE_ADMIN')) {
        return 'ADMIN';
      } else if (authInfo.includes('ROLE_VENDOR')) {
        return 'VENDOR';
      } else if (authInfo.includes('ROLE_USER')) {
        return 'USER';
      }
    }
    return 'UNDEFINED';
  }

  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

}

