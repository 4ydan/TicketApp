import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ShoppingCartService} from '../../services/shopping-cart.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  isAdmin: boolean;
  isVendor: boolean;

  constructor(public authService: AuthService,
              public shoppingCartService: ShoppingCartService,
              public router: Router) {
  }

  ngOnInit() {
    const role = this.authService.getUserRole();
    this.isAdmin = role === 'ADMIN';
    this.isVendor = role === 'VENDOR';
  }

  logoutUser() {
    this.isAdmin = false;
    this.authService.logoutUser();
  }

  countItems() {
    return this.shoppingCartService.getTotalItems();
  }
}
