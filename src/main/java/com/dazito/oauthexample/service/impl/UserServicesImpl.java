package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.config.oauth.UserDetailsConfig;
import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.dao.OrganizationRepo;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.OAuth2Service;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DeleteAccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.request.OrganizationDto;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

@Service(value = "userService")
public class UserServicesImpl implements UserService {

    @Value("${root.path}")
    String root;
    @Value("${content.admin}")
    String contentName;
    @Resource(name = "conversionService")
    ConversionService conversionService;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrganizationRepo organizationRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private ContentService contentService;
    @Autowired
    private OAuth2Service oAuth2Service;

    // Change user password
    @Override
    public EditedPasswordDto editPassword(Long id, String newPassword, String rawOldPassword) {
        AccountEntity foundedUser = findByIdAccountRepo(id);
        if (!isEmpty(newPassword)) return null;
        if (!isEmpty(rawOldPassword)) return null;

        AccountEntity currentUser = getCurrentUser();
        String passwordCurrentUser = currentUser.getPassword();
        AccountEntity accountToBeEdited;
        String encodedPassword = passwordEncode(newPassword);
        boolean matches;

        if (id == null) {
            accountToBeEdited = currentUser;
            matches = checkMatches(rawOldPassword, passwordCurrentUser);
            return savePassword(matches, encodedPassword, accountToBeEdited);
        }

        if (!adminRightsCheck(getCurrentUser())) return null; // current user is not Admin;

        String organizationName = getOrganizationNameByUser(foundedUser);
        if (organizationMatch(organizationName, currentUser)) return null; // organization current user and user from account dto is not match

        accountToBeEdited = getCurrentUser();
        matches = checkMatches(rawOldPassword, passwordCurrentUser);
        return savePassword(matches, encodedPassword, accountToBeEdited);
    }

    // Change user email and name, documentation on it in UserService
    @Override
    public EditedEmailNameDto editPersonData(Long id, EditPersonalDataDto personalData) {

        String newName;
        String newEmail;

        AccountEntity currentUser = getCurrentUser();

        newEmail = personalData.getNewEmail();
        boolean checkedEmailOnNull = isEmpty(newEmail);
        if (!checkedEmailOnNull) newEmail = currentUser.getEmail();

        if (findUserByEmail(newEmail) != null) return null; // user with such email exist;

        newName = personalData.getNewName();
        boolean checkedNameOnNull = isEmpty(newName);
        if (!checkedNameOnNull) newName = currentUser.getUsername();

        AccountEntity accountWithNewEmail;
        if (id == null) {
            accountWithNewEmail = accountEditedSetEmailAndName(newEmail, newName, currentUser);
            accountRepository.saveAndFlush(accountWithNewEmail);
            return responseDto(accountWithNewEmail);
        }

        if (!adminRightsCheck(currentUser)) return null; // current user is not Admin;

        AccountEntity foundedAccount = findByIdAccountRepo(id);
        String organizationName = getOrganizationNameByUser(foundedAccount);

        if (!organizationMatch(organizationName, currentUser)) return null; // organization current user and user from account dto is not match

        accountWithNewEmail = accountEditedSetEmailAndName(newEmail, newName, currentUser);
        accountRepository.saveAndFlush(accountWithNewEmail);
        return responseDto(accountWithNewEmail);
    }

