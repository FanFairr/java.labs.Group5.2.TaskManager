package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringConverterController {

    public static ObservableList<String> formatTaskArr(ArrayList<Task> list) {
        ObservableList<String> TaskList = FXCollections.observableArrayList();
        for (Task task: list) {
            TaskList.add(task.toString());
            System.out.println(task.toString());
        }
        return TaskList;
    }

    public static Task makeTaskFromString (String strTask) {
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
                e.printStackTrace();
            }
            Task task = new Task(title, start ,end, repInt);
            task.setActive(getActive(strTask));
            return task;
        } else {
            Date time = null;
            try {
                time = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(getDate(strTask));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Task task = new Task(title, time);
            task.setActive(getActive(strTask));
            return task;
        }
    }

    private static String getTitle(String str) {
        String string = str.replace("Title: \'","");
        return string.substring(0, string.indexOf("\',"));
    }

    private static String getDate(String str) {
        String string = str.replaceAll(".+time: ","");
        return string.substring(0, string.indexOf(","));
    }

    private static String getStartDate(String str) {
        String string = str.replaceAll(".+startTime: ","");
        return string.substring(0, string.indexOf(","));
    }

    private static String getEndDate(String str) {
        String string = str.replaceAll(".+endTime: ","");
        return string.substring(0, string.indexOf(","));
    }

    private static boolean getActive(String str) {
        String active = str.replaceAll(".+active: ","").replace(";","");
        return active.equals("true");
    }

    /**метод, что записывает интервал повторения
     *задачи в текстовый формат(строку).
     *@param repeatInterval интервал повторения задачи
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
    /**метод, что парсит строку в int
     * значение интервала повторение задачи.
     *@param data строчное представление интервала повторения задачи
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
                        minutes = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                }
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Integer parse error при парсеровке repeatInterval");
        }
        return ((days * 86400) + (hours * 3600) + (minutes * 60) + seconds);
    }
}
