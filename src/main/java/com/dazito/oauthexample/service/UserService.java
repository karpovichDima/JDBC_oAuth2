package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DtoForEditingPersonalData;
import com.dazito.oauthexample.service.dto.request.OrganizationDto;
import com.dazito.oauthexample.service.dto.response.EmailNameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * edit password of the current user
     * @param id is id user, whose password we edit
     * @param newPassword
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
     * edit name/email of the current user
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
     * Adds an organization to AccountDto
     * @param foundedUser is user which we will convert to AccountDto
     * @return AccountDto
     */
    AccountDto addToAccountDtoOrganization(AccountEntity foundedUser);

}
