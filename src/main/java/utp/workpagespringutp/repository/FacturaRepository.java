package utp.workpagespringutp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utp.workpagespringutp.model.Factura;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByUsuarioId(Long usuarioId);
}