import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-productos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './productos.html'
})
export class AdminProductos implements OnInit {
  public productos = signal<any[]>([]);

  // Formulario Agregar Producto
  public newProducto = { nombre: '', precio: 0, stock: 0, imagen: '', categoria: 'Celulares' };

  // Formulario Editar Producto
  public editProducto = { id: 0, nombre: '', precio: 0, stock: 0, imagen: '', categoria: 'Celulares' };

  // Formulario Actualizar Stock
  public stockUpdate = { id: 0, nombre: '', stock: 0 };

  constructor(private adminService: AdminService, private router: Router) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  public loadProducts(): void {
    this.adminService.getProductos().subscribe(data => {
      this.productos.set(data);
    });
  }

  public onAgregar(): void {
    this.adminService.agregarProducto(this.newProducto).subscribe({
      next: (res) => {
        if (res.success) {
          this.mostrarAlerta(res.message, 'success');
          this.loadProducts();
          this.closeModal('agregarProductoModal');
          this.newProducto = { nombre: '', precio: 0, stock: 0, imagen: '', categoria: 'Celulares' };
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      }
    });
  }

  public onEditar(): void {
    this.adminService.actualizarProducto(this.editProducto.id, this.editProducto).subscribe({
      next: (res) => {
        if (res.success) {
          this.mostrarAlerta(res.message, 'success');
          this.loadProducts();
          this.closeModal('editarProductoModal');
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      }
    });
  }

  public onActualizarStock(): void {
    this.adminService.actualizarStock(this.stockUpdate.id, this.stockUpdate.stock).subscribe({
      next: (res) => {
        if (res.success) {
          this.mostrarAlerta(res.message, 'success');
          this.loadProducts();
          this.closeModal('actualizarStockModal');
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      }
    });
  }

  public onEliminar(id: number): void {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      this.adminService.eliminarProducto(id).subscribe({
        next: (res) => {
          if (res.success) {
            this.mostrarAlerta(res.message, 'success');
            this.loadProducts();
          } else {
            this.mostrarAlerta(res.message, 'danger');
          }
        }
      });
    }
  }

  // Cargar datos en el formulario de edición
  public openEditModal(producto: any): void {
    this.editProducto = { ...producto };
  }

  // Cargar datos en el formulario de stock
  public openStockModal(producto: any): void {
    this.stockUpdate = { id: producto.id, nombre: producto.nombre, stock: producto.stock };
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
