package utp.workpagespringutp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.workpagespringutp.model.Factura;
import utp.workpagespringutp.repository.FacturaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    public List<Factura> obtenerTodasLasFacturas() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> obtenerFacturaPorId(Long id) {
        return facturaRepository.findById(id);
    }

    @Transactional
    public void eliminarFactura(Long id) {
        facturaRepository.deleteById(id);
    }

    public List<Factura> obtenerFacturasPorUsuario(Long usuarioId) {
        return facturaRepository.findByUsuarioId(usuarioId);
    }
}