package org.hidog.member.controllers;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

@Data
public class RequestJoin implements Serializable {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank @Size(min = 2, max = 10)
    private String userName;

    @NotNull
    private Long zipcode;

    @NotBlank
    private String address;

    private String detailAddress;

    @AssertTrue
    private boolean agree;
}