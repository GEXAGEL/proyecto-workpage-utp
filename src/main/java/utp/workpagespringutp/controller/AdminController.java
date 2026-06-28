package utp.workpagespringutp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.Producto;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.ProductoService;
import utp.workpagespringutp.service.UsuarioService;
import utp.workpagespringutp.service.FacturaService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FacturaService facturaService;

    // ============ GESTIÓN DE PRODUCTOS ============

    @GetMapping("/productos")
    public ResponseEntity<?> listarProductos() {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos());
    }

    @PostMapping("/productos/agregar")
    public ResponseEntity<?> agregarProducto(@RequestBody Producto producto) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.guardarProducto(producto);
            response.put("success", true);
            response.put("message", "Producto agregado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar producto: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/productos/actualizar/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Map<String, Object> response = new HashMap<>();
        try {
            producto.setId(id);
            productoService.actualizarProducto(producto);
            response.put("success", true);
            response.put("message", "Producto actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar producto: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/productos/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.eliminarProducto(id);
            response.put("success", true);
            response.put("message", "Producto eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar producto: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/productos/actualizar-stock/{id}")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestParam Integer stock) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.actualizarStock(id, stock);
            response.put("success", true);
            response.put("message", "Stock actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar stock: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ============ GESTIÓN DE USUARIOS ============

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    @PostMapping("/usuarios/cambiar-rol/{id}")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        Map<String, Object> response = new HashMap<>();
        try {
            usuarioService.cambiarRol(id, nuevoRol);
            response.put("success", true);
            response.put("message", "Rol actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar rol: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            usuarioService.eliminarCuenta(id);
            response.put("success", true);
            response.put("message", "Usuario eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar usuario: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ============ GESTIÓN DE FACTURAS ============

    @GetMapping("/facturas")
    public ResponseEntity<?> listarFacturas() {
        return ResponseEntity.ok(facturaService.obtenerTodasLasFacturas());
    }

    @GetMapping("/facturas/{id}")
    public ResponseEntity<?> verDetalleFactura(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerFacturaPorId(id).orElse(null));
    }

    @PostMapping("/facturas/eliminar/{id}")
    public ResponseEntity<?> eliminarFactura(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            facturaService.eliminarFactura(id);
            response.put("success", true);
            response.put("message", "Factura eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar factura: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}