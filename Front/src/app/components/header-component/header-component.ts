import { Component } from '@angular/core';
import { LucideAngularModule, TextAlignJustify } from 'lucide-angular';

@Component({
  selector: 'app-header-component',
  imports: [LucideAngularModule],
  templateUrl: './header-component.html',
  styleUrl: './header-component.scss',
})
export class HeaderComponent {
  isMenuOpen = false;

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen
  }
}
