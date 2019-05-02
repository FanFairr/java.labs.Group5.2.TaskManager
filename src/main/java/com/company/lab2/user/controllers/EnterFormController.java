package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EnterFormController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private PasswordField password;
    @FXML
    private Button registrationBtn;
    @FXML
    private TextField login;
    @FXML
    private Button signInBtn;

    static String logIn;

    @FXML
    void initialize() {
        WindowMaker.getStage().setOnCloseRequest(event -> Controller.interrupt());
        login.setTooltip(new Tooltip("Your login"));
        password.setTooltip(new Tooltip("Your password"));
        signInBtn.setOnAction(event -> {
            final String alertTitle = "Warning";
            final String alertHeader = "Action mistake";
            final String alertText ="Login & password fields should be filled";
            if (ValidateController.isEmpty(login)|| password.getText().trim().equals("")) {
                WindowMaker.alertWindowWarning(alertTitle, alertHeader, alertText);
            } else {
                logIn = login.getText();
                Controller.signIn(login.getText(), password.getText());
            }
        });
        registrationBtn.setOnMouseClicked(event -> {
            Stage current = WindowMaker.getStage();
            final String path = "/view/user/RegistrationForm.fxml";
            final String header = "Registration";
            Platform.runLater(() -> WindowMaker.closeWindow(current));
            WindowMaker.makeWindow(path, header);
        });

    }
}
