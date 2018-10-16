package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
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
    EditedPasswordDto editPassword(Long id, String newPassword, String rawOldPassword) throws EmptyFieldException, CurrentUserIsNotAdminException, OrganizationIsNotMuchException, PasswordNotMatchesException;

    /**
     * edit name/email of the current user
     * @param personalData is case of the different properties user
     * @return EditedEmailNameDto is successful edit of the edit
     */
    EditedEmailNameDto editPersonData(Long id, EditPersonalDataDto personalData) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException, UserWithSuchEmailExistException;

    /**
     * We pass the user to understand whether he is in our database or not
     * @param accountDto is userDto which we will find in DB
     * @return EditedEmailNameDto is successful search result user
     */
    EditedEmailNameDto createUser(AccountDto accountDto) throws ValidationException, OrganizationIsNotMuchException, CurrentUserIsNotAdminException;

    /**
     * delete user from DB, by id or DeleteAccountDto
     * @param accountDto is entity which we will find in DB and after that, delete
     */
    DeletedUserDto deleteUser(Long id, DeleteAccountDto accountDto) throws EmailIsNotMatchesException, PasswordNotMatchesException, CurrentUserIsNotAdminException, OrganizationIsNotMuchException;

    void messageReply(SetPasswordDto setPasswordDto);

    void forgotPassword(SetPasswordDto setPasswordDto);

    void saveAccount(AccountEntity accountEntity);

    AccountEntity findUserByUuid(String uuid);

    EditedPasswordDto savePassword(String encodedPassword, AccountEntity accountToBeEdited);

    void saveEncodedPassword(String encodedPassword, AccountEntity accountToBeEdited);

    String passwordEncode(String newPassword);

    void isMatchesPassword(String rawOldPassword, String passwordCurrentUser) throws PasswordNotMatchesException;

    EditedPasswordDto convertToResponsePassword(String newPassword);

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
    AccountEntity findByIdAccountRepo(Long id);

    /**
     * check rights of the Admin
     * @param entity is entity, on which we want to check of the admin right
     * @return true = if current user hasRole(ADMIN), false = hasRole(NOT ADMIN)
     */
    void adminRightsCheck(AccountEntity entity) throws CurrentUserIsNotAdminException;

    void isMatchesOrganization(String userOrganization, AccountEntity currentUser) throws OrganizationIsNotMuchException;

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

    EditedEmailNameDto responsePersonalDataDto(AccountEntity accountEntity);

    ChangedActivateDto editActivate(AccountDto accountDto) throws OrganizationIsNotMuchException, CurrentUserIsNotAdminException;










}
