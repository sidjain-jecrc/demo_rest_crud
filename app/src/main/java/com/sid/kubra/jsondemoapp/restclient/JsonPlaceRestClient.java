package com.sid.kubra.jsondemoapp.restclient;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sid.kubra.jsondemoapp.model.User;
import com.sid.kubra.jsondemoapp.model.UserAddress;
import com.sid.kubra.jsondemoapp.model.UserPost;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Siddharth on 6/24/2017.
 */

public class JsonPlaceRestClient {

    protected static final String TAG = JsonPlaceRestClient.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final String baseUrl = "http://jsonplaceholder.typicode.com";
    private JSONParser parser;
    private static OkHttpClient httpClient = new OkHttpClient();

    public JsonPlaceRestClient() {
        parser = new JSONParser();
    }

    public List<User> requestUsersList() {

        try {
            String requestUrl = baseUrl + "/users";
            String jsonString = performGetRequest(requestUrl);
            Log.d(TAG, "Json response: " + jsonString);
            return parseUserList(jsonString);

        } catch (Exception e) {
            Log.e(TAG, "Error - request users list: " + e.getMessage());
        }
        return null;
    }

    public List<UserPost> requestUserPosts(String userId) {

        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/posts").newBuilder();
            urlBuilder.addQueryParameter("userId", userId);
            String requestUrl = urlBuilder.build().toString();
            String jsonString = performGetRequest(requestUrl);
            return parsePostList(jsonString);

        } catch (Exception e) {
            Log.e(TAG, "Error - request user posts: " + e.getMessage());
        }
        return null;
    }

    public UserPost addPost(UserPost userPost) {

        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/posts").newBuilder();
            String requestUrl = urlBuilder.build().toString();
            String postJsonString = buildPostJsonString(userPost);
            String responseJsonString = performPostRequest(requestUrl, postJsonString);
            return parsePostJsonToObject(responseJsonString);

        } catch (Exception e) {
            Log.e(TAG, "Error - add post: " + e.getMessage());
        }
        return null;
    }

    private UserPost parsePostJsonToObject(String jsonString) {
        UserPost newPost = null;
        try {
            JSONObject postObject = (JSONObject) parser.parse(jsonString);
            long postId = (long) postObject.get("id");
            String title = (String) postObject.get("title");
            String body = (String) postObject.get("body");
            newPost = new UserPost(postId, title, body);

        } catch (ParseException e) {
            Log.e(TAG, "Add Post:Parser exception: " + e.getMessage());
        }
        return newPost;
    }

    private List<UserPost> parsePostList(String jsonString) {

        List<UserPost> postList = new ArrayList<>();
        try {
            JSONArray postArray = (JSONArray) parser.parse(jsonString);
            for (int i = 0; i < postArray.size(); i++) {

                JSONObject postObject = (JSONObject) postArray.get(i);
                Long postId = (Long) postObject.get("id");
                String title = (String) postObject.get("title");
                String body = (String) postObject.get("body");

                UserPost post = new UserPost(postId, title, body);
                postList.add(post);
            }

        } catch (ParseException e) {
            Log.e(TAG, "PostList:Parser exception: " + e.getMessage());
        }
        return postList;
    }

    private List<User> parseUserList(String jsonString) {

        List<User> userList = new ArrayList<>();
        try {
            JSONArray userArray = (JSONArray) parser.parse(jsonString);
            for (int i = 0; i < userArray.size(); i++) {

                JSONObject userObject = (JSONObject) userArray.get(i);
                Long userId = (Long) userObject.get("id");
                String username = (String) userObject.get("username");

                JSONObject addressObject = (JSONObject) userObject.get("address");
                String street = (String) addressObject.get("street");
                String suite = (String) addressObject.get("suite");
                String city = (String) addressObject.get("city");
                String zipCode = (String) addressObject.get("zipcode");

                UserAddress address = new UserAddress(street, suite, city, zipCode);
                User user = new User(userId, username, address);

                userList.add(user);
            }

        } catch (ParseException e) {
            Log.e(TAG, "UserList:Parser exception: " + e.getMessage());
        }
        return userList;
    }

    private String performGetRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    private String performPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            Log.d(TAG, "add post request successfull");
            return response.body().string();
        }
        return null;
    }

    private String buildPostJsonString(UserPost post) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String userPostString = mapper.writeValueAsString(post);
        return userPostString;
    }

}
