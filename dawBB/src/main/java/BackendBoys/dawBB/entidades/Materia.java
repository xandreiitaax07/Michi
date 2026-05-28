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
//Se excluyen del toString las convocatorias y pruebas, ya que no han de representarse con la materia
@ToString(exclude = {"pruebas"})
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private Boolean eliminada;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prueba> pruebas = new ArrayList<>();

    /*//Estos métodos se usan para que a la hora de guardar cosas en la bbdd también las guardemos en nuestra clase y esta esté actualizada
    public void addPrueba(Prueba prueba) {
        this.pruebas.add(prueba);
        prueba.setMateria(this);
    }

    public void removePrueba(Prueba prueba) {
        pruebas.remove(prueba);
        prueba.setMateria(null);
    }*/
}
