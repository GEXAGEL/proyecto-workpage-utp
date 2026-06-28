import { Routes } from '@angular/router';
import { ClientLayout } from './layouts/client/client-layout';
import { AdminLayout } from './layouts/admin/admin-layout';
import { Home } from './pages/home/home';
import { Productos } from './pages/productos/productos';
import { Contacto } from './pages/contacto/contacto';
import { Nosotros } from './pages/nosotros/nosotros';
import { Perfil } from './pages/perfil/perfil';
import { AdminDashboard } from './pages/admin/dashboard/dashboard';
import { AdminProductos } from './pages/admin/productos/productos';
import { AdminUsuarios } from './pages/admin/usuarios/usuarios';
import { AdminFacturas } from './pages/admin/facturas/facturas';

export const routes: Routes = [
  {
    path: '',
    component: ClientLayout,
    children: [
      { path: '', component: Home },
      { path: 'productos', component: Productos },
      { path: 'contacto', component: Contacto },
      { path: 'nosotros', component: Nosotros },
      { path: 'perfil', component: Perfil }
    ]
  },
  {
    path: 'admin',
    component: AdminLayout,
    children: [
      { path: 'dashboard', component: AdminDashboard },
      { path: 'productos', component: AdminProductos },
      { path: 'usuarios', component: AdminUsuarios },
      { path: 'facturas', component: AdminFacturas }
    ]
  }
];
