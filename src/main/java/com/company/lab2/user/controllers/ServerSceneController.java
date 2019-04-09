package com.company.lab2.user.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;


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

    @FXML
    void initialize() {

    }
}