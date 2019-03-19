package com.company.lab2.controllers;

import com.company.lab2.Controller;
import com.company.lab2.model.Task;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.Stage;

/**
 * Class controller for Main.fxml view
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

    private static ObservableList<Task> TaskList;
    private static Task task;
    private TimeToDoTask alarm;
    private static boolean saved = true;


    @FXML
    void initialize() {
        Stage stage = WindowMaker.getStage();
        stageIsClosing(stage);
        TaskList = Controller.getFromFileToTaskList();
        setItemsInViewList(taskListView, TaskList);
        multipleSelectionChoose(taskListView);
        alarm();
        catchTaskListChanges();
        addBtn.setOnAction(event -> {
            final String path = "/view/AddOrChangeTask.fxml";
            final String header = "Task Adding";
            WindowMaker.makeWindow(path, header);
        });
        makeClBtn.setOnAction(event -> {
            final String path = "/view/MakeCalendar.fxml";
            final String header = "Calendar";
            WindowMaker.makeWindow(path, header);
        });
        saveBtn.setOnAction(event -> Controller.saveInFile(TaskList));
    }

    /**Method create new Thread and start it.
     * To make message for user
     * when its time to do task.
     */
    private void alarm(){
        alarm = new TimeToDoTask();
        alarm.start();
    }

    /**Method for reacting on taskList changing
     * If taskList was changed
     * interrupt current alarm Thread
     * and make new according to changes
     */
    private void catchTaskListChanges(){
        TaskList.addListener((ListChangeListener<Task>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    if (alarm != null) {
                        alarm.interrupt();
                        alarm();
                    }
                }
            }
        });
    }

    /**Method react on request for closing window
     * close window if tasks are saved or not changed
     * else generate new window to confirm closing without saving
     * @param stage stage to request
     */
    private void stageIsClosing(Stage stage) {
        stage.setOnCloseRequest(event -> {
            if(saved)
                alarm.interrupt();
            else {
                final String path = "/view/Confirm.fxml";
                final String header = "Confirm exit";
                WindowMaker.makeWindow(path, header);
                if (ConfirmController.isExit()) {
                    alarm.interrupt();
                } else event.consume();
            }
        });
    }

    /**Method for setting items in TaskListView
     * @param taskListView where to sat
     * @param taskList items to set
     */
    private static void setItemsInViewList(ListView<Task> taskListView, ObservableList<Task> taskList) {
        taskListView.refresh();
        taskListView.setItems(taskList);
        taskListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(Controller.formatListViewItems(item));
                }
            }
        });
    }

    /**Method for selecting items in TaskListView
     * @param taskListView where to select
     */
    private static void multipleSelectionChoose(ListView<Task> taskListView) {
        MultipleSelectionModel<Task> selectionModel = taskListView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> taskListView.setOnMouseClicked(event -> {
            if (newValue != null) {
                //System.out.println("Selected: " + newValue.toString());
                task = newValue;
                final String path = "/view/Task.fxml";
                final String header = "Task";
                WindowMaker.makeWindow(path, header);
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

    public static ObservableList<Task> getTaskList() {
        return TaskList;
    }

    static void setTaskList(ObservableList<Task> taskList) {
        TaskList = taskList;
    }

    public static void setSaved(boolean value) {
        saved = value;
    }

}