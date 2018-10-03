package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByName(String name);
    Optional<FileEntity> findById(Long id);
    Optional<FileEntity> findByUuid(String uuid);
}
