import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './perfil.html'
})
export class Perfil implements OnInit {
  public userForm: any = {
    id: null,
    username: '',
    email: '',
    nombreCompleto: '',
    password: ''
  };

  constructor(public auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Verificar sesión y cargar datos en el formulario
    this.auth.checkStatus().subscribe(res => {
      if (res && res.autenticado) {
        this.userForm = { ...res.usuario, password: '' };
      } else {
        this.router.navigate(['/']);
      }
    });
  }

  public onSave(): void {
    this.auth.updateProfile(this.userForm).subscribe({
      next: (res) => {
        if (res.success) {
          this.mostrarAlerta(res.message, 'success');
          this.userForm = { ...res.usuario, password: '' };
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      },
      error: () => {
        this.mostrarAlerta('Error al actualizar el perfil', 'danger');
      }
    });
  }

  private mostrarAlerta(mensaje: string, tipo: string): void {
    const alertaHTML = `
      <div class="alert alert-${tipo} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3"
           role="alert" style="z-index: 9999; min-width: 300px;">
          <i class="fas fa-${tipo === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
          ${mensaje}
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    `;
    document.body.insertAdjacentHTML('afterbegin', alertaHTML);

    setTimeout(() => {
      const alerta = document.querySelector('.alert');
      if (alerta) alerta.remove();
    }, 3000);
  }
}
