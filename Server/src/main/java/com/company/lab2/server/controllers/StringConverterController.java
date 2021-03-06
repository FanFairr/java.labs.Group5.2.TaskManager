package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class to convert collections to another type
 */
public class StringConverterController {
    private static Logger LOGGER = Logger.getLogger(StringConverterController.class);

    /**
     * translation method ArrayList to ObservableList
     * @param list - task list
     * @return - task observable list
     */
    static ObservableList<String> formatTaskArr(ArrayList<Task> list) {
        ObservableList<String> TaskList = FXCollections.observableArrayList();
        for (Task task: list) {
            TaskList.add(task.toString());
        }
        return TaskList;
    }

    /**
     * translation method ArrayList to ObservableList
     * @param userList - user list
     * @return - user observable list
     */
    static ObservableList<String> convertUserList(ArrayList<User> userList) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (User user: userList) {
            list.add(user.toString());
        }
        return list;
    }

    /**
     * translation method String to User
     * @param strUser - user information
     * @return - user
     */
    static User makeUserFromString (String strUser) {
        String login = getLogin(strUser);
        String pw = getPassword(strUser);
        String ban = getBanned(strUser);
        String adm = getAdmin(strUser);
        return new User(login,pw,Boolean.valueOf(ban),adm);
    }

    /**
     * translation method String to Task
     * @param strTask - task information
     * @return - task
     */
    static Task makeTaskFromString (String strTask) {
        String title = getTitle(strTask);
        String buffer;
        if (strTask.contains("repeatInterval")) {
            buffer = strTask.replaceAll(".+repeatInterval: ","").replaceAll(",.+", "");
            int repInt;
            if(buffer.matches("\\d+"))
                repInt = Integer.parseInt(buffer);
            else
                repInt = parseRepeatInterval(buffer);
            Date start = null;
            Date end = null;
            try {
                start = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(getStartDate(strTask));
                end = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(getEndDate(strTask));
            } catch (ParseException e) {
                LOGGER.error("Error parsing date on line 78-79 of class StringConverterController");
            }
            Task task = new Task(title, start ,end, repInt);
            task.setActive(getActive(strTask));
            return task;
        } else {
            Date time = null;
            try {
                time = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(getDate(strTask));
            } catch (ParseException e) {
                LOGGER.error("Error parsing date on line 90 of class StringConverterController");
            }
            Task task = new Task(title, time);
            task.setActive(getActive(strTask));
            return task;
        }
    }

    /**
     * the method that returns the login from the string
     * @param str - user information
     * @return - user login
     */
    private static String getLogin(String str) {
        String string = str.replace("Login: '","");
        return string.substring(0, string.indexOf("',"));
    }

    /**
     * the method that returns the password from the string
     * @param str - user information
     * @return - user password
     */
    private static String getPassword(String str) {
        String string = str.replaceAll(".+password: '","");
        return string.substring(0, string.indexOf("',"));
    }

    /**
     * the method that returns the ban status from the string
     * @param str - user information
     * @return - user ban status
     */
    private static String getBanned(String str) {
        String string = str.replaceAll(".+banned: ","");
        return string.substring(0, string.indexOf(", "));
    }

    /**
     * the method that returns the admin status from the string
     * @param str - user information
     * @return - user admin status
     */
    private static String getAdmin(String str) {
        String string = str.replaceAll(".+admin: '","");
        return string.substring(0, string.indexOf("'"));
    }

    /**
     * the method that returns the task title from the string
     * @param str - task information
     * @return - task title
     */
    private static String getTitle(String str) {
        String string = str.replace("Title: \'","");
        return string.substring(0, string.indexOf("\',"));
    }

    /**
     * the method that returns the task date from the string
     * @param str - task information
     * @return - task date
     */
    private static String getDate(String str) {
        String string = str.replaceAll(".+time: ","");
        return string.substring(0, string.indexOf(","));
    }

    /**
     * the method that returns the task start time from the string
     * @param str - task information
     * @return - task start time
     */
    private static String getStartDate(String str) {
        String string = str.replaceAll(".+startTime: ","");
        return string.substring(0, string.indexOf(","));
    }

    /**
     * the method that returns the task end time from the string
     * @param str - task information
     * @return - task end time
     */
    private static String getEndDate(String str) {
        String string = str.replaceAll(".+endTime: ","");
        return string.substring(0, string.indexOf(","));
    }

    /**
     * the method that returns the task active status from the string
     * @param str - task information
     * @return - task active status
     */
    private static boolean getActive(String str) {
        String active = str.replaceAll(".+active: ","").replace(";","");
        return active.equals("true");
    }

    /**
     * a method that writes a task repetition interval
     * to a text format (string).
     * @param repeatInterval task repetition interval
     */
    public static String getStringFromRepeatInterval(final int repeatInterval) {
        StringBuilder builderString = new StringBuilder();
        int days = repeatInterval / 86400;
        int hours =  (repeatInterval / 3600) % 24;
        int minutes = (repeatInterval / 60) % 60;
        int seconds = repeatInterval % 60;
        builderString.append((days == 0? "": days + (days > 1 ? " days " : " day ")));
        builderString.append((hours == 0? "": hours + (hours > 1 ? " hours " : " hour ")));
        builderString.append((minutes == 0? "": minutes + (minutes > 1 ? " minutes " : " minute ")));
        builderString.append((seconds == 0? "": seconds + (seconds > 1 ? " seconds " : " second ")));
        String s = new String(builderString);
        return s.trim();
    }
    /**
     * a method that parses the string in the int
     * value of the repetition interval of the task.
     *@param data string representation of the task repetition interval
     */
    private static int parseRepeatInterval(String data) throws NumberFormatException {
        int days = 0, hours = 0, minutes = 0, seconds = 0;
        String parsed [] = new String[4];
        Matcher matcher = Pattern.compile("\\d+\\s\\w").matcher(data);
        int counter = 0;
        while (matcher.find()) {
            parsed[counter] = matcher.group();
            counter++;
        }
        try {
            for (int i=0; i<counter; i++)  {
                switch (parsed[i].substring(parsed[i].length() - 1)) {
                    case "d":
                        days = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                    case "h":
                        hours = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                    case "m":
                        minutes = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                    case "s":
                        seconds = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                }
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Integer parse error при парсеровке repeatInterval");
        }
        return ((days * 86400) + (hours * 3600) + (minutes * 60) + seconds);
    }
}
