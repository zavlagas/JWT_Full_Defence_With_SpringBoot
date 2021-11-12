package com.jwtfulldefence.exception.model;


import com.jwtfulldefence.constant.ExceptionMessageConstant;

public class EmailExistException extends Exception {

    public EmailExistException() {
        super(ExceptionMessageConstant.EMAIL_EXIST_MESSAGE);
    }
}
