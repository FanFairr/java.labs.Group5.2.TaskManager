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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * class for working with events of the "ServerScene" window
 */
public class ServerSceneController {
    private Logger logger = Logger.getLogger(ServerSceneController.class);

    /** list of clients */
    private static ObservableList<User> observableList;
    /** server socket */
    private ServerSocket serverSocket;
    /** client socket list */
    private static ArrayList<Socket> socketList = new ArrayList<>();

    /** list of clients */
    private ArrayList<User> usersList = new ArrayList<>();
    /** information of client tasks */
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

    /**
     * method for initializing components
     * @param usersList - list of clients
     * @param tasksList - information of client tasks
     * @param serverSocket - server socket
     * @param socketList - client socket list
     */
    public void setParams(ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList, ServerSocket serverSocket, ArrayList<Socket> socketList) {
        this.usersList = usersList;
        this.tasksList = tasksList;
        this.serverSocket = serverSocket;
        ServerSceneController.socketList = socketList;
        observableList = FXCollections.observableArrayList(usersList);

        login.setCellValueFactory(new PropertyValueFactory<User, String>("login"));
        password.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        isBanned.setCellValueFactory(new PropertyValueFactory<User, Boolean>("banned"));
        admin.setCellValueFactory(new PropertyValueFactory<User, String>("admin"));

        table.setItems(observableList);
    }

    /**
     * server reboot event handler
     * @param actionEvent - action event
     */
    public void rebut(ActionEvent actionEvent) {
        try {
            synchronized (socketList) {
                for (Socket socket : socketList) {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println("Exit");
                    printWriter.flush();
                }
                socketList.clear();
            }

        } catch (IOException e) {
            logger.error("class ServerSceneController line 90 Error when working with sockets");
        }
    }

    /**
     * event handler - client's ban
     * @param actionEvent - action event
     */
    public void banUnban(ActionEvent actionEvent) {
        User user = table.getSelectionModel().getSelectedItem();
        user.setBanned(!user.isBanned());
        table.refresh();
    }

    /**
     * event handler - server shutdown
     * @param actionEvent - action event
     */
    public void exit(ActionEvent actionEvent) {
        synchronized (tasksList){
            synchronized (usersList) {
                TaskIO readerWriter = new TaskIO();
                readerWriter.writeData(tasksList, usersList);

                try {
                    for (Socket socket : socketList) {
                        if (!socket.isClosed()) {
                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                            printWriter.write("Exit\n");
                            printWriter.flush();
                        }
                    }
                } catch (IOException e) {
                    logger.error("class ServerSceneController line 123 Error when working with sockets");
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error("class ServerSceneController line 129 Error while working with thread");
                }
                Platform.exit();
                System.exit(0);
            }
        }
    }

    /**
     * event handler - update table
     * @param actionEvent - action event
     */
    public void refresh(ActionEvent actionEvent) {
        synchronized (usersList) {
            observableList.clear();
            observableList.addAll(usersList);
            table.refresh();
        }
    }

    /**
     * event handler - giving the admin rights
     * @param actionEvent - action event
     */
    public void adminka(ActionEvent actionEvent) {
        User user = table.getSelectionModel().getSelectedItem();
        user.setAdmin("admin".equals(user.getAdmin()) ? "false" : "admin");
        table.refresh();
    }
}
