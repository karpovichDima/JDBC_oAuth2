package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
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
     * find user
     * @param id the identifier of the user we are looking for
     * @return AccountEntity
     */
    AccountEntity findById(Long id);

    /**
     * returns the current authorized user
     * @return AccountEntity
     */
    AccountEntity getCurrentUser();

    /**
     * converting AccountEntity to AccountDto
     * @return AccountDto
     */
    AccountDto converterAccountEntityToDto(AccountEntity accountEntity);

    /**
     * converting AccountDto to AccountEntity
     * @param accountDto is user which we will convert to AccountEntity
     * @return AccountEntity
     */
    AccountEntity converterAccountDtoToEntity(AccountDto accountDto);

    /**
     * Adds an organization to AccountDto
     * @param foundedUser is user which we will convert to AccountDto
     * @return AccountDto
     */
    AccountDto addToAccountDtoOrganization(AccountEntity foundedUser);

    /**
     * We pass the user to understand whether he is in our database or not
     * @param accountDto is userDto which we will find in DB
     * @return EmailNameDto is successful search result user
     */
    EmailNameDto createUser(AccountDto accountDto);

    /**
     * find user by email
     * @param email is email which we will find in DB
     * @return AccountEntity is successful search result user
     */
    AccountEntity findUserByEmail(String email);

    /**
     * delete user from DB, by id or DeleteAccountDto
     * @param accountDto is entity which we will find in DB and after that, delete
     */
    void deleteUser(Long id, DeleteAccountDto accountDto);

    boolean checkOptionalOnNull(Optional val);

    boolean adminRightsCheck(AccountEntity entity);
}
