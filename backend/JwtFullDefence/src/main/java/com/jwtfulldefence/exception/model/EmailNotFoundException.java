package com.jwtfulldefence.exception.model;

import com.jwtfulldefence.constant.ExceptionMessageConstant;

public class EmailNotFoundException extends Exception {


    public EmailNotFoundException() {
        super(ExceptionMessageConstant.EMAIL_NOT_FOUND_MESSAGE);
    }
}
