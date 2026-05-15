import { ComponentFixture, TestBed } from '@angular/core/testing';

import { header } from './header';

describe('Header', () => {
  let component: header;
  let fixture: ComponentFixture<header>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [header],
    }).compileComponents();

    fixture = TestBed.createComponent(header);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
