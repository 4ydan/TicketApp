import {Component, OnInit} from '@angular/core';
import {Merchandise, MerchandiseRedeem} from '../../dtos/merchandise';
import {ShoppingCartService} from '../../services/shopping-cart.service';
import {MerchandiseService} from '../../services/merchandise.service';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';


@Component({
  selector: 'app-merchandise',
  templateUrl: './merchandise.component.html',
  styleUrls: ['./merchandise.component.scss']
})
export class MerchandiseComponent implements OnInit {

  amounts: Map<Merchandise, number> = new Map<Merchandise, number>();

  inTheCart: Map<Merchandise, boolean> = new Map<Merchandise, boolean>();

  inStock: Map<Merchandise, number[]> = new Map<Merchandise, number[]>();

  page = 0;

  filter = 'all';
  filterDisplay = 'All';

  sort = 'newest';
  sortDisplay = 'Newest';

  redeemingItem: Merchandise;

  isLoggedIn = false;
  availableBonusPoints = null;
  isCustomer: boolean;

  private merchandise: Merchandise[];

  constructor(private shoppingCartService: ShoppingCartService,
              private merchandiseService: MerchandiseService,
              private notification: ToastrService,
              private userService: UserService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      const role = this.authService.getUserRole();
      this.isCustomer = role === 'USER';
      this.getAvailableBonusPoints();
      this.isLoggedIn = true;
    }
    this.loadMerchandises();
  }

  fillInStock() {
    this.merchandise.forEach((value: Merchandise) => {
      this.inStock.set(value, Array.from({length: value.inStock}, (_, i) => i + 1));
    });
  }

  fillAmounts() {
    this.getMerchandise().forEach(merch => {
      this.amounts.set(merch, 1);
    });
  }

  fillInTheCart() {
    this.getMerchandise().forEach(merch => {
      this.inTheCart.set(merch, this.isInTheCart(merch.id));
    });
  }

  getAvailableBonusPoints() {
    this.userService.getLoggedInUser().subscribe({
      next: value => {
        this.availableBonusPoints = value.bonusPoints;
      },
      error: err => {
        console.error('Error! ', err.message);
        this.notification.error('Something went wrong, please try again.');
      }
    });
  }

  getMerchandise(): Merchandise[] {
    return this.merchandise === undefined ? undefined : this.merchandise.filter(m => m.inStock > 0);
  }

  loadMerchandises() {
    this.merchandiseService.getMerchandise(this.page, this.filter, this.sort).subscribe({
      next: (merchandise: Merchandise[]) => {
        this.merchandise = merchandise;
        this.fillInStock();
        this.fillAmounts();
        this.fillInTheCart();
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Backend is not running'
          : error.error;
        this.notification.error(errorMessage, 'Could Not Fetch Merchandises');
      }
    });
  }

  addToCart(merchandise: Merchandise) {
    this.shoppingCartService.setMerchandiseItem(merchandise, this.amounts.get(merchandise));
    this.inTheCart.set(merchandise, true);
  }

  updateAmount(merch: Merchandise, amount: number) {
    this.amounts.set(merch, amount);
  }

  isInTheCart(id: number): boolean {
    let is = false;
    this.shoppingCartService.getMerchandiseItems().forEach((value, key) => {
      if (key.id === id) {
        is = true;
      }
    });
    return is;
  }

  multiply(a: number, b: number): number {
    return a * b;
  }

  subtract(a: number, b: number): number {
    return a - b;
  }

  selectItem(merchandise: Merchandise) {
    if (!this.authService.isLoggedIn()) {
      window.scroll(0, 0);
      this.notification.warning('To redeem the product you need to log in!');
    } else {
      this.redeemingItem = merchandise;
    }
  }

  redeemItem(itemId: number, amount: number) {
    let redeem = new MerchandiseRedeem();
    redeem.userId = JSON.parse(localStorage.getItem('user'))?.id;
    redeem.itemId = itemId;
    redeem.amount = amount;

    this.merchandiseService.redeemMerchandise(redeem).subscribe({
      next: () => {
        this.notification.success('The product was claimed successfully!');
        redeem = new MerchandiseRedeem();
        this.getAvailableBonusPoints();
        this.loadMerchandises();
      },
      error: error => {
        const errorMessage = error.error;
        this.notification.error(errorMessage, 'Redeeming cannot be performed');
      }
    });
    setTimeout(() => {
      window.scrollTo(0, 0);
    }, 300);
  }
}
