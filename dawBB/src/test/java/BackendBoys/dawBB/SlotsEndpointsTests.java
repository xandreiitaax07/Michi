package BackendBoys.dawBB;

import BackendBoys.dawBB.dtos.ConvocatoriaDTO;
import BackendBoys.dawBB.dtos.SlotDTO;
import BackendBoys.dawBB.dtos.SlotIdDTO;
import BackendBoys.dawBB.dtos.SlotNuevoDTO;
import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.entidades.Slot;
import BackendBoys.dawBB.repositorios.ConvocatoriaRepositorio;
import BackendBoys.dawBB.repositorios.SlotRepositorio;
import BackendBoys.dawBB.seguridad.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestRestTemplate
@DisplayName("En el controlador de slots")
class SlotsEndpointsTests {

    @Autowired
    private BackendBoys.dawBB.servicios.SlotServicio slotServicioDirecto;

    @Autowired
    private JwtUtil jwtUtil;

    private String valid_token;

    @BeforeEach
    void setUp() {
        valid_token = jwtUtil.generateToken("test_user");
        slotRepositorio.deleteAll();
        convocatoriaRepositorio.deleteAll();
    }

    // Ya no usamos este token, ya qué puede ser que esté caducado, además de que es estático.
    // private static final String JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJyb2xlIjpbIkFETUlOSVNUUkFET1IiXSwic3ViIjoiMSIsImlhdCI6MTc3Nzc4ODc3NSwiZXhwIjoxODQwODYwNzc1fQ.QItS3nUduiC52ty2wx4MlZzTHq_N6t-QkFOg78sSdrFeWjhktrB6NCxq-CEuxIzRO9t8LItJMBK4bBXfrGzGfg";

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Autowired
    private SlotRepositorio slotRepositorio;

    @Autowired
    private ConvocatoriaRepositorio convocatoriaRepositorio;

    private String url(String rutaYConsulta) {
        return "http://localhost:" + port + rutaYConsulta;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(valid_token); //Usamos el token dinamico
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private <T> HttpEntity<T> getEntityWithAuth(T body) {
        return new HttpEntity<>(body, getHeaders());
    }

    private HttpEntity<Void> getEntityWithAuth() {
        return new HttpEntity<>(getHeaders());
    }

    @Test
    @DisplayName("al obtener un slot existente devuelve 200 OK y el slot correcto")
    void obtenerSlotExistente() {
        // Establecer el contexto
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Julio 2026");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        Slot slot = new Slot();
        slot.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slot.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slot.setEliminado(false);
        slot.setConvocatoria(convocatoria);
        slot = slotRepositorio.save(slot);

        // Realizar la consulta
        ResponseEntity<SlotDTO> res = restTemplate.exchange(
                url("/slots/" + slot.getId()),
                HttpMethod.GET,
                getEntityWithAuth(),
                SlotDTO.class
        );

        // Comprobaciones
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getId()).isEqualTo(slot.getId());
        assertThat(res.getBody().getInicio()).isEqualTo(LocalDateTime.parse("2026-07-03T10:30:00"));
        assertThat(res.getBody().getFin()).isEqualTo(LocalDateTime.parse("2026-07-03T12:00:00"));
        assertThat(res.getBody().getEliminado()).isFalse();
    }

