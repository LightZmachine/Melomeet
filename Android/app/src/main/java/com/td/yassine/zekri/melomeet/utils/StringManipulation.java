package com.td.yassine.zekri.melomeet.utils;

import java.util.Random;

public class StringManipulation {

    public static boolean isValidEmail(String email) {
        return email.contains("@");
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

}
