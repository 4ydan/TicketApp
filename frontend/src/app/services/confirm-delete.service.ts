import {Injectable} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmDeleteDialogComponent} from '../components/confirm-delete-dialog/confirm-delete-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class ConfirmationDialogService {

  constructor(private modalService: NgbModal) {
  }

  /**
   * Confirm deletion
   */
  public confirm(message: any): Promise<boolean> {
    const modalRef = this.modalService.open(ConfirmDeleteDialogComponent);
    modalRef.componentInstance.message = message;
    return modalRef.result;
  }
}
