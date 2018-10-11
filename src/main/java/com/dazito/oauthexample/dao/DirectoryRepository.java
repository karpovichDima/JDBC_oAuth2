package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByName(String name);
    Optional<Directory> findById(Long id);
    Optional<Directory> findContentByOwnerIsNullAndOrganization(String organization);
}
