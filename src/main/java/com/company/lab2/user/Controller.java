package com.company.lab2.user;

import com.company.lab2.user.controllers.MainController;
import com.company.lab2.user.controllers.WindowMaker;
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
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller extends Application {

    private static BufferedReader reader;
    private static PrintWriter writer;
    private static String title;
    private static String header;
    private static String content;
    private static Boolean calendarIsEmpty;
    private static Boolean tParsed;
    public final static Logger logger = Logger.getLogger(MainController.class);
    public static ObservableList<String> taskList;
    public static String tTitle;
    public static String tDate;
    public static String tActive;
    public static String tSDate;
    public static String tEDate;
    public static String tStrInterval;
    public static int tIntInterval;
    public static SortedMap<Date, Set<String>> tCalendar;
    public static Date lastKey;
    public static int becomeAdmTry = 1;

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
                                String list = reader.readLine();
                                System.out.println(list);
                                taskList = FXCollections.observableList(new Gson().fromJson(list, new TypeToken<ArrayList<String>>(){}.getType()));
                                break;
                            case "task":
                                tTitle = reader.readLine();
                                tDate = reader.readLine();
                                tActive = reader.readLine();
                                tStrInterval = null;
                                tParsed = Boolean.TRUE;
                                break;
                            case "taskRep":
                                tTitle = reader.readLine();
                                tSDate = reader.readLine();
                                tEDate = reader.readLine();
                                tStrInterval = reader.readLine();
                                tIntInterval = Integer.parseInt(reader.readLine());
                                tActive = reader.readLine();
                                tParsed = Boolean.TRUE;
                                break;
                            case "calendar":
                                String str = reader.readLine();
                                System.out.println(str);
                                if (str.equals("empty"))
                                    calendarIsEmpty = Boolean.TRUE;
                                else {
                                    tCalendar = new TreeMap<>();
                                    Date date;
                                    Set<String> set;
                                    while (!str.equals("end")) {
                                        date = new Gson().fromJson(str, new TypeToken<Date>(){}.getType());
                                        str = reader.readLine();
                                        set = new Gson().fromJson(str, new TypeToken<Set<String>>(){}.getType());
                                        tCalendar.put(date,set);
                                        str = reader.readLine();
                                    }
                                    lastKey = new Gson().fromJson(reader.readLine(), new TypeToken<Date>(){}.getType());
                                    calendarIsEmpty = Boolean.FALSE;
                                }
                                break;
                            case "doneADCH":
                                title = response;
                                break;
                            case "already exist login":
                                title = "Error";
                                header = "Wrong login";
                                content = "Login already exist";
                                break;
                            case "active user":
                                title = "Error";
                                header = "Active user";
                                content = "This account already uses";
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
                            case "isAdmin":
                                title = reader.readLine();
                                break;
                            case "wrong code":
                                becomeAdmTry = Integer.parseInt(reader.readLine());
                                title = "Error";
                                header = "Wrong code";
                                String s = becomeAdmTry > 1? becomeAdmTry +" try's left. ": becomeAdmTry +" try left. ";
                                content = s + "Connect with mainAdmin to get code";
                                break;
                            case "congratulations":
                                title = "Cool";
                                header = "Congratulations";
                                content = "You are admin now";
                                break;
                            case "already admin":
                                title = "woops";
                                header = "already admin";
                                content = "";
                                break;
                            case "Exit":
                                exit();
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

    public static void addTask(String task) {
        title = null;
        streamWrite("Add:\n" + task + "\n");
        System.out.println(task);
        taskList.add(task);
        w84Response();
    }

    public static void deleteTask(String task) {
        title = null;
        streamWrite("Delete:\n" + task + "\n");
        taskList.remove(task);
        w84Response();
    }

    public static void changeTask(String oldT, String newT) {
        title = null;
        streamWrite("Change:\n" + oldT + "\n" + newT + "\n");
        taskList.set(taskList.indexOf(oldT), newT);
        w84Response();
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
        streamWrite("Become adm:\n" + code);
        while (true) {
            if (header != null) {
                if (header.equals("Wrong code")) {
                    WindowMaker.alertWindowInf(title, header, content);
                    header = null;
                    break;
                }
                if (header.equals("Congratulations")) {
                    WindowMaker.alertWindowInf(title, header, content);
                    header = null;
                    break;
                }
                if (header.equals("already admin")) {
                    WindowMaker.alertWindowInf(title, header, content);
                    header = null;
                    break;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runMain() {
        while (true) {
            if (taskList != null) {
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

    public static SortedMap<Date, Set<String>> calendar(Date start,Date end) {
        streamWrite("Calendar:\n" + new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy").format(start) + "\n" + new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy").format(end) + "\n");
        calendarIsEmpty = null;
        while (true) {
            if (calendarIsEmpty !=  null) {
                if (calendarIsEmpty) {
                    return null;
                } else {
                    return tCalendar;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void parsTaskStringRequest(String task) {
        tParsed = null;
        streamWrite("Task:\n" + task + "\n");
        while (true) {
            if (tParsed !=  null) break;
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

    private static void exit() {
        whileCondition = false;
        streamWrite("Exit:\n");
        connection.interrupt();
        Platform.exit();
        System.exit(0);
    }

    private static void streamWrite(String write) {
        writer.println(write);
        writer.flush();
    }

    private static void w84Response() {
        while (true) {
            if (title !=  null)
                if (title.equals("doneADCH"))
                    break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MainController.notificationInterrupt();
    }
}

