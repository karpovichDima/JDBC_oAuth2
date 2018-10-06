package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DeleteAccountDto;
import com.dazito.oauthexample.service.dto.request.DtoForEditingPersonalData;
import com.dazito.oauthexample.service.dto.request.OrganizationDto;
import com.dazito.oauthexample.service.dto.response.EmailNameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {

    /**
     * edit password of the current user
     * @param id is id user, whose password we edit
     * @param newPassword is new password
     * @param rawOldPassword is current password, unencrypted
     * @return PasswordDto is successful password change response
     */
    PasswordDto editPassword(Long id, String newPassword, String rawOldPassword);

    /**
     * edit name/email of the current user
     * @param personalData is case of the different properties user
     * @return EmailNameDto is successful edit of the edit
     */
    EmailNameDto editPersonData(Long id, DtoForEditingPersonalData personalData);

    /**
     * We pass the user to understand whether he is in our database or not
     * @param accountDto is userDto which we will find in DB
     * @return EmailNameDto is successful search result user
     */
    EmailNameDto createUser(AccountDto accountDto);

    /**
     * delete user from DB, by id or DeleteAccountDto
     * @param accountDto is entity which we will find in DB and after that, delete
     */
    void deleteUser(Long id, DeleteAccountDto accountDto);

    PasswordDto ifMatchesTrueSetPasswordAndReturn(boolean matches, String encodedPassword, AccountEntity accountToBeEdited);

    void setAndSaveEncodedPassword(String encodedPassword, AccountEntity accountToBeEdited);

    String passwordEncode(String newPassword);

    boolean checkMatches(String rawOldPassword, String passwordCurrentUser);

    PasswordDto responsePassword(String newPassword);

    AccountEntity accountEditedSetEmailAndName(String newEmail, String newName, AccountEntity accountToBeEdited);

    /**
     * find user by email
     * @param email is email which we will find in DB
     * @return AccountEntity is successful search result user
     */
    AccountEntity findUserByEmail(String email);

    boolean checkStringOnNull(String val);

    /**
     * check optional on null
     * @param val is optinal, which we want to check on null
     * @return true = if not null, false = null
     */
    boolean checkOptionalOnNull(Optional val);

    /**
     * find user
     * @param id the identifier of the user we are looking for
     * @return AccountEntity
     */
    AccountEntity findByIdAccountRepo(Long id);

    /**
     * check rights of the Admin
     * @param entity is entity, on which we want to check of the admin right
     * @return true = if current user hasRole(ADMIN), false = hasRole(NOT ADMIN)
     */
    boolean adminRightsCheck(AccountEntity entity);

    boolean organizationMatch(String userOrganization, AccountEntity currentUser);

    String getOrganizationNameCurrentUser(AccountEntity currentUser);

    /**
     * returns the current authorized user
     * @return AccountEntity
     */
    AccountEntity getCurrentUser();

    /**
     * converting AccountEntity to AccountDto
     * @return AccountDto
     */
    AccountDto convertAccountToDto(AccountEntity accountEntity);

    /**
     * converting AccountDto to AccountEntity
     * @param accountDto is user which we will convert to AccountEntity
     * @return AccountEntity
     */
    AccountEntity convertAccountToEntity(AccountDto accountDto);

    /**
     * Adds an organization to AccountDto
     * @param foundedUser is user which we will convert to AccountDto
     * @return AccountDto
     */
    AccountDto addToAccountDtoOrganization(AccountEntity foundedUser);

    String getOrganizationNameByUser(AccountEntity accountEntity);

    Organization findOrganizationByName(String organizationName);

    EmailNameDto responseDto(AccountEntity accountEntity);

    Long getCountUsersWithContent();










}
