package com.company.lab2.user.controllers;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.ZoneId;
import java.util.Date;

public class ConvertController {



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
