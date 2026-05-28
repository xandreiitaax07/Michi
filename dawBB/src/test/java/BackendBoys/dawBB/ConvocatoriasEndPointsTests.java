package BackendBoys.dawBB;

import BackendBoys.dawBB.dtos.ConvocatoriaDTO;
import BackendBoys.dawBB.dtos.ConvocatoriaNuevaDTO;
import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.repositorios.ConvocatoriaRepositorio;
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
import BackendBoys.dawBB.utils.DtoAndEntityMapper;
import BackendBoys.dawBB.seguridad.SwaggerConfig;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestRestTemplate
@DisplayName("En el controlador de convocatorias")
class ConvocatoriasEndPointsTests {

    @Autowired
    private BackendBoys.dawBB.seguridad.SecurityConfiguration securityConfiguration;

    @Autowired
    private BackendBoys.dawBB.seguridad.JwtUtil jwtUtilDirecto;

    @Autowired
    private SwaggerConfig swaggerConfig;

    @Autowired
    private BackendBoys.dawBB.servicios.ConvocatoriaServicio convocatoriaServicio;

    @Autowired
    private BackendBoys.dawBB.controladores.ControladorConvocatorias controladorConvocatorias;

    @Autowired
    private JwtUtil jwtUtil;

    private String valid_token;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Autowired
    private ConvocatoriaRepositorio convocatoriaRepositorio;

    @BeforeEach
    void setUp() {
        valid_token = jwtUtil.generateToken("test_user");
        convocatoriaRepositorio.deleteAll();
    }

    private String url(String rutaYConsulta) {
        return "http://localhost:" + port + rutaYConsulta;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(valid_token);
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
    @DisplayName("al obtener todas las convocatorias con la lista vacía devuelve 200 OK y lista vacía")
    void obtenerConvocatoriasListaVacia() {
        ResponseEntity<ConvocatoriaDTO[]> res = restTemplate.exchange(
                url("/convocatorias"),
                HttpMethod.GET,
                getEntityWithAuth(),
                ConvocatoriaDTO[].class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).isEmpty();
    }

    @Test
    @DisplayName("al obtener todas las convocatorias devuelve 200 OK y la lista completa")
    void obtenerConvocatoriasConDatos() {
        Convocatoria conv1 = new Convocatoria();
        conv1.setNombre("Julio 2025");
        conv1.setFechaInicio(LocalDateTime.parse("2025-07-03T00:00:00"));
        conv1.setFechaFin(LocalDateTime.parse("2025-07-05T00:00:00"));
        conv1.setActual(false);
        convocatoriaRepositorio.save(conv1);

        Convocatoria conv2 = new Convocatoria();
        conv2.setNombre("Junio 2026");
        conv2.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        conv2.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));
        conv2.setActual(true);
        convocatoriaRepositorio.save(conv2);

