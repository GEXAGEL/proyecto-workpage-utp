package utp.workpagespringutp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utp.workpagespringutp.model.Carrito;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.itemCarritos WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}