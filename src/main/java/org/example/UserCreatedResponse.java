package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCreatedResponse {
    @JsonProperty("success")
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN, pattern = "true")
    private Boolean success;

    @JsonProperty("message") String message;
    @JsonProperty("details") User user;
}
