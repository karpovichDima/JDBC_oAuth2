package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.config.oauth.UserDetailsConfig;
import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.dao.OrganizationRepo;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.ResponseCode;
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
    public EditedPasswordDto editPassword(Long id, String newPassword, String rawOldPassword) throws AppException {
        AccountEntity foundedUser = findByIdAccountRepo(id);
        if (!isEmpty(newPassword) || !isEmpty(rawOldPassword)) throw new AppException("Field is empty", ResponseCode.EMPTY_FIELD);

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
    public EditedEmailNameDto editPersonData(Long id, @NonNull EditPersonalDataDto personalData) throws AppException {
        AccountEntity currentUser = getCurrentUser();
        String newEmail = personalData.getNewEmail();
        if (findUserByEmail(newEmail) != null)
            throw new AppException("User with such email exist.", ResponseCode.USER_WITH_SUCH_EMAIL_EXIST);
        String newName = personalData.getNewName();
        if (id == null) {
            currentUser.setEmail(newEmail);
            currentUser.setUsername(newName);
            accountRepository.saveAndFlush(currentUser);
            return responsePersonalDataDto(currentUser, null);
        }
        adminRightsCheck(currentUser);
        AccountEntity foundedAccount = findByIdAccountRepo(id);
        String organizationName = getOrganizationNameByUser(foundedAccount);

        isMatchesOrganization(organizationName, currentUser);
        foundedAccount.setEmail(newEmail);
        foundedAccount.setUsername(newName);
        accountRepository.saveAndFlush(foundedAccount);
        return responsePersonalDataDto(foundedAccount, null);
    }

    @Transactional
    @Override
    public DeletedUserDto deleteUser(Long id, DeleteAccountDto accountDto) throws AppException {
        AccountEntity currentUser = getCurrentUser();
        if (id == null) {
            String email = accountDto.getEmail();
            String password = accountDto.getRawPassword();
            isMatchesEmail(currentUser.getEmail(), email);
            String encodedPassword = getCurrentUser().getPassword();
            isMatchesPassword(password, encodedPassword);

            List<StorageElement> children = contentService.findContentByUser(currentUser).getChildren();
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
            List<StorageElement> children = contentService.findContentByUser(foundedUser).getChildren();
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
    public EditedEmailNameDto createUser(AccountDto accountDto) throws ValidationException, AppException {
        AccountEntity currentUser = getCurrentUser();
        String email = accountDto.getEmail();
        String organizationName = accountDto.getOrganizationName();
        if (findUserByEmail(email) != null){
            throw new AppException("User with such email exist.", ResponseCode.USER_WITH_SUCH_EMAIL_EXIST);
        }
        adminRightsCheck(currentUser);
        isMatchesOrganization(organizationName, currentUser);

        String userName = accountDto.getUsername();
        boolean isActivated = accountDto.getIsActivated();
        UserRole role = accountDto.getRole();
        AccountEntity newUser = new AccountEntity();
        newUser.setEmail(email);
        Organization organization = findOrganizationByName(organizationName);
        newUser.setUsername(userName);
        newUser.setIsActivated(isActivated);
        newUser.setRole(role);
        newUser.setOrganization(organization);

        Content rootContent = null;
        if (getCountStorageWithOwnerNullAndNotNullOrganization() < 1 || role.equals(UserRole.USER)) {
            rootContent = contentService.createContent(newUser);
        }
        String uuid = UUID.randomUUID() + "";
        Date date = new Date();
        LocalDateTime utc = LocalDateTime.from(date.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1);
        Timestamp timestamp = Timestamp.valueOf(utc);
        newUser.setUuid(uuid);
        newUser.setTokenEndDate(timestamp);
        accountRepository.saveAndFlush(newUser);
        contentService.saveContent(rootContent);
        mailService.emailPreparation(newUser);
        return responsePersonalDataDto(newUser, rootContent);
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
        String encodedPassword = passwordEncode(setPasswordDto.getPassword());
        foundUser.setPassword(encodedPassword);
        accountRepository.saveAndFlush(foundUser);
    }


    public void isMatchesEmail(@NonNull String emailCurrentUser, @NonNull String email) throws AppException {
        boolean equals = emailCurrentUser.equals(email);
        if (!equals) throw new AppException("Current user's email does not match mail from dto.", ResponseCode.EMAIL_NOT_MATCH);
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
    public void isMatchesPassword(String rawOldPassword, String passwordCurrentUser) throws AppException {
        boolean matches = passwordEncoder.matches(rawOldPassword, passwordCurrentUser);
        if (!matches) throw new AppException("Passwords not matches.", ResponseCode.PASSWORDS_NOT_MATCHES);
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
    public AccountEntity findByIdAccountRepo(@NonNull Long id) throws AppException {
        Optional<AccountEntity> foundOptional = accountRepository.findById(id);
        if (!foundOptional.isPresent()) throw new AppException("No objects were found by your request.",ResponseCode.NO_SUCH_ELEMENT);
        return foundOptional.get();
    }

    @Override
    public void adminRightsCheck(AccountEntity entity) throws AppException {
        UserRole role = entity.getRole();
        if (role != UserRole.ADMIN)
            throw new AppException("Authorized user is not an administrator.", ResponseCode.CURRENT_USER_IS_NOT_ADMIN);
    }

    @Override
    public void isMatchesOrganization(@NonNull String userOrganization, @NonNull AccountEntity currentUser) throws AppException {
        String userCurrentOrganization = getOrganizationNameCurrentUser(currentUser);
        boolean equals = userOrganization.equals(userCurrentOrganization);
        if (!equals)
            throw new AppException("Organization current user and user from account dto is not match.", ResponseCode.ORGANIZATIONS_NOT_MUCH);
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
    public EditedEmailNameDto responsePersonalDataDto(AccountEntity accountEntity, Content rootContent) {
        EditedEmailNameDto editedEmailNameDto = new EditedEmailNameDto();
        editedEmailNameDto.setUsername(accountEntity.getUsername());
        editedEmailNameDto.setEmail(accountEntity.getEmail());
        editedEmailNameDto.setUuid(accountEntity.getUuid());
        editedEmailNameDto.setContentId(rootContent.getId());
        return editedEmailNameDto;
    }

    @Override
    public ChangedActivateDto editActivate(AccountDto accountDto) throws AppException {
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