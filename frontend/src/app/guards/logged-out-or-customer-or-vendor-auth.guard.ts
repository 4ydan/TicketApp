import { Injectable } from '@angular/core';
import {CanActivate, Router} from '@angular/router';

import {AuthService} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoggedOutOrCustomerOrVendorAuthGuard implements CanActivate {
  constructor(private authService: AuthService,
              private router: Router) {
  }

  canActivate(): boolean {
    if (!this.authService.isLoggedIn() || this.authService.getUserRole() === 'USER' || this.authService.getUserRole() === 'VENDOR') {
      return true;
    } else {
      this.router.navigate(['']);
      return false;
    }
  }
}
