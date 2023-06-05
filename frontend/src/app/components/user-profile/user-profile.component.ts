import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UserDetailed} from '../../dtos/user-detailed';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {Address} from '../../dtos/Address';
import {ToastrService} from 'ngx-toastr';
import {ConfirmationDialogService} from '../../services/confirm-delete.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  address: Address = {
    addressId: null,
    country: null,
    city: null,
    postalCode: null,
    street: null,
    streetNr: null,
  };

  user: UserDetailed = {
    id: null,
    firstName: null,
    lastName: null,
    email: null,
    password: null,
    isActivated: false,
    role: null,
    createFrom: null,
    address: this.address,
    failedLogins: null,
    bonusPoints: null,
  };
  isCustomer: boolean;
  userAddress: string;

  constructor(
    public router: Router,
    private userService: UserService,
    private authService: AuthService,
    private notification: ToastrService,
    private confirmation: ConfirmationDialogService,
  ) {
  }

  ngOnInit(): void {
    const role = this.authService.getUserRole();
    this.isCustomer = role === 'USER';
    this.userService.getLoggedInUser().subscribe({
      next: value => {
        this.user = value;
        this.userAddress = null;

        if (this.user.address.city === '' &&
          this.user.address.street === '' &&
          this.user.address.streetNr === null &&
          this.user.address.postalCode === null &&
          this.user.address.country === '') {
          this.userAddress = '-';
        } else {
            if (this.user.address.country !== '') {
              this.userAddress = this.user.address.country;
              if (this.user.address.city !== '') {
                this.userAddress += ', ' + this.user.address.city;
              }
              if (this.user.address.postalCode !== null) {
                this.userAddress += ', ' + this.user.address.postalCode;
              }
              if (this.user.address.street !== '') {
                this.userAddress += ', ' + this.user.address.street;
                if (this.user.address.streetNr !== null) {
                  this.userAddress += ' ' + this.user.address.streetNr;
                }
              }
            } else {
                if (this.user.address.city !== '') {
                  this.userAddress = this.user.address.city;
                  if (this.user.address.postalCode !== null) {
                    this.userAddress += ', ' + this.user.address.postalCode;
                  }
                  if (this.user.address.street !== '') {
                    this.userAddress += ', ' + this.user.address.street;
                    if (this.user.address.streetNr !== null) {
                      this.userAddress += ' ' + this.user.address.streetNr;
                    }
                  }
                } else {
                  if (this.user.address.postalCode !== null) {
                    this.userAddress = '' + this.user.address.postalCode;
                    if (this.user.address.street !== '') {
                      this.userAddress += ', ' + this.user.address.street;
                      if (this.user.address.streetNr !== null) {
                        this.userAddress += ' ' + this.user.address.streetNr;
                      }
                    }
                  } else {
                    if (this.user.address.street !== '') {
                      this.userAddress = this.user.address.street;
                      if (this.user.address.streetNr !== null) {
                        this.userAddress += ' ' + this.user.address.streetNr;
                      }
                    }
                  }
                }
            }
        }
      },
      error: err => {
        console.error('Error! ', err.message);
        this.notification.error('Something went wrong, please try again.');
      }
    });
  }

  public openConfirmationDialog() {
    this.confirmation.confirm('Are you sure you want to delete your profile?')
      .then((confirmed) => {
        if (confirmed) {
          this.delete();
        }
      });
  }

  delete() {
    this.userService.deleteUser(this.user.email).subscribe(() => {
        this.notification.success('User successfully deleted.');
        this.authService.logoutUser();
        this.router.navigate(['/']);
      }
    );
  }
}
