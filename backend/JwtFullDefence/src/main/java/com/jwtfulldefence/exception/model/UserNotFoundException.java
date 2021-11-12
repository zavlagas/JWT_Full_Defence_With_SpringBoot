package com.jwtfulldefence.exception.model;

import com.jwtfulldefence.constant.ExceptionMessageConstant;

public class UserNotFoundException extends Exception {


    public UserNotFoundException() {
        super(ExceptionMessageConstant.USER_NOT_FOUND_MESSAGE);
    }
}
