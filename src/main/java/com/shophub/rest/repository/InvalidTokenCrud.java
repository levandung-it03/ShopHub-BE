package com.shophub.rest.repository;

import com.shophub.rest.entity.dtrcache.InvalidToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenCrud extends CrudRepository<InvalidToken, String> {
}
