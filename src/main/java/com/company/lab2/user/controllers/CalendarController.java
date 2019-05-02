
package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
    private Button MoreBtn;

    @FXML
    private TreeView<String> calendarTreeView;

    private Date start = MakeCalendarController.getStart();
    private Date end = MakeCalendarController.getEnd();
    private SortedMap<Date, Set<String>> calendar;
    private SortedMap<Date, Set<String>> calendarByDays = new TreeMap<>();

    @FXML
    void initialize() {
        MoreBtn.setVisible(false);
        calendar = Controller.tCalendar;
        fillTreeView();
        if (!calendar.lastKey().equals(Controller.lastKey))
            MoreBtn.setVisible(true);
        MoreBtn.setOnAction(event -> {
            Controller.calendar(calendar.lastKey(), Controller.lastKey);
            calendar.putAll(Controller.tCalendar);
            fillTreeView();
            if (calendar.lastKey().equals(Controller.lastKey))
                MoreBtn.setVisible(false);
        });
        if (!MoreBtn.isVisible()) {
            MoreBtn.setText("Exit");
            MoreBtn.setVisible(true);
            MoreBtn.setOnAction(event -> WindowMaker.closeWindow(WindowMaker.getStage()));
        }
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

    /**Method for filling TreeView*/
    private void fillTreeView() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Start.setText(format.format(start));
        End.setText(format.format(end));
        if (makeCalendarByDays()) {
            TreeItem<String> rootItem = new TreeItem<>("Root",null);
            rootItem.setExpanded(true);
            for (Date date: calendarByDays.keySet()) {
                TreeItem<String> day = new TreeItem<>(format.format(date));
                for (Date dateTime : calendar.keySet()) {
                    if (date.getTime() <= dateTime.getTime()
                            && dateTime.getTime() < (date.getTime() + 3600000 * 24)) {
                        for (String task : calendar.get(dateTime)) {
                            String taskToPut = task.substring(0, task.indexOf(", a"));
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
        }
    }
}