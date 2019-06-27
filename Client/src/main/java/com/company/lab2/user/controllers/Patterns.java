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
        WOOPS ("Woops");

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
        WRONGLOGIN ("Wrong login"),
        ACTIVEUSER ("Active user"),
        WRONGPASSWORD ("Wrong password"),
        BANNED ("U are banned"),
        WRONGCODE ("Wrong code"),
        CONGRATULATIONS ("Congratulations"),
        ALREADYADMIN ("Already admin");

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
        WRONGLOGIN ("Login doesn't exist"),
        ALREADYEXISTLOGIN ("already exist login"),
        ACTIVEUSER ("This account is active wright now"),
        LOGINNOTEXIST ("Login doesn't exist"),
        WRONGPASSWORD ("Password is incorrect"),
        BANNED ("Good luck"),
        WRONGCODE ("Contact with mainAdmin to get code"),
        CONGRATULATIONS ("You are on waiting list to become admin"),
        ALREADYADMIN ("");

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
