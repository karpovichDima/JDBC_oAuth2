package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long> {

    Optional<Organization> findById(long id);
    Optional<Organization> findByOrganizationName(String organizationName);

}
