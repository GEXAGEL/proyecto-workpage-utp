package utp.workpagespringutp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @OneToMany(
            mappedBy = "carrito",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ItemCarrito> itemCarritos = new ArrayList<>();

    public void addItemCarrito(ItemCarrito itemCarrito){
        itemCarritos.add(itemCarrito);
        itemCarrito.setCarrito(this);
    }

    public void removeItemCarrito(ItemCarrito itemCarrito){
        itemCarritos.remove(itemCarrito);
        itemCarrito.setCarrito(null);
    }

    public void vaciarCarrito(){
        itemCarritos.clear();
    }
}