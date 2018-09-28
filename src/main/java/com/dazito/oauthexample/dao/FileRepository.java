package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<StorageElement, Long> {

}
