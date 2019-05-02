
package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;



/**
 * Class controller for Calendar.fxml view.user
 */
public class CalendarController {

    @FXML
    private Label Start;
    @FXML
    private Label End;

    @FXML
    private Button ExitBtn;

    @FXML
    private TreeView<String> calendarTreeView;

    private Date start = MakeCalendarController.getStart();
    private Date end = MakeCalendarController.getEnd();
    private SortedMap<Date, Set<String>> calendar;
    private SortedMap<Date, Set<String>> calendarByDays = new TreeMap<>();

    @FXML
    void initialize() {
        fillTreeView();
        ExitBtn.setOnAction(event -> WindowMaker.closeWindow(WindowMaker.getStage()));
    }



    /**Method make calendar by days
     * @return true if made, else false
     */
    private boolean makeCalendarByDays() {
        if (calendar != null) {
            for (Date date:calendar.keySet()) {
                Date startDay = new Date(date.getTime() - 3600000 * 2 - date.getTime() % 86400000);
                calendarByDays.put(startDay, calendar.get(date));
            }
            return true;
        }
        return false;
    }


    /**Method for redacting visualization of tasks in TreeView*/
    private void redactTextInTreeView() {
        calendarTreeView.setCellFactory(p -> new TextFieldTreeCell<>(new StringConverter<String>(){
            @Override
            public String toString(String object) {
                String string = object.replace("Title: ","");
                String title = string.substring(0, string.indexOf("\'," + 1));
                String dString = object.replaceAll(".+time: ","");
                String date = dString.substring(0, dString.indexOf(",")) + ";";
                return title + date;
            }
            @Override
            public String fromString(String string) {
                return string;
            }
        }));
    }


    /**Method for filling TreeView*/
    private void fillTreeView() {
        calendar = Controller.tCalendar;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Start.setText(format.format(start));
        End.setText(format.format(end));
        if (makeCalendarByDays()) {
            TreeItem<String> rootItem = new TreeItem<>("Root",null);
            rootItem.setExpanded(true);
            for (Date date: calendarByDays.keySet()) {
                TreeItem<String> day = new TreeItem<>(format.format(date), null);
                for (Date dateTime : calendar.keySet()) {
                    if (date.getTime() <= dateTime.getTime()
                            && dateTime.getTime() < (date.getTime() + 3600000 * 24)) {
                        for (String task : calendar.get(dateTime)) {
                            String dString = task.replaceAll(".+time: ","");
                            String replace = dString.substring(0, dString.indexOf(","));
                            String taskToPut = task.replace(replace, new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(dateTime));
                            TreeItem<String> taskItem = new TreeItem<>(taskToPut);
                            day.getChildren().add(taskItem);
                        }
                    }
                }
                if (day.getChildren().size() != 0)
                    rootItem.getChildren().add(day);
            }
            calendarTreeView.setRoot(rootItem);
            calendarTreeView.setShowRoot(false);
            calendarTreeView.refresh();
            redactTextInTreeView();
        }
    }
}