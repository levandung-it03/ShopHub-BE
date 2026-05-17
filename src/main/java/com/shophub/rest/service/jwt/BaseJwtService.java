package com.shophub.rest.service.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.shophub.rest.config.rest.ETokenType;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.entity.enums.EAuthority;
import com.shophub.rest.entity.enums.EClaimName;
import com.shophub.rest.entity.enums.EProvider;
import com.shophub.rest.entity.rest.JwtInfo;
import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.shophub.rest.entity.enums.EClaimName.SCOPES;
import static com.shophub.rest.util.contants.CCommon.*;

public abstract class BaseJwtService {
    protected final String KEY_SPEC_ALGO = "RSA";

    //--We don't share the generation module.
    public abstract boolean checkIsValid(String token);
    public abstract JwtInfo read(String token);
    public abstract JwtInfo decode(String token) throws JwtServiceException;

    protected RSAPrivateKey loadPrivateKey(Resource resource) throws Exception {
        String strKey = new String(resource.getInputStream().readAllBytes())
            .replace(TOKEN.PEM_STARTER(TOKEN.PEM_PRIVATE), Symbols.EMPTY)
            .replace(TOKEN.PEM_ENDER(TOKEN.PEM_PRIVATE), Symbols.EMPTY)
            .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(strKey);
        var keySpec = new PKCS8EncodedKeySpec(keyBytes);
        var keyFact = KeyFactory.getInstance(KEY_SPEC_ALGO);

        return (RSAPrivateKey) keyFact.generatePrivate(keySpec);
    }

    protected RSAPublicKey loadPublicKey(Resource resource) throws Exception {
        String strKey = new String(resource.getInputStream().readAllBytes())
            .replace(TOKEN.PEM_STARTER(TOKEN.PEM_PUBLIC), Symbols.EMPTY)
            .replace(TOKEN.PEM_ENDER(TOKEN.PEM_PUBLIC), Symbols.EMPTY)
            .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(strKey);
        var keySpec = new X509EncodedKeySpec(keyBytes);
        var keyFact = KeyFactory.getInstance(KEY_SPEC_ALGO);

        return (RSAPublicKey) keyFact.generatePublic(keySpec);
    }

    protected JwtInfo buildJwtInfo(JWTClaimsSet claimsSet) throws JwtServiceException {
        try {
            String scope = claimsSet.getClaim(SCOPES.toStr()).toString();
            EProvider provider = claimsSet.getClaim(EClaimName.PROVIDER.toStr()) != null
                ? EProvider.valueOf(claimsSet.getClaim(EClaimName.PROVIDER.toStr()).toString())    // May throw exc
                : null;

            return JwtInfo.builder()
                .jti(claimsSet.getJWTID())
                .userId(Long.parseLong(claimsSet.getSubject()))
                .fullName(claimsSet.getClaim(EClaimName.FULL_NAME.toStr()).toString())
                .role(EAuthority.valueOf(scope))
                .type(claimsSet.getExpirationTime() != null ? ETokenType.JWT_ACCESS : ETokenType.JWT_REFRESH)
                .provider(provider)
                .issuedAt(claimsSet.getIssueTime().toInstant())
                .expiresAt(claimsSet.getExpirationTime().toInstant())
                .build();
        } catch (Exception e) {
            throw new JwtServiceException(ErrorCodes.INVALID_TOKEN, e);
        }
    }
}
