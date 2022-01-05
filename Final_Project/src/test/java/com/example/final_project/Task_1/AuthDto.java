package com.example.final_project.Task_1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDto {
    public String code;
    public String message;
}