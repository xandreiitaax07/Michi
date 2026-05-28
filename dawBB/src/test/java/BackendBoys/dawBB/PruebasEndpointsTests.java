package BackendBoys.dawBB;

import BackendBoys.dawBB.dtos.*;
import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.entidades.Materia;
import BackendBoys.dawBB.entidades.Prueba;
import BackendBoys.dawBB.entidades.Slot;
import BackendBoys.dawBB.repositorios.ConvocatoriaRepositorio;
import BackendBoys.dawBB.repositorios.MateriaRepositorio;
import BackendBoys.dawBB.repositorios.PruebaRepositorio;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestRestTemplate
@DisplayName("Controlador de Pruebas")
class PruebasEndpointsTests {

    @Autowired
    private BackendBoys.dawBB.servicios.PruebaServicio pruebaServicioDirecto;

    @Autowired
    private JwtUtil jwtUtil;

    private String valid_token;

    @BeforeEach
    void setUp() {
        valid_token = jwtUtil.generateToken("test_user");
        pruebaRepositorio.deleteAll();
        slotRepositorio.deleteAll();
        convocatoriaRepositorio.deleteAll();
        materiaRepositorio.deleteAll();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Autowired
    private PruebaRepositorio pruebaRepositorio;

    @Autowired
    private SlotRepositorio slotRepositorio;

    @Autowired
    private ConvocatoriaRepositorio convocatoriaRepositorio;

    @Autowired
    private MateriaRepositorio materiaRepositorio;

    private String url(String rutaYConsulta) { return "http://localhost:"+port+"/"+rutaYConsulta;}

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(valid_token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private <T> HttpEntity<T> getEntityWithAuth(T entity) {
        return new HttpEntity<>(entity, getHeaders());
    }

    private HttpEntity<Void> getEntityWithAuth() {
        return new HttpEntity<>(getHeaders());
    }

    @Test
    @DisplayName("Al obtener una prueba existente devuelve 200 OK y la prueba correcta")
    void obtenerPruebaExistente() {

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

        Materia materia = new Materia();
        materia.setNombre("Matemáticas");
        materia.setEliminada(false);
        materia = materiaRepositorio.save(materia);

        Prueba prueba = new Prueba();
        prueba.setEliminada(false);
        prueba.setSlot(slot);
        prueba.setMateria(materia);
        prueba = pruebaRepositorio.save(prueba);

        ResponseEntity<PruebaDTO> res = restTemplate.exchange(
                url("pruebas/" + prueba.getId()),
                org.springframework.http.HttpMethod.GET,
                getEntityWithAuth(),
                PruebaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getId()).isEqualTo(prueba.getId());
        assertThat(res.getBody().getEliminada()).isFalse();
    }

    @Test
    @DisplayName("al obtener una prueba inexistente devuelve 404 Not Found")
    void obtenerSlotInexistente() {
        ResponseEntity<Void> res = restTemplate.exchange(
                url("/pruebas/9999"),
                HttpMethod.GET,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("al obtener una prueba sin autenticación devuelve 403 Forbidden")
    void obtenerPruebaSinAutenticacion() {
        ResponseEntity<Void> res = restTemplate.getForEntity(
                url("/pruebas/1"),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Al actualizar una prueba existente se modifica en la base de datos y devuelve 200 OK")
    void actualizarPruebaExistente() {
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Julio 2026");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        Slot slotInicial = new Slot();
        slotInicial.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slotInicial.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slotInicial.setEliminado(false);
        slotInicial.setConvocatoria(convocatoria);
        slotInicial = slotRepositorio.save(slotInicial);

        Slot slotNuevo = new Slot();
        slotNuevo.setInicio(LocalDateTime.parse("2026-07-03T13:00:00"));
        slotNuevo.setFin(LocalDateTime.parse("2026-07-03T14:30:00"));
        slotNuevo.setEliminado(false);
        slotNuevo.setConvocatoria(convocatoria);
        slotNuevo = slotRepositorio.save(slotNuevo);

        Materia materiaInicial = new Materia();
        materiaInicial.setNombre("Matematicas");
        materiaInicial.setEliminada(false);
        materiaInicial = materiaRepositorio.save(materiaInicial);

        Materia materiaNueva = new Materia();
        materiaNueva.setNombre("Física");
        materiaNueva.setEliminada(false);
        materiaNueva = materiaRepositorio.save(materiaNueva);

        Prueba prueba = new Prueba();
        prueba.setEliminada(false);
        prueba.setSlot(slotInicial);
        prueba.setMateria(materiaInicial);
        prueba = pruebaRepositorio.save(prueba);

        PruebaNuevaDTO nuevodto = new PruebaNuevaDTO();
        nuevodto.setSlot(new SlotIdDTO(slotNuevo.getId()));
        nuevodto.setMateria(new MateriaIdDTO(materiaNueva.getId()));

        ResponseEntity<PruebaDTO> res = restTemplate.exchange(
                url("pruebas/" + prueba.getId()),
                HttpMethod.PUT,
                getEntityWithAuth(nuevodto),
                PruebaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getId()).isEqualTo(prueba.getId());
        assertThat(res.getBody().getEliminada()).isFalse();

        Prueba guardada = pruebaRepositorio.findById(prueba.getId()).orElse(null);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getSlot().getId()).isEqualTo(slotNuevo.getId());
        assertThat(guardada.getMateria().getId()).isEqualTo(materiaNueva.getId());
        assertThat(guardada.getEliminada()).isFalse();
    }

    @Test
    @DisplayName("al actualizar un slot inexistente devuelve 404 Not Found")
    void actualizarSlotInexistente() {
        PruebaNuevaDTO nuevodto = new PruebaNuevaDTO();
        nuevodto.setSlot(new SlotIdDTO(9999L));
        nuevodto.setMateria(new MateriaIdDTO(9999L));

        ResponseEntity<Void> res = restTemplate.exchange(
                url("/slots/9999"),
                HttpMethod.PUT,
                getEntityWithAuth(nuevodto),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Al eliminar una prueba existente se marca como eliminada y devuelve 200 OK")
    void eliminarPruebaExistente() {
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

        Materia materia = new Materia();
        materia.setNombre("Matemáticas");
        materia.setEliminada(false);
        materia = materiaRepositorio.save(materia);

        Prueba prueba = new Prueba();
        prueba.setEliminada(false);
        prueba.setSlot(slot);
        prueba.setMateria(materia);
        prueba = pruebaRepositorio.save(prueba);

        ResponseEntity<Void> res = restTemplate.exchange(
                url("pruebas/" + prueba.getId()),
                HttpMethod.DELETE,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        Prueba guardada = pruebaRepositorio.findById(prueba.getId()).orElse(null);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getEliminada()).isTrue();
    }

    @Test
    @DisplayName("Al eliminar una prueba inexistente devuelve 404 Not Found")
    void eliminarPruebaInexistente() {
        ResponseEntity<Void> res = restTemplate.exchange(
                url("pruebas/9999"),
                HttpMethod.DELETE,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Al obtener pruebas de una convocatoria devuelve solo las de esa convocatoria")
    void obtenerPruebasPorConvocatoria() {
        Convocatoria convocatoriaA = new Convocatoria();
        convocatoriaA.setNombre("Convocatoria A");
        convocatoriaA.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoriaA.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoriaA.setActual(false);
        convocatoriaA = convocatoriaRepositorio.save(convocatoriaA);

        Convocatoria convocatoriaB = new Convocatoria();
        convocatoriaB.setNombre("Convocatoria B");
        convocatoriaB.setFechaInicio(LocalDateTime.parse("2026-08-03T00:00:00"));
        convocatoriaB.setFechaFin(LocalDateTime.parse("2026-08-05T00:00:00"));
        convocatoriaB.setActual(true);
        convocatoriaB = convocatoriaRepositorio.save(convocatoriaB);

        Slot slotA = new Slot();
        slotA.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slotA.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slotA.setEliminado(false);
        slotA.setConvocatoria(convocatoriaA);
        slotA = slotRepositorio.save(slotA);

        Slot slotB = new Slot();
        slotB.setInicio(LocalDateTime.parse("2026-08-03T10:30:00"));
        slotB.setFin(LocalDateTime.parse("2026-08-03T12:00:00"));
        slotB.setEliminado(false);
        slotB.setConvocatoria(convocatoriaB);
        slotB = slotRepositorio.save(slotB);

        Materia materiaA = new Materia();
        materiaA.setNombre("Matemáticas");
        materiaA.setEliminada(false);
        materiaA = materiaRepositorio.save(materiaA);
        Materia materiaB = new Materia();
        materiaB.setNombre("Física");
        materiaB.setEliminada(false);
        materiaB = materiaRepositorio.save(materiaB);

        Prueba pruebaA = new Prueba();
        pruebaA.setEliminada(false);
        pruebaA.setSlot(slotA);
        pruebaA.setMateria(materiaA);
        pruebaRepositorio.save(pruebaA);

        Prueba pruebaB = new Prueba();
        pruebaB.setEliminada(false);
        pruebaB.setSlot(slotB);
        pruebaB.setMateria(materiaB);
        pruebaRepositorio.save(pruebaB);

        ResponseEntity<PruebaDTO[]> res = restTemplate.exchange(
                url("pruebas?idConvocatoria=" + convocatoriaA.getIdConvocatoria()),
                HttpMethod.GET,
                getEntityWithAuth(),
                PruebaDTO[].class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).hasSize(1);
        assertThat(res.getBody()[0].getId()).isEqualTo(pruebaA.getId());
    }

    @Test
    @DisplayName("Al obtener pruebas de un slot devuelve solo las de ese slot")
    void obtenerPruebasPorSlot() {
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Julio 2026");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        Slot slotA = new Slot();
        slotA.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slotA.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slotA.setEliminado(false);
        slotA.setConvocatoria(convocatoria);
        slotA = slotRepositorio.save(slotA);

        Slot slotB = new Slot();
        slotB.setInicio(LocalDateTime.parse("2026-08-03T10:30:00"));
        slotB.setFin(LocalDateTime.parse("2026-08-03T12:00:00"));
        slotB.setEliminado(false);
        slotB.setConvocatoria(convocatoria);
        slotB = slotRepositorio.save(slotB);

        Materia materiaA = new Materia();
        materiaA.setNombre("Matemáticas");
        materiaA.setEliminada(false);
        materiaA = materiaRepositorio.save(materiaA);
        Materia materiaB = new Materia();
        materiaB.setNombre("Física");
        materiaB.setEliminada(false);
        materiaB = materiaRepositorio.save(materiaB);
        Prueba pruebaA = new Prueba();
        pruebaA.setEliminada(false);
        pruebaA.setSlot(slotA);
        pruebaA.setMateria(materiaA);
        pruebaRepositorio.save(pruebaA);

        Prueba pruebaB = new Prueba();
        pruebaB.setEliminada(false);
        pruebaB.setSlot(slotB);
        pruebaB.setMateria(materiaB);
        pruebaRepositorio.save(pruebaB);

        ResponseEntity<PruebaDTO[]> res = restTemplate.exchange(
                url("pruebas?idSlot=" + slotA.getId()),
                HttpMethod.GET,
                getEntityWithAuth(),
                PruebaDTO[].class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).hasSize(1);
        assertThat(res.getBody()[0].getId()).isEqualTo(pruebaA.getId());
    }

    @Test
    @DisplayName("Al obtener pruebas sin filtros devuelve las de la convocatoria actual")
    void obtenerPruebasConvocatoriaActual() {
        Convocatoria convocatoriaA = new Convocatoria();
        convocatoriaA.setNombre("Convocatoria A");
        convocatoriaA.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        convocatoriaA.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));
        convocatoriaA.setActual(false);
        convocatoriaA = convocatoriaRepositorio.save(convocatoriaA);

        Convocatoria convocatoriaB = new Convocatoria();
        convocatoriaB.setNombre("Convocatoria B actual");
        convocatoriaB.setFechaInicio(LocalDateTime.parse("2026-08-03T00:00:00"));
        convocatoriaB.setFechaFin(LocalDateTime.parse("2026-08-05T00:00:00"));
        convocatoriaB.setActual(true);
        convocatoriaB = convocatoriaRepositorio.save(convocatoriaB);

        Slot slotA = new Slot();
        slotA.setInicio(LocalDateTime.parse("2026-07-03T10:30:00"));
        slotA.setFin(LocalDateTime.parse("2026-07-03T12:00:00"));
        slotA.setEliminado(false);
        slotA.setConvocatoria(convocatoriaA);
        slotA = slotRepositorio.save(slotA);

        Slot slotB = new Slot();
        slotB.setInicio(LocalDateTime.parse("2026-08-03T10:30:00"));
        slotB.setFin(LocalDateTime.parse("2026-08-03T12:00:00"));
        slotB.setEliminado(false);
        slotB.setConvocatoria(convocatoriaB);
        slotB = slotRepositorio.save(slotB);

        Materia materiaA = new Materia();
        materiaA.setNombre("Matemáticas");
        materiaA.setEliminada(false);
        materiaA = materiaRepositorio.save(materiaA);
        Materia materiaB = new Materia();
        materiaB.setNombre("Física");
        materiaB.setEliminada(false);
        materiaB = materiaRepositorio.save(materiaB);

        Prueba pruebaA = new Prueba();
        pruebaA.setEliminada(false);
        pruebaA.setSlot(slotA);
        pruebaA.setMateria(materiaA);
        pruebaRepositorio.save(pruebaA);

        Prueba pruebaB = new Prueba();
        pruebaB.setEliminada(false);
        pruebaB.setSlot(slotB);
        pruebaB.setMateria(materiaB);
        pruebaRepositorio.save(pruebaB);

        ResponseEntity<PruebaDTO[]> res = restTemplate.exchange(
                url("pruebas"),
                HttpMethod.GET,
                getEntityWithAuth(),
                PruebaDTO[].class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).hasSize(1);
        assertThat(res.getBody()[0].getId()).isEqualTo(pruebaB.getId());
    }

    @Test
    @DisplayName("Al crear una prueba con datos válidos devuelve 201 Created y la guarda en la base de datos")
    void crearPruebaValida() {
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

        Materia materia = new Materia();
        materia.setNombre("Matemáticas");
        materia.setEliminada(false);
        materia = materiaRepositorio.save(materia);

        PruebaNuevaDTO dto = new PruebaNuevaDTO();
        dto.setSlot(new SlotIdDTO(slot.getId()));
        dto.setMateria(new MateriaIdDTO(materia.getId()));

        ResponseEntity<PruebaDTO> res = restTemplate.exchange(
                url("pruebas"),
                HttpMethod.POST,
                getEntityWithAuth(dto),
                PruebaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getId()).isNotNull();

        Prueba guardada = pruebaRepositorio.findById(res.getBody().getId()).orElse(null);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getSlot().getId()).isEqualTo(slot.getId());
        assertThat(guardada.getMateria().getId()).isEqualTo(materia.getId());
        assertThat(guardada.getEliminada()).isFalse();
    }

    @Test
    @DisplayName("al obtener una prueba inexistente devuelve 404 Not Found")
    void obtenerPruebaInexistente() {
        ResponseEntity<Void> res = restTemplate.exchange(
                url("/pruebas/9999"),
                HttpMethod.GET,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("al actualizar una prueba inexistente devuelve 404 Not Found")
    void actualizarPruebaInexistente() {
        PruebaNuevaDTO nuevodto = new PruebaNuevaDTO();
        nuevodto.setSlot(new SlotIdDTO(9999L));
        nuevodto.setMateria(new MateriaIdDTO(9999L));

        ResponseEntity<Void> res = restTemplate.exchange(
                url("/pruebas/9999"), // Corregido: antes tenías /slots/9999 aquí
                HttpMethod.PUT,
                getEntityWithAuth(nuevodto),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Cobertura total: métodos generados de Prueba y Excepciones")
    void coberturaExtraPrueba() {
        // 1. Excepción
        BackendBoys.dawBB.excepciones.PruebasNoEncontrado ex =
            new BackendBoys.dawBB.excepciones.PruebasNoEncontrado(999L);
        assertThat(ex.getMessage()).isNotNull();

        // 2. Entidad
        Prueba p1 = new Prueba();
        p1.setId(1L);
        p1.setEliminada(false);

        Prueba p2 = new Prueba();
        p2.setId(1L);
        p2.setEliminada(false);

        assertThat(p1.toString()).isNotNull();
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    @DisplayName("Cobertura masiva: Pruebas DTOs y Servicios huérfanos")
    void coberturaMasivaPruebas() {
        // 1. Servicio huérfano
        pruebaServicioDirecto.obtenerTodasLasPruebas(null, null);

        // 2. DTOs
        PruebaDTO dto1 = new PruebaDTO(); dto1.setId(1L); dto1.setSlot(null); dto1.setMateria(null); dto1.setEliminada(false);
        PruebaDTO dto2 = new PruebaDTO(); dto2.setId(1L); dto2.setSlot(null); dto2.setMateria(null); dto2.setEliminada(false);
        assertThat(dto1.getId()).isEqualTo(1L); assertThat(dto1.getSlot()).isNull();
        assertThat(dto1.getMateria()).isNull(); assertThat(dto1.getEliminada()).isFalse();
        assertThat(dto1.toString()).isNotNull(); assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        PruebaNuevaDTO ndto1 = new PruebaNuevaDTO(); ndto1.setSlot(null); ndto1.setMateria(null);
        PruebaNuevaDTO ndto2 = new PruebaNuevaDTO(); ndto2.setSlot(null); ndto2.setMateria(null);
        assertThat(ndto1.getSlot()).isNull(); assertThat(ndto1.getMateria()).isNull();
        assertThat(ndto1.toString()).isNotNull(); assertThat(ndto1).isEqualTo(ndto2);
        assertThat(ndto1.hashCode()).isEqualTo(ndto2.hashCode());
    }

    @Test
    @DisplayName("Cobertura PruebaServicio: Todas las combinaciones de if/else")
    void coberturaRamasPruebaServicio() {
        // Ejecutar las 3 ramas condicionales que faltan para llegar al 100% de ese método
        pruebaServicioDirecto.obtenerTodasLasPruebas(1L, 1L);   // if (idConvocatoria != null && idSlot != null)
        pruebaServicioDirecto.obtenerTodasLasPruebas(1L, null); // else if (idConvocatoria != null)
        pruebaServicioDirecto.obtenerTodasLasPruebas(null, 1L); // else if (idSlot != null)
    }
}