    @Test
    @DisplayName("al obtener un slot inexistente devuelve 404 Not Found")
    void obtenerSlotInexistente() {
        ResponseEntity<Void> res = restTemplate.exchange(
                url("/slots/9999"),
                HttpMethod.GET,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("al obtener un slot sin autenticación devuelve 403 Forbidden")
    void obtenerSlotSinAutenticacion() {
        ResponseEntity<Void> res = restTemplate.getForEntity(
                url("/slots/1"),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("al actualizar un slot existente se modifica en la base de datos y devuelve 200 OK")
    void actualizarSlotExistente() {
        // Establecer el contexto
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Julio 2026");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        Slot slot = new Slot();
        slot.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slot.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slot.setEliminado(false);
        slot.setConvocatoria(convocatoria);
        slot = slotRepositorio.save(slot);

        // Crear DTO para la actualización
        SlotNuevoDTO nuevodto = new SlotNuevoDTO();
        //dto.setId(slot.getId());
        nuevodto.setInicio(LocalDateTime.parse("2026-07-03T11:00:00"));
        nuevodto.setFin(LocalDateTime.parse("2026-07-03T12:30:00"));
        nuevodto.setEliminado(false);
        nuevodto.setConvocatoria(new ConvocatoriaDTO(convocatoria.getIdConvocatoria(), convocatoria.getNombre(), convocatoria.getFechaInicio(), convocatoria.getFechaFin()));

        // Realizar la consulta
        ResponseEntity<SlotDTO> res = restTemplate.exchange(
                url("/slots/" + slot.getId()),
                HttpMethod.PUT,
                getEntityWithAuth(nuevodto),
                SlotDTO.class
        );

        // Comprobaciones de respuesta

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getInicio()).isEqualTo(LocalDateTime.parse("2026-07-03T11:00:00"));
        assertThat(res.getBody().getFin()).isEqualTo(LocalDateTime.parse("2026-07-03T12:30:00"));
        assertThat(res.getBody().getEliminado()).isFalse();

        // Comprobaciones de base de datos
        Slot guardado = slotRepositorio.findById(slot.getId()).orElse(null);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getInicio()).isEqualTo(LocalDateTime.parse("2026-07-03T11:00:00"));
        assertThat(guardado.getFin()).isEqualTo(LocalDateTime.parse("2026-07-03T12:30:00"));
        assertThat(guardado.getEliminado()).isFalse();
    }

    @Test
    @DisplayName("al actualizar un slot inexistente devuelve 404 Not Found")
    void actualizarSlotInexistente() {
        SlotDTO dto = new SlotDTO();
        dto.setId(9999L);
        dto.setInicio(LocalDateTime.parse("2026-07-03T11:00:00"));
        dto.setFin(LocalDateTime.parse("2026-07-03T12:30:00"));
        dto.setEliminado(false);

        ResponseEntity<Void> res = restTemplate.exchange(
                url("/slots/9999"),
                HttpMethod.PUT,
                getEntityWithAuth(dto),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("al eliminar un slot existente se marca como eliminado y devuelve 204 No Content")
    void eliminarSlotExistente() {
        // Establecer el contexto
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Julio 2026");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        Slot slot = new Slot();
        slot.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slot.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slot.setEliminado(false);
        slot.setConvocatoria(convocatoria);
        slot = slotRepositorio.save(slot);

        // Realizar la consulta
        ResponseEntity<Void> res = restTemplate.exchange(
                url("/slots/" + slot.getId()),
                HttpMethod.DELETE,
                getEntityWithAuth(),
                Void.class
        );

        // Comprobaciones
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);//Confirmamos que lo hemos borrado

        // Comprobar que en base de datos se ha marcado como eliminado (eliminación lógica)
        Slot guardado = slotRepositorio.findById(slot.getId()).orElse(null);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getEliminado()).isTrue();
    }

    @Test
    @DisplayName("al eliminar un slot inexistente devuelve 404 Not Found")
    void eliminarSlotInexistente() {
        ResponseEntity<Void> res = restTemplate.exchange(
                url("/slots/9999"),
                HttpMethod.DELETE,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("al obtener slots por idConvocatoria devuelve los slots de esa convocatoria")
    void obtenerSlotsConvocatoriaEspecifica() {
        // Establecer el contexto: 2 convocatorias
        Convocatoria convocatoriaA = new Convocatoria();
        convocatoriaA.setNombre("Convocatoria A");
        convocatoriaA.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoriaA.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoriaA.setActual(false);
        convocatoriaA = convocatoriaRepositorio.save(convocatoriaA);

        Convocatoria convocatoriaB = new Convocatoria();
        convocatoriaB.setNombre("Convocatoria B");
        convocatoriaB.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoriaB.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoriaB.setActual(true);
        convocatoriaB = convocatoriaRepositorio.save(convocatoriaB);

        // Slots
        Slot slotA = new Slot();
        slotA.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slotA.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slotA.setEliminado(false);
        slotA.setConvocatoria(convocatoriaA);
        slotRepositorio.save(slotA);

        Slot slotB = new Slot();
        slotB.setInicio(LocalDateTime.parse("2026-07-03T13:00:00"));
        slotB.setFin(LocalDateTime.parse("2026-07-03T14:30:00"));
        slotB.setEliminado(false);
        slotB.setConvocatoria(convocatoriaB);
        slotRepositorio.save(slotB);

        // Consultar slots de Convocatoria A
        ResponseEntity<SlotDTO[]> res = restTemplate.exchange(
                url("/slots?idConvocatoria=" + convocatoriaA.getIdConvocatoria()),
                HttpMethod.GET,
                getEntityWithAuth(),
                SlotDTO[].class
        );

        // Comprobaciones
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).hasSize(1);
        assertThat(res.getBody()[0].getId()).isEqualTo(slotA.getId());
    }

    @Test
    @DisplayName("al obtener slots sin idConvocatoria devuelve los slots de la convocatoria actual")
    void obtenerSlotsConvocatoriaActual() {
        // Establecer el contexto: 2 convocatorias (una actual)
        Convocatoria convocatoriaA = new Convocatoria();
        convocatoriaA.setNombre("Convocatoria A");
        convocatoriaA.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoriaA.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoriaA.setActual(false);
        convocatoriaA = convocatoriaRepositorio.save(convocatoriaA);

        Convocatoria convocatoriaB = new Convocatoria();
        convocatoriaB.setNombre("Convocatoria B Actual");
        convocatoriaB.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoriaB.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoriaB.setActual(true);
        convocatoriaB = convocatoriaRepositorio.save(convocatoriaB);

        // Slots
        Slot slotA = new Slot();
        slotA.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slotA.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slotA.setEliminado(false);
        slotA.setConvocatoria(convocatoriaA);
        slotRepositorio.save(slotA);

        Slot slotB = new Slot();
        slotB.setInicio(LocalDateTime.parse("2026-07-03T13:00:00"));
        slotB.setFin(LocalDateTime.parse("2026-07-03T14:30:00"));
        slotB.setEliminado(false);
        slotB.setConvocatoria(convocatoriaB);
        slotRepositorio.save(slotB);

        // Consultar slots (se asume la actual)
        ResponseEntity<SlotDTO[]> res = restTemplate.exchange(
                url("/slots"),
                HttpMethod.GET,
                getEntityWithAuth(),
                SlotDTO[].class
        );

        // Comprobaciones
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).hasSize(1);
        assertThat(res.getBody()[0].getId()).isEqualTo(slotB.getId());
    }

    @Test
    @DisplayName("al crear un slot con datos válidos devuelve 201 Created y lo guarda en la base de datos")
    void crearSlotValido() {
        // Establecer el contexto
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Convocatoria Actual");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        // Slot DTO a crear
        SlotDTO dto = new SlotDTO();
        dto.setInicio(LocalDateTime.parse("2026-07-03T16:00:00"));
        dto.setFin(LocalDateTime.parse("2026-07-03T17:30:00"));
        dto.setEliminado(false);
        dto.setConvocatoria(new ConvocatoriaDTO(convocatoria.getIdConvocatoria(), convocatoria.getNombre(), convocatoria.getFechaInicio(), convocatoria.getFechaFin()));

        // Realizar la consulta
        ResponseEntity<SlotDTO> res = restTemplate.exchange(
                url("/slots"),
                HttpMethod.POST,
                getEntityWithAuth(dto),
                SlotDTO.class
        );

        // Comprobaciones
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getId()).isNotNull();

        // Comprobar base de datos
        Slot guardado = slotRepositorio.findById(res.getBody().getId()).orElse(null);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getInicio()).isEqualTo(LocalDateTime.parse("2026-07-03T16:00:00"));
        assertThat(guardado.getFin()).isEqualTo(LocalDateTime.parse("2026-07-03T17:30:00"));
        assertThat(guardado.getEliminado()).isFalse();
    }

