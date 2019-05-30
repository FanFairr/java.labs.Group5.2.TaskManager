package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Class controller for Task.fxml view.user
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
    @FXML
    private Label Active;

    /**current stage*/
    private static Stage stage;

    @FXML
    void initialize() {
        stage = WindowMaker.getStage();
        showTaskData();
        ChangeBtn.setOnAction(event -> {
            final String path = "/view/user/AddOrChangeTask.fxml";
            final String header = "Task Changing";
            WindowMaker.makeWindow(path, header);
            showTaskData();
        });
        DeleteBtn.setOnAction(event -> {
            final String path = "/view/user/Confirm.fxml";
            final String header = "Confirm deleting";
            WindowMaker.makeWindow(path, header);
        });
        CloseBtn.setOnAction(event -> WindowMaker.closeWindow(stage));
    }

    /**Method for showing task data*/
    private void showTaskData(){
            Title.setText(Controller.tTitle);
            if (Controller.tStrInterval != null) {
                DateBox.setVisible(false);
                StartBox.setVisible(true);
                EndBox.setVisible(true);
                IntrevBox.setVisible(true);
                End.setText(Controller.tEDate);
                Start.setText(Controller.tSDate);
                Interval.setText(Controller.tStrInterval);
            } else {
                DateBox.setVisible(true);
                StartBox.setVisible(false);
                EndBox.setVisible(false);
                IntrevBox.setVisible(false);
                Date.setText(Controller.tDate);
            }
            if (Controller.tActive != null)
                Active.setText(Controller.tActive);
    }

    static Stage getStage() {
        return stage;
    }
}
