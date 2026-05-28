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
//Se excluyen del toString las materias y slots, ya que no han de representarse con la convocatoria
@ToString(exclude = {"materias", "slots"})
@Table(name = "convocatorias")
public class Convocatoria {

    @Id
    //Autoincremento para el ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Asi incluimos que parámetro se usara para el Equals
    @EqualsAndHashCode.Include
    private Long idConvocatoria;

    @Column(nullable = false, length = 100)
    private String nombre;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private Boolean actual;

    @ManyToMany
    //Con esto creamos la tabla intermedia que se usa de apoyo para las relaciones ManyToMany
    @JoinTable(name = "convocatoria_materia", joinColumns = @JoinColumn(name = "id_convocatoria"),
            inverseJoinColumns = @JoinColumn(name = "id_materia"))
    private List<Materia> materias = new ArrayList<>();

    //Con CascadeType.ALL conseguimos que cualquier operación con convocatoria creer, actualice o borre secuencialmente otros slots
    //Con orphanRemoval = true si borramos un slot de una convocatoria se borra el slot de la tabla slot
    @OneToMany(mappedBy = "convocatoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Slot> slots = new ArrayList<>();
}
