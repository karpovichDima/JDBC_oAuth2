package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.config.oauth.UserDetailsConfig;
import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.dao.OrganizationRepo;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DeleteAccountDto;
import com.dazito.oauthexample.service.dto.request.DtoForEditingPersonalData;
import com.dazito.oauthexample.service.dto.request.OrganizationDto;
import com.dazito.oauthexample.service.dto.response.EmailNameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service(value = "userService")
public class UserServicesImpl implements UserService {

    private final AccountRepository accountRepository;
    private final OrganizationRepo organizationRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${root.path}")
    String root;

    @Autowired
    public UserServicesImpl(AccountRepository accountRepository, OrganizationRepo organizationRepo, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.organizationRepo = organizationRepo;
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

        if (!adminRightsCheck(getCurrentUser())) return null; // current user is not Admin;

        String organizationName = getOrganizationNameFoundedUser(foundedUser);
        if (!organizationMatch(organizationName)) return null; // organization current user and user from account dto is not match

        accountToBeEdited = getCurrentUser();
        boolean matches = passwordEncoder.matches(rawOldPassword, currentUser.getPassword());
        if (matches) {
            accountToBeEdited.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.saveAndFlush(accountToBeEdited);
            return responsePassword(accountToBeEdited.getPassword());
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

        if (findUserByEmail(newEmail) != null) {
            return null; // user with such email exist;
        }

        if (checkStringOnNull(personalData.getNewName())) {
            newName = personalData.getNewName();
        } else {
            newName = getCurrentUser().getUsername();
        }

        AccountEntity accountToBeEdited;

        if (id == null) {
            accountToBeEdited = getCurrentUser();
            accountToBeEdited.setEmail(newEmail);
            accountToBeEdited.setUsername(newName);

            accountRepository.saveAndFlush(accountToBeEdited);
            return responseDto(accountToBeEdited);
        }

        if (!adminRightsCheck(getCurrentUser())) return null; // current user is not Admin;

        AccountEntity foundedAccount = findById(id);
        String organizationName = getOrganizationNameFoundedUser(foundedAccount);

        if (!organizationMatch(organizationName)) return null; // organization current user and user from account dto is not match

        accountToBeEdited = getCurrentUser();
        accountToBeEdited.setUsername(newName);
        accountToBeEdited.setEmail(newEmail);

        accountRepository.saveAndFlush(accountToBeEdited);
        return responseDto(accountToBeEdited);
    }

    // get AccountEntity of the current user, documentation on it in UserService
    @Override
    public AccountEntity getCurrentUser() {
        Long id = ((UserDetailsConfig) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
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

    // converter, documentation on it in UserService
    @Override
    public AccountEntity converterAccountDtoToEntity(AccountDto accountDto) {
        return conversionService.convert(accountDto, AccountEntity.class);
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

    // Create a new user
    @Override
    public EmailNameDto createUser(AccountDto accountDto) {
        String email = accountDto.getEmail();
        String organizationName = accountDto.getOrganizationName();

        if (findUserByEmail(email) != null) {
            return null; // user with such email exist;
        }

        if (!adminRightsCheck(getCurrentUser())) {
            return null; // current user is not Admin;
        }

        if (!organizationMatch(organizationName)) {
            return null; // organization current user and user from account dto is not match
        }

        String password = accountDto.getPassword();
        String userName = accountDto.getUsername();
        Boolean isActivated = accountDto.getIsActivated();
        UserRole role = accountDto.getRole();

        AccountEntity newUser = new AccountEntity();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setUsername(userName);
        newUser.setIsActivated(isActivated);
        newUser.setRole(UserRole.ADMIN);
        newUser.setOrganization(findOrganizationByName(organizationName));
        newUser.setRootPath(root);
        accountRepository.saveAndFlush(newUser);

        return responseDto(newUser);
    }

    // Find user by email, documentation on it in UserService
    @Override
    public AccountEntity findUserByEmail(String email) {
        Optional<AccountEntity> foundedUser = accountRepository.findUserByEmail(email);
        if (checkOptionalOnNull(foundedUser)) {
            return foundedUser.get();
        }
        return null;
    }

    // Delete user by id or current user
    @Override
    public void deleteUser(Long id, DeleteAccountDto accountDto) {
        String email;
        String password;
        if(id == null){
            email = accountDto.getEmail();
            password = accountDto.getRawPassword();

            boolean checkEmail = getCurrentUser().getEmail().equals(email);
            if (!checkEmail) return; // email not matches

            String encodedPassword = getCurrentUser().getPassword();
            boolean checkPassword = passwordEncoder.matches(password, encodedPassword);
            if (!checkPassword) return; // password not matches

            AccountEntity account = findUserByEmail(email);
            accountRepository.delete(account);
        }

        if (!adminRightsCheck(getCurrentUser())) return; // current user is not Admin;

        AccountEntity foundedUser = findById(id);
        String organizationName = getOrganizationNameFoundedUser(foundedUser);
        if (!organizationMatch(organizationName)) return; // organization current user and user from account dto is not match

        accountRepository.delete(foundedUser);
    }

    // Check optional on null
    @Override
    public boolean checkOptionalOnNull(Optional val) {
        return val.isPresent();
    }

    // Check rights "ADMIN" to change personal data
    public boolean adminRightsCheck(AccountEntity entity) {
        UserRole role = entity.getRole();
        if (role == UserRole.ADMIN) {
            return true;
        } else {
            return false;
        }
    }

    // We check the organization of the administrator and the user for a match
    private boolean organizationMatch(String userOrganization) {
        String userCurrentOrganization = getOrganizationNameCurrentUser(getCurrentUser());
        if (userOrganization.equals(userCurrentOrganization)) {
            return true;
        }
        return false;
    }

    // get Organization name user from AccountEntity
    private String getOrganizationNameCurrentUser(AccountEntity accountEntity) {
        String organizationName = getCurrentUser().getOrganization().getOrganizationName();
        return organizationName;
    }

    // get Organization name user from AccountEntity
    private String getOrganizationNameFoundedUser(AccountEntity accountEntity) {
        String organizationName = accountEntity.getOrganization().getOrganizationName();
        return organizationName;
    }

    Organization findOrganizationByName(String organizationName) {
        Optional<Organization> foundedOrganization = organizationRepo.findByOrganizationName(organizationName);
        if (checkOptionalOnNull(foundedOrganization)) {
            return foundedOrganization.get();
        }
        return null;
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
        return ((UserDetailsConfig) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
    }
}