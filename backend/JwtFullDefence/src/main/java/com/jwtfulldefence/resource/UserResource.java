package com.jwtfulldefence.resource;


import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtfulldefence.constant.SecurityConstant;
import com.jwtfulldefence.exception.model.*;
import com.jwtfulldefence.jwt.JwtTokenProvider;
import com.jwtfulldefence.model.User;
import com.jwtfulldefence.security.HttpResponse;
import com.jwtfulldefence.security.UserPrincipal;
import com.jwtfulldefence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jwtfulldefence.constant.SecurityConstant.TOKEN_PREFIX;

@RestController
@RequestMapping(value = "/api/user")
public class UserResource extends ExceptionHandling {


    private UserService service;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;


    @Autowired
    public UserResource(UserService service, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {

        authenticate(user.getUsername(), user.getPassword());
        User loginUser = service.findByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);


        return new ResponseEntity(loginUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws
            UserNotFoundException,
            UsernameExistException,
            EmailExistException, MessagingException {
        User newUser = service.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(newUser);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('admiral:create')")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("companyId") String companyId,
                                           @RequestParam("active") String isActive,
                                           @RequestParam("notLocked") String isNotLocked)
            throws
            UserNotFoundException,
            UsernameExistException,
            EmailExistException,
            MessagingException {

        User newUser = service.addNewUser(
                firstName,
                lastName,
                username,
                email,
                role,
                companyId,
                Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive));

        return ResponseEntity.ok(newUser);

    }


    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("companyId") String companyId,
                                           @RequestParam("active") String isActive,
                                           @RequestParam("notLocked") String isNotLocked)
            throws
            UserNotFoundException,
            UsernameExistException,
            EmailExistException {

        User updatedUser = service.updateUser(
                currentUsername,
                firstName,
                lastName,
                username,
                email,
                role,
                companyId,
                Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive));

        return ResponseEntity.ok(updatedUser);

    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.findByUsername(username));
    }


    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(service.getUsers());
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        service.resetPassword(email);
        return response(HttpStatus.OK, SecurityConstant.RESET_PASSWORD_MSG + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('admiral:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) {
        service.deleteUser(username);
        return response(HttpStatus.OK, SecurityConstant.USER_DELETED_MESSAGE);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        return new ResponseEntity(
                new HttpResponse(status.value(),
                        status,
                        status.getReasonPhrase().toUpperCase(),
                        message),
                status);
    }

    // Implement http-only Cookie
    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    }
}
