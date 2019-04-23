package com.company.lab2.user;

import com.company.lab2.user.controllers.EnterFormController;
import com.company.lab2.user.controllers.MainController;
import com.company.lab2.user.controllers.WindowMaker;
import com.company.lab2.user.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private static BufferedReader reader;
    private static PrintWriter writer;
    private static Gson gson = new Gson();
    private static String title;
    private static String header;
    private static String content;
    private static Thread connection;
    private static boolean whileCondition = true;
    private static Stage firstStage;
    @Override
    public void start(Stage primaryStage) {
        final String path = "/view/user/EnterForm.fxml";
        final String header = "SignIn";
        WindowMaker.makeWindow(path, header);
    }

    public static void main(String[] args) {
        connection = new Thread(() -> {
            try {
                Socket client = new Socket("127.0.0.1", 1488);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream());
                while (whileCondition) {
                    System.out.println("hi");
                    String response = reader.readLine();
                    System.out.println(response);
                    if (response != null && !response.isEmpty()) {
                        System.out.println("here");
                        switch (response) {
                            case "connected":
                                Type token = new TypeToken<ArrayList<Task>>() {}.getType();
                                String list = reader.readLine();
                                System.out.println(list);
                                if ("[]".equals(list)) {
                                    System.out.println("v if");
                                    tasksArr = new ArrayList<>();
                                } else {
                                    tasksArr = gson.fromJson(list, token);
                                    System.out.println("v else");
                                }
                                login = EnterFormController.logIn;
                                taskList.setAll(tasksArr);                                
                                break;
                            case "already exist login":
                                title = "Error";
                                header = "Wrong login";
                                content = "Login already exist";
                                break;
                            case "login not exist":
                                System.out.println("lnex");
                                title = "Error";
                                header = "Wrong login";
                                content = "Login doesn't exist";
                                break;
                            case "wrong password":
                                title = "Error";
                                header = "Wrong password";
                                content = "Password is incorrect";
                                break;
                            case "banned":
                                title = "Nope";
                                header = "U are banned";
                                content = "Good luck";
                                break;
                            case "true":
                                title = response;
                                break;
                            case "false":
                                title = response;
                                break;
                            case "wrong code":
                                title = "Error";
                                header = "Wrong code";
                                content = "Connect with mainAdmin to get code";
                                break;
                            case "congratulations":
                                title = "Cool";
                                header = "Congratulations";
                                content = "You are admin now";
                                break;
                            default:
                                System.out.println("smth wrong with response");
                                break;
                        }
                    }
                }
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        connection.start();
        launch(args);
        connection.interrupt();
    }

    public static void registration(String login, String name, String password) {
        streamWrite("Registration:\n" + login + "\n" + password + "\n");
        Platform.runLater(Controller::runMain);
    }

    public static void signIn(String login, String password) {
        streamWrite("Login:\n" + login + "\n" + password + "\n");
        Platform.runLater(Controller::runMain);
    }

    public static void addTask(Task task) {
        streamWrite("Add:\n" + gson.toJson(task) + "\n");
        taskList.add(task);
        MainController.notificationInterrupt();
    }

    public static void deleteTask(Task task) {
        streamWrite("Delete:\n" + gson.toJson(task) + "\n");
        taskList.remove(task);
        MainController.notificationInterrupt();
    }

    public static void changeTask(Task oldT, Task newT) {
        streamWrite("Change:\n" + gson.toJson(oldT) + "\n" + gson.toJson(newT) + "\n");
        taskList.set(taskList.indexOf(oldT), newT);
        MainController.notificationInterrupt();
    }


    public static boolean isAdmin() {
        streamWrite("isAdmin:\n");
        boolean to_return;
        while (true) {
            if(title != null) {
                if(title.equals("true")) {
                    to_return = true;
                    break;
                } else if (title.equals("false")) {
                    to_return = false;
                    break;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        title = null;
        return to_return;
    }

    public static void becomeAdmin(String code) {
        streamWrite("Become adm:\n" + code + "\n");
    }

    private static void runMain() {
        while (true) {
            if (tasksArr != null) {
                firstStage = WindowMaker.getStage();
                Platform.runLater(()-> firstStage.close());
                WindowMaker.makeWindow("/view/user/Main.fxml", "Task Manager");
                break;
            } else if (title != null) {
                WindowMaker.alertWindowInf(title, header, content);
                title = null;
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void interrupt() {
        whileCondition = false;
        streamWrite("Exit:\n");
        connection.interrupt();
        Platform.exit();
        System.exit(0);
    }

    private static void streamWrite(String write) {
        writer.write(write);
        writer.flush();
    }
}