    // Create a new user
    @Override
    public EditedEmailNameDto createUser(AccountDto accountDto, boolean createPassword) throws ValidationException {
        AccountEntity currentUser = getCurrentUser();
        String email = accountDto.getEmail();
        String organizationName = accountDto.getOrganizationName();

        String password;
        String encodedPassword = null;

        if (findUserByEmail(email) != null) return null; // user with such email exist;
        if (createPassword)if (!adminRightsCheck(currentUser)) return null; // current user is not Admin, if create user with password
        if (!organizationMatch(organizationName, currentUser)) return null; // organization current user and user from account dto is not match

        if (createPassword){
            password = accountDto.getPassword();
            encodedPassword = passwordEncode(password);
        }
        String userName = accountDto.getUsername();
        boolean isActivated = accountDto.getIsActivated();
        UserRole role = accountDto.getRole();

        AccountEntity newUser = new AccountEntity();
        newUser.setEmail(email);

        Organization organization = findOrganizationByName(organizationName);

        if (createPassword) newUser.setPassword(encodedPassword);
        newUser.setUsername(userName);
        newUser.setIsActivated(isActivated);
        newUser.setRole(role);
        newUser.setOrganization(organization);

        Content rootContent = null;

        if (getCountStorageWithOwnerNullAndNotNullOrganization() < 1 || role.equals(UserRole.USER)) {
            rootContent = contentService.createContent(newUser);
        }

        newUser.setContent(rootContent);
        accountRepository.saveAndFlush(newUser);

        if (createPassword) oAuth2Service.sendEmail(newUser);

        return responseDto(newUser);
    }



    // Delete user by id or current user
    @Transactional
    @Override
    public AccountDto deleteUser(Long id, DeleteAccountDto accountDto) {
        String email;
        String password;
        Long idContent;
        AccountEntity currentUser = getCurrentUser();
        if (id == null) {
            email = accountDto.getEmail();
            password = accountDto.getRawPassword();

            boolean checkEmail = getCurrentUser().getEmail().equals(email);
            if (!checkEmail) return null;

            String encodedPassword = getCurrentUser().getPassword();
            boolean checkPassword = passwordEncoder.matches(password, encodedPassword);
            if (!checkPassword) return null;

            List<StorageElement> children = currentUser.getContent().getChildren();
            AccountEntity account = findUserByEmail(email);
            accountRepository.delete(account);
            if (account.getRole().equals(UserRole.USER)) contentService.delete(children);
        }

        if (!adminRightsCheck(getCurrentUser())) return null;

        AccountEntity foundedUser = findByIdAccountRepo(id);
        String organizationName = getOrganizationNameByUser(foundedUser);
        if (!organizationMatch(organizationName, currentUser)) return null;

        List<StorageElement> children = foundedUser.getContent().getChildren();
        UserRole role = foundedUser.getRole();
        accountRepository.delete(foundedUser);
        if (role.equals(UserRole.USER)) contentService.delete(children);

        AccountDto accountDtoResponse = new AccountDto();
        return accountDtoResponse;
    }

    @Override
    public EditedPasswordDto savePassword(boolean matches, String encodedPassword, AccountEntity accountToBeEdited) {
        if (matches) {
            saveEncodedPassword(encodedPassword, accountToBeEdited);
            return convertToResponsePassword(accountToBeEdited.getPassword());
        }
        return null;
    }

    @Override
    public void saveEncodedPassword(String encodedPassword, AccountEntity accountToBeEdited) {
        accountToBeEdited.setPassword(encodedPassword);
        accountRepository.saveAndFlush(accountToBeEdited);
    }

    @Override
    public String passwordEncode(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }

    @Override
    public boolean checkMatches(String rawOldPassword, String passwordCurrentUser) {
        return passwordEncoder.matches(rawOldPassword, passwordCurrentUser);
    }

    @Override
    public EditedPasswordDto convertToResponsePassword(String newPassword) {
        EditedPasswordDto editedPasswordDto = new EditedPasswordDto();
        editedPasswordDto.setPassword(newPassword);
        return editedPasswordDto;
    }

    @Override
    public AccountEntity accountEditedSetEmailAndName(String newEmail, String newName, AccountEntity accountToBeEdited) {
        accountToBeEdited.setEmail(newEmail);
        accountToBeEdited.setUsername(newName);
        return accountToBeEdited;
    }

    @Override
    public AccountEntity findUserByEmail(String email) {
        Optional<AccountEntity> foundedUser = accountRepository.findUserByEmail(email);
        if (isOptionalNotNull(foundedUser)) return foundedUser.get();
        return null;
    }

