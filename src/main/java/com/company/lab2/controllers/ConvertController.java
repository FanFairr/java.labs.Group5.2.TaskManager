package com.company.lab2.controllers;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.ZoneId;
import java.util.Date;

class ConvertController {
    /**метод, что записывает интервал повторения
     *задачи в текстовый формат(строку).
     *@param repeatInterval интервал повторения задачи
     */
    static String getStringFromRepeatInterval(final int repeatInterval) {
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

    /**Method for show not repeated task radioButton content
     * @param date date to get from
     * @param time long time to add in seconds
     * @return new Date
     */
    static Date makeDate(DatePicker date, Long time) {
        if( time != null) {
            Date current = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            long toDate = current.getTime() + time * 1000;
            return new Date(toDate);
        }
        return null;
    }

    /**Method for making long digit from textFields
     * @param h hours TextField
     * @param m minutes TextField
     * @param s seconds TextField
     * @return long h+m+s || null
     */
    static Long makeLongFromTimeFields(TextField h, TextField m, TextField s) {
        if (!ValidateController.isEmpty(h) || !ValidateController.isEmpty(s) || !ValidateController.isEmpty(m)){
            ValidateController.setZeroIfEmpty(h);
            ValidateController.setZeroIfEmpty(m);
            ValidateController.setZeroIfEmpty(s);
            int hours = Integer.parseInt(ValidateController.removeZeroBeforeNumber(h.getText())) * 3600;
            int minutes = Integer.parseInt(ValidateController.removeZeroBeforeNumber(m.getText())) * 60;
            int seconds = Integer.parseInt(ValidateController.removeZeroBeforeNumber(s.getText()));
            return (long) (hours + minutes + seconds);
        } else {
            return null;
        }
    }

}
