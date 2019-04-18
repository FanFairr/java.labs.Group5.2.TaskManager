package com.company.lab2.user;

import com.company.lab2.user.controllers.MainController;
import com.company.lab2.user.controllers.WindowMaker;
import com.company.lab2.user.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;

public class Controller extends Application {
    private static String login;
    public static ObservableList<Task> taskList = FXCollections.observableArrayList();
    private static ArrayList<Task> tasksArr;
    public final static Logger logger = Logger.getLogger(MainController.class);
    private static BufferedReader reader = null;
    private static BufferedWriter writer = null;
    private static Gson gson = new Gson();
    @Override
    public void start(Stage primaryStage) throws Exception{
        /*
        final String path = "/view.user/Main.fxml";
        final String header = "Task Manager";
        */
        final String path = "/view/user/EnterForm.fxml";
        final String header = "SignIn";
        WindowMaker.makeWindow(path, header, Modality.NONE);
    }

    public static void main(String[] args) {
        launch(args);
        try {
            Socket client = new Socket("127.0.0.1", 1488);
            PrintWriter printWriter = new PrintWriter(client.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            printWriter.println("Login: aq bd");
            printWriter.flush();

            String list;

            while (true) {
                list = reader.readLine();
                if (!list.isEmpty()) {
                    System.out.println(list);
                    if (list.equals("Exit"))
                        System.exit(0);
                }
                Thread.sleep(10);
            }
            /*ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            out.writeObject("Login: aa ba");
            out.flush();
            while (true) {
                String list = (String) in.readObject();
                if (!"".equals(list)) {
                    System.out.println(list);
                    break;
                }
            }*/
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void registration(String login, String name, String password) {
        try {
            writer.write("Registration\n" + login + "\n" + name + "\n" + password + "\n");
            writer.flush();
            String response = reader.readLine();
            switch (response) {
                case "connected":
                    tasksArr = new ArrayList<>();
                    break;
                case "already exist login":
                    WindowMaker.alertWindowInf("Error", "Wrong login", "Login already exist");
                    break;
                default:
                    System.out.println("smth wrong with response reg");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void signIn(String login, String password) {
        try {
            writer.write("Login\n" + login + "\n" + password + "\n");
            writer.flush();
            String response = reader.readLine();
            switch (response) {
                case "connected":
                    Type token = new TypeToken<ArrayList<Task>>(){}.getType();
                    tasksArr = gson.fromJson(reader.readLine(), token);
                    taskList.setAll(tasksArr);
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
                default:
                    System.out.println("smth wrong with response signin");
                    break;
            } } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addTask(Task task) {
        try {
            writer.write("Add\n");
            writer.flush();
            writer.write(gson.toJson(task) + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskList.add(task);
        MainController.notificationInterrupt();
    }

    public static void deleteTask(Task task) {
        try {
            writer.write("Delete\n");
            writer.flush();
            writer.write(gson.toJson(task) + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskList.remove(task);
        MainController.notificationInterrupt();
    }

    public static void changeTask(Task oldT, Task newT) {
        try {
            writer.write("Change\n");
            writer.flush();
            writer.write(gson.toJson(oldT) + "\n");
            writer.flush();
            writer.write(gson.toJson(newT) + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskList.set(taskList.indexOf(oldT), newT);
        MainController.notificationInterrupt();
    }


    public static Boolean isAdmin() {
        try {
            writer.write("isAdmin\n");
            writer.flush();
            String resp = reader.readLine();
            if (resp.equals("true"))
                return true;
            else if (resp.equals("false"))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void becomeAdmin(String code) {
        try {
            writer.write("Become adm\n" + code + "\n");
            writer.flush();
            String resp = reader.readLine();
            if (resp.equals("wrong code"))
                WindowMaker.alertWindowInf("Error", "Wrong code", "Connect with mainAdmin to get code");
            else if (resp.equals("success"))
                WindowMaker.alertWindowInf("Cool", "Congratulations", "You are admin now");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
