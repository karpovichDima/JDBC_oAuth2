package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.SomeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByName(String name);
    Optional<Content> findById(Long id);
    Optional<Content> findContentByOwnerIsNullAndOrganization(Organization organization);
    Content findByTypeAndOwner(SomeType type, AccountEntity owner);
}
