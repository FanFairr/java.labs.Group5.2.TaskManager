package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class controller for Connection.fxml view.user
 */
public class ConnectionController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button connectBtn;
    @FXML
    private TextField port;
    @FXML
    private TextField host1;
    @FXML
    private TextField host2;
    @FXML
    private TextField host3;
    @FXML
    private TextField host4;

    @FXML
    void initialize() {
        Stage stage = WindowMaker.getStage();
        stage.setOnCloseRequest(event -> {
            if (stage.equals(WindowMaker.getStage())) {
                Platform.exit();
                System.exit(0);
            }
        });
        ValidateController.portValidate(port);
        ValidateController.hostValidate(host1);
        ValidateController.hostValidate(host2);
        ValidateController.hostValidate(host3);
        ValidateController.hostValidate(host4);
        connectBtn.setOnAction(event -> {
            final String alertTitle = "Warning";
            final String alertHeader = "Action mistake";
            String alertText = "";
            boolean alertMade = true;
            if (port.getText() == null)
                alertText = "Port field should be filled";
            else if (host1.getText() == null || host2.getText() == null || host3.getText() == null || host4.getText() == null)
                alertText = "Host fields should be filled";
            else {
                String host = host1.getText() + "." + host2.getText() + "." + host3.getText() + "." + host4.getText();
                int porT = Integer.valueOf(port.getText());
                Socket client = null;
                try {
                    client = new Socket(host, porT);
                } catch (IOException e) {
                    alertText = "Failed connect to server! Try again or change host/port data!";
                }
                if (client != null) {
                    alertMade = false;
                    Controller.setClient(client);
                    Platform.runLater(stage::close);
                    final String path = "/view/user/EnterForm.fxml";
                    final String header = "SignIn";
                    WindowMaker.makeWindow(path, header);
                }
            }
            if (alertMade) WindowMaker.alertWindowWarning(alertTitle, alertHeader, alertText);
        });
    }
}