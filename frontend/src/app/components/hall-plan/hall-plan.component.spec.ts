import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallPlanComponent } from './hall-plan.component';

describe('HallPlanComponent', () => {
  let component: HallPlanComponent;
  let fixture: ComponentFixture<HallPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HallPlanComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HallPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
