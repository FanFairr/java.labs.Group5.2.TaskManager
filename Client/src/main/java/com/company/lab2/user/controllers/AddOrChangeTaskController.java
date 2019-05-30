package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class controller for AddOrChangeTask.fxml view.user
 */
public class AddOrChangeTaskController {

    @FXML
    private DatePicker date;
    @FXML
    private DatePicker start;
    @FXML
    private DatePicker end;
    @FXML
    private TextField idays;
    @FXML
    private TextField hours;
    @FXML
    private TextField shours;
    @FXML
    private TextField ehours;
    @FXML
    private TextField ihours;
    @FXML
    private TextField min;
    @FXML
    private TextField smin;
    @FXML
    private TextField emin;
    @FXML
    private TextField imin;
    @FXML
    private TextField sec;
    @FXML
    private TextField ssec;
    @FXML
    private TextField esec;
    @FXML
    private TextField isec;
    @FXML
    private TextField Title;
    @FXML
    private RadioButton Rep;
    @FXML
    private RadioButton NRep;
    @FXML
    private CheckBox active;
    @FXML
    private Button AddBtn;
    @FXML
    private Button ExitBtn;
    @FXML
    private HBox NRepHBox;
    @FXML
    private VBox RepVBox;
    @FXML
    private Label Header;

    /**Current task*/
    private String task;
    /**task repInterval*/
    private int interval;

    @FXML
    void initialize() {
        Stage stage = WindowMaker.getStage();
        ShowToolTip();
        hideContent();
        Rep.setOnAction(event -> showRepRadioBtnContent());
        NRep.setOnAction(event -> showNotRepRadioBtnContent());
        if (stage.getTitle().matches("Task Adding")) {
            AddBtn.setText("Add");
            AddBtn.setOnAction(event -> {
                if (MakeNewTask()) {
                    Controller.addTask(task);
                    WindowMaker.closeWindow(stage);
                }
            });
        } else if (stage.getTitle().matches("Task Changing")){
            Header.setText("Change Task");
            task = MainController.getTask();
            setTaskValuesToShow();
            AddBtn.setText("Save");
            AddBtn.setOnAction(event -> {
                String oldTask = task;
                if (MakeNewTask()) {
                    if (!task.equals(oldTask)) {
                        Controller.changeTask(oldTask,task);
                        WindowMaker.closeWindow(stage);
                        Platform.runLater(() -> WindowMaker.closeWindow(TaskController.getStage()));
                    } else WindowMaker.closeWindow(stage);
                }
            });
        }
        ExitBtn.setOnAction(event -> WindowMaker.closeWindow(stage));
    }

    /**Method for hiding content on scene*/
    private void hideContent() {
        NRepHBox.managedProperty().bind(NRepHBox.visibleProperty());
        NRepHBox.setVisible(false);
        RepVBox.managedProperty().bind(RepVBox.visibleProperty());
        RepVBox.setVisible(false);
    }


