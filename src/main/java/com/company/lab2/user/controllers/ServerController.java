package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ServerController {

    @FXML
    private ListView<String> usersList;

    public void initialize() {
        //usersList.setItems(Controller.getUsers());
    }

    public void rebut(ActionEvent actionEvent) {
        Controller.rebut();
    }

    public void banned(ActionEvent actionEvent) {
        Controller.banned("");
    }

    public void admin(ActionEvent actionEvent) {
        Controller.admin("");
    }
}
