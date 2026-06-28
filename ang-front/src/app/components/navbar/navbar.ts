import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './navbar.html'
})
export class Navbar implements OnInit {
  // Formulario Login
  public loginData = { username: '', password: '' };
  public loginError = '';

  // Formulario Registro
  public registerData = { username: '', password: '', email: '', nombreCompleto: '' };
  public registerError = '';
  public registerSuccess = '';

  // Resumen de la compra para el modal final
  public purchaseSummary = signal<any>({ items: [], total: 0 });

  constructor(
    public auth: AuthService,
    public cart: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Verificar si el usuario está autenticado al cargar el componente
    this.auth.checkStatus().subscribe(res => {
      if (res && res.autenticado) {
        this.cart.loadCart().subscribe();
      }
    });
  }

  public onLogin(): void {
    this.auth.login(this.loginData).subscribe({
      next: (res) => {
        if (res.success) {
          this.loginError = '';
          this.cart.loadCart().subscribe();
          this.closeModal('loginModal');
          this.loginData = { username: '', password: '' };
          this.mostrarAlerta(res.message, 'success');
        } else {
          this.loginError = res.message;
        }
      },
      error: () => {
        this.loginError = 'Error al conectar con el servidor';
      }
    });
  }

  public onRegister(): void {
    this.auth.register(this.registerData).subscribe({
      next: (res) => {
        if (res.success) {
          this.registerError = '';
          this.registerSuccess = res.message;
          this.mostrarAlerta(res.message, 'success');
          
          // Cerrar modal de registro y abrir el de login después de un momento
          setTimeout(() => {
            this.closeModal('registroModal');
            this.openModal('loginModal');
            this.registerSuccess = '';
            this.registerData = { username: '', password: '', email: '', nombreCompleto: '' };
          }, 1500);
        } else {
          this.registerError = res.message;
        }
      },
      error: () => {
        this.registerError = 'Error al registrar el usuario';
      }
    });
  }

  public onLogout(): void {
    this.auth.logout().subscribe({
      next: (res) => {
        this.mostrarAlerta(res.message, 'success');
        this.router.navigate(['/']);
      }
    });
  }

  public onDeleteAccount(): void {
    this.auth.deleteAccount().subscribe({
      next: (res) => {
        if (res.success) {
          this.closeModal('eliminarCuentaModal');
          this.mostrarAlerta(res.message, 'success');
          this.router.navigate(['/']);
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      }
    });
  }

  public changeQuantity(itemId: number, newQty: number): void {
    if (newQty < 1) {
      this.removeItem(itemId);
      return;
    }
    this.cart.updateQuantity(itemId, newQty).subscribe({
      error: (err) => {
        this.mostrarAlerta('No hay suficiente stock disponible', 'danger');
      }
    });
  }

  public removeItem(itemId: number): void {
    this.cart.removeFromCart(itemId).subscribe({
      next: (res) => {
        this.mostrarAlerta(res.message, 'success');
      }
    });
  }

  public onFinalizarCompra(): void {
    const currentCart = this.cart.cartData();
    if (!currentCart.items || currentCart.items.length === 0) {
      this.mostrarAlerta('El carrito está vacío', 'warning');
      return;
    }

    // Copiar items al resumen antes de limpiar el carrito al finalizar
    this.purchaseSummary.set({
      items: JSON.parse(JSON.stringify(currentCart.items)),
      total: currentCart.total
    });

    this.cart.checkout().subscribe({
      next: (res) => {
        if (res.success) {
          this.closeOffcanvas('carritoOffcanvas');
          this.openModal('resumenCompraModal');
        } else {
          this.mostrarAlerta(res.message, 'danger');
        }
      },
      error: (err) => {
        this.mostrarAlerta('Error al procesar la compra', 'danger');
      }
    });
  }

  // Métodos auxiliares para interactuar con JavaScript de Bootstrap
  private closeModal(id: string): void {
    const modalElement = document.getElementById(id);
    if (modalElement) {
      const closeBtn = modalElement.querySelector('[data-bs-dismiss="modal"]') as HTMLElement;
      if (closeBtn) closeBtn.click();
    }
  }

  private openModal(id: string): void {
    const modalElement = document.getElementById(id);
    if (modalElement) {
      const modalInstance = new (window as any).bootstrap.Modal(modalElement);
      modalInstance.show();
    }
  }

  private closeOffcanvas(id: string): void {
    const offcanvasElement = document.getElementById(id);
    if (offcanvasElement) {
      const closeBtn = offcanvasElement.querySelector('[data-bs-dismiss="offcanvas"]') as HTMLElement;
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
