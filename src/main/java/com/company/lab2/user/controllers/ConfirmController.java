package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


/**
 * Class controller for Confirm.fxml view.user
 */
public class ConfirmController {

    @FXML
    private Button Deny;
    @FXML
    private Button Confirm;
    @FXML
    private Label label;
    private Stage confirmStage;
    private static boolean exit;

    @FXML
    void initialize() {
        confirmStage = WindowMaker.getStage();
        if (confirmStage.getTitle().matches("Confirm exit")) {
            label.setText("Exit without saving changes?");
            Confirm.setOnAction(event -> {
                exit = true;
                WindowMaker.closeWindow(confirmStage);
            });
        } else if (confirmStage.getTitle().matches("Confirm deleting")){
            label.setText("Remove " + Controller.tTitle);
            Confirm.setOnAction(event -> {
                Controller.deleteTask(MainController.getTask());
                WindowMaker.closeWindow(confirmStage);
                Platform.runLater(() -> WindowMaker.closeWindow(TaskController.getStage()));
            });
        }
        Deny.setOnAction(event -> WindowMaker.closeWindow(confirmStage));
    }
}
