package BackendBoys.dawBB.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import java.util.Optional;

//Archivo de configuración (Spring boot lo lee y usa al inicio)
@Configuration
//Activamos con esta anotación Spring security
@EnableWebSecurity
public class SecurityConfiguration {
    //Con esta anotacion SpringBoot ha de encargarse de enlazar el jwtRequestFilter.
    @Autowired
    //Aplica los filtros y reglas de seguridad que especifiquemos.
    private JwtRequestFilter jwtRequestFilter;

    //El bean es para darle una herramienta a SpringBoot
    @Bean
    //Con esto ciframos las contraseñas usando Bcrypt (transforma "1234" --> "churro ilegible")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //Esto se desactiva, ya que no usamos cookies (desactivamos la protección contra falsificación de peticiones)
                .formLogin(AbstractHttpConfigurer::disable) //Desactivamos el formulario de inicio de sesión automático
                .httpBasic(AbstractHttpConfigurer::disable) //Desactivamos el modelo de formulario básico de springboot
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests //
                                .requestMatchers("/").permitAll() //Aceptamos todas las peticiones que quieran ver la raíz y el swagger ui (sin filtro de seguridad)
                                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated() // Todas las demas peticiones hayq ue pasar el filtro de autenticación
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//Al no tener estado en cada peticion debemos comprobar el token de seguridad (sin memoria)
                );
        http.addFilterAfter(jwtRequestFilter, LogoutFilter.class);// Añadimos el jwtRequestFilter que antes hemos inicializado para que monitorice las peticiones
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager mgr = authenticationConfiguration.getAuthenticationManager();
        if (mgr != null) return mgr;
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                throw new AuthenticationCredentialsNotFoundException("No AuthenticationManager configured");
            }
        };
    }

    //Como tenemos una política stateless cuando el usuario esta haciendo cualquier petición la información de este se guarda en
    // otra sección del código, con este método accedemos ahi y obtenemos los datos del usuario.
    public static Optional<UserDetails> getAuthenticatedUser() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return Optional.empty();
        return Optional.ofNullable((UserDetails) authentication.getPrincipal());
    }


}
