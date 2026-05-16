package com.shophub.rest.repository;

import com.shophub.rest.entity.auth.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Long, Account> {

    Optional<Account> findByEmail(String email);
}
