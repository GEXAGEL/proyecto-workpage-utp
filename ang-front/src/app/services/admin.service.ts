import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  constructor(private http: HttpClient) {}

  // Productos
  public getProductos(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/admin/productos`);
  }

  public agregarProducto(producto: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/admin/productos/agregar`, producto);
  }

  public actualizarProducto(id: number, producto: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/admin/productos/actualizar/${id}`, producto);
  }

  public eliminarProducto(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/admin/productos/eliminar/${id}`, {});
  }

  public actualizarStock(id: number, stock: number): Observable<any> {
    const params = { stock: stock.toString() };
    return this.http.post<any>(`${environment.apiUrl}/admin/productos/actualizar-stock/${id}`, null, { params });
  }

  // Usuarios
  public getUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/admin/usuarios`);
  }

  public cambiarRol(id: number, nuevoRol: string): Observable<any> {
    const params = { nuevoRol };
    return this.http.post<any>(`${environment.apiUrl}/admin/usuarios/cambiar-rol/${id}`, null, { params });
  }

  public eliminarUsuario(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/admin/usuarios/eliminar/${id}`, {});
  }

  // Facturas
  public getFacturas(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/admin/facturas`);
  }

  public getDetalleFactura(id: number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/admin/facturas/${id}`);
  }

  public eliminarFactura(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/admin/facturas/eliminar/${id}`, {});
  }
}