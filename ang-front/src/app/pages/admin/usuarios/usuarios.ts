import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.html'
})
export class AdminUsuarios implements OnInit {
  public usuarios = signal<any[]>([]);

  // Formulario Cambiar Rol
  public roleUpdate = { id: 0, username: '', nuevoRol: 'CLIENTE' };

  constructor(private adminService: AdminService, private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  public loadUsers(): void {
    this.adminService.getUsuarios().subscribe(data => {
      this.usuarios.set(data);
    });
  }

  public onCambiarRol(): void {
    this.adminService.cambiarRol(this.roleUpdate.id, this.roleUpdate.nuevoRol).subscribe({
      next: (res) => {
        if (res.success) {
          this.mostrarAlerta(res.message, 'success');
          this.loadUsers();
          this.closeModal('cambiarRolModal');
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      }
    });
  }

  public onEliminar(id: number): void {
    if (confirm('¿Estás seguro de eliminar este usuario?')) {
      this.adminService.eliminarUsuario(id).subscribe({
        next: (res) => {
          if (res.success) {
            this.mostrarAlerta(res.message, 'success');
            this.loadUsers();
          } else {
            this.mostrarAlerta(res.message, 'danger');
          }
        }
      });
    }
  }

  public openRoleModal(user: any): void {
    this.roleUpdate = { id: user.id, username: user.username, nuevoRol: user.rol };
  }

  public goBack(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  private closeModal(id: string): void {
    const modalElement = document.getElementById(id);
    if (modalElement) {
      const closeBtn = modalElement.querySelector('[data-bs-dismiss="modal"]') as HTMLElement;
      if (closeBtn) closeBtn.click();
    }
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
