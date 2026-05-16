package com.shophub.rest.service;

import com.shophub.rest.entity.auth.Account;
import com.shophub.rest.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    AccountRepository accountRepository;

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }
}
