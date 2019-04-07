package com.company.lab2;

import com.company.lab2.serverControllers.ServerSceneController;
import com.company.lab2.serverControllers.ServerThread;
import com.company.lab2.serverModel.Task;
import com.company.lab2.serverModel.TaskIO;
import com.company.lab2.serverModel.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

public class Server extends Application {
    private static ServerSocket serverSocket;
    private static ArrayList<Socket> socketList = new ArrayList<>();

    private final static ArrayList<User> usersList = new ArrayList<>();
    private final static TreeMap<String, ArrayList<Task>> tasksList = new TreeMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ServerScene.fxml"));
        Parent root = fxmlLoader.load();

        ((ServerSceneController) fxmlLoader.getController()).setParams(usersList, tasksList, serverSocket, socketList);

        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 444, 318));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
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
        });
    }

    public static void main(String[] args) {
        TaskIO taskIO = new TaskIO();

        /*usersList.add(new User("aa", "ba", false));
        usersList.add(new User("ab", "bb", false));
        usersList.add(new User("ac", "bc", false));
        usersList.add(new User("ad", "bd", false));
        usersList.add(new User("ae", "be", false));
        usersList.add(new User("af", "bf", false));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
        ArrayList<Task> arrayList = new ArrayList<>();
        arrayList.add(new Task("Hello", new Date(), false));
        for (User user : usersList) {
            tasksList.put(user.getLogin(), arrayList);
        }*/
        taskIO.readData(tasksList, usersList);

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(1488);
                while (true) {
                    synchronized (serverSocket) {
                        Socket socket = serverSocket.accept();
                        socketList.add(socket);
                        new ServerThread(socketList.get(socketList.size() - 1), usersList, tasksList).start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        launch(args);
    }
}
