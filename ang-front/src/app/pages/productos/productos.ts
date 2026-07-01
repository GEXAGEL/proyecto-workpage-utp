import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-productos-catalog',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './productos.html'
})
export class Productos implements OnInit {
  public productos = signal<any[]>([]);
  public categoriaSeleccionada = signal<string>('Todos');

  constructor(
    private productService: ProductService,
    private cart: CartService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.filtrarCategoria('Todos');
  }

  public filtrarCategoria(categoria: string): void {
    this.categoriaSeleccionada.set(categoria);
    this.productService.getProducts(categoria).subscribe(data => {
      this.productos.set(data);
    });
  }

public onAddToCart(productoId: number): void {
    // 1. Validamos usando el token real que guardamos en el navegador
    const token = localStorage.getItem('token');

    if (!token) {
      this.mostrarAlerta('Debes iniciar sesión para agregar productos al carrito', 'warning');
      const modalElement = document.getElementById('loginModal');
      if (modalElement) {
        const modalInstance = new (window as any).bootstrap.Modal(modalElement);
        modalInstance.show();
      }
      return;
    }

    // 2. Si el token existe, el interceptor lo inyectará automáticamente
    this.cart.addToCart(productoId).subscribe({
      next: (res) => {
        if (res.success) {
          this.mostrarAlerta(res.message, 'success');
          // Recargar los productos para actualizar el stock
          this.filtrarCategoria(this.categoriaSeleccionada());
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      },
      error: () => {
        this.mostrarAlerta('Error al agregar producto al carrito', 'danger');
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
