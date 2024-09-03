package de.zedalite.quotes.repository;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class JwtKeySetRepository {

  private final JwkProvider jwkProvider;

  public JwtKeySetRepository(final @Value("${app.security.jwk.url}") URL jwkHost) {
    this.jwkProvider = new JwkProviderBuilder(jwkHost).cached(10, 24, TimeUnit.HOURS).build();
  }

  public RSAKeyProvider getKeyProvider() {
    return new RSAKeyProvider() {
      @Override
      public RSAPublicKey getPublicKeyById(final String keyId) {
        try {
          return (RSAPublicKey) jwkProvider.get(keyId).getPublicKey();
        } catch (final JwkException e) {
          throw new JWTDecodeException(e.getMessage());
        }
      }

      @Override
      public RSAPrivateKey getPrivateKey() {
        throw new UnsupportedOperationException();
      }

      @Override
      public String getPrivateKeyId() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
