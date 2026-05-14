import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { header } from "./components/header/header";
import { footer } from "./components/footer/footer";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, header, footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Front');
}
