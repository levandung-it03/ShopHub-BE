package com.shophub.rest.service;

import com.shophub.rest.entity.auth.UserProfile;
import com.shophub.rest.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserProfileRepository userProfileRepo;

    public Optional<UserProfile> findByAccountId(Long accId) {
        return userProfileRepo.findByAccountId(accId);
    }
}
