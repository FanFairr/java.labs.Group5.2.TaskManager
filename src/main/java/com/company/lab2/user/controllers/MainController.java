package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import com.company.lab2.user.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
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
    private Button saveBtn;
    @FXML
    private ListView<Task> taskListView;

    private static Task task;
    private static Notification alarm;


    @FXML
    void initialize() {
        Stage stage = WindowMaker.getStage();
        stageIsClosing(stage);
        taskListView = Controller.getTaskListView();
        multipleSelectionChoose(taskListView);
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
        //TODO заменить кнопку?
        saveBtn.setOnAction(event -> {
            final String path = "/view/user/MyAccount.fxml";
            final String header = "Account";
            WindowMaker.makeWindow(path, header);
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


    /**Method react on request for closing window
     * close window if tasks are saved or not changed
     * else generate new window to confirm closing without saving
     * @param stage stage to request
     */
    private void stageIsClosing(Stage stage) {
        stage.setOnCloseRequest(event -> {
                alarm.interrupt();
        });
    }



    /**Method for selecting items in TaskListView
     * @param taskListView where to select
     */
    private void multipleSelectionChoose(ListView<Task> taskListView) {
        MultipleSelectionModel<Task> selectionModel = taskListView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> taskListView.setOnMouseClicked(event -> {
            if (newValue != null) {
                //System.out.println("Selected: " + newValue.toString());
                task = newValue;
                final String path = "/view/user/Task.fxml";
                final String header = "Task";
                WindowMaker.makeWindow(path, header);
                taskListView.refresh();
            }
        }));
    }

    //TODO перенести на сервер
    /**Method for setting items in TaskListView
     * @param Login view.user login
     */
    private void makeListView(String Login) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        //это твоя мапа с тасками
        // Map<String,TaskArr> map....
        /*for (Task someTask:map.get(Login);) {
            TaskList.add(someTask);
        }*/
        ListView<Task> taskListView = new ListView<>();
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
    //TODO перенести на сервер
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

    public static Task getTask() {
        return task;
    }

    public static void setTask(Task task) {
        MainController.task = task;
    }

}