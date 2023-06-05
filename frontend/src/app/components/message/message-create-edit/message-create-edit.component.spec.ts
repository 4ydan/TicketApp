import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageCreateEditComponent } from './message-create-edit.component';

describe('MessageCreateEditComponent', () => {
  let component: MessageCreateEditComponent;
  let fixture: ComponentFixture<MessageCreateEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MessageCreateEditComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageCreateEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
