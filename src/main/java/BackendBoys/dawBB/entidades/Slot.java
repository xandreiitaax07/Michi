package BackendBoys.dawBB.entidades;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
//No se usan todos los parámetros solos los indicados
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//Se excluyen del toString la convocatoria y pruebas, ya que no han de representarse con los slots
@ToString(exclude = {"convocatoria", "pruebas"})
@Table(name = "slots")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fin;

    private Boolean eliminado = false;

    @ManyToOne
    @JoinColumn(name = "fk_convocatoria", nullable = false)
    private Convocatoria convocatoria;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prueba> pruebas = new ArrayList<>();

    /*//Estos métodos se usan para que a la hora de guardar cosas en la bbdd también las guardemos en nuestra clase y esta esté actualizada
    public void addPrueba(Prueba prueba) {
        this.pruebas.add(prueba);
        prueba.setSlot(this);
    }

    public void removePrueba(Prueba prueba) {
        pruebas.remove(prueba);
        prueba.setSlot(null);
    }*/
}
