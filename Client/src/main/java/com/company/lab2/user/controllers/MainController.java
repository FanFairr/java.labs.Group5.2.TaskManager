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

    /**selected task in taskListView*/
    private static String task;
    /**notification thread*/
    private static Notification alarm;

    @FXML
    void initialize() {
        final Stage stage = WindowMaker.getStage();
        stage.setOnCloseRequest(event -> {
            if (alarm != null)
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
                Controller.getPanelData("UsersList:");
                final String path = "/view/user/AdminPanel.fxml";
                final String header = "Admin Panel";
                WindowMaker.makeWindow(path, header);
            } else {
                if (Controller.waiting4Adm.equals("waiting")) {
                    WindowMaker.alertWindowInf("Waite", "You are in waiting list now", "Waite till SuperAdmin make you administrator");
                } else if (Controller.waiting4Adm.equals("notWaiting")) {
                    if (Controller.becomeAdmTry == 0) {
                        adminBtn.setText("Exit");
                        adminBtn.setOnAction(actionEvent -> {
                            if (alarm != null)
                                alarm.interrupt();
                            Controller.interrupt();
                        });
                    } else {
                        final String path = "/view/user/BecomeAdmin.fxml";
                        final String header = "BecomeAdmin";
                        WindowMaker.makeWindow(path, header);
                    }
                }
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
                task = newValue;
                Controller.parsTaskStringRequest(task);
                final String path = "/view/user/Task.fxml";
                final String header = "Task";
                WindowMaker.makeWindow(path, header);
                taskListView.refresh();
            }
        }));
    }

    /**method for getting current task
     * @return task*/
    public static String getTask() {
        return task;
    }
    /**method for setting current task
     * @param task task to set*/
    public static void setTask(String task) {
        MainController.task = task;
    }
}