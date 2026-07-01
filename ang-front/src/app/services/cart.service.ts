import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  public cartData = signal<any>({ items: [], total: 0.0 });
  public cartCount = signal<number>(0);

  constructor(private http: HttpClient) { }

  public loadCart(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/carrito/datos`).pipe(
      tap(data => {
        this.cartData.set(data);
        const count = data.items ? data.items.reduce((sum: number, item: any) => sum + item.cantidad, 0) : 0;
        this.cartCount.set(count);
      })
    );
  }

public addToCart(productoId: number, cantidad: number = 1): Observable<any> {
  const params = { productoId: productoId.toString(), cantidad: cantidad.toString() };
  
  // Agregamos headers para ser explícitos y evitar bloqueos de seguridad
  const headers = { 'Content-Type': 'application/json' };

  return this.http.post<any>(`${environment.apiUrl}/carrito/agregar`, null, { 
    params, 
    headers, // <-- añadimos los headers
    withCredentials: true 
  }).pipe(
    tap(res => {
      if (res && res.success) {
        this.loadCart().subscribe();
      }
    })
  );
}

  public removeFromCart(itemId: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/carrito/eliminar/${itemId}`, {}).pipe(
      tap(res => {
        if (res && res.success) {
          this.loadCart().subscribe();
        }
      })
    );
  }

  public updateQuantity(itemId: number, cantidad: number): Observable<any> {
    const params = { cantidad: cantidad.toString() };
    return this.http.post<any>(`${environment.apiUrl}/carrito/actualizar/${itemId}`, null, { params }).pipe(
      tap(res => {
        if (res && res.success) {
          this.loadCart().subscribe();
        }
      })
    );
  }

  public checkout(): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/carrito/finalizar`, {}).pipe(
      tap(res => {
        if (res && res.success) {
          this.cartData.set({ items: [], total: 0.0 });
          this.cartCount.set(0);
        }
      })
    );
  }
}