package com.company.lab2.user;

import com.company.lab2.user.controllers.MainController;
import com.company.lab2.user.controllers.WindowMaker;
import com.company.lab2.user.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;

public class Controller extends Application {
    private static String login;
    public static ObservableList<Task> taskList = FXCollections.observableArrayList();
    private static ArrayList<Task> tasksArr;
    public final static Logger logger = Logger.getLogger(MainController.class);
    private static BufferedReader reader = null;
    private static PrintWriter writer = null;
    private static Gson gson = new Gson();
    @Override
    public void start(Stage primaryStage) {
        /*
        final String path = "/view.user/Main.fxml";
        final String header = "Task Manager";
        */
        final String path = "/view/user/EnterForm.fxml";
        final String header = "SignIn";
        WindowMaker.makeWindow(path, header, Modality.NONE);
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Socket client = new Socket("127.0.0.1", 1488);
                PrintWriter printWriter = new PrintWriter(client.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream());
                while (true) {
                    String response = reader.readLine();
                    if (!response.isEmpty()) {
                        switch (response) {
                            case "connected":
                                Type token = new TypeToken<ArrayList<Task>>() {
                                }.getType();
                                String list = reader.readLine();
                                if ("[]".equals(list)) {
                                    tasksArr = new ArrayList<>();
                                } else {
                                    tasksArr = gson.fromJson(list, token);
                                }
                                taskList.setAll(tasksArr);
                                Stage current = WindowMaker.getStage();
                                WindowMaker.makeWindow("/view.user/Main.fxml", "Task Manager", Modality.WINDOW_MODAL);
                                Platform.runLater(() -> WindowMaker.closeWindow(current));
                                break;
                            case "already exist login":
                                WindowMaker.alertWindowInf("Error", "Wrong login", "Login already exist");
                                break;
                            case "login not exist":
                                WindowMaker.alertWindowInf("Error", "Wrong login", "Login doesn't exist");
                                break;
                            case "wrong password":
                                WindowMaker.alertWindowInf("Error", "Wrong password", "Password is incorrect");
                                break;
                            case "banned":
                                WindowMaker.alertWindowInf("Nope", "U are banned", "Think about your ");
                                break;
                            case "true":
                                MainController.setAdmin(true);
                                break;
                            case "false":
                                MainController.setAdmin(false);
                                break;
                            case "wrong code":
                                WindowMaker.alertWindowInf("Error", "Wrong code", "Connect with mainAdmin to get code");
                                break;
                            case "congratulations":
                                WindowMaker.alertWindowInf("Cool", "Congratulations", "You are admin now");
                                break;
                            default:
                                System.out.println("smth wrong with response");
                                break;
                        }
                    }
                    Thread.sleep(10);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        launch(args);
    }

    public static void registration(String login, String name, String password) {
        writer.println("Registration: " + login + " " + password);
        writer.flush();
    }

    public static void signIn(String login, String password) {
        writer.println("Login: " + login + " " + password);
        writer.flush();
    }

    public static void addTask(Task task) {

        writer.write("Add:\n");
        writer.flush();
        writer.write(gson.toJson(task) + "\n");
        writer.flush();
        taskList.add(task);
        MainController.notificationInterrupt();
    }

    public static void deleteTask(Task task) {
        writer.println("Delete: " + gson.toJson(task));
        writer.flush();
        taskList.remove(task);
        MainController.notificationInterrupt();
    }

    public static void changeTask(Task oldT, Task newT) {
        writer.write("Change: " + gson.toJson(oldT) + " " + gson.toJson(newT));
        writer.flush();
        taskList.set(taskList.indexOf(oldT), newT);
        MainController.notificationInterrupt();
    }


    public static void isAdmin() {
        writer.println("isAdmin:");
        writer.flush();
    }

    public static void becomeAdmin(String code) {
        writer.println("Become adm: " + code);
        writer.flush();
    }
}
