import { Component, OnInit } from '@angular/core';
import {Message} from '../../../dtos/message';
import {MessageService} from '../../../services/message.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, ReplaySubject} from 'rxjs';
import { NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CreateEditMessage } from 'src/app/dtos/CreateEditMessage';

export enum MessageCreateEditMode {
  create,
  edit,
};

@Component({
  selector: 'app-message-create-edit',
  templateUrl: './message-create-edit.component.html',
  styleUrls: ['./message-create-edit.component.scss']
})
export class MessageCreateEditComponent implements OnInit {
  mode: MessageCreateEditMode = MessageCreateEditMode.create;
  message: Message = {
    title: '',
    shortDescription: '',
    description: '',
    publishedAt: undefined,
    picture: '',
    id: 0
  };
  newMessage: CreateEditMessage = {
    title: '',
    shortDescription: '',
    description: '',
    picture: null,
  };
  errorMessage = '';

  constructor(
    private messageService: MessageService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService,
    private http: HttpClient,
    ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case MessageCreateEditMode.create:
        return 'Create News';
      case MessageCreateEditMode.edit:
        return 'Edit News';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case MessageCreateEditMode.create:
        return 'Create';
      case MessageCreateEditMode.edit:
        return 'Edit';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === MessageCreateEditMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case MessageCreateEditMode.create:
        return 'created';
      case MessageCreateEditMode.edit:
        return 'edited';
      default:
        return '?';
    }
  }

  uploadSingleImage(event) {
    const file = event.target.files[0];
    this.getBase64(file).subscribe(base64 => {
      this.message.picture = base64;
    });
    this.newMessage.picture = file;
  }

  getBase64(file: File): Observable<string> {
    const result = new ReplaySubject<string>(1);
    const reader = new FileReader();
    reader.readAsBinaryString(file);
    reader.onload = (event) => result.next(btoa(event.target.result.toString()));
    return result;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    if (this.modeIsCreate === false) {
      let id = 0;
      this.route.params.subscribe((params: Params) => {
        id = params.id;
      });
      this.getMessageById(id);
    } else {
      this.http.get('assets/initialImage.txt', {responseType: 'text'})
        .subscribe(data => this.message.picture = data);
    }
  }

  getMessageById(id: number): void {
    this.messageService.getMessageById(id)
      .subscribe({
        next: data => {
          console.log('received news', data);
          this.message = data;
        },
        error: error => {
          console.error('Error fetching message', error);
          if (error.status === 0) {
            this.errorMessage = 'Is the backend up?';
          } else if (error.status === 409) {
            this.errorMessage = error.error.message;
          } else if (error.status === 422) {
            this.errorMessage = error.error.errors;
          }
          this.notification.error(this.errorMessage, 'Could Not Fetch message');
        }
      });
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      if (this.message.description === '') {
        delete this.message.description;
      }
      let messageObservable: Observable<CreateEditMessage>;
      this.newMessage.title = this.message.title;
      this.newMessage.shortDescription = this.message.shortDescription;
      this.newMessage.description = this.message.description;
      switch (this.mode) {
        case MessageCreateEditMode.create:
          messageObservable = this.messageService.createMessage(this.newMessage);
          break;
        case MessageCreateEditMode.edit:
          messageObservable = this.messageService.editMessage(this.newMessage, this.message.id);
          break;
        default:
          console.error('Unknown MessageCreateEditMode', this.mode);
          return;
      }
      messageObservable.subscribe({
        next: data => {
          this.notification.success(`News ${this.message.title} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/message']);
        },
        error: error => {
          console.error('Error creating news', error);
          if (error.status === 0) {
            this.errorMessage = 'Is the backend up?';
          } else if (error.status === 409) {
            this.errorMessage = error.error;
          } else if (error.status === 422) {
            this.errorMessage = error.error;
          }
          this.notification.error(this.errorMessage, 'Could Not Create/Edit News');
        }
      });
    }
  }
}
