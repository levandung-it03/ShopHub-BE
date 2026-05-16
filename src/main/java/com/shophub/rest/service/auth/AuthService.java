package com.shophub.rest.service.auth;

import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.dto.AuthRequest;
import com.shophub.rest.entity.auth.Account;
import com.shophub.rest.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    AccountService accountService;
    PasswordEncoder userPasswordEncoder;

    public void authenticate(AuthRequest request) {
        Account account = accountService.findByEmail(request.getEmail())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.USER_NOTFOUND));
    }
}
