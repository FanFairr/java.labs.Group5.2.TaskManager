package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import com.company.lab2.user.model.Task;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

/**
 * Class controller for Main.fxml view.user
 */
public class MainController {

    @FXML
    private Button addBtn;
    @FXML
    private Button makeClBtn;
    @FXML
    private Button adminBtn;
    @FXML
    private ListView<Task> taskListView;

    private static ObservableList<Task> TaskList;
    private static Task task;
    private static Notification alarm;
    private static boolean isAdmin = true;


    @FXML
    void initialize() {
        Stage stage = WindowMaker.getStage();
        stage.setOnCloseRequest(event -> {
            Controller.exitWork();
            alarm.interrupt();
        });
        TaskList = Controller.taskList;
        setItemsInViewList(taskListView, TaskList);
        multipleSelectionChoose();
        notification();
        addBtn.setOnAction(event -> {
            final String path = "/view/user/AddOrChangeTask.fxml";
            final String header = "Task Adding";
            WindowMaker.makeWindow(path, header, Modality.WINDOW_MODAL);
        });
        makeClBtn.setOnAction(event -> {
            final String path = "/view/user/MakeCalendar.fxml";
            final String header = "Calendar";
            WindowMaker.makeWindow(path, header, Modality.WINDOW_MODAL);
        });

        adminBtn.setOnAction(event -> {
            if (isAdmin) {
                final String path = "/view/user/ServerScene.fxml";
                final String header = "Account";
                WindowMaker.makeWindow(path, header, Modality.WINDOW_MODAL);
            } else {
                final String alertTitle = "Warning";
                final String alertHeader = "You got no rights";
                final String alertText ="Excess denied";
                WindowMaker.alertWindowWarning(alertTitle, alertHeader, alertText);
            }
        });
    }

    /**Method create new Thread and start it.
     * To make message for view.user
     * when its time to do task.
     */
    private static void notification(){
        alarm = new Notification();
        alarm.start();
    }

    /**Method for reacting on taskList changing
     * If taskList was changed
     * interrupt current alarm Thread
     * and make new according to changes
     */
    public static void notificationInterrupt(){
        if (alarm != null) {
            alarm.interrupt();
            notification();
        }
    }

    /**Method for setting items in TaskListView
     * @param taskListView where to sat
     * @param taskList items to set
     */
    private void setItemsInViewList(ListView<Task> taskListView, ObservableList<Task> taskList) {
        taskListView.refresh();
        taskListView.setItems(taskList);
        taskListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatListViewItems(item));
                }
            }
        });
    }

    /**Method for formatting ListView Items
     */
    private String formatListViewItems(Task item) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        if (item.isRepeated()) {
            return "Title: '" + item.getTitle() +
                    "', startTime: " + format.format(item.getStartTime()) +
                    ", endTime: " +  format.format(item.getEndTime()) +
                    ", repeatInterval: " + ConvertController.getStringFromRepeatInterval(item.getRepeatInterval()) +
                    ", active: " + item.isActive() + ";";
        }
        else {
            return"Title: '" + item.getTitle() +
                    "', time: " +  format.format(item.getTime()) +
                    ", active: " + item.isActive() + ";";
        }
    }


    /**Method for selecting items in TaskListView
     */
    private void multipleSelectionChoose() {
        MultipleSelectionModel<Task> selectionModel = taskListView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> taskListView.setOnMouseClicked(event -> {
            if (newValue != null) {
                //System.out.println("Selected: " + newValue.toString());
                task = newValue;
                final String path = "/view/user/Task.fxml";
                final String header = "Task";
                WindowMaker.makeWindow(path, header, Modality.WINDOW_MODAL);
                taskListView.refresh();
            }
        }));
    }


    public static Task getTask() {
        return task;
    }

    public static void setTask(Task task) {
        MainController.task = task;
    }

    public static void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}