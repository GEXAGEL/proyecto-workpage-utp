import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    return this.http.get<any[]>('/api/productos', { params });
  }
}
