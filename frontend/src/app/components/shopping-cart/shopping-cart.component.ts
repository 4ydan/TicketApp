import {Component, HostListener, OnInit} from '@angular/core';
import {Merchandise} from '../../dtos/merchandise';
import {ShoppingCartService} from '../../services/shopping-cart.service';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../services/user.service';
import {Address} from '../../dtos/Address';
import {AbstractControl, AsyncValidatorFn, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {TicketShoppingCart} from '../../dtos/event/ticket';
import {debounceTime, distinctUntilChanged, first, map, switchMap} from 'rxjs';
import {UserDetailed} from '../../dtos/user-detailed';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.scss']
})
export class ShoppingCartComponent implements OnInit {

  createForm: UntypedFormGroup;

  emailForm: UntypedFormGroup;

  inStock: Map<Merchandise, number[]> = new Map<Merchandise, number[]>();

  userAddress: Address;

  paymentIsActive = false;

  paymentOption = '';

  addressConfirmed = false;

  paddingClass;

  isVendor: boolean;

  submitted = false;

  private merchandiseItems: Map<Merchandise, number>;
  private ticketItems: TicketShoppingCart[];

  constructor(private shoppingCartService: ShoppingCartService,
              private authService: AuthService,
              private notification: ToastrService,
              private userService: UserService,
              private formBuilder: UntypedFormBuilder,) {

    this.createForm = this.formBuilder.group({
      cardNr: ['', [Validators.required, Validators.maxLength(16), Validators.minLength(16)]],
      cardName: ['', [Validators.required, Validators.maxLength(100), this.noWhitespaceValidator]],
      cardDate: ['', this.dateValidator],
      cardCVN: ['', [Validators.required, Validators.maxLength(3), Validators.minLength(3)]],
      ppEmail: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      ppPassword: ['', [Validators.required]],
      createFrom: [1],
    });
    this.emailForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'),
        this.noWhitespaceValidator], this.emailExistValidation()]
    },);

    this.onResize();
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    if (window.innerWidth < 1000) {
      this.paddingClass = 'pt-5';
    } else {
      this.paddingClass = 'pt-2';
    }
  }


  ngOnInit(): void {
    this.refreshMerchandise();
    this.refreshTickets();
    this.fillInStock();
    if (this.authService.isLoggedIn()) {
      this.getUserAddress();
      const role = this.authService.getUserRole();
      this.isVendor = role === 'VENDOR';
    }
  }

  getUserAddress() {
    this.userService.getLoggedInUser().subscribe({
      next: value => {
        this.userAddress = value.address;
      },
      error: err => {
        console.error('Error! ', err.message);
        this.notification.error('Something went wrong, please try again.');
      }
    });
  }

  fillInStock() {
    this.merchandiseItems.forEach((value: number, key: Merchandise) => {
      this.inStock.set(key, Array.from({length: key.inStock}, (_, i) => i + 1));
    });
    console.log(this.inStock);
  }

  getTotalItems(): number {
    return this.shoppingCartService.getTotalItems();
  }

  getTotalSum() {
    return this.shoppingCartService.getTotalSum();
  }

  refreshMerchandise() {
    this.merchandiseItems = this.shoppingCartService.getMerchandiseItems();
  }

  refreshTickets() {
    this.ticketItems = this.shoppingCartService.getTicketItems();
  }

  getMerchandiseItems(): Map<Merchandise, number> {
    return this.merchandiseItems;
  }

  getTicketItems(): TicketShoppingCart[] {
    return this.ticketItems;
  }

  removeMerchandise(merch: Merchandise) {
    this.shoppingCartService.removeMerchandiseItem(merch);
    this.refreshMerchandise();
  }

  removeTicket(ticket: TicketShoppingCart) {
    this.shoppingCartService.removeTicketItem(ticket);
    this.refreshTickets();
  }

  updateAmount(merch: Merchandise, amount: number) {
    this.shoppingCartService.setMerchandiseItem(merch, amount);
    this.refreshMerchandise();
  }

  buy() {
    if (!this.authService.isLoggedIn()) {
      this.notification.warning('To finish the purchase you need to log in!');
    } else {
      this.paymentIsActive = true;
    }
  }

  purchase() {
    this.shoppingCartService.purchase();
    this.paymentIsActive = false;
  }

  purchaseForCustomer() {
    this.submitted = true;
    if (this.emailForm.valid) {
      this.shoppingCartService.purchaseFromVendor(this.emailForm.controls.email.value);
      this.paymentIsActive = false;
      this.submitted = false;
    } else {
      console.log('Invalid input');
    }
  }

  numbersOnlyPaste(event): boolean {
    const data = event.clipboardData.getData('text');
    if (!Number(data)) {
      return false;
    }
    return true;
  }

  numberOnlyKeyPress(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;

  }

  noWhitespaceValidator(control: AbstractControl
  ) {
    const isWhitespace = (control && control.value && control.value.toString() || '').trim().length === 0 && control.value !== '';
    const isValid = !isWhitespace;
    return isValid ? null : {whitespace: true};
  }

  dateValidator(control: AbstractControl
  ) {
    return new Date(control.value) > new Date() ? null : {invalid: true};
  }

  uncheckRadioButtons() {
    // @ts-ignore
    const radio1: HTMLInputElement = document.getElementById('radio1');
    radio1.checked = false;
    // @ts-ignore
    const radio2: HTMLInputElement = document.getElementById('radio2');
    radio2.checked = false;
  }

  checkPaymentInformation(): boolean {
    if (this.paymentOption === '') {
      return true;
    }
    if (this.paymentOption === 'visa') {
      if (this.createForm.get('cardNr').invalid || this.createForm.get('cardNr').value === '') {
        return true;
      }
      if (this.createForm.get('cardName').invalid || this.createForm.get('cardName').value === '') {
        return true;
      }
      if (this.createForm.get('cardDate').invalid || this.createForm.get('cardDate').value === '') {
        return true;
      }
      if (this.createForm.get('cardCVN').invalid || this.createForm.get('cardCVN').value === '') {
        return true;
      }
    }
    if (this.paymentOption === 'paypal') {
      if (this.createForm.get('ppEmail').invalid || this.createForm.get('ppEmail').value === '') {
        return true;
      }
      if (this.createForm.get('ppPassword').invalid || this.createForm.get('ppPassword').value === '') {
        return true;
      }
    }
    return false;
  }

  emailExistValidation():
    AsyncValidatorFn {
    return control => control.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(value => this.userService.getActivatedCustomerByEmail(value)),
        map((unique: UserDetailed) => (unique ?  null: {customerNotFound: true})),
        first());
  }
}
