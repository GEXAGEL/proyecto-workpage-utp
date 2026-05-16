package utp.workpagespringutp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utp.workpagespringutp.model.Producto;
import utp.workpagespringutp.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public boolean verificarStock(Long productoId, Integer cantidad) {
        Optional<Producto> producto = productoRepository.findById(productoId);
        return producto.isPresent() && producto.get().getStock() >= cantidad;
    }

    public void reducirStock(Long productoId, Integer cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            int nuevoStock = producto.getStock() - cantidad;
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente");
            }
            producto.setStock(nuevoStock);
            productoRepository.save(producto);
            System.out.println("Stock actualizado: " + producto.getNombre() + " - Nuevo stock: " + nuevoStock);
        }
    }

    public void aumentarStock(Long productoId, Integer cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setStock(producto.getStock() + cantidad);
            productoRepository.save(producto);
        }
    }

    // Métodos para administrador

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void actualizarProducto(Producto producto) {
        productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public void actualizarStock(Long id, Integer nuevoStock) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setStock(nuevoStock);
            productoRepository.save(producto);
        }
    }
}