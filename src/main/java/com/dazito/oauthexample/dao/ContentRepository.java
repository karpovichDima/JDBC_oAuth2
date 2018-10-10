package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByName(String name);
    Optional<Content> findById(Long id);
    Optional<Content> findContentByOwnerIsNullAndOrganization(String organization);
}
