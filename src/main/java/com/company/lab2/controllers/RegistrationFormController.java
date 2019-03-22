package com.company.lab2.controllers;

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
    private Label singIn;
    @FXML
    private TextField login;
    @FXML
    private TextField email;

    @FXML
    void initialize() {
        assert registrateBtn != null : "fx:id=\"registrateBtn\" was not injected: check your FXML file 'RegistrationForm.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'RegistrationForm.fxml'.";
        assert passwordConfirm != null : "fx:id=\"passwordConfirm\" was not injected: check your FXML file 'RegistrationForm.fxml'.";
        assert name != null : "fx:id=\"name\" was not injected: check your FXML file 'RegistrationForm.fxml'.";
        assert singIn != null : "fx:id=\"singIn\" was not injected: check your FXML file 'RegistrationForm.fxml'.";
        assert login != null : "fx:id=\"login\" was not injected: check your FXML file 'RegistrationForm.fxml'.";
        assert email != null : "fx:id=\"email\" was not injected: check your FXML file 'RegistrationForm.fxml'.";

    }
}
