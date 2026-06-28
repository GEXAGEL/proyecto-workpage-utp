package utp.workpagespringutp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.Producto;
import utp.workpagespringutp.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos(@RequestParam(required = false) String categoria) {
        if (categoria == null || categoria.equals("Todos")) {
            return productoService.obtenerTodosLosProductos();
        } else {
            return productoService.obtenerProductosPorCategoria(categoria);
        }
    }
}