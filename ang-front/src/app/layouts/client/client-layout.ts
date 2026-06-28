import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { Botonwsp } from '../../components/botonwsp/botonwsp';

@Component({
  selector: 'app-client-layout',
  standalone: true,
  imports: [RouterOutlet, Navbar, Footer, Botonwsp],
  templateUrl: './client-layout.html'
})
export class ClientLayout {}
