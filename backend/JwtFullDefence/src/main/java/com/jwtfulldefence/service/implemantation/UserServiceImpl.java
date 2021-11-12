package com.jwtfulldefence.service.implemantation;

import com.jwtfulldefence.constant.SecurityConstant;
import com.jwtfulldefence.exception.model.EmailExistException;
import com.jwtfulldefence.exception.model.EmailNotFoundException;
import com.jwtfulldefence.exception.model.UserNotFoundException;
import com.jwtfulldefence.exception.model.UsernameExistException;
import com.jwtfulldefence.model.User;
import com.jwtfulldefence.repository.UserRepository;
import com.jwtfulldefence.security.UserPrincipal;
import com.jwtfulldefence.security.UserRole;
import com.jwtfulldefence.service.EmailService;
import com.jwtfulldefence.service.LoginAttemptService;
import com.jwtfulldefence.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());


    private UserRepository userRepository;
    private EmailService emailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        UserPrincipal userPrincipal;
        if (user == null) {
            String userNotFoundMessage = "User Not Found By Username " + username;
            LOGGER.error(userNotFoundMessage);
            throw new UsernameNotFoundException(userNotFoundMessage);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning user by username " + username);
            return userPrincipal;
        }

    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceedMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName,
                         String lastName,
                         String username,
                         String email)
            throws
            UserNotFoundException,
            UsernameExistException,
            EmailExistException, MessagingException {

        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);

        User user = new User();

        user.setUserId(generateUserId());
        user.setCompanyId("");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setSignUpDate(new Date());

        user.setUsername(username);
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setPassword(encodedPassword);

        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(UserRole.ROLE_USER);
        emailService.sendNewPasswordEmail(firstName, password, email);
        userRepository.save(user);
        LOGGER.info("New user password : " + password);
        return user;
    }


    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    @Override
    public User addNewUser(String firstName,
                           String lastName,
                           String username,
                           String email,
                           String role,
                           String companyId,
                           boolean isNotLocked,
                           boolean isActive) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User newUser = new User();
        String password = generatePassword();
        LOGGER.info("User Password + " + password);
        newUser.setUserId(generateUserId());
        newUser.setCompanyId(companyId);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setSignUpDate(new Date());
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encodePassword(password));
        newUser.setActive(isActive);
        newUser.setNotLocked(isNotLocked);
        newUser.setRole(checkAndGetRole(role));
        userRepository.save(newUser);
        emailService.sendNewPasswordEmail(firstName, password, email);
        return newUser;

    }


    @Override
    public User updateUser(String currentUsername,
                           String newFirstName,
                           String newLastName,
                           String newUsername,
                           String newEmail,
                           String newRole,
                           String newCompanyId,
                           boolean isNotLocked,
                           boolean isActive) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User user = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        user.setCompanyId(newCompanyId);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(checkAndGetRole(newRole));
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(String username) {
        User user = findByUsername(username);
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException();
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }


    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername,
                                             String newUsername,
                                             String newEmail)
            throws
            UserNotFoundException,
            UsernameExistException,
            EmailExistException {

        User userByUsername = findByUsername(newUsername);
        User userByEmail = findByEmail(newEmail);

        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException();
            }
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException();
            }
            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistException();
            }
            return currentUser;
        } else {
            if (userByUsername != null) {
                throw new UsernameExistException();
            }
            if (userByEmail != null) {
                throw new EmailExistException();
            }
            return null;
        }
    }

    private UserRole checkAndGetRole(String role) {
        UserRole userRole = Optional.ofNullable(UserRole.valueOf(role.toUpperCase())).orElse(UserRole.ROLE_USER);
        return userRole;
    }


}
