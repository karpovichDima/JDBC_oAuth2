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
import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.OAuth2Service;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.*;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import com.dazito.oauthexample.service.dto.response.DeletedUserDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
import com.dazito.oauthexample.utils.exception.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
    @Autowired
    MailService mailService;

    @Override
    public EditedPasswordDto editPassword(Long id, String newPassword, String rawOldPassword) throws EmptyFieldException, CurrentUserIsNotAdminException, OrganizationIsNotMuchException, PasswordNotMatchesException {
        AccountEntity foundedUser = findByIdAccountRepo(id);
        if (!isEmpty(newPassword) || !isEmpty(rawOldPassword)) throw new EmptyFieldException("Field is empty");

        AccountEntity currentUser = getCurrentUser();
        String passwordCurrentUser = currentUser.getPassword();
        AccountEntity accountToBeEdited;
        String encodedPassword = passwordEncode(newPassword);
        if (id == null) {
            accountToBeEdited = currentUser;
            isMatchesPassword(rawOldPassword, passwordCurrentUser);
            return savePassword(encodedPassword, accountToBeEdited);
        }
        adminRightsCheck(getCurrentUser());
        String organizationName = getOrganizationNameByUser(foundedUser);
        isMatchesOrganization(organizationName, currentUser);
        accountToBeEdited = getCurrentUser();
        isMatchesPassword(rawOldPassword, passwordCurrentUser);
        return savePassword(encodedPassword, accountToBeEdited);
    }

    @Override
    public EditedEmailNameDto editPersonData(Long id, @NonNull EditPersonalDataDto personalData) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException, UserWithSuchEmailExistException {
        AccountEntity currentUser = getCurrentUser();
        String newEmail = personalData.getNewEmail();
        if (findUserByEmail(newEmail) != null)
            throw new UserWithSuchEmailExistException("User with such email exist.");
        String newName = personalData.getNewName();
        if (id == null) {
            currentUser.setEmail(newEmail);
            currentUser.setUsername(newName);
            accountRepository.saveAndFlush(currentUser);
            return responsePersonalDataDto(currentUser);
        }
        adminRightsCheck(currentUser);
        AccountEntity foundedAccount = findByIdAccountRepo(id);
        String organizationName = getOrganizationNameByUser(foundedAccount);

        isMatchesOrganization(organizationName, currentUser);
        foundedAccount.setEmail(newEmail);
        foundedAccount.setUsername(newName);
        accountRepository.saveAndFlush(foundedAccount);
        return responsePersonalDataDto(foundedAccount);
    }

    @Transactional
    @Override
    public DeletedUserDto deleteUser(Long id, DeleteAccountDto accountDto) throws EmailIsNotMatchesException, PasswordNotMatchesException, CurrentUserIsNotAdminException, OrganizationIsNotMuchException {
        AccountEntity currentUser = getCurrentUser();
        if (id == null) {
            String email = accountDto.getEmail();
            String password = accountDto.getRawPassword();
            isMatchesEmail(currentUser.getEmail(), email);
            String encodedPassword = getCurrentUser().getPassword();
            isMatchesPassword(password, encodedPassword);

            List<StorageElement> children = currentUser.getContent().getChildren();
            AccountEntity account = findUserByEmail(email);
            accountRepository.delete(account);
            if (account.getRole().equals(UserRole.USER)) contentService.delete(children);
        }
        adminRightsCheck(getCurrentUser());
        AccountEntity foundedUser = findByIdAccountRepo(id);
        String organizationName = getOrganizationNameByUser(foundedUser);
        isMatchesOrganization(organizationName, currentUser);

        UserRole role = foundedUser.getRole();
        if (role == UserRole.USER) {
            List<StorageElement> children = foundedUser.getContent().getChildren();
            accountRepository.delete(foundedUser);
            contentService.delete(children);
        } else {
            accountRepository.delete(foundedUser);
        }
        DeletedUserDto userDto = new DeletedUserDto();
        userDto.setMessage("User is deleted");
        return userDto;
    }

    @Override
    public EditedEmailNameDto createUser(AccountDto accountDto) throws ValidationException, OrganizationIsNotMuchException, CurrentUserIsNotAdminException, UserWithSuchEmailExistException {
        AccountEntity currentUser = getCurrentUser();
        String email = accountDto.getEmail();
        String organizationName = accountDto.getOrganizationName();
        if (findUserByEmail(email) != null){
            throw new UserWithSuchEmailExistException("User with such email exist.");
        }
        adminRightsCheck(currentUser);
        isMatchesOrganization(organizationName, currentUser);
        String password = accountDto.getPassword();
        String encodedPassword = passwordEncode(password);

        String userName = accountDto.getUsername();
        boolean isActivated = accountDto.getIsActivated();
        UserRole role = accountDto.getRole();
        AccountEntity newUser = new AccountEntity();
        newUser.setEmail(email);
        Organization organization = findOrganizationByName(organizationName);
        if (currentUser != null) newUser.setPassword(encodedPassword);
        newUser.setUsername(userName);
        newUser.setIsActivated(isActivated);
        newUser.setRole(role);
        newUser.setOrganization(organization);

        Content rootContent = null;
        if (getCountStorageWithOwnerNullAndNotNullOrganization() < 1 || role.equals(UserRole.USER)) {
            rootContent = contentService.createContent(newUser);
        }
        newUser.setContent(rootContent);
        String uuid = UUID.randomUUID() + "";
        Date date = new Date();
        LocalDateTime utc = LocalDateTime.from(date.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1);
        Timestamp timestamp = Timestamp.valueOf(utc);
        newUser.setUuid(uuid);
        newUser.setTokenEndDate(timestamp);
        accountRepository.saveAndFlush(newUser);
        mailService.emailPreparation(newUser);
        return responsePersonalDataDto(newUser);
    }

    @Override
    public void messageReply(SetPasswordDto setPasswordDto) {
        String uuid = setPasswordDto.getUuid();
        String password = setPasswordDto.getPassword();

        AccountEntity foundUser = findUserByUuid(uuid);
        if (foundUser == null) return;

        Date date = new Date();
        LocalDateTime utc = LocalDateTime.from(date.toInstant().atZone(ZoneId.of("UTC")));
        Timestamp timestamp = Timestamp.valueOf(utc);
        long timeResponse = timestamp.getTime();

        Timestamp tokenEndDate = foundUser.getTokenEndDate();
        long timeFromDB = tokenEndDate.getTime();

        if (timeResponse > timeFromDB) return;

        String encodedPassword = passwordEncode(password);

        foundUser.setPassword(encodedPassword);

        accountRepository.saveAndFlush(foundUser);
    }

    public void forgotPassword(SetPasswordDto setPasswordDto) {
        AccountEntity foundUser = findUserByUuid(setPasswordDto.getUuid());
        if (foundUser == null) return;
        String encodedPassword = passwordEncode(setPasswordDto.getPassword());
        foundUser.setPassword(encodedPassword);
        accountRepository.saveAndFlush(foundUser);
    }


    private void isMatchesEmail(@NonNull String emailCurrentUser, @NonNull String email) throws EmailIsNotMatchesException {
        boolean equals = emailCurrentUser.equals(email);
        if (!equals) throw new EmailIsNotMatchesException("Current user's email does not match mail from dto.");
    }

    @Override
    public EditedPasswordDto savePassword(String encodedPassword, AccountEntity accountToBeEdited) {
        saveEncodedPassword(encodedPassword, accountToBeEdited);
        return convertToResponsePassword(accountToBeEdited.getPassword());
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
    public void isMatchesPassword(String rawOldPassword, String passwordCurrentUser) throws PasswordNotMatchesException {
        boolean matches = passwordEncoder.matches(rawOldPassword, passwordCurrentUser);
        if (!matches) throw new PasswordNotMatchesException("Passwords not matches.");
    }

    @Override
    public EditedPasswordDto convertToResponsePassword(String newPassword) {
        EditedPasswordDto editedPasswordDto = new EditedPasswordDto();
        editedPasswordDto.setPassword(newPassword);
        return editedPasswordDto;
    }

    @Override
    public AccountEntity findUserByEmail(String email) {
        Optional<AccountEntity> foundedUser = accountRepository.findUserByEmail(email);
        if (isOptionalNotNull(foundedUser)) return foundedUser.get();
        return null;
    }

    @Override
    public AccountEntity findUserByUuid(String uuid) throws NoSuchElementException {
        return accountRepository.findUserByUuid(uuid).get();
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
    public AccountEntity findByIdAccountRepo(@NonNull Long id) throws NoSuchElementException {
        return accountRepository.findById(id).get();
    }

    @Override
    public void adminRightsCheck(AccountEntity entity) throws CurrentUserIsNotAdminException {
        UserRole role = entity.getRole();
        if (role != UserRole.ADMIN)
            throw new CurrentUserIsNotAdminException("Authorized user is not an administrator.");
    }

    @Override
    public void isMatchesOrganization(@NonNull String userOrganization, @NonNull AccountEntity currentUser) throws OrganizationIsNotMuchException {
        String userCurrentOrganization = getOrganizationNameCurrentUser(currentUser);
        boolean equals = userOrganization.equals(userCurrentOrganization);
        if (!equals)
            throw new OrganizationIsNotMuchException("Organization current user and user from account dto is not match.");
    }

    @Override
    public String getOrganizationNameCurrentUser(AccountEntity currentUser) {
        return currentUser.getOrganization().getOrganizationName();
    }

    @Override
    public AccountEntity getCurrentUser() throws NoSuchElementException {
        Object anonymous = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (anonymous.equals("anonymousUser")) return null; // current user == null, only without authorization
        Long id = ((UserDetailsConfig) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
        return accountRepository.findById(id).get();
    }

    @Override
    public AccountDto addToAccountDtoOrganization(@NonNull AccountEntity foundedUser) throws NoSuchElementException {
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
    public Organization findOrganizationByName(String organizationName) throws NoSuchElementException {
        return organizationRepo.findByOrganizationName(organizationName).get();
    }

    @Override
    public EditedEmailNameDto responsePersonalDataDto(AccountEntity accountEntity) {
        EditedEmailNameDto editedEmailNameDto = new EditedEmailNameDto();
        editedEmailNameDto.setUsername(accountEntity.getUsername());
        editedEmailNameDto.setEmail(accountEntity.getEmail());
        return editedEmailNameDto;
    }

    @Override
    public ChangedActivateDto editActivate(AccountDto accountDto) throws OrganizationIsNotMuchException, CurrentUserIsNotAdminException {
        AccountEntity currentUser = getCurrentUser();
        String organizationName = currentUser.getOrganization().getOrganizationName();

        Boolean isActivated = accountDto.getIsActivated();
        Long id = accountDto.getId();
        adminRightsCheck(currentUser);

        AccountEntity account = findByIdAccountRepo(id);
        isMatchesOrganization(organizationName, account);
        account.setIsActivated(isActivated);
        accountRepository.saveAndFlush(account);
        if (!isActivated) oAuth2Service.deleteToken(account);

        ChangedActivateDto changedActivateDto = new ChangedActivateDto();
        changedActivateDto.setId(id);
        changedActivateDto.setIsActivated(isActivated);
        return changedActivateDto;
    }

    @Override
    public void saveAccount(AccountEntity accountEntity) {
        accountRepository.saveAndFlush(accountEntity);
    }

    public Long getCountStorageWithOwnerNullAndNotNullOrganization() {
        return storageRepository.countStorageElementByOwnerIsNullAndOrganizationIsNotNull();
    }

}