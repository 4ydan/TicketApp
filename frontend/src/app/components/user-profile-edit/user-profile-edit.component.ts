import {Component, OnInit} from '@angular/core';
import {UserDetailed} from '../../dtos/user-detailed';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {Address} from '../../dtos/Address';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-user-profile-edit',
  templateUrl: './user-profile-edit.component.html',
  styleUrls: ['./user-profile-edit.component.scss']
})
export class UserProfileEditComponent implements OnInit {
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

  isUpdated: boolean;

  constructor(
    private userService: UserService,
    public router: Router,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.isUpdated = false;
    this.userService.getLoggedInUser().subscribe({
      next: value => {
        this.user = value;
      },
      error: err => {
        console.error('Error! ', err.message);
        this.notification.error('Something went wrong, please try again.');
      }
    });
  }

  saveUser(user: UserDetailed) {
    if (this.isUpdated) {
      this.userService.editUser(user).subscribe({
        next: () => {
          this.notification.success('Changes saved successfully');
          this.router.navigate(['/user-profile']);
        },
        error: err => {
          this.notification.error('Something went wrong, please try again.');
          console.error('Error! ', err.message);
        }
      });
    }
  }

  isChanged() {
    this.isUpdated = true;
  }
}
