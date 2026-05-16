package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.*;
import utp.workpagespringutp.service.CarritoService;
import utp.workpagespringutp.service.ProductoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    @PostMapping("/agregar")
    @ResponseBody
    public ResponseEntity<?> agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam(defaultValue = "1") Integer cantidad,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Map<String, Object> response = new HashMap<>();

        if (usuarioLogueado == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión para agregar productos al carrito");
            return ResponseEntity.ok(response);
        }

        if (!productoService.verificarStock(productoId, cantidad)) {
            response.put("success", false);
            response.put("message", "No hay suficiente stock disponible");
            return ResponseEntity.ok(response);
        }

        try {
            carritoService.agregarProductoAlCarrito(usuarioLogueado.getId(), productoId, cantidad);
            response.put("success", true);
            response.put("message", "Producto agregado al carrito");
            response.put("cantidadItems", carritoService.obtenerCantidadItemsCarrito(usuarioLogueado.getId()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar producto al carrito");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/eliminar/{itemId}")
    @ResponseBody
    public ResponseEntity<?> eliminarDelCarrito(
            @PathVariable Long itemId,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Map<String, Object> response = new HashMap<>();

        if (usuarioLogueado == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión");
            return ResponseEntity.ok(response);
        }

        try {
            carritoService.eliminarItemDelCarrito(itemId);
            response.put("success", true);
            response.put("message", "Producto eliminado del carrito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar producto");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/actualizar/{itemId}")
    @ResponseBody
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestParam Integer cantidad,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Map<String, Object> response = new HashMap<>();

        if (usuarioLogueado == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión");
            return ResponseEntity.ok(response);
        }

        try {
            carritoService.actualizarCantidadItem(itemId, cantidad);
            response.put("success", true);
            response.put("message", "Cantidad actualizada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/finalizar")
    @ResponseBody
    public ResponseEntity<?> finalizarCompra(HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Map<String, Object> response = new HashMap<>();

        if (usuarioLogueado == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión");
            return ResponseEntity.ok(response);
        }

        try {
            carritoService.finalizarCompra(usuarioLogueado.getId());
            response.put("success", true);
            response.put("message", "Compra realizada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al procesar la compra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/datos")
    @ResponseBody
    public ResponseEntity<?> obtenerDatosCarrito(HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("items", new ArrayList<>());
            response.put("total", 0.0);
            return ResponseEntity.ok(response);
        }

        try {
            Carrito carrito = carritoService.obtenerCarritoUsuario(usuarioLogueado.getId());

            // Crear una lista de items serializables
            List<Map<String, Object>> itemsSerializables = new ArrayList<>();
            for (ItemCarrito item : carrito.getItemCarritos()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("cantidad", item.getCantidad());

                Map<String, Object> productoMap = new HashMap<>();
                productoMap.put("id", item.getProducto().getId());
                productoMap.put("nombre", item.getProducto().getNombre());
                productoMap.put("precio", item.getProducto().getPrecio());
                productoMap.put("imagen", item.getProducto().getImagen());
                productoMap.put("stock", item.getProducto().getStock());

                itemMap.put("producto", productoMap);
                itemsSerializables.add(itemMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("items", itemsSerializables);
            response.put("total", carritoService.calcularTotalCarrito(carrito));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("items", new ArrayList<>());
            response.put("total", 0.0);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}