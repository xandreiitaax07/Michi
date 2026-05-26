package BackendBoys.dawBB.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Component
public class JwtUtil {

    //Spring busca el valor de jwt.secret en application.properties y lo mete en secret
    @Value("${jwt.secret}")
    private String secret;

    // Obtener username del JWT
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Obtener la fecha de expiración del JWT
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //Este método se usará para poder parsear y obtener información del token que tenemos
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //Cogemos y verificamos que el token que hemos recibido ha sido firmado con nuestra key, si es asi obtenemos su payload
    private Claims getAllClaimsFromToken(String token) {
        byte[] keyBytes = secret.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        JwtParser parser = Jwts.parser().verifyWith(key).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    // Comprueba si el token ha expirado
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return (expiration == null) || expiration.before(new Date());
    }

    public String generateToken(String username) {
        byte[] keyBytes = secret.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(username) //Usamos el username para la clave
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de inicio de validez del token
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Caduca en 10 horas
                .signWith(key) // Firmamos con esta
                .compact(); // Empaquetamos en formato ilegible
    }

}