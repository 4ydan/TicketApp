import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderSearchComponent } from './header-search.component';

describe('SearchComponent', () => {
  let component: HeaderSearchComponent;
  let fixture: ComponentFixture<HeaderSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HeaderSearchComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
