package utp.workpagespringutp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.workpagespringutp.model.*;
import utp.workpagespringutp.repository.*;
import utp.workpagespringutp.repository.ItemCarritoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProductoService productoService;

    @Transactional
    public Carrito obtenerCarritoUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Carrito> carritoOpt = carritoRepository.findByUsuarioId(usuarioId);

        if (carritoOpt.isPresent()) {
            Carrito carrito = carritoOpt.get();
            // Forzar la carga de los items
            carrito.getItemCarritos().size();
            return carrito;
        } else {
            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setUsuario(usuario);
            return carritoRepository.save(nuevoCarrito);
        }
    }

    @Transactional
    public void agregarProductoAlCarrito(Long usuarioId, Long productoId, Integer cantidad) {
        Carrito carrito = obtenerCarritoUsuario(usuarioId);
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar primero si ya existe el producto en el carrito
        Optional<utp.workpagespringutp.model.ItemCarrito> itemExistente = carrito.getItemCarritos()
                .stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        int cantidadTotal = cantidad;
        if (itemExistente.isPresent()) {
            cantidadTotal += itemExistente.get().getCantidad();
        }

        // Verificar stock con la cantidad total (sin reducir el stock real todavía)
        if (!productoService.verificarStock(productoId, cantidadTotal)) {
            throw new RuntimeException("No hay suficiente stock disponible. Stock actual: " + producto.getStock());
        }

        if (itemExistente.isPresent()) {
            utp.workpagespringutp.model.ItemCarrito item = itemExistente.get();
            item.setCantidad(cantidadTotal);
            itemCarritoRepository.save(item);
        } else {
            utp.workpagespringutp.model.ItemCarrito nuevoItem = new utp.workpagespringutp.model.ItemCarrito();
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setCarrito(carrito);
            carrito.getItemCarritos().add(nuevoItem);
            carritoRepository.save(carrito);
        }

        System.out.println("Producto agregado al carrito. Total items: " + carrito.getItemCarritos().size());
    }

    @Transactional
    public void eliminarItemDelCarrito(Long itemId) {
        itemCarritoRepository.deleteById(itemId);
    }

    @Transactional
    public void actualizarCantidadItem(Long itemId, Integer cantidad) {
        utp.workpagespringutp.model.ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!productoService.verificarStock(item.getProducto().getId(), cantidad)) {
            throw new RuntimeException("No hay suficiente stock disponible");
        }

        item.setCantidad(cantidad);
        itemCarritoRepository.save(item);
    }

    public double calcularTotalCarrito(Carrito carrito) {
        return carrito.getItemCarritos().stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();
    }

    public int obtenerCantidadItemsCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarritoUsuario(usuarioId);
        return carrito.getItemCarritos().stream()
                .mapToInt(utp.workpagespringutp.model.ItemCarrito::getCantidad)
                .sum();
    }

    @Transactional
    public void finalizarCompra(Long usuarioId) {
        Carrito carrito = obtenerCarritoUsuario(usuarioId);

        if (carrito.getItemCarritos().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Verificar stock antes de procesar
        for (utp.workpagespringutp.model.ItemCarrito item : carrito.getItemCarritos()) {
            if (!productoService.verificarStock(item.getProducto().getId(), item.getCantidad())) {
                throw new RuntimeException("No hay suficiente stock para: " + item.getProducto().getNombre());
            }
        }

        // Crear la factura
        Factura factura = new Factura();
        factura.setUsuario(carrito.getUsuario());
        factura.setFecha(LocalDate.now());
        factura.setDescripcion("Compra realizada");

        // Agregar detalles y reducir stock
        for (ItemCarrito item : carrito.getItemCarritos()) {
            DetalleFactura detalle = new DetalleFactura();
            detalle.setProductoNombre(item.getProducto().getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.valueOf(item.getProducto().getPrecio()));
            factura.addDetalleFactura(detalle);

            // AQUÍ es donde se reduce el stock real
            productoService.reducirStock(item.getProducto().getId(), item.getCantidad());
        }

        // Guardar factura y vaciar carrito
        facturaRepository.save(factura);
        carrito.vaciarCarrito();
        carritoRepository.save(carrito);
    }
}