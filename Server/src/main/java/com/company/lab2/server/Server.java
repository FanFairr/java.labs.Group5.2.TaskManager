package com.company.lab2.server;

import com.company.lab2.server.controllers.ServerSceneController;
import com.company.lab2.server.controllers.ServerThread;
import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.TaskIO;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

public class Server extends Application {
    /** logger */
    private static Logger logger = Logger.getLogger(Server.class);

    /** server socket */
    private static ServerSocket serverSocket;
    /** clients socket list */
    private static ArrayList<Socket> socketList = new ArrayList<>();

    /** active users list */
    private static LinkedList<User> activeUsers = new LinkedList<>();
    /** users list */
    private final static ArrayList<User> usersList = new ArrayList<>();
    /** information of client tasks */
    private final static TreeMap<String, ArrayList<Task>> tasksList = new TreeMap<>();
    /** a list of customers that want to get the admin rights */
    private static ArrayList<User> adminList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/server/ServerScene.fxml"));
        Parent root = fxmlLoader.load();

        ((ServerSceneController) fxmlLoader.getController()).setParams(usersList, tasksList, serverSocket, socketList);

        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 444, 318));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(t -> {
            Gson gson = new Gson();
            synchronized (tasksList){
                synchronized (usersList) {
                    TaskIO readerWriter = new TaskIO();
                    readerWriter.writeData(tasksList, usersList);

                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("adminka.txt"))) {
                        synchronized (adminList) {
                            bufferedWriter.write(gson.toJson(adminList));
                        }
                    } catch (IOException e) {
                        logger.error("class Server line 73 Error when working with sockets");
                    }

                    try {
                        for (Socket socket : socketList) {
                            if (!socket.isClosed()){
                                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                                printWriter.println("Exit");
                                printWriter.flush();
                            }
                        }
                    } catch (IOException e) {
                        logger.error("class Server line 85 Error when working with sockets");
                    }

                    Platform.exit();
                    System.exit(0);
                }
            }
        });
    }

    public static void main(String[] args) {
        TaskIO taskIO = new TaskIO();
        Gson gson = new Gson();

        taskIO.readData(tasksList, usersList);

        try {
            File file = new File("adminka.txt");
            file.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String str = bufferedReader.readLine();
            if (str != null && !str.equals("[]"))
                adminList = new ArrayList<>(Arrays.asList(gson.fromJson(str, User[].class)));
        } catch (IOException e) {
            logger.error("class Server line 110 Error when working with file adminka.txt");
        }

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(1488);
                while (true) {
                    synchronized (serverSocket) {
                        Socket socket = serverSocket.accept();
                        socketList.add(socket);
                        new ServerThread(socketList.get(socketList.size() - 1), usersList, tasksList, adminList, activeUsers).start();
                    }
                }
            } catch (IOException e) {
                logger.error("class Server line 124 Error when working with sockets");
            }
        }).start();

        launch(args);
    }
}
