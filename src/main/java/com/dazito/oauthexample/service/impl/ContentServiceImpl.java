package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ContentRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import com.dazito.oauthexample.service.dto.response.DirectoryDeletedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    @Value("${root.path}")
    Path root;
    @Resource(name = "conversionService")
    ConversionService conversionService;

    @Autowired
    private UserService userService;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private UtilService utilService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DirectoryService directoryService;


    // create root for all directories and files(for Admins) or for one User
    @Override
    public Content createContent(AccountEntity newUser) {

        UserRole role = newUser.getRole();
        String nameNewFolder = newUser.getEmail();
        Organization organization = newUser.getOrganization();

        Content content = new Content();

        switch (role) {
            case USER:
                content.setName("Content " + newUser.getEmail());
                utilService.createSinglePath(root + File.separator + nameNewFolder);
                content.setRoot(root + File.separator + nameNewFolder);
                break;
            case ADMIN:
                content.setName("CONTENT_" + organization.getOrganizationName());
                content.setRoot(root.toString());
                break;
        }
        content.setParent(null);
        content.setSize(0L);
        content.setOrganization(organization);

        return content;
    }

    @Override
    public ContentUpdatedDto updateContent(ContentUpdateDto contentDto) {
        AccountEntity currentUser = userService.getCurrentUser();

        Long id = contentDto.getId();
        String name = contentDto.getNewName();
        String root = contentDto.getNewRoot();

        StorageElement foundContent = storageService.findById(id);
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
        return utilService.matchesOrganizations(organizationUser, organizationContent);
    }

    @Override
    public void deleteContent(Long id) {
        AccountEntity currentUser = userService.getCurrentUser();
        StorageElement foundStorage = storageService.findById(id);

        boolean canChange = filePermissionsCheck(currentUser, foundStorage);
        if (!canChange) return;

        storageRepository.delete(id);
    }

    @Override
    public Content findById(Long id) {
        Optional<Content> foundContent = contentRepository.findById(id);
        return foundContent.orElse(null);
    }

    @Override
    public Content findByName(String name) {
        Optional<Content> foundContent = contentRepository.findByName(name);
        return foundContent.orElse(null);
    }

    @Override
    public Content findContentForAdmin(String organizationName) {
        Optional<Content> foundOptional = contentRepository.findContentByOwnerIsNullAndOrganization(organizationName);
        return foundOptional.orElse(null);
    }

    @Override
    public void delete(List<StorageElement> children) {
        if(children.size() == 0) return;
        List<StorageElement> listChildToDelete = new ArrayList<>();
        listChildToDelete.addAll(children);
        directoryService.deleteChildFiles(listChildToDelete, children);
        storageRepository.delete(listChildToDelete);
    }
}
