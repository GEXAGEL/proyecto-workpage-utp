package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.workpagespringutp.model.Producto;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.ProductoService;
import utp.workpagespringutp.service.UsuarioService;
import utp.workpagespringutp.service.FacturaService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FacturaService facturaService;

    // Dashboard principal
    @GetMapping({"/dashboard", ""})
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        return "admin/dashboard";
    }

    // ============ GESTIÓN DE PRODUCTOS ============

    @GetMapping("/productos")
    public String listarProductos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        return "admin/productos";
    }

    @PostMapping("/productos/agregar")
    public String agregarProducto(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        try {
            productoService.guardarProducto(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto agregado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al agregar producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        try {
            producto.setId(id);
            productoService.actualizarProducto(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/actualizar-stock/{id}")
    public String actualizarStock(@PathVariable Long id, @RequestParam Integer stock, RedirectAttributes redirectAttributes) {
        try {
            productoService.actualizarStock(id, stock);
            redirectAttributes.addFlashAttribute("mensajeExito", "Stock actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar stock: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    // ============ GESTIÓN DE USUARIOS ============

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id, @RequestParam String nuevoRol, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarRol(id, nuevoRol);
            redirectAttributes.addFlashAttribute("mensajeExito", "Rol actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar rol: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarCuenta(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // ============ GESTIÓN DE FACTURAS ============

    @GetMapping("/facturas")
    public String listarFacturas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("facturas", facturaService.obtenerTodasLasFacturas());
        return "admin/facturas";
    }

    @GetMapping("/facturas/{id}")
    public String verDetalleFactura(@PathVariable Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("factura", facturaService.obtenerFacturaPorId(id).orElse(null));
        return "admin/detalle-factura";
    }

    @PostMapping("/facturas/eliminar/{id}")
    public String eliminarFactura(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facturaService.eliminarFactura(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Factura eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar factura: " + e.getMessage());
        }
        return "redirect:/admin/facturas";
    }
}