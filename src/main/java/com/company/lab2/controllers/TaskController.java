package com.company.lab2.controllers;

import java.text.SimpleDateFormat;
import com.company.lab2.model.TaskIO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.company.lab2.model.Task;

/**
 * Class controller for Task.fxml view
 */
public class TaskController {

    @FXML
    private Button DeleteBtn;
    @FXML
    private Button CloseBtn;
    @FXML
    private Button ChangeBtn;

    @FXML
    private Label Title;
    @FXML
    private Label Date;
   @FXML
    private Label Start;
    @FXML
    private Label End;
    @FXML
    private Label Interval;

    @FXML
    private HBox EndBox;
    @FXML
    private HBox DateBox;
    @FXML
    private HBox StartBox;
    @FXML
    private HBox IntrevBox;

    private static Stage stage;

    @FXML
    void initialize() {
        stage = WindowMaker.getStage();
        showTaskData();
        ChangeBtn.setOnAction(event -> {
            final String path = "/view/AddOrChangeTask.fxml";
            final String header = "Task Changing";
            WindowMaker.makeWindow(path, header);
            showTaskData();
        });
        DeleteBtn.setOnAction(event -> {
            final String path = "/view/Confirm.fxml";
            final String header = "Confirm deleting";
            WindowMaker.makeWindow(path, header);
        });
        CloseBtn.setOnAction(event -> WindowMaker.closeWindow(stage));
    }

    /**Method for showing task data
     */
    private void showTaskData(){
        Task task = MainController.getTask();
        if (task != null){
            Title.setText(task.getTitle());
            SimpleDateFormat format = new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy");
            if (task.isRepeated()) {
                DateBox.setVisible(false);
                StartBox.setVisible(true);
                EndBox.setVisible(true);
                IntrevBox.setVisible(true);
                End.setText(format.format(task.getEndTime()));
                Start.setText(format.format(task.getStartTime()));
                Interval.setText(TaskIO.getStringFromRepeatInterval(task.getRepeatInterval()));
            } else {
                DateBox.setVisible(true);
                StartBox.setVisible(false);
                EndBox.setVisible(false);
                IntrevBox.setVisible(false);
                Date.setText(format.format(task.getTime()));
            }
        } else System.out.println("Null task TaskController");
    }

    static Stage getStage() {
        return stage;
    }
}