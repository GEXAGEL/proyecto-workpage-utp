import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private http: HttpClient) {}

  public getProducts(categoria?: string): Observable<any[]> {
    const params: any = {};
    if (categoria && categoria !== 'Todos') {
      params.categoria = categoria;
    }
    return this.http.get<any[]>(`${environment.apiUrl}/api/productos`, { params });
  }
}