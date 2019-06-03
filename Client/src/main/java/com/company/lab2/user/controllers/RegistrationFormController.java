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

/**
 * Class controller for RegistrationForm.fxml view.user
 */
public class RegistrationFormController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button registrateBtn;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordConfirm;
    @FXML
    private TextField name;
    @FXML
    private TextField login;
    @FXML
    private Button signInBtn;
    @FXML
    private TextField email;

    @FXML
    void initialize() {
        WindowMaker.getStage().setOnCloseRequest(event -> Controller.interrupt());
        login.setTooltip(new Tooltip("Your login"));
        password.setTooltip(new Tooltip("Your password"));
        passwordConfirm.setTooltip(new Tooltip("Confirm your password"));
        name.setTooltip(new Tooltip("Your name"));
        email.setTooltip(new Tooltip("Your email"));
        registrateBtn.setOnAction(event -> {
            boolean alertMade = true;
            final String alertTitle = "Warning";
            final String alertHeader = "Action mistake";
            String alertText = "";
            if (ValidateController.isEmpty(login) || ValidateController.isEmpty(name)
                    || ValidateController.isEmpty(email) ||"".equals(password.getText().trim())
                    || "".equals(passwordConfirm.getText().trim())) {
                alertText ="All fields should be filled";
            } else if (!password.getText().equals(passwordConfirm.getText())){
                alertText ="Passwords not match!";
            } else {
                alertMade = false;
                EnterFormController.logIn = login.getText();
                Controller.registration(login.getText(), password.getText());
            }
            if (alertMade)
                WindowMaker.alertWindowWarning(alertTitle, alertHeader, alertText);
        });
        signInBtn.setOnAction(event -> {
            Stage current = WindowMaker.getStage();
            final String path = "/view/user/EnterForm.fxml";
            final String header = "SignIn";
            Platform.runLater(() -> WindowMaker.closeWindow(current));
            WindowMaker.makeWindow(path, header);
        });

    }
}
