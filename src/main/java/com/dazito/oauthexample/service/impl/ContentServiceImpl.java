package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.DirectoryService;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    FileService fileService;

    @Autowired
    UserService userService;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    DirectoryService directoryService;

    @Resource(name = "conversionService")
    ConversionService conversionService;

    @Override
    public ContentUpdatedDto updateContent(ContentUpdateDto contentDto) {
        AccountEntity currentUser = userService.getCurrentUser();

        Long id = contentDto.getId();
        String name = contentDto.getNewName();
        String root = contentDto.getNewRoot();

        StorageElement foundContent = fileService.findByIdInStorageRepo(id);
        if (foundContent == null) return null;
        Content content = (Content) foundContent;

        boolean canChange = filePermissionsCheck(currentUser, foundContent);
        if (!canChange) return null;

        content.setName(name);
        content.setRoot(root);

        storageRepository.saveAndFlush(content);

        ContentUpdatedDto contentUpdatedDto = new ContentUpdatedDto();
        contentUpdatedDto.setId(id);
        contentUpdatedDto.setNewName(name);
        contentUpdatedDto.setNewRoot(root);


        return contentUpdatedDto;
    }

    @Override
    public boolean filePermissionsCheck(AccountEntity currentUser, StorageElement foundContent) {
        boolean checkedOnTheAdmin = userService.adminRightsCheck(currentUser);
        if (!checkedOnTheAdmin) return false;
        Organization organizationUser = currentUser.getOrganization();
        Organization organizationContent = foundContent.getOrganization();

        boolean checkedMatchesOrganization = fileService.matchesOrganizations(organizationUser, organizationContent);
        if (!checkedMatchesOrganization) return false;
        return true;
    }

    @Override
    public void deleteContent(Long id) {
        AccountEntity currentUser = userService.getCurrentUser();
        StorageElement foundStorage = fileService.findByIdInStorageRepo(id);

        boolean canChange = filePermissionsCheck(currentUser, foundStorage);
        if (!canChange) return;

        storageRepository.delete(id);
    }

}
