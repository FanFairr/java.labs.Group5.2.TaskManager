package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import com.company.lab2.user.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.company.lab2.user.Controller.logger;

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
    private TreeView<Task> calendarTreeView;

    private Date start = MakeCalendarController.getStart();
    private Date end = MakeCalendarController.getEnd();
    private SortedMap<Date, Set<Task>> calendar;
    private SortedMap<Date, Set<Task>> calendarByDays = new TreeMap<>();

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


    //TODO перенос на сервер
    /**Method for filling TreeView
     */
    private void fillTreeView() {
        calendar = Controller.getCalendar(start, end);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Start.setText(format.format(start));
        End.setText(format.format(end));
        if (makeCalendarByDays()) {
            TreeItem<Task> rootItem = new TreeItem<>(new Task("Root",null));
            rootItem.setExpanded(true);
            for (Date date: calendarByDays.keySet()) {
                TreeItem<Task> day = new TreeItem<>(new Task(format.format(date), null));
                for (Date dateTime : calendar.keySet()) {
                    if (date.getTime() <= dateTime.getTime()
                            && dateTime.getTime() < (date.getTime() + 3600000 * 24)) {
                        for (Task task : calendar.get(dateTime)) {
                            try {
                                Task taskToPut = task.clone();
                                taskToPut.setTime(dateTime);
                                TreeItem<Task> taskItem = new TreeItem<>(taskToPut);
                                day.getChildren().add(taskItem);
                            } catch (CloneNotSupportedException e) {
                                logger.error(e.getMessage(),e);
                                e.printStackTrace();
                            }
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
    /**Method for redacting visualization of tasks in TreeView
     */
    private void redactTextInTreeView() {
        calendarTreeView.setCellFactory(p -> new TextFieldTreeCell<>(new StringConverter<Task>(){
            @Override
            public String toString(Task object) {
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                String title = object.getTitle();
                String date = object.getTime() == null? "": ", time: " + format1.format(object.getTime()) +";";
                return title + date;
            }
            @Override
            public Task fromString(String string) {
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                string = string.replace("Title: '","");
                String title =  string.replaceAll("\', time:.+","");
                String date = string.replaceAll(".+ ","").replace(";","");
                try {
                    Date d = format1.parse(date);
                    return new Task(title,d);
                } catch (ParseException e) {
                    logger.error(e.getMessage(),e);
                    e.printStackTrace();
                }
                return new Task(title,null);
            }
        }));
    }
}
