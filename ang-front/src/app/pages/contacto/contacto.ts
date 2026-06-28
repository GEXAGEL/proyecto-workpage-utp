import { Component } from '@angular/core';

@Component({
  selector: 'app-contacto',
  standalone: true,
  templateUrl: './contacto.html'
})
export class Contacto {
  public onSubmit(event: Event): void {
    event.preventDefault();
    alert('Mensaje enviado exitosamente. Nos brindará mayor información y nos comunicaremos contigo pronto.');
  }
}
