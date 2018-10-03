package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.StorageElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<StorageElement, Long> {
    Optional<StorageElement> findByName(String name);
    Optional<StorageElement> findById(Long id);
}