    /**Method for making new task
     * or show alert massage if input isn't valid
     */
    private boolean MakeNewTask() {
        final String alertTitle = "Warning";
        final String alertHeader = "Action mistake";
        String alertText = "";
        boolean taskReady = false;
        boolean alertMade = true;
        Long SDate = Convert.makeLongFromTimeFields(shours, smin, ssec);
        Long EDate = Convert.makeLongFromTimeFields(ehours, emin, esec);
        Long lDate = Convert.makeLongFromTimeFields(hours, min, sec);
        if (ValidateController.isEmpty(Title)) {
            alertText = "Title field should be filled";
        } else {
            String strInterval = makeStrFromInterval();
            if (Rep.isSelected()) {
                if (start.getValue() == null || end.getValue() == null) {
                    alertText = "Date field should be filled";
                } else if ( SDate == null || EDate == null) {
                    alertText = "At least one of time fields in one row should be filled";
                } else if (strInterval == null) {
                    alertText = "At least one of \"interval\" fields in one row should be filled";
                } else if (interval == 0) {
                    alertText = "\"Interval\" must be greater than zero!";
                } else {
                    Date startD = Convert.makeDate(start, SDate);
                    Date endD = Convert.makeDate(end, EDate);
                    if (endD.getTime() < startD.getTime() + interval * 1000) {
                        alertText = "End time of the task must be greater than Start time + interval";
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Title: \'").append(Title.getText()).append("', startTime: ");
                        builder.append(new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy").format(startD)).append(", endTime: ");
                        builder.append(new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy").format(endD)).append(", repeatInterval: ");
                        builder.append(strInterval).append(", active: ");
                        if (active.isSelected())
                            builder.append("true;");
                        else
                            builder.append("false;");
                        task = builder.toString();
                        taskReady = true;
                        alertMade = false;
                    }
                }
            } else if (NRep.isSelected()) {
                if (date.getValue() == null) {
                    alertText = "Date field should be filled";
                } else if (Convert.makeLongFromTimeFields(hours, min, sec) == null) {
                    alertText = "At least one of time fields should be filled";
                } else {
                    StringBuilder builder = new StringBuilder();
                    Date time = Convert.makeDate(date, lDate);
                    builder.append("Title: \'").append(Title.getText()).append("', time: ");
                    builder.append(new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy").format(time)).append(", active: ");
                    if (active.isSelected())
                        builder.append("true;");
                     else
                        builder.append("false;");
                    task = builder.toString();
                    taskReady = true;
                    alertMade = false;
                }
            } else alertText = "Chose one of RadioButtons before adding Task";
        }
        if (alertMade) WindowMaker.alertWindowWarning(alertTitle, alertHeader, alertText);
        return taskReady;
    }

    /**Method for showing task content on scene*/
    private void setTaskValuesToShow() {
        Title.setText(Controller.tTitle);
        if (Controller.tActive.equals("Active")) active.setSelected(true);
        if (Controller.tStrInterval != null) {
            Rep.setSelected(true);
            showRepRadioBtnContent();
            try {
                makeTimeAndDateToShow(new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(Controller.tSDate), shours, smin, ssec, start);
                makeTimeAndDateToShow(new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(Controller.tEDate), ehours, emin, esec, end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            makeIntervalToShow(Controller.tIntInterval, idays, ihours, imin, isec);
        } else {
            showNotRepRadioBtnContent();
            NRep.setSelected(true);
            try {
                makeTimeAndDateToShow(new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(Controller.tDate), hours, min, sec, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**Method for making String from repeatInterval fields*/
    private String makeStrFromInterval() {
        if (!ValidateController.isEmpty(idays) || !ValidateController.isEmpty(ihours)
                || !ValidateController.isEmpty(imin) || !ValidateController.isEmpty(isec)) {
            ValidateController.setZeroIfEmpty(idays);
            ValidateController.setZeroIfEmpty(ihours);
            ValidateController.setZeroIfEmpty(imin);
            ValidateController.setZeroIfEmpty(isec);
            StringBuilder builder = new StringBuilder();
            int days = Integer.parseInt(ValidateController.removeZeroBeforeNumber(idays.getText()));// * 86400;
            int hours = Integer.parseInt(ValidateController.removeZeroBeforeNumber(ihours.getText()));// * 3600;
            int minutes = Integer.parseInt(ValidateController.removeZeroBeforeNumber(imin.getText()));// * 60;
            int seconds = Integer.parseInt(ValidateController.removeZeroBeforeNumber(isec.getText()));
            builder.append((days == 0? "": days + (days > 1 ? " days " : " day ")));
            builder.append((hours == 0? "": hours + (hours > 1 ? " hours " : " hour ")));
            builder.append((minutes == 0? "": minutes + (minutes > 1 ? " minutes " : " minute ")));
            builder.append((seconds == 0? "": seconds + (seconds > 1 ? " seconds " : " second ")));
            interval = days * 86400 + hours * 3600 + minutes * 60 + seconds;
            return builder.toString().trim();
        } else return null;
    }


    /**Method for making Date from text fields
     * @param date Date to make
     * @param h hours TextField
     * @param m minutes TextField
     * @param s seconds TextField
     * @param datePicker DatePicker date to get
     */
    private void makeTimeAndDateToShow(Date date, TextField h, TextField m, TextField s, DatePicker datePicker) {
        LocalDate startDateToSow = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long timeToParse = (date.getTime() - Date.from(startDateToSow.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()) / 1000;
        long hours =  (timeToParse / 3600) % 24;
        long minutes = (timeToParse / 60) % 60;
        long seconds = timeToParse % 60;
        h.setText(String.valueOf(hours));
        m.setText(String.valueOf(minutes));
        s.setText(String.valueOf(seconds));
        datePicker.setValue(startDateToSow);
    }

    /**Method for making Interval from text fields
     * @param interval int task interval
     * @param d days TextField;
     * @param h hours TextField
     * @param m minutes TextField
     * @param s seconds TextField
     */
    private void makeIntervalToShow(int interval, TextField d, TextField h, TextField m, TextField s) {
        int days = interval / 86400;
        int hours = (interval / 3600) % 24;
        int minutes = (interval / 60) % 60;
        int seconds = interval % 60;
        d.setText(String.valueOf(days));
        h.setText(String.valueOf(hours));
        m.setText(String.valueOf(minutes));
        s.setText(String.valueOf(seconds));
    }

    /**Method for show repeated task radioButton content*/
    private void showRepRadioBtnContent() {
        RepVBox.managedProperty().bind(RepVBox.visibleProperty());
        RepVBox.setVisible(true);
        NRepHBox.managedProperty().bind(NRepHBox.visibleProperty());
        NRepHBox.setVisible(false);
        ValidateController.hoursMinSecondsValidate(shours, smin, ssec);
        ValidateController.hoursMinSecondsValidate(ehours, emin, esec);
        ValidateController.daysValidate(idays);
        ValidateController.hoursMinSecondsValidate(ihours, imin, isec);
    }

    /**Method for show not repeated task radioButton content*/
    private void showNotRepRadioBtnContent() {
        NRepHBox.managedProperty().bind(NRepHBox.visibleProperty());
        NRepHBox.setVisible(true);
        RepVBox.managedProperty().bind(RepVBox.visibleProperty());
        RepVBox.setVisible(false);
        ValidateController.hoursMinSecondsValidate(hours, min, sec);
    }


    /**Method for showing tip on fields which may/should be filled*/
    private void ShowToolTip() {
        Title.setTooltip(new Tooltip("Task title"));
        active.setTooltip(new Tooltip("Check to make active task"));
        ValidateController.toolTipForDateAndTime(shours, smin, ssec, start);
        ValidateController.toolTipForDateAndTime(ehours, emin, esec, end);
        ValidateController.toolTipForDateAndTime(hours, min, sec, date);
        ValidateController.toolTipForInterval(idays, ihours, imin, isec);
        Rep.setTooltip(new Tooltip("Press to make repeated task"));
        NRep.setTooltip(new Tooltip("Press to make not repeated task"));
    }
}
