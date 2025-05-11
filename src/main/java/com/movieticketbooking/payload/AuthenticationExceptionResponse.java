package com.movieticketbooking.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationExceptionResponse {

    private String timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;

}
