package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.config.oauth.CustomUserDetails;
import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DtoForEditingPersonalData;
import com.dazito.oauthexample.service.dto.request.OrganizationDto;
import com.dazito.oauthexample.service.dto.response.EmailNameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class UserServicesImpl implements UserService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServicesImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Resource(name = "conversionService")
    ConversionService conversionService;

    // Change user password, documentation on it in UserService
    @Override
    public PasswordDto editPassword(Long id, String newPassword, String rawOldPassword) {
        AccountEntity foundedUser = findById(id);
        if (!checkStringOnNull(newPassword)) return null;
        if (!checkStringOnNull(rawOldPassword)) return null;

        AccountEntity currentUser = getCurrentUser();
        AccountEntity accountToBeEdited;
        if (id == null) {
            accountToBeEdited = currentUser;
            boolean matches = passwordEncoder.matches(rawOldPassword, currentUser.getPassword());
            if (matches) {
                accountToBeEdited.setPassword(passwordEncoder.encode(newPassword));
                accountRepository.saveAndFlush(accountToBeEdited);
                return responsePassword(accountToBeEdited.getPassword());
            }
            return null;
        }
        accountToBeEdited = getCurrentUser();
        if (organizationMatch(foundedUser)) {
            if (adminRightsCheck(getCurrentUser())) {
                boolean matches = passwordEncoder.matches(rawOldPassword, currentUser.getPassword());
                if (matches) {
                    accountToBeEdited.setPassword(passwordEncoder.encode(newPassword));
                    accountRepository.saveAndFlush(accountToBeEdited);
                    return responsePassword(accountToBeEdited.getPassword());
                }
            }
        }
        return null;
    }

    // Change user email and name, documentation on it in UserService
    @Override
    public EmailNameDto editPersonData(Long id, DtoForEditingPersonalData personalData) {
        String newName;
        String newEmail;

        if (checkStringOnNull(personalData.getNewEmail())) {
            newEmail = personalData.getNewEmail();
        } else {
            newEmail = getCurrentUser().getEmail();
        }

        if (checkStringOnNull(personalData.getNewName())) {
            newName = personalData.getNewName();
        } else {
            newName = getCurrentUser().getUsername();
        }

        AccountEntity currentUser = getCurrentUser();
        AccountEntity accountToBeEdited;
        if (id == null) {
            accountToBeEdited = currentUser;
            accountToBeEdited.setEmail(newEmail);
            accountToBeEdited.setUsername(newName);

            accountRepository.saveAndFlush(accountToBeEdited);
            return responseDto(accountToBeEdited);
        } else {
            AccountEntity fundedAccount = findById(id);
            if (!organizationMatch(fundedAccount)) {
                return null;
            }
            if (adminRightsCheck(currentUser)) {
                accountToBeEdited = currentUser;
                accountToBeEdited.setUsername(newName);
                accountToBeEdited.setEmail(newEmail);

                accountRepository.saveAndFlush(accountToBeEdited);
                return responseDto(accountToBeEdited);
            }
        }
        return null;
    }

    // get AccountEntity of the current user, documentation on it in UserService
    @Override
    public AccountEntity getCurrentUser() {
        Long id = ((CustomUserDetails) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
        Optional<AccountEntity> optionalById = accountRepository.findById(id);
        if (optionalById.isPresent()) {
            return optionalById.get();
        }
        return null;
    }

    // get AccountEntity by id, documentation on it in UserService
    @Override
    public AccountEntity findById(Long id) {
        if (id == null) return null;
        if (accountRepository.findById(id).isPresent()) {
            return accountRepository.findById(id).get();
        }
        return null;
    }

    // converter, documentation on it in UserService
    @Override
    public AccountDto converterAccountEntityToDto(AccountEntity accountEntity) {
        return conversionService.convert(accountEntity, AccountDto.class);
    }

    // Add information about the organization in UserDto, documentation on it in UserService
    @Override
    public AccountDto addToAccountDtoOrganization(AccountEntity foundedUser) {
        Organization organization = foundedUser.getOrganization();
        OrganizationDto convertedOrganization = conversionService.convert(organization, OrganizationDto.class);
        AccountDto accountDto = conversionService.convert(foundedUser, AccountDto.class);
        accountDto.setOrganizationName(convertedOrganization.getOrganizationName());
        return accountDto;
    }

    // Check rights "ADMIN" to change personal data
    private boolean adminRightsCheck(AccountEntity entity) {
        String role = entity.getRole();
        if (role.equals("ADMIN")) {
            return true;
        } else {
            return false;
        }
    }

    // We check the organization of the administrator and the user for a match
    private boolean organizationMatch(AccountEntity foundedAccount) {
        String userOrganization = foundedAccount.getOrganization().getOrganizationName();
        String userCurrentOrganization = getCurrentUser().getOrganization().getOrganizationName();
        if (userOrganization.equals(userCurrentOrganization)) {
            return true;
        }
        return false;
    }

    // Check strings on null
    private boolean checkStringOnNull(String val) {
        if (val != null && !val.equals("")) {
            return true;
        }
        return false;
    }

    // Reply to the user when changing personal data
    private EmailNameDto responseDto(AccountEntity accountEntity) {
        EmailNameDto emailNameDto = new EmailNameDto();
        emailNameDto.setUsername(accountEntity.getUsername());
        emailNameDto.setEmail(accountEntity.getEmail());
        return emailNameDto;
    }

    // Reply to the user when changing password
    private PasswordDto responsePassword(String newPassword) {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setPassword(newPassword);
        return passwordDto;
    }

    // get id of the current user
    private Long findOutIdUser() {
        return ((CustomUserDetails) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
    }
}