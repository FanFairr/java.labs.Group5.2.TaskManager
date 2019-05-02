package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.Stage;

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
    private ListView<String> taskListView;
    private static String task;
    private static Notification alarm;

    @FXML
    void initialize() {
        final Stage stage = WindowMaker.getStage();
        stage.setOnCloseRequest(event -> {
            alarm.interrupt();
            Controller.interrupt();
        });

        taskListView.refresh();
        taskListView.setItems(Controller.taskList);
        multipleSelectionChoose();
        notification();
        addBtn.setOnAction(event -> {
            final String path = "/view/user/AddOrChangeTask.fxml";
            final String header = "Task Adding";
            WindowMaker.makeWindow(path, header);
        });
        makeClBtn.setOnAction(event -> {
            final String path = "/view/user/MakeCalendar.fxml";
            final String header = "Calendar";
            WindowMaker.makeWindow(path, header);
        });

        adminBtn.setOnAction(event -> {
            if (Controller.isAdmin()) {
                final String path = "/view/user/ServerScene.fxml";
                final String header = "Account";
                WindowMaker.makeWindow(path, header);
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


    /**Method for selecting items in TaskListView
     */
    private void multipleSelectionChoose() {
        MultipleSelectionModel<String> selectionModel = taskListView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> taskListView.setOnMouseClicked(event -> {
            if (newValue != null) {
                //System.out.println("Selected: " + newValue.toString());
                task = newValue;
                Controller.parsTaskStringRequest(task);
                final String path = "/view/user/Task.fxml";
                final String header = "Task";
                WindowMaker.makeWindow(path, header);
                taskListView.refresh();
            }
        }));
    }

    public static String getTask() {
        return task;
    }
    public static void setTask(String task) {
        MainController.task = task;
    }
}