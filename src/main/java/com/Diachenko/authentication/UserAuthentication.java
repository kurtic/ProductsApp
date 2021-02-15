package com.Diachenko.authentication;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserAuthentication {
    private boolean auth;
    public Map<Integer, String> userMap = new HashMap<>();
    private int user_id = 1;

    public void authentication() {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.println("Please, enter your password for create new user:");
        String password = scanner.next();
        userMap.put(user_id, password);
        auth = true;
        System.out.println("Successfully!Your user id is: " + user_id);
        System.out.println("Don't forget please!");
        user_id++;
    }

    public boolean isAuth() {
        return auth;
    }

    public int getUserId() {
        return user_id;
    }
}
