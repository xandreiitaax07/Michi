package BackendBoys.dawBB.entidades;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
//No se usan todos los parámetros solos los indicados
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//Se excluyen del toString la materia y slot, ya que no han de representarse con las pruebas
@ToString(exclude = {"materia", "slot"})
@Table(name = "pruebas")
public class Prueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_slot", nullable = false)
    private Slot slot;

    @ManyToOne
    @JoinColumn(name = "fk_materia", nullable = false)
    private Materia materia;

    @Column(nullable = false)
    private Boolean eliminada = false;
}
