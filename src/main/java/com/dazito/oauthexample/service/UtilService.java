package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.utils.exception.OrganizationIsNotMuchException;
import lombok.NonNull;

import java.io.File;

public interface UtilService{

    boolean matchesOrganizations(Organization organizationUser, Organization organizationStorage);

    /**
     * check matches if of the current user and if ot the file owner
     * @param idCurrent is id of the current user
     * @param ownerId is id ot the file owner
     * @return true = if emailCurrent == ownerEmail
     */
    boolean matchesOwner(Long idCurrent, Long ownerId);

    boolean isPermissionsAdminOrUserIsOwner(AccountEntity currentUser, AccountEntity owner, StorageElement foundFile);

    boolean checkPermissionsOnChangeByOrganization(AccountEntity currentUser, StorageElement foundFile);

    /**
     * create single directory by path
     * @param path of the which we will create directory
     * @return new File directory
     */
    File createSinglePath(String path);

    File createMultiplyPath(String path);

    void isMatchesOrganization(@NonNull String organizationName1, @NonNull String organizationName2) throws OrganizationIsNotMuchException;
}
