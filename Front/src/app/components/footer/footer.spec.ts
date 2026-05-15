import { ComponentFixture, TestBed } from '@angular/core/testing';

import { footer } from './footer';

describe('footer', () => {
  let component: footer;
  let fixture: ComponentFixture<footer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [footer],
    }).compileComponents();

    fixture = TestBed.createComponent(footer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
