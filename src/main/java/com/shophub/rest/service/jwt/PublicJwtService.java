package com.shophub.rest.service.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.entity.dtrcache.InvalidToken;
import com.shophub.rest.entity.dtrcache.RefreshToken;
import com.shophub.rest.entity.rest.JwtInfo;
import com.shophub.rest.service.dtrcache.InvalidTokenService;
import com.shophub.rest.service.dtrcache.RefreshTokenService;
import com.shophub.rest.util.contants.CCommon;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Usage: As a common-service that is used by any implemented services.
 */
@Component
@RequiredArgsConstructor
public class PublicJwtService extends BaseJwtService {
    private final CommonEnvConfig env;
    private final InvalidTokenService invalidTokenService;
    private final RefreshTokenService refreshTokenService;
    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() throws JwtServiceException {
        try {
            this.publicKey = super.loadPublicKey(env.PUBLIC_KEY());
        } catch (Exception e) {
            throw new JwtServiceException(e);
        }
    }

    /**
     * Aggregation of `decode()` and checking expiration.
     * @param token raw `String`
     */
    @Override
    public boolean checkIsValid(String token) {
        boolean result;
        try {
            JwtInfo tokenInfo = this.decode(token);
            Instant nowInstant = ZonedDateTime.now(ZoneId.of(env.DATETIME_ZONE())).toInstant();
            result = tokenInfo.getExpiresAt().isAfter(nowInstant);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public JwtInfo read(String token) {
        try {
            token = token.contains(CCommon.TOKEN.BEARER_)
                ? token.split(CCommon.TOKEN.BEARER_)[1]
                : token;
            var signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            return this.buildJwtInfo(claimsSet);
        } catch (Exception e) {
            return JwtInfo.builder().build();
        }
    }

    @Override
    public JwtInfo decode(String token) throws JwtServiceException {
        try {
            token = token.contains(CCommon.TOKEN.BEARER_)
                ? token.split(CCommon.TOKEN.BEARER_)[1]
                : token;
            var signedJWT = SignedJWT.parse(token);

            boolean verified = signedJWT.verify(new RSASSAVerifier(this.publicKey));
            if (!verified)
                throw new JwtServiceException(ErrorCodes.INVALID_TOKEN);

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            return this.buildJwtInfo(claims);
        } catch (JOSEException | ParseException e) {
            throw new JwtServiceException(ErrorCodes.INVALID_TOKEN, e);
        }
    }

    public void killAccessToken(String jti) {
        invalidTokenService.save(InvalidToken.builder().id(jti).build());
    }

    public void killRefreshToken(String jti) {
        refreshTokenService.deleteById(jti);
    }

    public boolean isAuthorizedToken(String token) throws JwtServiceException {
        JwtInfo tknInfo = this.decode(token);
        return switch (tknInfo.getType()) {
            case JWT_ACCESS -> this.isAuthorizedAccessToken(tknInfo);
            case JWT_REFRESH -> this.isAuthorizedRefreshToken(tknInfo);
        };
    }

    private boolean isAuthorizedRefreshToken(JwtInfo tknInfo) throws JwtServiceException {
        Optional<RefreshToken> cachedTkn = refreshTokenService.findById(tknInfo.getJti());
        return cachedTkn.isPresent();
    }

    private boolean isAuthorizedAccessToken(JwtInfo tknInfo) throws JwtServiceException {
        return invalidTokenService.findById(tknInfo.getJti()).isEmpty();
    }
}
