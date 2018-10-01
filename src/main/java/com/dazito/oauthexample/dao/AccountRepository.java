package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findById(Long id);
    Optional<AccountEntity> findByUsername(String username);
    Collection<AccountEntity> findByRole(String role);
    Optional<AccountEntity> findUserByEmail(String email);
}