    @Override
    public boolean isEmpty(String val) {
        return val != null && !val.equals("");
    }

    @Override
    public boolean isOptionalNotNull(Optional val) {
        return val.isPresent();
    }


    @Override
    public AccountEntity findByIdAccountRepo(Long id) {
        if (id == null) return null;
        Optional<AccountEntity> foundByIdOptional = accountRepository.findById(id);
        boolean checkedOnNull = isOptionalNotNull(foundByIdOptional);
        if (checkedOnNull) return foundByIdOptional.get();
        return null;
    }

    @Override
    public boolean adminRightsCheck(AccountEntity entity) {
        UserRole role = entity.getRole();
        return role == UserRole.ADMIN;
    }

    @Override
    public boolean organizationMatch(String userOrganization, AccountEntity currentUser) {
        String userCurrentOrganization = getOrganizationNameCurrentUser(currentUser);
        return userOrganization.equals(userCurrentOrganization);
    }

    @Override
    public String getOrganizationNameCurrentUser(AccountEntity currentUser) {
        return currentUser.getOrganization().getOrganizationName();
    }

    @Override
    public AccountEntity getCurrentUser() {
        Long id = ((UserDetailsConfig) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
        Optional<AccountEntity> optionalById = accountRepository.findById(id);
        return optionalById.orElse(null);
    }

    @Override
    public AccountDto convertAccountToDto(AccountEntity accountEntity) {
        return conversionService.convert(accountEntity, AccountDto.class);
    }

    @Override
    public AccountEntity convertAccountToEntity(AccountDto accountDto) {
        return conversionService.convert(accountDto, AccountEntity.class);
    }

    @Override
    public AccountDto addToAccountDtoOrganization(AccountEntity foundedUser) {
        Organization organization = foundedUser.getOrganization();
        OrganizationDto convertedOrganization = conversionService.convert(organization, OrganizationDto.class);
        AccountDto accountDto = conversionService.convert(foundedUser, AccountDto.class);
        accountDto.setOrganizationName(convertedOrganization.getOrganizationName());
        return accountDto;
    }

    @Override
    public String getOrganizationNameByUser(AccountEntity accountEntity) {
        return accountEntity.getOrganization().getOrganizationName();
    }

    @Override
    public Organization findOrganizationByName(String organizationName) {
        Optional<Organization> foundedOrganization = organizationRepo.findByOrganizationName(organizationName);
        if (isOptionalNotNull(foundedOrganization)) return foundedOrganization.get();
        return null;
    }

    @Override
    public EditedEmailNameDto responseDto(AccountEntity accountEntity) {
        EditedEmailNameDto editedEmailNameDto = new EditedEmailNameDto();
        editedEmailNameDto.setUsername(accountEntity.getUsername());
        editedEmailNameDto.setEmail(accountEntity.getEmail());
        return editedEmailNameDto;
    }

    @Override
    public ChangedActivateDto editActivate(AccountDto accountDto) {
        AccountEntity currentUser = getCurrentUser();
        String organizationName = currentUser.getOrganization().getOrganizationName();

        Boolean isActivated = accountDto.getIsActivated();
        Long id = accountDto.getId();

        if (!adminRightsCheck(currentUser)) return null; // current user is not Admin;

        AccountEntity account = findByIdAccountRepo(id);
        if (!organizationMatch(organizationName, account)) return null; // organization current user and user from account dto is not match

        account.setIsActivated(isActivated);
        accountRepository.saveAndFlush(account);

        if (!isActivated) {
            oAuth2Service.deleteToken(account);
        }

        ChangedActivateDto changedActivateDto = new ChangedActivateDto();
        changedActivateDto.setId(id);
        changedActivateDto.setIsActivated(isActivated);
        return changedActivateDto;
    }



    public Long getCountStorageWithOwnerNullAndNotNullOrganization(){
        return storageRepository.countStorageElementByOwnerIsNullAndOrganizationIsNotNull();
    }

}