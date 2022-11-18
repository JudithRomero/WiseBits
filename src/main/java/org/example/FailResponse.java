package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FailResponse {

    @JsonProperty("success")
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN, pattern = "false")
    private Boolean success;

    @JsonProperty("message")
    List<String> message;

}
