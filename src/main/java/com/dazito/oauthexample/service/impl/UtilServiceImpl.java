package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class UtilServiceImpl implements UtilService {

    private final UserService userService;

    @Autowired
    public UtilServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean matchesOrganizations(Organization organizationUser, Organization organizationStorage) {
        String organizationNameUser = organizationUser.getOrganizationName();
        String organizationNameFile = organizationStorage.getOrganizationName();
        return organizationNameUser.equals(organizationNameFile);
    }

    @Override
    public boolean matchesOwner(Long idCurrent, Long ownerId) {
        return Objects.equals(idCurrent, ownerId);
    }

    @Override
    public boolean isPermissionsAdminOrUserIsOwner(AccountEntity currentUser, AccountEntity owner, StorageElement foundFile) {
        UserRole role = currentUser.getRole();
        Long idUser = currentUser.getId();
        boolean checkedOnTheAdmin = userService.adminRightsCheck(currentUser);
        if (!checkedOnTheAdmin) {
            boolean checkedMatchesOwner = matchesOwner(idUser, owner.getId());
            if (!checkedMatchesOwner) return false;
        }
        if (role.equals(UserRole.ADMIN) && owner == null){
            return true;
        }
        return true;
    }

    @Override
    public boolean checkPermissionsOnChangeByOrganization(AccountEntity currentUser, StorageElement foundFile) {
        Organization organizationUser = currentUser.getOrganization();
        Organization organizationFile = foundFile.getOrganization();
        boolean checkedMatchesOrganization = matchesOrganizations(organizationUser, organizationFile);
        if (!checkedMatchesOrganization) return false;
        return true;
    }

    @Override
    public File createSinglePath(String path) {
        File rootPath = new File(path);
        if (!rootPath.exists()) {
            if (rootPath.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        return rootPath;
    }

    @Override
    public File createMultiplyPath(String path) {
        File rootPath2 = new File(path + "\\Directory\\Sub\\Sub-Sub");
        if (!rootPath2.exists()) {
            if (rootPath2.mkdirs()) {
                System.out.println("Multiple directories are created!");
            } else {
                System.out.println("Failed to create multiple directories!");
            }
        }
        return rootPath2;
    }
}
