package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {

    Optional<Organization> findById(long id);

}
