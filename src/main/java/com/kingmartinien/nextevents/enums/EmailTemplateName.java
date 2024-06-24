package com.kingmartinien.nextevents.enums;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    EMAIL_CONFIRMATION("email_confirmation"),
    RESET_PASSWORD_REQUEST("reset_password_request");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }

}