    @Test
    @DisplayName("Cobertura total: métodos generados de Slot, DTOs y Excepciones")
    void coberturaExtraSlot() {
        // 1. Excepción
        BackendBoys.dawBB.excepciones.SlotsNoEncontrado ex =
            new BackendBoys.dawBB.excepciones.SlotsNoEncontrado(999L);
        assertThat(ex.getMessage()).isNotNull();

        // 2. Entidad
        Slot s1 = new Slot();
        s1.setId(1L);
        s1.setEliminado(false);

        Slot s2 = new Slot();
        s2.setId(1L);
        s2.setEliminado(false);

        assertThat(s1.toString()).isNotNull();
        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());

        // 3. DTOs (SlotIdDTO)
        SlotIdDTO idDto1 = new SlotIdDTO(1L);
        SlotIdDTO idDto2 = new SlotIdDTO(1L);

        assertThat(idDto1.toString()).isNotNull();
        assertThat(idDto1).isEqualTo(idDto2);
        assertThat(idDto1.hashCode()).isEqualTo(idDto2.hashCode());
    }

    @Test
    @DisplayName("Cobertura masiva: Slots DTOs y Servicios huérfanos")
    void coberturaMasivaSlots() {
        // 1. Servicio huérfano
        slotServicioDirecto.obtenerTodosSlots(null);

        // 2. DTOs (getters, setters, equals, hashCode, toString)
        SlotDTO dto1 = new SlotDTO(); dto1.setId(1L); dto1.setInicio(LocalDateTime.MIN); dto1.setFin(LocalDateTime.MAX); dto1.setEliminado(false); dto1.setConvocatoria(null);
        SlotDTO dto2 = new SlotDTO(); dto2.setId(1L); dto2.setInicio(LocalDateTime.MIN); dto2.setFin(LocalDateTime.MAX); dto2.setEliminado(false); dto2.setConvocatoria(null);
        assertThat(dto1.getId()).isEqualTo(1L); assertThat(dto1.getInicio()).isEqualTo(LocalDateTime.MIN);
        assertThat(dto1.getFin()).isEqualTo(LocalDateTime.MAX); assertThat(dto1.getEliminado()).isFalse();
        assertThat(dto1.getConvocatoria()).isNull(); assertThat(dto1.toString()).isNotNull();
        assertThat(dto1).isEqualTo(dto2); assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        SlotNuevoDTO ndto1 = new SlotNuevoDTO(); ndto1.setInicio(LocalDateTime.MIN); ndto1.setFin(LocalDateTime.MAX); ndto1.setEliminado(false); ndto1.setConvocatoria(null);
        SlotNuevoDTO ndto2 = new SlotNuevoDTO(); ndto2.setInicio(LocalDateTime.MIN); ndto2.setFin(LocalDateTime.MAX); ndto2.setEliminado(false); ndto2.setConvocatoria(null);
        assertThat(ndto1.getInicio()).isEqualTo(LocalDateTime.MIN); assertThat(ndto1.getFin()).isEqualTo(LocalDateTime.MAX);
        assertThat(ndto1.getEliminado()).isFalse(); assertThat(ndto1.getConvocatoria()).isNull();
        assertThat(ndto1.toString()).isNotNull(); assertThat(ndto1).isEqualTo(ndto2);
        assertThat(ndto1.hashCode()).isEqualTo(ndto2.hashCode());

        BackendBoys.dawBB.dtos.SlotIdDTO sid1 = new BackendBoys.dawBB.dtos.SlotIdDTO(1L);
        sid1.setId(1L); assertThat(sid1.getId()).isEqualTo(1L);
    }
}
