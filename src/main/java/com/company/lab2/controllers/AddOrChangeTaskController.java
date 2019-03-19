package com.company.lab2.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.company.lab2.model.Task;

/**
 * Class controller for AddOrChangeTask.fxml view
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

    private Task task;

    @FXML
    void initialize() {
        Stage stage = WindowMaker.getStage();
        ShowToolTip();
        hideContent();
        Rep.setOnAction(event -> showRepRadioBtnContent());
        NRep.setOnAction(event -> showNotRepRadioBtnContent());
        if (stage.getTitle().matches("Task Adding")) {
            Header.setText("Adding new Task");
            AddBtn.setText("Add");
            AddBtn.setOnAction(event -> {
                if (MakeNewTask()) {
                    if (active.isSelected()) task.setActive(true);
                    ObservableList<Task> taskList = MainController.getTaskList();
                    taskList.add(task);
                    MainController.setTaskList(taskList);
                    MainController.setSaved(false);
                    WindowMaker.closeWindow(stage);
                }
            });
        } else if (stage.getTitle().matches("Task Changing")){
            Header.setText("Change Task");
            task = MainController.getTask();
            setTaskValuesToShow();
            AddBtn.setText("Save");
            AddBtn.setOnAction(event -> {
                Task oldTask = task;
                if (MakeNewTask()) {
                    if (active.isSelected()) task.setActive(true);
                    if (!task.equals(oldTask)) {
                        ObservableList<Task> taskList = MainController.getTaskList();
                        int position = taskList.indexOf(oldTask);
                        taskList.remove(position);
                        taskList.add(position, task);
                        MainController.setTaskList(taskList);
                        MainController.setTask(task);
                        MainController.setSaved(false);
                        WindowMaker.closeWindow(stage);
                    } else WindowMaker.closeWindow(stage);
                }
            });
        }
        ExitBtn.setOnAction(event -> WindowMaker.closeWindow(stage));
    }
    /**Method for hiding content on scene
     */
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
        Long SDate = makeLongFromTimeFields(shours, smin, ssec);
        Long EDate = makeLongFromTimeFields(ehours, emin, esec);
        Long lDate = makeLongFromTimeFields(hours, min, sec);
        if (ValidateController.isEmpty(Title)) {
            alertText = "Title field should be filled";
        } else {
            Integer intInterval = makeIntFromInterval();
            if (Rep.isSelected()) {
                if (start.getValue() == null || end.getValue() == null) {
                    alertText = "Date field should be filled";
                } else if ( SDate == null || EDate == null) {
                    alertText = "At least one of time fields in one row should be filled";
                } else if (intInterval == null) {
                    alertText = "At least one of \"interval\" fields in one row should be filled";
                } else if (intInterval == 0) {
                    alertText = "\"Interval\" must be greater than zero!";
                } else {
                    int interval = intInterval;
                    Date startD = makeDate(start, SDate);
                    Date endD = makeDate(end, EDate);
                    if (endD.getTime() < startD.getTime() + interval) {
                        alertText = "End time of the task must be greater than Start time + interval";
                    } else {
                        task = new Task(Title.getText(), startD, endD, interval);
                        taskReady = true;
                        alertMade = false;
                    }
                }
            } else if (NRep.isSelected()) {
                if (date.getValue() == null) {
                    alertText = "Date field should be filled";
                } else if (makeLongFromTimeFields(hours, min, sec) == null) {
                    alertText = "At least one of time fields should be filled";
                } else {
                    Date time = makeDate(date, lDate);
                    task = new Task(Title.getText(), time);
                    taskReady = true;
                    alertMade = false;
                }
            } else alertText = "Chose one of RadioButtons before adding Task";
        }
        if (alertMade) WindowMaker.alertWindowWarning(alertTitle, alertHeader, alertText);
        return taskReady;
    }

    /**Method for showing task content on scene
     */
    private void setTaskValuesToShow() {
        Title.setText(task.getTitle());
        if (task.isActive()) active.setSelected(true);
        if (task.isRepeated()) {
            Rep.setSelected(true);
            showRepRadioBtnContent();
            makeTimeAndDateToShow(task.getStartTime(), shours, smin, ssec, start);
            makeTimeAndDateToShow(task.getEndTime(), ehours, emin, esec, end);
            makeIntervalToShow(task.getRepeatInterval(), idays, ihours, imin, isec);
        } else if (!task.isRepeated()){
            showNotRepRadioBtnContent();
            NRep.setSelected(true);
            makeTimeAndDateToShow(task.getTime(), hours, min, sec, date);
        }
    }

    /**Method for making Integer from repeatInterval
     */
    private Integer makeIntFromInterval() {
        if (!ValidateController.isEmpty(idays) || !ValidateController.isEmpty(ihours)
                || !ValidateController.isEmpty(imin) || !ValidateController.isEmpty(isec)) {
            ValidateController.setZeroIfEmpty(idays);
            ValidateController.setZeroIfEmpty(ihours);
            ValidateController.setZeroIfEmpty(imin);
            ValidateController.setZeroIfEmpty(isec);
            int days = Integer.parseInt(ValidateController.removeZeroBeforeNumber(idays.getText())) * 86400;
            int hours = Integer.parseInt(ValidateController.removeZeroBeforeNumber(ihours.getText())) * 3600;
            int minutes = Integer.parseInt(ValidateController.removeZeroBeforeNumber(imin.getText())) * 60;
            int seconds = Integer.parseInt(ValidateController.removeZeroBeforeNumber(isec.getText()));
            return days + hours + minutes + seconds;
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

    /**Method for show repeated task radioButton content
     */
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

    /**Method for show not repeated task radioButton content
     */
    private void showNotRepRadioBtnContent() {
        NRepHBox.managedProperty().bind(NRepHBox.visibleProperty());
        NRepHBox.setVisible(true);
        RepVBox.managedProperty().bind(RepVBox.visibleProperty());
        RepVBox.setVisible(false);
        ValidateController.hoursMinSecondsValidate(hours, min, sec);
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

    /**Method for showing tip on fields which may/should be filled
     */
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