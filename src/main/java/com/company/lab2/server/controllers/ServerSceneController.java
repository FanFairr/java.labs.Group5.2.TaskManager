package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.TaskIO;
import com.company.lab2.server.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 */
public class ServerSceneController {
    private ObservableList<User> observableList;
    private ServerSocket serverSocket;
    private ArrayList<Socket> socketList = new ArrayList<>();

    private ArrayList<User> usersList = new ArrayList<>();
    private TreeMap<String, ArrayList<Task>> tasksList = new TreeMap<>();

    @FXML
    private TableView<User> table;
    @FXML
    private TableColumn login;
    @FXML
    private TableColumn password;
    @FXML
    private TableColumn isBanned;
    @FXML
    private TableColumn admin;

    public void setParams(ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList, ServerSocket serverSocket, ArrayList<Socket> socketList) {
        this.usersList = usersList;
        this.tasksList = tasksList;
        this.serverSocket = serverSocket;
        this.socketList = socketList;
        observableList = FXCollections.observableArrayList(usersList);

        login.setCellValueFactory(new PropertyValueFactory<User, String>("login"));
        password.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        isBanned.setCellValueFactory(new PropertyValueFactory<User, Boolean>("banned"));
        admin.setCellValueFactory(new PropertyValueFactory<User, String>("admin"));

        table.setItems(observableList);
    }

    public void rebut(ActionEvent actionEvent) {
        try {
            synchronized (socketList) {
                for (Socket socket : socketList) {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

                    printWriter.println("Exit");
                    printWriter.flush();
                }
            }

            serverSocket.close();
            serverSocket = new ServerSocket(1488);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void banUnban(ActionEvent actionEvent) {
        User user = table.getSelectionModel().getSelectedItem();
        user.setBanned(!user.isBanned());
        table.refresh();
    }

    public void exit(ActionEvent actionEvent) {
        synchronized (tasksList){
            synchronized (usersList) {
                TaskIO readerWriter = new TaskIO();
                readerWriter.writeData(tasksList, usersList);

                try {
                    for (Socket socket : socketList) {
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                        printWriter.println("Exit");
                        printWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Platform.exit();
                System.exit(0);
            }
        }
    }

    public void refresh(ActionEvent actionEvent) {
        synchronized (usersList) {
            observableList.removeAll();
            observableList.addAll(usersList);
            table.refresh();
        }
    }
}
