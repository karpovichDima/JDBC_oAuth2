package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Channel;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.type.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findById(Long id);
    Optional<AccountEntity> findByUsername(String username);
    Optional<AccountEntity> findUserByEmail(String email);
    Optional<AccountEntity> findUserByUuid(String uuid);
}
