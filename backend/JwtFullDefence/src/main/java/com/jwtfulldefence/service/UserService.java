package com.jwtfulldefence.service;

import com.jwtfulldefence.exception.model.EmailExistException;
import com.jwtfulldefence.exception.model.EmailNotFoundException;
import com.jwtfulldefence.exception.model.UserNotFoundException;
import com.jwtfulldefence.exception.model.UsernameExistException;
import com.jwtfulldefence.model.User;
import com.jwtfulldefence.security.UserRole;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {


    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    List<User> getUsers();

    User findByUsername(String username);

    User findByEmail(String email);


    User addNewUser(String firstName,
                    String lastName,
                    String username,
                    String email,
                    String role,
                    String armyId,
                    boolean isNotLocked,
                    boolean isActive) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    User updateUser(String currentUsername,
                    String newFirstName,
                    String newLastName,
                    String newUsername,
                    String newEmail,
                    String newRole,
                    String newArmyId,
                    boolean isNotLocked,
                    boolean isActive) throws UserNotFoundException, UsernameExistException, EmailExistException;

    void deleteUser(String username);

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

}
