import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallPlanAppLayoutComponent } from './hall-plan-app-layout.component';

describe('HallPlanAppLayoutComponent', () => {
  let component: HallPlanAppLayoutComponent;
  let fixture: ComponentFixture<HallPlanAppLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HallPlanAppLayoutComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HallPlanAppLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
