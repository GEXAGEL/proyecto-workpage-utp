import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-facturas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './facturas.html'
})
export class AdminFacturas implements OnInit {
  public facturas = signal<any[]>([]);
  public selectedFactura = signal<any>(null);
  public showDetails = signal<boolean>(false);

  constructor(private adminService: AdminService, private router: Router) {}

  ngOnInit(): void {
    this.loadFacturas();
  }

  public loadFacturas(): void {
    this.adminService.getFacturas().subscribe({
      next: (data) => {
        this.facturas.set(data);
      },
      error: (error) => {
        console.error('Error loading facturas:', error);
        this.mostrarAlerta('Error al cargar facturas', 'danger');
      }
    });
  }

  public viewFactura(factura: any): void {
    this.selectedFactura.set(factura);
    this.showDetails.set(true);
  }

  public closeDetails(): void {
    this.showDetails.set(false);
    this.selectedFactura.set(null);
  }

  public goBack(): void {
    this.router.navigate(['/admin/dashboard']);
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
