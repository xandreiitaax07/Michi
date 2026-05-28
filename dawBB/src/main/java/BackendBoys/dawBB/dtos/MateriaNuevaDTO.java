package BackendBoys.dawBB.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@Schema(name = "MateriaNueva")
public class MateriaNuevaDTO {
    private String nombre;
}
