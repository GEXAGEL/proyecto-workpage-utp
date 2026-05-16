package utp.workpagespringutp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utp.workpagespringutp.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto,Long> {
    List<Producto> findByCategoria(String categoria);
}
