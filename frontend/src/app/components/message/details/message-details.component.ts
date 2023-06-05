import {Component, HostListener, OnInit} from '@angular/core';
import {Message} from '../../../dtos/message';
import {MessageService} from '../../../services/message.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {ConfirmationDialogService} from '../../../services/confirm-delete.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-message-details',
  templateUrl: './message-details.component.html',
  styleUrls: ['./message-details.component.scss']
})
export class MessageDetailsComponent implements OnInit {

  public message: Message;

  constructor(private messageService: MessageService,
              private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService,
              private notification: ToastrService,
              private confirmation: ConfirmationDialogService) {
  }

  @HostListener('window:popstate', ['$event'])
  onPopState() {
    localStorage.setItem('gotBack', 'true');
  }


  ngOnInit(): void {
    let id = 0;
    this.route.params.subscribe((params: Params) => {
      id = params.id;
    });
    this.loadMessage(id);
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  public openConfirmationDialog() {
    this.confirmation.confirm('Do you really want to delete this article?')
      .then((confirmed) => {
        if (confirmed) {
          this.deleteMessage();
        }
      });
  }

  deleteMessage() {
    this.messageService.deleteById(this.message.id).subscribe(
      () => {
        this.notification.success('Article successfully deleted.');
        this.router.navigate(['/message']);
      },
      (error) => {
        this.notification.error(`ERROR: ${error.error.message}.`);
      }
    );
  }

  private loadMessage(id) {
    this.messageService.getMessageById(id).subscribe({
      next: (message: Message) => {
        this.message = message;
      },
      error: error => {
        this.notification.error(`ERROR: ${error.error.message}.`);
      }
    });
  }
}
