package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import com.dazito.oauthexample.utils.exception.AppException;

import java.util.List;

public interface ContentService {

    /**
     * create root point for all user directories
     * @param newUser the user for which we will create the root point
     * @return Content is root point object
     */
    Content createContent(AccountEntity newUser);

    /**
     * Transfer content from one parent to another. Name change.
     * @param contentDto the the entity from which we take data to change
     * @return ContentUpdatedDto is response, about successful operation
     */
    ContentUpdatedDto updateContent(ContentUpdateDto contentDto) throws AppException;

    /**
     * checking the rights of the current user, to change the storage, by organization
     * @param currentUser the user from whom we take the organization
     * @param foundContent the content from whom we take the organization
     */
    void filePermissionsCheck(AccountEntity currentUser, StorageElement foundContent) throws AppException;

    /**
     * delete root directory by id
     * @param id is id by which we will to delete content
     */
    void deleteContent(Long id) throws AppException;

    /**
     * content search by id
     * @param id is id by which we will to find content
     * @return Content is root point object
     */
    Content findById(Long id);

    /**
     * content search by name
     * @param name is name by which we will to find content
     * @return Content is root point object
     */
    Content findByName(String name);

    /**
     * search for content that belongs to all administrators
     * @param organization is organizationName by which we will to find content
     * @return Content is root point object
     */
    Content findContentForAdmin(Organization organization);

    /**
     * remove all children from content
     * @param children this is a list of children content
     */
    void delete(List<StorageElement> children);

    Content findContentByUser(AccountEntity user) throws AppException;

    void saveContent(Content newContent) throws AppException;
}
