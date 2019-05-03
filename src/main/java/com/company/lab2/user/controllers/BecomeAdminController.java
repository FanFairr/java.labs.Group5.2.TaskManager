package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BecomeAdminController {

        @FXML
        private ResourceBundle resources;
        @FXML
        private URL location;
        @FXML
        private TextField Code;
        @FXML
        private Button SendBtn;
        @FXML
        void initialize() {
            SendBtn.setOnAction(event -> {
                if (Code.getText().isEmpty()) {
                    WindowMaker.alertWindowInf("Code is empty", "Code field is empty", "pls filed code field before sending");
                } else {
                    Stage current = WindowMaker.getStage();
                    Controller.becomeAdmin(Code.getText());
                    WindowMaker.closeWindow(current);
                }
            });
        }
    }