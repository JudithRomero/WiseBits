package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class ParseTest {

    @Test
    void testMapUser() throws Exception {
        var mapper = new ObjectMapper();
        var userJsonString = """
        {
                "id": 1,
                "username": "test",
                "email": "test@test.com",
                "password": "$2a$10$5hEv.bjy6wM.OfXZaWuvTexAg9cUTWQc1HQeIeB.WnRE8Mt8FD0vC",
                "created_at": "2022-10-13 11:57:35",
                "updated_at": "2022-10-13 11:57:35"
        }
        """;
        mapper.readValue(userJsonString, User.class);

    }

    @Test
    void testMapFailResponse() throws Exception {
        var mapper = new ObjectMapper();
        var userJsonString = """
        {
                "success": false,
                "message": ["MSG"]
        }
        """;
        mapper.readValue(userJsonString, FailResponse.class);

    }

    @Test
    void testUserCreatedResponse() throws Exception {
        var mapper = new ObjectMapper();
        var userJsonString = """
        {
            "success": true,
            "details": {
                "username": "ncewck2",
                "email": "njcjna@nkwq.jb,,cje",
                "password": "$2a$10$4iVgJbUtwMK7VpwcfRqb5OEJYd30Z7Enlp64suZlgIlVVfhbAru66",
                "created_at": "2022-11-17 20:26:18",
                "updated_at": "2022-11-17 20:26:18",
                "id": 21474836538
            },
            "message": "User Successully created"
        }
        """;
        mapper.readValue(userJsonString, UserCreatedResponse.class);

    }

}
