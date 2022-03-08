package com.example.geektrust.utility;

public class Logger {

    private Logger() {

    }

    public static void success() {
        Logger.log("SUCCESS");
    }

    public static void failure() {
        Logger.log("FAILURE");
    }

    public static void houseful() {
        Logger.log("HOUSEFUL");
    }

    public static void memberNotFound() {
        Logger.log("MEMBER_NOT_FOUND");
    }

    public static void incorrectPayment() {
        Logger.log("INCORRECT_PAYMENT");
    }

    public static void log(String info) {
        System.out.println(info);
    }

    public static void log(int info) {
        System.out.println(info);
    }

}