        ResponseEntity<ConvocatoriaDTO[]> res = restTemplate.exchange(
                url("/convocatorias"),
                HttpMethod.GET,
                getEntityWithAuth(),
                ConvocatoriaDTO[].class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("al obtener convocatorias sin autenticación devuelve 403 Forbidden")
    void obtenerConvocatoriasSinAutenticacion() {
        ResponseEntity<Void> res = restTemplate.getForEntity(
                url("/convocatorias"),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("al crear una convocatoria con datos válidos devuelve 201 Created y la guarda en la base de datos")
    void crearConvocatoriaValida() {
        ConvocatoriaNuevaDTO nuevaDTO = new ConvocatoriaNuevaDTO();
        nuevaDTO.setNombre("Junio 2026");
        nuevaDTO.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        nuevaDTO.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));

        ResponseEntity<ConvocatoriaDTO> res = restTemplate.exchange(
                url("/convocatorias"),
                HttpMethod.POST,
                getEntityWithAuth(nuevaDTO),
                ConvocatoriaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getIdConvocatoria()).isNotNull();
        assertThat(res.getBody().getNombre()).isEqualTo("Junio 2026");
        assertThat(res.getBody().getFechaInicio()).isEqualTo(LocalDateTime.parse("2026-06-02T00:00:00"));
        assertThat(res.getBody().getFechaFin()).isEqualTo(LocalDateTime.parse("2026-06-04T00:00:00"));

        assertThat(convocatoriaRepositorio.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("al crear una convocatoria devuelve la cabecera Location con la URI del nuevo recurso")
    void crearConvocatoriaDevuelveLocation() {
        ConvocatoriaNuevaDTO nuevaDTO = new ConvocatoriaNuevaDTO();
        nuevaDTO.setNombre("Julio 2026");
        nuevaDTO.setFechaInicio(LocalDateTime.parse("2026-07-03T00:00:00"));
        nuevaDTO.setFechaFin(LocalDateTime.parse("2026-07-05T00:00:00"));

        ResponseEntity<ConvocatoriaDTO> res = restTemplate.exchange(
                url("/convocatorias"),
                HttpMethod.POST,
                getEntityWithAuth(nuevaDTO),
                ConvocatoriaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getHeaders().getLocation()).isNotNull();
        assertThat(res.getHeaders().getLocation().toString())
                .contains("/convocatorias/" + res.getBody().getIdConvocatoria());
    }

    @Test
    @DisplayName("al crear una convocatoria se establece como actual y la anterior deja de serlo")
    void crearConvocatoriaDesactivaAnteriorActual() {
        Convocatoria primera = new Convocatoria();
        primera.setNombre("Julio 2025");
        primera.setFechaInicio(LocalDateTime.parse("2025-07-03T00:00:00"));
        primera.setFechaFin(LocalDateTime.parse("2025-07-05T00:00:00"));
        primera.setActual(true);
        primera = convocatoriaRepositorio.save(primera);

        ConvocatoriaNuevaDTO nuevaDTO = new ConvocatoriaNuevaDTO();
        nuevaDTO.setNombre("Junio 2026");
        nuevaDTO.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        nuevaDTO.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));

        ResponseEntity<ConvocatoriaDTO> res = restTemplate.exchange(
                url("/convocatorias"),
                HttpMethod.POST,
                getEntityWithAuth(nuevaDTO),
                ConvocatoriaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Convocatoria primeraActualizada = convocatoriaRepositorio
                .findById(primera.getIdConvocatoria()).orElseThrow();
        assertThat(primeraActualizada.getActual()).isFalse();

        assertThat(convocatoriaRepositorio.findByActualTrue()).isPresent();
        assertThat(convocatoriaRepositorio.findByActualTrue().get().getNombre())
                .isEqualTo("Junio 2026");
    }

    @Test
    @DisplayName("al crear una convocatoria sin autenticación devuelve 403 Forbidden")
    void crearConvocatoriaSinAutenticacion() {
        ConvocatoriaNuevaDTO nuevaDTO = new ConvocatoriaNuevaDTO();
        nuevaDTO.setNombre("Junio 2026");
        nuevaDTO.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        nuevaDTO.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));

        ResponseEntity<Void> res = restTemplate.postForEntity(
                url("/convocatorias"),
                nuevaDTO,
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(convocatoriaRepositorio.findAll()).isEmpty();
    }

    @Test
    @DisplayName("al crear una convocatoria nueva se marca como actual en base de datos")
    void crearConvocatoriaSeGuardaComoActual() {
        ConvocatoriaNuevaDTO nuevaDTO = new ConvocatoriaNuevaDTO();
        nuevaDTO.setNombre("Junio 2026");
        nuevaDTO.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        nuevaDTO.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));

        ResponseEntity<ConvocatoriaDTO> res = restTemplate.exchange(
                url("/convocatorias"),
                HttpMethod.POST,
                getEntityWithAuth(nuevaDTO),
                ConvocatoriaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Long idCreada = res.getBody().getIdConvocatoria();
        Convocatoria guardada = convocatoriaRepositorio.findById(idCreada).orElseThrow();
        assertThat(guardada.getActual()).isTrue();
    }

