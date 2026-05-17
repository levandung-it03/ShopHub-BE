package com.shophub.rest.service.dtrcache;

import com.shophub.rest.entity.dtrcache.InvalidToken;
import com.shophub.rest.repository.InvalidTokenCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenService {
    InvalidTokenCrud tokenRepo;

    public String upsert(InvalidToken token) {
        return tokenRepo.save(token).getId();
    }

    public void delete(InvalidToken token) {
        tokenRepo.delete(token);
    }

    public Optional<InvalidToken> findById(String tokenId) {
        return tokenRepo.findById(tokenId);
    }

    public void save(InvalidToken token) {
        tokenRepo.save(token);
    }
}
