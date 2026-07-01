import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public currentUser = signal<any>(null);
  public isAuthenticated = signal<boolean>(false);

  constructor(private http: HttpClient) {}

  public checkStatus(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/login/status`).pipe(
      tap(res => {
        if (res && res.autenticado) {
          this.currentUser.set(res.usuario);
          this.isAuthenticated.set(true);
        } else {
          this.currentUser.set(null);
          this.isAuthenticated.set(false);
        }
      })
    );
  }

  public login(credentials: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/login/iniciar`, credentials).pipe(
      tap(res => {
        if (res && res.success) {
          // GUARDAMOS EL TOKEN EN EL NAVEGADOR
          localStorage.setItem('token', res.token); 
          this.currentUser.set(res.usuario);
          this.isAuthenticated.set(true);
        }
      })
    );
  }

  public register(user: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/login/registrar`, user);
  }

  public logout(): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/login/cerrar`, {}).pipe(
      tap(() => {
        // ELIMINAMOS EL TOKEN
        localStorage.removeItem('token');
        this.currentUser.set(null);
        this.isAuthenticated.set(false);
      })
    );
  }

  public updateProfile(user: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/perfil/actualizar`, user).pipe(
      tap(res => {
        if (res && res.success) {
          this.currentUser.set(res.usuario);
        }
      })
    );
  }

  public deleteAccount(): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/perfil/eliminar`, {}).pipe(
      tap(res => {
        if (res && res.success) {
          this.currentUser.set(null);
          this.isAuthenticated.set(false);
        }
      })
    );
  }
}