package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;

public class AdminPanelController {

    @FXML
    private ListView<String> usersList;
    @FXML
    private Button rebutBtn;
    @FXML
    private Button banBtn;
    @FXML
    private Button grantBtn;

    private String user;

    public void initialize() {
        usersList.setItems(Controller.usersList);
        MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> usersList.setOnMouseClicked(event -> {
            if (newValue != null) {
                user = newValue;
            }
        }));
        rebutBtn.setOnAction(event -> Controller.rebut());
        banBtn.setOnAction(event -> {
            Controller.banned(user);
            Controller.getAdminPanelData();
            usersList.refresh();
        });
        grantBtn.setOnAction(event -> {
            Controller.grantAdmin(user);
            Controller.getAdminPanelData();
            usersList.refresh();
        });
    }


}
