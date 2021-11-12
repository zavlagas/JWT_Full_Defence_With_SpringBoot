package com.jwtfulldefence.exception.model;

import com.jwtfulldefence.constant.ExceptionMessageConstant;

public class UsernameExistException extends Exception {


    public UsernameExistException() {
        super(ExceptionMessageConstant.USERNAME_EXIST_MESSAGE);
    }
}
