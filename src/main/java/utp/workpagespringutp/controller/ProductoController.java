package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.Producto;
import utp.workpagespringutp.service.ProductoService;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String listarProductos(
            @RequestParam(required = false) String categoria,
            HttpSession session,
            Model model) {

        List<Producto> productos;

        if (categoria == null || categoria.equals("Todos")) {
            productos = productoService.obtenerTodosLosProductos();
        } else {
            productos = productoService.obtenerProductosPorCategoria(categoria);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categoriaSeleccionada", categoria != null ? categoria : "Todos");
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));

        return "html/productos";
    }
}