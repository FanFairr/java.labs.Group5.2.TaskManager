package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.Stage;

/**
 * Class controller for AdminPanel.fxml view.user
 */
public class AdminPanelController {

    @FXML
    private ListView<String> usersList;
    @FXML
    private Button rebutBtn;
    @FXML
    private Button banBtn;
    @FXML
    private Button grantBtn;

    /**selected user for actions*/
    private String user;

    public void initialize() {
        Stage stage = WindowMaker.getStage();
        if (stage.getTitle().matches("Admin Panel")) {
            usersList.setItems(Controller.usersList);
            if (!"SuperAdmin".equals(Controller.getAdminValue())) {
                grantBtn.setText("Exit");
                grantBtn.setOnAction(event -> WindowMaker.closeWindow(WindowMaker.getStage()));
            } else {
                grantBtn.setOnAction(event -> {
                    Controller.getPanelData("AdminsList:");
                    if (Controller.adminsList != null && Controller.adminsList.size() != 0) {
                        final String path = "/view/user/AdminPanel.fxml";
                        final String header = "Grant Admin Panel";
                        WindowMaker.makeWindow(path, header);
                        Controller.getPanelData("UsersList:");
                        usersList.setItems(Controller.usersList);
                    } else WindowMaker.alertWindowInf("AdminRequests", "There are no Requests", "Pls come later!");
                });
            }
            rebutBtn.setOnAction(event -> Controller.rebut());
            banBtn.setOnAction(event -> {
                Controller.banned(user);
                Controller.getPanelData("UsersList:");
                usersList.setItems(Controller.usersList);
            });
        } else if (stage.getTitle().matches("Grant Admin Panel")) {
            usersList.setItems(Controller.adminsList);
            grantBtn.setVisible(false);
            rebutBtn.setVisible(false);
            banBtn.setText("Grant");
            banBtn.setOnAction(event -> {
                Controller.grantAdmin(user);
                Controller.getPanelData("AdminsList:");
                if (Controller.adminsList != null && Controller.adminsList.size() != 0)
                    usersList.setItems(Controller.adminsList);
                else stage.close();
            });
        }

        MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> usersList.setOnMouseClicked(event -> {
            if (newValue != null) {
                user = newValue;
            }
        }));
    }
}
