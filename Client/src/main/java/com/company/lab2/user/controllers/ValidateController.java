package com.company.lab2.user.controllers;

import javafx.application.Platform;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Class for fields validation
 */
class ValidateController {

    /**Method for hours validation
     * @param textField hours TextField
     */
    private static void hoursValidate(TextField textField) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("0[0-9]|[0-9]|1[0-9]|2[0-3]")) {
                    Platform.runLater(textField::clear);
                }
            });
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if(!newValue)
                if (textField.getText().matches("[0-9]")){
                    textField.setText("0"+textField.getText());
                }
        });
    }

    /**Method for minutes or seconds validation
     * @param textField minutes or seconds
     */
    private static void minAndSecondsValidate(TextField textField){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]|[0-5][0-9]")){
                Platform.runLater(textField::clear);
            }
        });
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if(!newValue)
                if (textField.getText().matches("[0-9]")){
                    textField.setText("0"+textField.getText());
                }
        });
    }

    /**Method for days validation
     * @param textField days
     */
    static void daysValidate(TextField textField){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]|[0-9]{2,4}|[1-2][1-4][1-8][1-5][1-4]")) {
                Platform.runLater(textField::clear);
            }
        });
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if(!newValue)
                if (textField.getText().matches("[0-9]")){
                    textField.setText("0"+textField.getText());
            }
        });
    }

    /**Method for port validation
     * @param textField port
     */
    static void portValidate(TextField textField){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{1,5}")){
                Platform.runLater(textField::clear);
            }
        });
        textField.setTooltip(new Tooltip("Server port"));
    }

    /**Method for host validation
     * @param host host ip
     */
    static void hostValidate(TextField host){
        host.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-5][0-5]")) {
                Platform.runLater(host::clear);
            }
        });
        host.setTooltip(new Tooltip("Server host"));
    }

    /**Method for setting "00" in field if its empty
     * @param textField field to check on emptiness and set value
     */
    static void setZeroIfEmpty(TextField textField) {
        if (isEmpty(textField))
            textField.setText("00");
    }

    /**Method for checking TextField n emptiness
     * @param textField to check
     * @return true if empty else false
     */
    static boolean isEmpty(TextField textField) {
        return textField.getText() == null || textField.getText().trim().equals("");
    }

    /**Method for removing "0" in String if string starts from it
     * @param s String to remove "0"
     * @return this string
     */
    static String removeZeroBeforeNumber(String s) {
        if (s.matches("^0+"))
            s=s.replaceAll("^0+", "0");
        else s=s.replaceAll("^0+", "");
        return s;
    }

    /**Method for hours, minutes, seconds to validate
     * @param hours hours
     * @param minutes minutes
     * @param seconds seconds
     */
    static void hoursMinSecondsValidate(TextField hours, TextField minutes, TextField seconds){
        hoursValidate(hours);
        minAndSecondsValidate(minutes);
        minAndSecondsValidate(seconds);
    }

    /**Method for making toolTip for date and time fields
     * @param h hours TextField
     * @param m minutes TextField
     * @param s seconds TextField
     * @param date date DatePicker
     */
    static void toolTipForDateAndTime (TextField h,TextField m, TextField s, DatePicker date) {
        h.setTooltip(new Tooltip("Hours"));
        m.setTooltip(new Tooltip("Minutes"));
        s.setTooltip(new Tooltip("Seconds"));
        date.setTooltip(new Tooltip("Date in format dd.MM.yyyy"));
    }

    /**Method for making toolTip for interval fields
     * @param d days TextField
     * @param h hours TextField
     * @param m minutes TextField
     * @param s seconds TextField
     */
    static void toolTipForInterval (TextField d,TextField h,TextField m, TextField s) {
        d.setTooltip(new Tooltip("Days"));
        h.setTooltip(new Tooltip("Hours"));
        m.setTooltip(new Tooltip("Minutes"));
        s.setTooltip(new Tooltip("Seconds"));
    }
}
