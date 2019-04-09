package com.company.lab2.user.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

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
    private Label signIn;
    @FXML
    private TextField login;
    @FXML
    private TextField email;

    @FXML
    void initialize() {
        /*signIn.setOnMouseClicked(event -> {
            Stage current = WindowMaker.getStage();
            final String path = "/view.user/EnterForm.fxml";
            final String header = "SignIn";
            WindowMaker.makeWindow(path, header);
            WindowMaker.closeWindow(current);
        });*/

    }
}
