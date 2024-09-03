package de.zedalite.quotes.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import de.zedalite.quotes.repository.JwtKeySetRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

  private final JWTVerifier verifier;

  public JwtTokenService(
    final JwtKeySetRepository repository,
    final @Value("${app.security.jwt.issuer}") List<String> jwtIssuer
  ) {
    this.verifier = JWT.require(Algorithm.RSA256(repository.getKeyProvider()))
      .withIssuer(jwtIssuer.toArray(new String[0]))
      .withClaimPresence("sub")
      .build();
  }

  public String validateToken(final String token) {
    final DecodedJWT jwt = verifier.verify(token);
    return jwt.getClaim("sub").asString();
  }
}
