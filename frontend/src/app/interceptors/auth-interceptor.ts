import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private globals: Globals) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authUri = this.globals.backendUri + '/authentication';


    if (req.url.includes(this.globals.backendUri + '/registrations')) {
      return next.handle(req);
    }
    if (req.url === this.globals.backendUri + '/addresses') {
      return next.handle(req);
    }
    if (req.url.includes(this.globals.backendUri + '/users')) {
      return next.handle(req);
    }
    if (req.url.includes(this.globals.backendUri + '/password-resets')) {
      return next.handle(req);
    }
    // Do not intercept authentication requests
    {
      if (req.url.includes(authUri)) {
        return next.handle(req);
      }
      // Do not intercept requests if user is not logged in
      if (!this.authService.isLoggedIn()) {
        return next.handle(req);
      }

      const authReq = req.clone({
        headers: req.headers.set('Authorization', this.authService.getToken())
      });

      return next.handle(authReq);
    }
  }
}
