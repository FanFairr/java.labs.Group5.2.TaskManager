package com.company.lab2.user.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    private Button singInBtn;
    @FXML
    private Label registration;
    @FXML
    private TextField userName;

    @FXML
    void initialize() {
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'EnterForm.fxml'.";
        assert singInBtn != null : "fx:id=\"singInBtn\" was not injected: check your FXML file 'EnterForm.fxml'.";
        assert registration != null : "fx:id=\"registration\" was not injected: check your FXML file 'EnterForm.fxml'.";
        assert userName != null : "fx:id=\"userName\" was not injected: check your FXML file 'EnterForm.fxml'.";

    }
}