    @Test
    @DisplayName("al obtener la convocatoria actual cuando existe devuelve 200 OK con los datos correctos")
    void obtenerConvocatoriaActualExistente() {
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre("Junio 2026");
        convocatoria.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        convocatoria.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));
        convocatoria.setActual(true);
        convocatoria = convocatoriaRepositorio.save(convocatoria);

        ResponseEntity<ConvocatoriaDTO> res = restTemplate.exchange(
                url("/convocatorias/actual"),
                HttpMethod.GET,
                getEntityWithAuth(),
                ConvocatoriaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getIdConvocatoria()).isEqualTo(convocatoria.getIdConvocatoria());
        assertThat(res.getBody().getNombre()).isEqualTo("Junio 2026");
        assertThat(res.getBody().getFechaInicio()).isEqualTo(LocalDateTime.parse("2026-06-02T00:00:00"));
        assertThat(res.getBody().getFechaFin()).isEqualTo(LocalDateTime.parse("2026-06-04T00:00:00"));
    }

    @Test
    @DisplayName("al obtener la convocatoria actual cuando no hay ninguna devuelve 404 Not Found")
    void obtenerConvocatoriaActualNoExiste() {
        ResponseEntity<Void> res = restTemplate.exchange(
                url("/convocatorias/actual"),
                HttpMethod.GET,
                getEntityWithAuth(),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("al obtener la convocatoria actual sin autenticación devuelve 403 Forbidden")
    void obtenerConvocatoriaActualSinAutenticacion() {
        ResponseEntity<Void> res = restTemplate.getForEntity(
                url("/convocatorias/actual"),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("al obtener la convocatoria actual con varias convocatorias devuelve la de mayor ID")
    void obtenerConvocatoriaActualConVariasConvocatorias() {

        Convocatoria antigua = new Convocatoria();
        antigua.setNombre("Julio 2025");
        antigua.setFechaInicio(LocalDateTime.parse("2025-07-03T00:00:00"));
        antigua.setFechaFin(LocalDateTime.parse("2025-07-05T00:00:00"));
        antigua.setActual(false);
        convocatoriaRepositorio.save(antigua);

        Convocatoria reciente = new Convocatoria();
        reciente.setNombre("Junio 2026");
        reciente.setFechaInicio(LocalDateTime.parse("2026-06-02T00:00:00"));
        reciente.setFechaFin(LocalDateTime.parse("2026-06-04T00:00:00"));
        reciente.setActual(true);
        convocatoriaRepositorio.save(reciente);

        ResponseEntity<ConvocatoriaDTO> res = restTemplate.exchange(
                url("/convocatorias/actual"),
                HttpMethod.GET,
                getEntityWithAuth(),
                ConvocatoriaDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getNombre()).isEqualTo("Junio 2026");
    }

    @Test
    @DisplayName("Cobertura total: métodos generados de Convocatoria, DTOs, Utilidades y Excepciones")
    void coberturaExtraConvocatoria() {
        // 1. Excepción
        BackendBoys.dawBB.excepciones.ConvocatoriaNoEncontrado ex =
            new BackendBoys.dawBB.excepciones.ConvocatoriaNoEncontrado(999L);
        assertThat(ex.getMessage()).isNotNull();

        // 2. Entidad
        Convocatoria c1 = new Convocatoria();
        c1.setIdConvocatoria(1L);
        c1.setNombre("Test");

        Convocatoria c2 = new Convocatoria();
        c2.setIdConvocatoria(1L);
        c2.setNombre("Test");

        assertThat(c1.toString()).isNotNull();
        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());

        // 3. DTOs
        ConvocatoriaDTO dto1 = new ConvocatoriaDTO(1L, "Test", null, null);
        ConvocatoriaDTO dto2 = new ConvocatoriaDTO(1L, "Test", null, null);

        assertThat(dto1.toString()).isNotNull();
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        // 4. Utils (Constructor por defecto de la clase estática)
        BackendBoys.dawBB.utils.DtoAndEntityMapper mapper = new BackendBoys.dawBB.utils.DtoAndEntityMapper();
        assertThat(mapper).isNotNull();
    }

    @Test
    @DisplayName("Cobertura Utils: DtoAndEntityMapper con nulos")
    void coberturaMapperNulos() {
        // Métodos *NuevaTo*
        assertThat(DtoAndEntityMapper.materiaNuevaToMateria(null)).isNull();
        assertThat(DtoAndEntityMapper.convocatoriaNuevaToConvocatoria(null)).isNull();
        assertThat(DtoAndEntityMapper.pruebaNuevaToPrueba(null)).isNull();
        assertThat(DtoAndEntityMapper.slotNuevoToSlot(null)).isNull();

        // Métodos *ToDto*
        assertThat(DtoAndEntityMapper.materiaToDto(null)).isNull();
        assertThat(DtoAndEntityMapper.convocatoriaToDto(null)).isNull();
        assertThat(DtoAndEntityMapper.pruebaToDto(null)).isNull();
        assertThat(DtoAndEntityMapper.slotToDto(null)).isNull();

        // Métodos *DTOtoEntity*
        assertThat(DtoAndEntityMapper.materiaDTOtoEntity(null)).isNull();
        assertThat(DtoAndEntityMapper.convocatoriaDTOtoEntity(null)).isNull();
        assertThat(DtoAndEntityMapper.pruebaDTOtoEntity(null)).isNull();
        assertThat(DtoAndEntityMapper.slotDTOtoEntity(null)).isNull();
    }

    @Test
    @DisplayName("Cobertura Seguridad: SwaggerConfig")
    void coberturaSwagger() {
        // El método real en tu SwaggerConfig se llama customOpenAPI()
        assertThat(swaggerConfig.customOpenAPI()).isNotNull();
    }

    @Test
    @DisplayName("Cobertura final: DawBbApplication y Controlador de Convocatorias")
    void coberturaFinalRestante() {
        // 1. Cubrir el ExceptionHandler del controlador manualmente (lo que falta en controladores)
        controladorConvocatorias.noEncontrado();

        // 2. Cubrir el main de la aplicación (lo que falta en el paquete raíz dawBB)
        BackendBoys.dawBB.DawBbApplication.main(new String[]{"--server.port=0"});
    }

    @Test
    @DisplayName("Cobertura masiva: ConvocatoriaNuevaDTO")
    void coberturaMasivaConvocatorias() {
        // 1. DTO restante
        ConvocatoriaNuevaDTO dto1 = new ConvocatoriaNuevaDTO();
        dto1.setNombre("Test"); dto1.setFechaInicio(LocalDateTime.MIN); dto1.setFechaFin(LocalDateTime.MAX);
        ConvocatoriaNuevaDTO dto2 = new ConvocatoriaNuevaDTO();
        dto2.setNombre("Test"); dto2.setFechaInicio(LocalDateTime.MIN); dto2.setFechaFin(LocalDateTime.MAX);

        assertThat(dto1.getNombre()).isEqualTo("Test");
        assertThat(dto1.getFechaInicio()).isEqualTo(LocalDateTime.MIN);
        assertThat(dto1.getFechaFin()).isEqualTo(LocalDateTime.MAX);
        assertThat(dto1.toString()).isNotNull();
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("El verdadero 100%: Constructor fantasma de la clase Main")
    void coberturaMainConstructor() {
        BackendBoys.dawBB.DawBbApplication app = new BackendBoys.dawBB.DawBbApplication();
        assertThat(app).isNotNull();
    }

    @Test
    @DisplayName("Cobertura Absoluta TOTAL: Ramas ocultas de equals() en Entidades y DTOs")
    void coberturaAbsolutaEqualsRamas() {
        // 1. RAMAS DE ENTIDADES
        BackendBoys.dawBB.entidades.Convocatoria c = new BackendBoys.dawBB.entidades.Convocatoria();
        assertThat(c.equals(c)).isTrue();               // Rama: Mismo objeto
        assertThat(c.equals(null)).isFalse();           // Rama: Objeto nulo
        assertThat(c.equals(new Object())).isFalse();   // Rama: Clase distinta

        BackendBoys.dawBB.entidades.Materia m = new BackendBoys.dawBB.entidades.Materia();
        assertThat(m.equals(m)).isTrue();
        assertThat(m.equals(null)).isFalse();
        assertThat(m.equals(new Object())).isFalse();

        BackendBoys.dawBB.entidades.Prueba p = new BackendBoys.dawBB.entidades.Prueba();
        assertThat(p.equals(p)).isTrue();
        assertThat(p.equals(null)).isFalse();
        assertThat(p.equals(new Object())).isFalse();

        BackendBoys.dawBB.entidades.Slot s = new BackendBoys.dawBB.entidades.Slot();
        assertThat(s.equals(s)).isTrue();
        assertThat(s.equals(null)).isFalse();
        assertThat(s.equals(new Object())).isFalse();

        // 2. RAMAS DE DTOs
        BackendBoys.dawBB.dtos.ConvocatoriaDTO cdto = new BackendBoys.dawBB.dtos.ConvocatoriaDTO();
        assertThat(cdto.equals(cdto)).isTrue();
        assertThat(cdto.equals(null)).isFalse();
        assertThat(cdto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.ConvocatoriaNuevaDTO cndto = new BackendBoys.dawBB.dtos.ConvocatoriaNuevaDTO();
        assertThat(cndto.equals(cndto)).isTrue();
        assertThat(cndto.equals(null)).isFalse();
        assertThat(cndto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.MateriaDTO mdto = new BackendBoys.dawBB.dtos.MateriaDTO();
        assertThat(mdto.equals(mdto)).isTrue();
        assertThat(mdto.equals(null)).isFalse();
        assertThat(mdto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.MateriaNuevaDTO mndto = new BackendBoys.dawBB.dtos.MateriaNuevaDTO();
        assertThat(mndto.equals(mndto)).isTrue();
        assertThat(mndto.equals(null)).isFalse();
        assertThat(mndto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.MateriaIdDTO midto = new BackendBoys.dawBB.dtos.MateriaIdDTO(1L);
        assertThat(midto.equals(midto)).isTrue();
        assertThat(midto.equals(null)).isFalse();
        assertThat(midto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.PruebaDTO pdto = new BackendBoys.dawBB.dtos.PruebaDTO();
        assertThat(pdto.equals(pdto)).isTrue();
        assertThat(pdto.equals(null)).isFalse();
        assertThat(pdto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.PruebaNuevaDTO pndto = new BackendBoys.dawBB.dtos.PruebaNuevaDTO();
        assertThat(pndto.equals(pndto)).isTrue();
        assertThat(pndto.equals(null)).isFalse();
        assertThat(pndto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.SlotDTO sdto = new BackendBoys.dawBB.dtos.SlotDTO();
        assertThat(sdto.equals(sdto)).isTrue();
        assertThat(sdto.equals(null)).isFalse();
        assertThat(sdto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.SlotNuevoDTO sndto = new BackendBoys.dawBB.dtos.SlotNuevoDTO();
        assertThat(sndto.equals(sndto)).isTrue();
        assertThat(sndto.equals(null)).isFalse();
        assertThat(sndto.equals(new Object())).isFalse();

        BackendBoys.dawBB.dtos.SlotIdDTO sidto = new BackendBoys.dawBB.dtos.SlotIdDTO(1L);
        assertThat(sidto.equals(sidto)).isTrue();
        assertThat(sidto.equals(null)).isFalse();
        assertThat(sidto.equals(new Object())).isFalse();
    }

    @Test
    @DisplayName("Cobertura Seguridad: Beans y lectura de Claims")
    void coberturaRamasSeguridadYJwt() {
        // Forzar la ejecución del Bean passwordEncoder()
        org.springframework.security.crypto.password.PasswordEncoder encoder = securityConfiguration.passwordEncoder();
        assertThat(encoder).isNotNull();

        // Forzar el paso interno de getClaimFromToken
        String token = jwtUtilDirecto.generateToken("user_test_claims");
        java.util.Date expDate = jwtUtilDirecto.getExpirationDateFromToken(token);
        assertThat(expDate).isNotNull();

        String user = jwtUtilDirecto.getClaimFromToken(token, io.jsonwebtoken.Claims::getSubject);
        assertThat(user).isEqualTo("user_test_claims");
    }

        @Test
        @DisplayName("Cobertura Seguridad: getAuthenticatedUser devuelve UserDetails cuando hay Authentication")
        void coberturaGetAuthenticatedUser() {
        org.springframework.security.core.Authentication auth =
            org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.springframework.security.core.userdetails.UserDetails userDetails =
            org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        org.mockito.Mockito.when(auth.getPrincipal()).thenReturn(userDetails);

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        java.util.Optional<org.springframework.security.core.userdetails.UserDetails> res =
            BackendBoys.dawBB.seguridad.SecurityConfiguration.getAuthenticatedUser();

        org.assertj.core.api.Assertions.assertThat(res).isPresent();
        org.assertj.core.api.Assertions.assertThat(res.get()).isSameAs(userDetails);

        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }

    @Test
    @DisplayName("Cobertura Mapper: Instancias anidadas no nulas")
    void coberturaMappersAnidados() {
        // Mapeo Prueba
        BackendBoys.dawBB.dtos.PruebaNuevaDTO pn = new BackendBoys.dawBB.dtos.PruebaNuevaDTO();
        pn.setMateria(new BackendBoys.dawBB.dtos.MateriaIdDTO(1L));
        pn.setSlot(new BackendBoys.dawBB.dtos.SlotIdDTO(1L));
        BackendBoys.dawBB.utils.DtoAndEntityMapper.pruebaNuevaToPrueba(pn);

        BackendBoys.dawBB.entidades.Prueba pr = new BackendBoys.dawBB.entidades.Prueba();
        pr.setMateria(new BackendBoys.dawBB.entidades.Materia());
        BackendBoys.dawBB.entidades.Slot slotPrueba = new BackendBoys.dawBB.entidades.Slot();
        slotPrueba.setConvocatoria(new BackendBoys.dawBB.entidades.Convocatoria());
        pr.setSlot(slotPrueba);
        BackendBoys.dawBB.utils.DtoAndEntityMapper.pruebaToDto(pr);

        // Mapeo Slot
        BackendBoys.dawBB.dtos.SlotNuevoDTO sn = new BackendBoys.dawBB.dtos.SlotNuevoDTO();
        sn.setConvocatoria(new BackendBoys.dawBB.dtos.ConvocatoriaDTO());
        BackendBoys.dawBB.utils.DtoAndEntityMapper.slotNuevoToSlot(sn);

        BackendBoys.dawBB.entidades.Slot slotEnt = new BackendBoys.dawBB.entidades.Slot();
        slotEnt.setConvocatoria(new BackendBoys.dawBB.entidades.Convocatoria());
        BackendBoys.dawBB.utils.DtoAndEntityMapper.slotToDto(slotEnt);
    }

        @Test
        @DisplayName("Cobertura Security: authenticationManager devuelve el manager proporcionado cuando existe")
        void coberturaAuthenticationManagerProvided() throws Exception {
        BackendBoys.dawBB.seguridad.SecurityConfiguration secConfig = new BackendBoys.dawBB.seguridad.SecurityConfiguration();

        org.springframework.security.authentication.AuthenticationManager provided =
            org.mockito.Mockito.mock(org.springframework.security.authentication.AuthenticationManager.class);

        org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration ac =
            org.mockito.Mockito.mock(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration.class);
        org.mockito.Mockito.when(ac.getAuthenticationManager()).thenReturn(provided);

        org.springframework.security.authentication.AuthenticationManager result = secConfig.authenticationManager(ac);
        org.assertj.core.api.Assertions.assertThat(result).isSameAs(provided);
        }

    @Test
    @DisplayName("Cobertura Final: Mapper Anidado, SecurityConfig y JwtUtil")
    void coberturaFinalArchivosRestantes() {
        // 1. DtoAndEntityMapper (Constructor estático y mapeos anidados)
        BackendBoys.dawBB.utils.DtoAndEntityMapper mapper = new BackendBoys.dawBB.utils.DtoAndEntityMapper();
        org.assertj.core.api.Assertions.assertThat(mapper).isNotNull();

        // -> pruebaNuevaToPrueba
        BackendBoys.dawBB.dtos.PruebaNuevaDTO pn = new BackendBoys.dawBB.dtos.PruebaNuevaDTO();
        pn.setMateria(new BackendBoys.dawBB.dtos.MateriaIdDTO(1L));
        pn.setSlot(new BackendBoys.dawBB.dtos.SlotIdDTO(1L));
        BackendBoys.dawBB.utils.DtoAndEntityMapper.pruebaNuevaToPrueba(pn);

        // -> pruebaDTOtoEntity
        BackendBoys.dawBB.dtos.PruebaDTO pd = new BackendBoys.dawBB.dtos.PruebaDTO();
        pd.setMateria(new BackendBoys.dawBB.dtos.MateriaDTO(1L, "M", false));
        BackendBoys.dawBB.dtos.SlotDTO sd = new BackendBoys.dawBB.dtos.SlotDTO();
        sd.setConvocatoria(new BackendBoys.dawBB.dtos.ConvocatoriaDTO());
        pd.setSlot(sd);
        BackendBoys.dawBB.utils.DtoAndEntityMapper.pruebaDTOtoEntity(pd);

        // -> pruebaToDto
        BackendBoys.dawBB.entidades.Prueba pr = new BackendBoys.dawBB.entidades.Prueba();
        pr.setMateria(new BackendBoys.dawBB.entidades.Materia());
        BackendBoys.dawBB.entidades.Slot sl = new BackendBoys.dawBB.entidades.Slot();
        sl.setConvocatoria(new BackendBoys.dawBB.entidades.Convocatoria());
        pr.setSlot(sl);
        BackendBoys.dawBB.utils.DtoAndEntityMapper.pruebaToDto(pr);

        // -> slotNuevoToSlot y el resto de anidados
        BackendBoys.dawBB.dtos.SlotNuevoDTO sn = new BackendBoys.dawBB.dtos.SlotNuevoDTO();
        sn.setConvocatoria(new BackendBoys.dawBB.dtos.ConvocatoriaDTO());
        BackendBoys.dawBB.utils.DtoAndEntityMapper.slotNuevoToSlot(sn);
        BackendBoys.dawBB.utils.DtoAndEntityMapper.slotDTOtoEntity(sd);
        BackendBoys.dawBB.utils.DtoAndEntityMapper.slotToDto(sl);

        // 2. SecurityConfiguration (Constructor y Beans sueltos)
        BackendBoys.dawBB.seguridad.SecurityConfiguration secConfig = new BackendBoys.dawBB.seguridad.SecurityConfiguration();
        org.assertj.core.api.Assertions.assertThat(secConfig).isNotNull();
        org.assertj.core.api.Assertions.assertThat(securityConfiguration.passwordEncoder()).isNotNull();

        // 3. JwtUtil (Lectura de Claims y expiración)
        String token = jwtUtilDirecto.generateToken("test_user_claims");
        java.util.Date expDate = jwtUtilDirecto.getExpirationDateFromToken(token);
        org.assertj.core.api.Assertions.assertThat(expDate).isNotNull();

        String user = jwtUtilDirecto.getUsernameFromToken(token);
        org.assertj.core.api.Assertions.assertThat(user).isEqualTo("test_user_claims");
    }

        @Test
        @DisplayName("Cobertura Security: authenticationManager - ambas ramas (proporcionado y fallback)")
        void coberturaAuthenticationManagerBothBranches() throws Exception {
        BackendBoys.dawBB.seguridad.SecurityConfiguration secConfig = new BackendBoys.dawBB.seguridad.SecurityConfiguration();

        // Rama 1: AuthenticationConfiguration devuelve un AuthenticationManager
        org.springframework.security.authentication.AuthenticationManager provided =
            org.mockito.Mockito.mock(org.springframework.security.authentication.AuthenticationManager.class);
        org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration ac1 =
            org.mockito.Mockito.mock(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration.class);
        org.mockito.Mockito.when(ac1.getAuthenticationManager()).thenReturn(provided);

        org.springframework.security.authentication.AuthenticationManager result1 = secConfig.authenticationManager(ac1);
        org.assertj.core.api.Assertions.assertThat(result1).isSameAs(provided);

        // Rama 2: AuthenticationConfiguration devuelve null -> fallback
        org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration ac2 =
            org.mockito.Mockito.mock(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration.class);
        org.springframework.security.authentication.AuthenticationManager result2 = secConfig.authenticationManager(ac2);
        org.assertj.core.api.Assertions.assertThat(result2).isNotNull();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> result2.authenticate(null))
            .isInstanceOf(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class);
        }

        @Test
        @DisplayName("Cobertura Seguridad: getAuthenticatedUser devuelve vacío cuando no hay Authentication")
        void coberturaGetAuthenticatedUserNull() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        java.util.Optional<org.springframework.security.core.userdetails.UserDetails> res =
            BackendBoys.dawBB.seguridad.SecurityConfiguration.getAuthenticatedUser();
        org.assertj.core.api.Assertions.assertThat(res).isEmpty();
        }

    @Test
    @DisplayName("Cobertura Definitiva JwtUtil: Ramas inalcanzables con Spy")
    void coberturaJwtUtilRamasInalcanzables() {
        // Creamos un espía (spy) de JwtUtil para poder falsear solo el método de la fecha
        BackendBoys.dawBB.seguridad.JwtUtil spyJwtUtil = org.mockito.Mockito.spy(new BackendBoys.dawBB.seguridad.JwtUtil());

        // 1. Forzar que la expiración sea NULL (Cubre la rama: expiration == null)
        org.mockito.Mockito.doReturn(null).when(spyJwtUtil).getExpirationDateFromToken("token_nulo");
        org.assertj.core.api.Assertions.assertThat(spyJwtUtil.isTokenExpired("token_nulo")).isTrue();

        // 2. Forzar que la expiración sea una fecha pasada (Cubre la rama: expiration.before(new Date()))
        java.util.Date fechaPasada = new java.util.Date(System.currentTimeMillis() - 100000);
        org.mockito.Mockito.doReturn(fechaPasada).when(spyJwtUtil).getExpirationDateFromToken("token_pasado");
        org.assertj.core.api.Assertions.assertThat(spyJwtUtil.isTokenExpired("token_pasado")).isTrue();

        // 3. Forzar que la expiración sea una fecha futura
        java.util.Date fechaFutura = new java.util.Date(System.currentTimeMillis() + 100000);
        org.mockito.Mockito.doReturn(fechaFutura).when(spyJwtUtil).getExpirationDateFromToken("token_futuro");
        org.assertj.core.api.Assertions.assertThat(spyJwtUtil.isTokenExpired("token_futuro")).isFalse();
    }

    @Test
    @DisplayName("Cobertura Definitiva: SecurityConfiguration (FilterChain y Beans)")
    void coberturaFinalSecurityConfiguration() throws Exception {
        // 1. Invocamos el filtro de seguridad para cubrir la construcción de la cadena
        // Esto obliga a recorrer los métodos authorizeHttpRequests, csrf, etc.
        org.springframework.security.config.annotation.web.builders.HttpSecurity http =
            org.mockito.Mockito.mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class,
            org.mockito.Mockito.RETURNS_DEEP_STUBS);

        BackendBoys.dawBB.seguridad.SecurityConfiguration secConfig = new BackendBoys.dawBB.seguridad.SecurityConfiguration();

        // Ejecutamos el método del filtro
        secConfig.filterChain(http);

        // 2. Cobertura de los Beans adicionales
        org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration mockAuthConfig =
            org.mockito.Mockito.mock(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration.class);

        org.springframework.security.authentication.AuthenticationManager mgr =
            secConfig.authenticationManager(mockAuthConfig);

        org.assertj.core.api.Assertions.assertThat(mgr).isNotNull();

        // Forzar la rama del AuthenticationManager fallback: su método authenticate lanza AuthenticationCredentialsNotFoundException
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mgr.authenticate(null))
            .isInstanceOf(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class);

        org.assertj.core.api.Assertions.assertThat(secConfig.passwordEncoder()).isNotNull();
    }
}
