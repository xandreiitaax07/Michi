package BackendBoys.dawBB.seguridad;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        //Obtenemos los datos de la cabecera de petición la parte de autorización (el token de seguridad)
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        // JWT Token viene en la forma de "Bearer token". Eliminamos el término Bearer del inicio
        // para obtener solo el token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Recortamos el "Bearer " para quedarnos solo con lo que necesitamos
            jwtToken = requestTokenHeader.substring(7);
            try {
                //Tratamos de obtener el nombre de usuario y lo guardamos
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.info("No puedo obtener el JWT");
            } catch (ExpiredJwtException e) {
                logger.info("El token ha expirado");
            }
            logger.info("usuario = " + username);
        } else {
            logger.info("El token no comienza con Bearer");
        }

        // Comprobamos si la autenticación y el obtener el username ha ido bien
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Creamos un usuario temporal con ese nombre y sin permisos
            UserDetails userDetails = new User(username, "", Collections.emptyList());

            if (!jwtTokenUtil.isTokenExpired(jwtToken)) {
                //Creamos un token para este usuario en específico
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                logger.debug("usernamePasswordAuthenticationToken = " + usernamePasswordAuthenticationToken);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.debug("Token no válido");
            }

        }
        // A la ida

        //La petición avanza y ya después de esto llega a la zona de controladores
        chain.doFilter(request, response);

        // A la vuelta
    }

}
