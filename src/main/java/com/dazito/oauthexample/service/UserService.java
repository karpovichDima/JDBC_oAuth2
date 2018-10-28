package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DeleteAccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import com.dazito.oauthexample.service.dto.response.DeletedUserDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
import com.dazito.oauthexample.utils.exception.*;
import lombok.NonNull;

import javax.xml.bind.ValidationException;
import java.util.Optional;

public interface UserService {

    /**
     * edit password of the current user
     * @param id is id user, whose password we edit
     * @param newPassword is new password
     * @param rawOldPassword is current password, unencrypted
     * @return EditedPasswordDto is successful password change response
     */
    EditedPasswordDto editPassword(Long id, String newPassword, String rawOldPassword) throws AppException;

    /**
     * edit name/email of the current user
     * @param personalData is case of the different properties user
     * @param id is ID of the user whose data the administrator wants to change
     * @return EditedEmailNameDto is successful edit of the edit
     */
    EditedEmailNameDto editPersonData(Long id, EditPersonalDataDto personalData) throws AppException;

    /**
     * We pass the user to understand whether he is in our database or not
     * @param accountDto is userDto which we will find in DB
     * @return EditedEmailNameDto is successful search result user
     */
    EditedEmailNameDto createUser(AccountDto accountDto) throws ValidationException, AppException;

    /**
     * delete user from DB, by id or DeleteAccountDto
     * @param accountDto is entity which we will find in DB and after that, delete
     * @param id is ID of the user the administrator wants to delete
     */
    DeletedUserDto deleteUser(Long id, DeleteAccountDto accountDto) throws AppException;

    /**
     * the method that sets a new password for the newly created user starts its
     * work only after the user follows the link in the letter
     * @param setPasswordDto is data to set a password
     */
    void messageReply(SetPasswordDto setPasswordDto);

    /**
     * we send the letter that the user forgot the password
     * @param setPasswordDto is password recovery data
     */
    void forgotPassword(SetPasswordDto setPasswordDto);

    /**
     * quick way to save an account
     * @param accountEntity is account that we want to keep
     */
    void saveAccount(AccountEntity accountEntity);

    /**
     * AccountEntity search by uuid
     * @param uuid is uuid by which we will to find AccountEntity
     * @return AccountEntity
     */
    AccountEntity findUserByUuid(String uuid);

    /**
     * save new password
//     * @param uuid is uuid by which we will to find AccountEntity
     * @return AccountEntity
     */
    EditedPasswordDto savePassword(String encodedPassword, AccountEntity accountToBeEdited);

    void saveEncodedPassword(String encodedPassword, AccountEntity accountToBeEdited);

    String passwordEncode(String newPassword);

    void isMatchesPassword(String rawOldPassword, String passwordCurrentUser) throws AppException;

    EditedPasswordDto convertToResponsePassword(String newPassword);

    void isMatchesEmail(@NonNull String emailCurrentUser, @NonNull String email) throws AppException;
    /**
     * find user by email
     * @param email is email which we will find in DB
     * @return AccountEntity is successful search result user
     */
    AccountEntity findUserByEmail(String email);

    boolean isEmpty(String val);

    /**
     * check optional on null
     * @param val is optinal, which we want to check on null
     * @return true = if not null, false = null
     */
    boolean isOptionalNotNull(Optional val);

    /**
     * find user
     * @param id the identifier of the user we are looking for
     * @return AccountEntity
     */
    AccountEntity findByIdAccountRepo(Long id) throws AppException;

    /**
     * check rights of the Admin
     * @param entity is entity, on which we want to check of the admin right
     * @return true = if current user hasRole(ADMIN), false = hasRole(NOT ADMIN)
     */
    void adminRightsCheck(AccountEntity entity) throws AppException;

    void isMatchesOrganization(String userOrganization, AccountEntity currentUser) throws AppException;

    String getOrganizationNameCurrentUser(AccountEntity currentUser);

    /**
     * returns the current authorized user
     * @return AccountEntity
     */
    AccountEntity getCurrentUser();

    /**
     * Adds an organization to AccountDto
     * @param foundedUser is user which we will convert to AccountDto
     * @return AccountDto
     */
    AccountDto addToAccountDtoOrganization(AccountEntity foundedUser);

    String getOrganizationNameByUser(AccountEntity accountEntity);

    Organization findOrganizationByName(String organizationName);

    EditedEmailNameDto responsePersonalDataDto(AccountEntity accountEntity, Content rootContent);

    ChangedActivateDto editActivate(AccountDto accountDto) throws AppException;

}
