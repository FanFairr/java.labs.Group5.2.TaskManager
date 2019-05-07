package com.company.lab2.server;

import com.company.lab2.server.controllers.ServerSceneController;
import com.company.lab2.server.controllers.ServerThread;
import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.TaskIO;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

public class Server extends Application {
    private static ServerSocket serverSocket;
    private static ArrayList<Socket> socketList = new ArrayList<>();

    private static LinkedList<User> activeUsers = new LinkedList<>();
    private final static ArrayList<User> usersList = new ArrayList<>();
    private final static TreeMap<String, ArrayList<Task>> tasksList = new TreeMap<>();
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

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
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
                            e.printStackTrace();
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
                            e.printStackTrace();
                        }

                        Platform.exit();
                        System.exit(0);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        TaskIO taskIO = new TaskIO();
        Gson gson = new Gson();

        /*usersList.add(new User("aa", "ba", false));
        usersList.add(new User("ab", "bb", false));
        usersList.add(new User("ac", "bc", false));
        usersList.add(new User("ad", "bd", false));
        usersList.add(new User("ae", "be", false));
        usersList.add(new User("af", "bf", false));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
        ArrayList<Task> arrayList = new ArrayList<>();
        arrayList.add(new Task("Hello", new Date(), false));
        for (User view.user : usersList) {
            tasksList.put(view.user.getLogin(), arrayList);
        }*/
        taskIO.readData(tasksList, usersList);

        try {
            File file = new File("adminka.txt");
            file.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String str = bufferedReader.readLine();
            if (str != null && !str.equals("[]"))
                adminList = (ArrayList<User>) Arrays.asList(gson.fromJson(str, User[].class));
        } catch (IOException e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }).start();

        launch(args);
    }
}
