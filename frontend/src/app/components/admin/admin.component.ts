import {Component, OnInit} from '@angular/core';
import {UserDetailed} from '../../dtos/user-detailed';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {ConfirmationDialogService} from '../../services/confirm-delete.service';
import {PasswordResetService} from 'src/app/services/password-reset.service';
import {UntypedFormBuilder} from '@angular/forms';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  users: UserDetailed[] = [];

  error = false;
  errorMessage = '';

  constructor(
    private userService: UserService,
    private passwordResetService: PasswordResetService,
    private formBuilder: UntypedFormBuilder,
    public router: Router,
    private notification: ToastrService,
    private confirmation: ConfirmationDialogService,
  ) {
  }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: value => {
        this.users = value;
        console.log('users: ', this.users);
      },
      error: err => {
        console.error('Error: ', err.message);
      },
    });
  }

  public openConfirmationDialog(user: UserDetailed) {
    this.confirmation.confirm('Are you sure you want to block ' + user.firstName + ' ' + user.lastName + '?')
      .then((confirmed) => {
        if (confirmed) {
          this.blockUser(user);
        }
      });
  }

  public openConfirmationDialogUnblock(user: UserDetailed) {
    this.confirmation.confirm('Are you sure you want to unblock ' + user.firstName + ' ' + user.lastName + '?')
      .then((confirmed) => {
        if (confirmed) {
          this.unblockUser(user);
        }
      });
  }

  public openConfirmationDialogResetPassword(user: UserDetailed) {
    this.confirmation.confirm('Are you sure you want to reset the password of ' + user.firstName + ' ' + user.lastName + '?')
      .then((confirmed) => {
        if (confirmed) {
          this.resetPassword(user);
        }
      });
  }

  async resetPassword(user: UserDetailed) {
    const toResetEmail = this.formBuilder.group({
      email: user.email
    });
    await this.passwordResetService.sendResetMail(toResetEmail).subscribe({
      next: () => {
        this.notification.info('User with email ' + user.email + ' received a password reset notification per mail');
      },
      error: error => {
        console.log('Failed sending mail to user');
        console.log(error);
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
        this.notification.error(`Sending mail failed: ` + error.error);
        console.log(this.errorMessage);
      }
    });
  }

  blockUser(user: UserDetailed) {
    if (user.role.includes('ADMIN')) {
      this.notification.info('Admin account cannot be blocked!');
      return;
    } else {
      this.userService.blockUser(user.email).subscribe({
        next: () => {
          this.notification.info('User with email ' + user.email + ' blocked');
          this.users.find(x => x.email === user.email).failedLogins = 5;
        },
        error: err => {
          console.error('Error! ', err.message.message);
          this.notification.error('Currently it is not possible to block this user');
        }
      });
    }
  }

  unblockUser(user: UserDetailed) {
    this.userService.unblockUser(user.email).subscribe({
      next: () => {
        this.notification.info('User with email ' + user.email + ' unblocked');
        this.users.find(x => x.email === user.email).failedLogins = 0;
      },
      error: err => {
        console.error('Error! ', err.message.message);
        this.notification.error('Currently it is not possible to unblock this user');
      }
    });
  }

}
