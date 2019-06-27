package com.company.lab2.user.controllers;


public class Patterns {
    public static final String CONNECTED = "connected";
    public static final String TASK = "task";
    public static final String TASKREP = "taskRep";
    public static final String CALENDAR = "calendar";
    public static final String EMPTY = "empty";
    public static final String END = "end";
    public static final String USERSLIST = "usersList";
    public static final String ADMINSLIST = "adminsList";
    public static final String DONEADCH = "doneADCH";
    public static final String ALREADYEXISTLOGIN = "already exist login";
    public static final String ACTIVEUSER = "active user";
    public static final String LOGINNOTEXIST = "login not exist";
    public static final String WRONGPASSWORD = "wrong password";
    public static final String BANNED = "banned";
    public static final String ISADMIN = "isAdmin";
    public static final String WRONGCODE = "wrong code";
    public static final String CONGRATULATIONS = "congratulations";
    public static final String ALREADYADMIN = "already admin";
    public static final String EXIT = "Exit";

    public enum TitleEnum {
        ERROR ("Error"),
        NOPE ("Nope"),
        COOl ("Cool"),
        WHOOPS("Whoops"),
        ADMIN_REQ ("AdminRequests"),
        EMPTY_CODE ("Code is empty"),
        WARNING ("Warning"),
        CALENDAR_NF ("Calendar not found");

        private String title;

        TitleEnum(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
        @Override
        public String toString() {
            return "Header = '" + title + '\'';
        }
    }

    public enum HeaderEnum {
        WRONG_LOGIN("Wrong login"),
        ACTIVE_USER("Active user"),
        WRONG_PASSWORD("Wrong password"),
        BANNED ("U are banned"),
        WRONG_CODE("Wrong code"),
        CONGRATULATIONS ("Congratulations"),
        ALREADY_ADMIN("Already admin"),
        ADMIN_REQ ("There are no Requests"),
        EMPTY_CODE ("Code field is empty"),
        WARNING ("Action mistake"),
        CALENDAR_NF ("Sorry :-(");

        private String title;

        HeaderEnum(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
        @Override
        public String toString() {
            return "Header = '" + title + '\'';
        }
    }

    public enum ContentEnum {
        ALREADY_EXIST_LOGIN ("Already exist login"),
        ACTIVE_USER("This account is active wright now"),
        LOGIN_NOT_EXIST("Login doesn't exist"),
        WRONG_PASSWORD("Password is incorrect"),
        BANNED ("Good luck"),
        WRONG_CODE("Contact with mainAdmin to get code"),
        CONGRATULATIONS ("You are on waiting list to become admin"),
        ALREADY_ADMIN(""),
        PORT ("Port field should be filled"),
        HOST ("Host fields should be filled"),
        FAILED_CONN ("Failed connect to server! Try again or change host/port data!"),
        TITLE ("Title field should be filled"),
        DATE ("Date field should be filled"),
        TIME ("At least one of time fields in one row should be filled"),
        INTERVAL_F ("At least one of \"interval\" fields in one row should be filled"),
        INTERVAL_Z ("\"Interval\" must be greater than zero!"),
        END_START_TIME ("End time of the task must be greater than Start time + interval"),
        RADIO ("Chose one of RadioButtons before adding Task"),
        ADMIN_REQ ("Pls come later!"),
        EMPTY_CODE ("pls filed code field before sending"),
        CALENDAR_NF ("There aren't any Tasks is such period of time"),
        FIELDS ("All fields should be filled"),
        PASSWORDS_NM ("Passwords not match!");

        private String title;

        ContentEnum(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
        @Override
        public String toString() {
            return "Header = '" + title + '\'';
        }
    }



}
