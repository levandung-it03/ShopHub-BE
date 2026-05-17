package com.shophub.rest.service.jwt;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.rest.ETokenType;
import com.shophub.rest.entity.enums.EClaimName;
import com.shophub.rest.entity.rest.JwtInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PrivateJwtService extends BaseJwtService {
    private final CommonEnvConfig env;
    private final PublicJwtService publicJwtService;
    private RSAPrivateKey privateKey;

    @PostConstruct
    public void init() throws JwtServiceException {
        try {
            this.privateKey = super.loadPrivateKey(env.PRIVATE_KEY());
        } catch (Exception e) {
            throw new JwtServiceException(e);
        }
    }

    public String generate(JwtInfo claims) throws JwtServiceException {
        try {
            int expiry = switch (claims.getType()) {
                case ETokenType.JWT_ACCESS -> env.ACCESS_EXPIRY();
                case ETokenType.JWT_REFRESH -> env.REFRESH_EXPIRY();
            };
            Instant now = ZonedDateTime.now(ZoneId.of(env.DATETIME_ZONE())).toInstant();
            String id = UUID.randomUUID().toString();
            var jwtClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(id)
                .issuer(env.SVC_NAME())
                .subject(claims.getUserId().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(expiry)))
                .claim(EClaimName.SCOPES.toStr(), claims.getRole())
                .claim(EClaimName.PROVIDER.toStr(), claims.getProvider())
                .claim(EClaimName.FULL_NAME.toStr(), claims.getFullName());

            var header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
            var jwtSigner = new JWSObject(header, jwtClaimsSet.build().toPayload());
            jwtSigner.sign(new RSASSASigner(this.privateKey));

            return jwtSigner.serialize();
        } catch (Exception e) {
            throw new JwtServiceException(e);
        }
    }

    @Override
    public boolean checkIsValid(String token) {
        return publicJwtService.checkIsValid(token);
    }

    @Override
    public JwtInfo read(String token) {
        return publicJwtService.read(token);
    }

    @Override
    public JwtInfo decode(String token) throws JwtServiceException {
        return publicJwtService.decode(token);
    }
}
