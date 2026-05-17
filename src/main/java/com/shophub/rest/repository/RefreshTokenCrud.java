package com.shophub.rest.repository;

import com.shophub.rest.entity.dtrcache.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenCrud extends CrudRepository<RefreshToken, String> {
}
