package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.Avatar;
import com.dazito.oauthexample.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByName(String name);
    Optional<Avatar> findById(Long id);
}
