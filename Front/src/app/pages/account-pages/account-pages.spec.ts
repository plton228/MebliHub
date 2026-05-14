import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountPages } from './account-pages';

describe('AccountPages', () => {
  let component: AccountPages;
  let fixture: ComponentFixture<AccountPages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountPages],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountPages);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
