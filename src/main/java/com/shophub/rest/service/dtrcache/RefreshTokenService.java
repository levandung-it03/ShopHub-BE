package com.shophub.rest.service.dtrcache;

import com.shophub.rest.entity.dtrcache.RefreshToken;
import com.shophub.rest.repository.RefreshTokenCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {
    RefreshTokenCrud tokenRepo;

    public RefreshToken upsert(RefreshToken token) {
        return tokenRepo.save(token);
    }

    public void delete(RefreshToken token) {
        tokenRepo.delete(token);
    }

    public void deleteById(String jti) {
        tokenRepo.deleteById(jti);
    }

    public Optional<RefreshToken> findById(String tokenId) {
        return Optional.empty();
    }
}
