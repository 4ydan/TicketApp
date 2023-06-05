import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ChangePassword} from '../../dtos/change-password';
import {UserDetailed} from '../../dtos/user-detailed';
import {AuthService} from '../../services/auth.service';
import {UserService} from '../../services/user.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  passwordObject: ChangePassword = {oldPassword: null, newPassword: null, email: null};
  confirmPassword: string;
  oldPassword: string;
  newPassword: string;
  user: UserDetailed;
  isVisibleOld: boolean;
  isVisibleNew: boolean;
  isVisibleConfirm: boolean;
  typeOld: any;
  typeNew: any;
  typeConfirm: any;

  constructor(
    public router: Router,
    public userService: UserService,
    public authService: AuthService,
    private notification: ToastrService
  ) { }

  ngOnInit() {
    this.isVisibleOld = false;
    this.isVisibleNew = false;
    this.isVisibleConfirm = false;
    this.typeOld = document.getElementById('oldPassword');
    this.typeNew = document.getElementById('newPassword');
    this.typeConfirm = document.getElementById('confirmPassword');
    this.userService.getLoggedInUser().subscribe({
      next: value => {
        this.user = value;
        this.passwordObject.email = this.user.email;
      },
      error: err => {
        console.error('Could not get logged-in user!', err.message);
        this.notification.error('Something went wrong, please try again.');
      }
    });
  }

  save(password: ChangePassword) {
    if (password.newPassword === this.confirmPassword) {
      this.userService.changePassword(password).subscribe({
        next: () => {
          this.notification.success('Password successfully changed');
          this.router.navigate(['/user-profile']);
        },
        error: err => {
          console.error('Error while changing password! ', err.message);
          this.notification.error('Password cannot be changed.');
        }
      });
    } else {
      this.notification.error('New password and confirm password do not match!');
    }
  }

  toggleOldPassword() {
    this.isVisibleOld = !this.isVisibleOld;
    if (this.isVisibleOld) {
      this.typeOld.setAttribute('type', 'text');
    } else {
      this.typeOld.setAttribute('type', 'password');
    }
  }
  toggleNewPassword() {
    this.isVisibleNew = !this.isVisibleNew;
    if (this.isVisibleNew) {
      this.typeNew.setAttribute('type', 'text');
    } else {
      this.typeNew.setAttribute('type', 'password');
    }
  }
  toggleConfirmPassword() {
    this.isVisibleConfirm = !this.isVisibleConfirm;
    if (this.isVisibleConfirm) {
      this.typeConfirm.setAttribute('type', 'text');
    } else {
      this.typeConfirm.setAttribute('type', 'password');
    }
  }

}
