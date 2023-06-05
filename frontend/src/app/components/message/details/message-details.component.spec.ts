import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MessageDetailsComponent} from './message-details.component';

describe('DetailsComponent', () => {
  let component: MessageDetailsComponent;
  let fixture: ComponentFixture<MessageDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MessageDetailsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(MessageDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
