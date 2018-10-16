package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import com.dazito.oauthexample.utils.exception.CurrentUserIsNotAdminException;
import com.dazito.oauthexample.utils.exception.OrganizationIsNotMuchException;

import java.util.List;

public interface ContentService {

    /**
     * create root point for all user directories
     * @param newUser the user for which we will create the root point
     * @return Content is root point object
     */
    Content createContent(AccountEntity newUser);

    ContentUpdatedDto updateContent(ContentUpdateDto contentDto) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException;

    void filePermissionsCheck(AccountEntity currentUser, StorageElement foundContent) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException;

    void deleteContent(Long id) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException;

    Content findById(Long id);

    Content findByName(String name);

    Content findContentForAdmin(String organizationName);

    void delete(List<StorageElement> children);

}
