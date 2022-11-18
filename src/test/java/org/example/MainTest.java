package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

class MainTest {

    private static RequestConfig requestConfig = RequestConfig.custom().build();
    private static CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    private Entry<Integer, String> postWithFormData(String url, List<NameValuePair> params) throws IOException {
        var request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(params));
        var response = getHttpClient().execute(request);
        var status = response.getStatusLine().getStatusCode();
        var jsonString = EntityUtils.toString(response.getEntity());
        return Map.entry(status, jsonString);
    }

    private Entry<Integer, String> createUser(String username, String email, String password) throws IOException {
        return postWithFormData("http://3.145.97.83:3333/user/create", List.of(
                new BasicNameValuePair("username", username),
                new BasicNameValuePair("email", email),
                new BasicNameValuePair("password", password)
        ));
    }

    private Entry<Integer, List<User>> getUsers() throws IOException {
        var request = new HttpGet("http://3.145.97.83:3333/user/get");
        var response = getHttpClient().execute(request);
        var status = response.getStatusLine().getStatusCode();
        var jsonString = EntityUtils.toString(response.getEntity());
        var mapper = new ObjectMapper();
        List<User> users = mapper.readValue(
                jsonString,
                TypeFactory.defaultInstance().constructCollectionType(List.class, User.class)
        );
        return Map.entry(status, users);
    }

    private void assertNewUserCreated(String name, String email, String password, String jsonString) throws Exception {

        var mapper = new ObjectMapper();
        var successResponse = mapper.readValue(jsonString, UserCreatedResponse.class);
        Assertions.assertTrue(successResponse.message.contains("User Successully created"));
        Assertions.assertTrue(successResponse.user.username.equals(name));
        Assertions.assertTrue(successResponse.user.email.equals(email));
        Assertions.assertFalse(successResponse.user.password.contains(password));
    }

    private void assertEmailAlreadyExists(String jsonString) throws Exception {
        var mapper = new ObjectMapper();
        var failResponse = mapper.readValue(jsonString, FailResponse.class);
        Assertions.assertTrue(failResponse.message.contains("Email already exists"));
    }

    private void assertUsernameAlreadyExists(String jsonString) throws Exception {
        var mapper = new ObjectMapper();
        var failResponse = mapper.readValue(jsonString, FailResponse.class);
        Assertions.assertTrue(failResponse.message.contains("This username is taken. Try another."));
    }

    @Test
    public void testCreateUniqueUser() throws Exception {
        var randomName = UUID.randomUUID().toString();
        var randomEmail = UUID.randomUUID().toString() + "@gmail.com";
        var randomPassword = UUID.randomUUID().toString();

        var createUser = createUser(randomName, randomEmail, randomPassword);
        Assertions.assertEquals(200, createUser.getKey());
        assertNewUserCreated(randomName, randomEmail, randomPassword, createUser.getValue());

        var getUsers = getUsers();
        var user = getUsers.getValue().stream().filter(u -> u.email.equals(randomEmail)).findFirst().get();
        Assertions.assertEquals(200, getUsers.getKey());
        Assertions.assertEquals(randomName, user.username);
        Assertions.assertEquals(randomEmail, user.email);
        Assertions.assertNotEquals(randomPassword, user.password);
    }

    @Test
    public void testCreateNonExistentNameExistingEmail() throws Exception {
        var randomName = UUID.randomUUID().toString();
        var email = "hello@mail.com";
        var createUser = createUser(randomName, email, "123456");
        Assertions.assertEquals(400, createUser.getKey());
        assertEmailAlreadyExists(createUser.getValue());

        var getUsers = getUsers();
        var user = getUsers.getValue().stream().filter(u -> u.email.equals(email)).findFirst().get();
        Assertions.assertEquals(200, getUsers.getKey());
        Assertions.assertNotEquals(randomName, user.username);
    }

    @Test
    public void testCreateExistentNameNonExistingEmail() throws Exception {
        var randomEmail = UUID.randomUUID().toString() + "@gmail.com";
        var username = "name";
        var createUser = createUser(username, randomEmail, "123456");
        Assertions.assertEquals(400, createUser.getKey());
        assertUsernameAlreadyExists(createUser.getValue());

        var getUsers = getUsers();
        var user = getUsers.getValue().stream().filter(u -> u.username.equals(username)).findFirst().get();
        Assertions.assertEquals(200, getUsers.getKey());
        Assertions.assertNotEquals(randomEmail, user.email);
    }

}