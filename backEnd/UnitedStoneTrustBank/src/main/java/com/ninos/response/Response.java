package com.ninos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private int statusCode;
    private String message;
    private T data; // T: is generic it could be(String or any DTO like: UserDTO, AccountDTO, LoginResponse,...)
    private Map<String, Serializable> meta;

}
