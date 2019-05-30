package com.company.lab2.user;

import com.company.lab2.user.controllers.MainController;
import com.company.lab2.user.controllers.Patterns;
import com.company.lab2.user.controllers.WindowMaker;
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
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 *Client controller class
 */
public class Controller extends Application {
    /** PrintWriter to stream write**/
    private static PrintWriter writer;
    private static String title;
    private static String header;
    private static String content;
    private static Boolean calendarIsEmpty;
    private static Boolean tParsed;
    private static String adminValue;
    public final static Logger logger = Logger.getLogger(MainController.class);
    /** tasks list */
    public static ObservableList<String> taskList;
    /** users list */
    public static ObservableList<String> usersList;
    /** admins list */
    public static ObservableList<String> adminsList;
    /** task title */
    public static String tTitle;
    /** task date */
    public static String tDate;
    /** task active */
    public static String tActive;
    /** task start date */
    public static String tSDate;
    /** task end date */
    public static String tEDate;
    /** task string interval */
    public static String tStrInterval;
    /** task int interval(mils) */
    public static int tIntInterval;
    /** tasks calendar map */
    public static SortedMap<Date, Set<String>> tCalendar;
    /** last date key in last calendar map request */
    public static Date lastKey;
    /** become admin try counter */
    public static int becomeAdmTry = 1;
    /** info about current request status in becoming admin */
    public static String waiting4Adm;
    /** server connection thread  */
    private static Thread connection;
    /** condition for while loop */
    private static boolean whileCondition = true;
    /** first opened stage after connection */
    private static Stage firstStage;
    /**client socket*/
    private static Socket client;

    @Override
    public void start(Stage primaryStage) {
        final String path = "/view/user/Connection.fxml";
        final String header = "Connection";
        WindowMaker.makeWindow(path, header, Modality.NONE);
    }

    public static void main(String[] args) {

        connection = new Thread(() -> {
            BufferedReader reader;
            while (client == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream());
                while (whileCondition) {
                    String response = reader.readLine();
                    if (response != null && !response.isEmpty()) {
                        switch (response) {
                            case Patterns.connected:
                                String list = reader.readLine();
                                taskList = FXCollections.observableList(new Gson().fromJson(list, new TypeToken<ArrayList<String>>(){}.getType()));
                                break;
                            case Patterns.task:
                                tTitle = reader.readLine();
                                tDate = reader.readLine();
                                tActive = reader.readLine();
                                tStrInterval = null;
                                tParsed = Boolean.TRUE;
                                break;
                            case Patterns.taskRep:
                                tTitle = reader.readLine();
                                tSDate = reader.readLine();
                                tEDate = reader.readLine();
                                tStrInterval = reader.readLine();
                                tIntInterval = Integer.parseInt(reader.readLine());
                                tActive = reader.readLine();
                                tParsed = Boolean.TRUE;
                                break;
                            case Patterns.calendar:
                                String str = reader.readLine();
                                if (Patterns.empty.equals(str))
                                    calendarIsEmpty = Boolean.TRUE;
                                else {
                                    tCalendar = new TreeMap<>();
                                    Date date;
                                    Set<String> set;
                                    while (!Patterns.end.equals(str)) {
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
                            case Patterns.usersList:
                                String users = reader.readLine();
                                usersList = FXCollections.observableList(new Gson().fromJson(users, new TypeToken<ArrayList<String>>(){}.getType()));
                                break;
                            case Patterns.adminsList:
                                String admins = reader.readLine();
                                adminsList = FXCollections.observableList(new Gson().fromJson(admins, new TypeToken<ArrayList<String>>(){}.getType()));
                                break;
                            case Patterns.doneADCH:
                                title = response;
                                break;
                            case Patterns.alreadyExistLogin:
                                title = "Error";
                                header = "Wrong login";
                                content = "Login already exist";
                                break;
                            case Patterns.activeUser:
                                title = "Error";
                                header = "Active user";
                                content = "This account already uses";
                                break;
                            case Patterns.loginNotExist:
                                title = "Error";
                                header = "Wrong login";
                                content = "Login doesn't exist";
                                break;
                            case Patterns.wrongPassword:
                                title = "Error";
                                header = "Wrong password";
                                content = "Password is incorrect";
                                break;
                            case Patterns.banned:
                                title = "Nope";
                                header = "U are banned";
                                content = "Good luck";
                                break;
                            case Patterns.isAdmin:
                                adminValue = reader.readLine();
                                if ("false".equals(adminValue))
                                    waiting4Adm = reader.readLine();
                                break;
                            case Patterns.wrongCode:
                                becomeAdmTry = Integer.parseInt(reader.readLine());
                                title = "Error";
                                header = "Wrong code";
                                String s = becomeAdmTry > 1? becomeAdmTry +" try's left. ": becomeAdmTry +" try left. ";
                                content = s + "Connect with mainAdmin to get code";
                                break;
                            case Patterns.congratulations:
                                title = "Cool";
                                header = "Congratulations";
                                content = "You are on waiting list to become admin";
                                break;
                            case Patterns.alreadyAdmin:
                                title = "woops";
                                header = "already admin";
                                content = "";
                                break;
                            case Patterns.exit:
                                interrupt();
                                break;
                            default:
                                logger.error("smth wrong with response");
                                break;
                        }
                    }
                }
                client.close();
            } catch (IOException e) {
                logger.error("exception on connection thread");
                logger.trace(e);
            }
        });
        connection.start();
        launch(args);
        connection.interrupt();
    }

    /** method for user registration
     * @param login user login
     * @param password user password*/
    public static void registration(String login, String password) {
        streamWrite("Registration:\n" + login + "\n" + password + "\n");
        Platform.runLater(Controller::runMain);
    }

    /** method for user logIn
     * @param login user login
     * @param password user password*/
    public static void signIn(String login, String password) {
        streamWrite("Login:\n" + login + "\n" + password + "\n");
        Platform.runLater(Controller::runMain);
    }

    /** method for sending task to add on server
     * @param task task to add*/
    public static void addTask(String task) {
        title = null;
        streamWrite("Add:\n" + task + "\n");
        taskList.add(task);
        w84Response();
    }
    /** method for sending task to delete on server
     * @param task task to delete*/
    public static void deleteTask(String task) {
        title = null;
        streamWrite("Delete:\n" + task + "\n");
        taskList.remove(task);
        w84Response();
    }
    /** method for sending task to  change on server
     * @param oldT old task to be changed
     * @param newT new task which replace old*/
    public static void changeTask(String oldT, String newT) {
        title = null;
        streamWrite("Change:\n" + oldT + "\n" + newT + "\n");
        taskList.set(taskList.indexOf(oldT), newT);
        w84Response();
    }

    /** method for sending request to check current user admin rights
     * @return true if admin or supperAdmin*/
    public static boolean isAdmin() {
        adminValue = null;
        waiting4Adm = null;
        streamWrite("isAdmin:\n");
        boolean to_return;
        while (true) {
            if(adminValue != null) {
                if (adminValue.equals("admin") || adminValue.equals("SuperAdmin")) {
                    to_return = true;
                    break;
                } else if (adminValue.equals("false")) {
                    if (waiting4Adm != null) {
                        if (waiting4Adm.equals("waiting") || waiting4Adm.equals("notWaiting")) {
                            to_return = false;
                            break;
                        }
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("exception in isAdmin()");
                logger.trace(e);
            }
        }
        return to_return;
    }

    /** method for getting adminPanel data
     * @param str can be UsersList to get userList
     *            or AdminsList to get AdminsList*/
    public static void getPanelData(String str) {
        usersList = null;
        adminsList = null;
        boolean boo = false;
        if ("UsersList:".equals(str)) {
            streamWrite("UsersList:\n");
            boo = true;
        }
        if ("AdminsList:".equals(str)) {
            streamWrite("AdminsList:\n");
            boo = true;
        }
        while (boo) {
            if (usersList != null) {
                break;
            }
            if (adminsList != null) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("exception in getPanelData()");
                logger.trace(e);
            }
        }
    }

    /** method for sending request to become admin
     * @param code secret code to become admin verification*/
    public static void becomeAdmin(String code) {
        streamWrite("Become adm:\n" + code + "\n");
        while (true) {
            if (header != null) {
                if (Patterns.wrongCode.equals(header)) {
                    WindowMaker.alertWindowInf(title, header, content);
                    header = null;
                    break;
                }
                if (Patterns.congratulations.equals(header)) {
                    WindowMaker.alertWindowInf(title, header, content);
                    header = null;
                    break;
                }
                if (Patterns.alreadyAdmin.equals(header)) {
                    WindowMaker.alertWindowInf(title, header, content);
                    header = null;
                    break;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("exception in becomeAdmin()");
                logger.trace(e);
            }
        }
    }

    /** method for running main scene */
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
                logger.error("exception in runMain()");
                logger.trace(e);
            }
        }
    }

    /** method for sending request to get tasks calendar
     * @param start start date of calendar
     * @param end end date of calendar*/
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
                logger.error("exception in calendar()");
                logger.trace(e);
            }
        }
    }

    /** method for sending request to parse task
     * @param task task to parse */
    public static void parsTaskStringRequest(String task) {
        tParsed = null;
        streamWrite("Task:\n" + task + "\n");
        while (true) {
            if (tParsed !=  null) break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("exception in parsTaskStringRequest()");
                logger.trace(e);
            }
        }
    }

    /** method for interrupting all threads and exit program*/
    public static void interrupt() {
        whileCondition = false;
        streamWrite("Exit:\n");
        connection.interrupt();
        Platform.exit();
        System.exit(0);
    }

    /** method for writing into the stream
     * @param write string to write*/
    private static void streamWrite(String write) {
        writer.print(write);
        writer.flush();
    }

    /** method for banning or unbanning user request
     * @param user user to ban or unban*/
    public static void banned(String user) {
        if (user != null && user.length() > 20)
            streamWrite("Banned:\n" + user + "\n");
    }

    /** method for server rebut request*/
    public static void rebut() {
        streamWrite("Rebut:\n");
    }

    public static void grantAdmin(String user) {
        if (user != null && user.length() > 20)
            streamWrite("Adminka:\n" + user + "\n");
    }

    /** method for waiting for response on add/delete/change requests*/
    private static void w84Response() {
        while (true) {
            if (Patterns.doneADCH.equals(title)) {
                MainController.notificationInterrupt();
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("exception in w84Response()");
                logger.trace(e);
            }
        }

    }

    /** method for getting isAdmin value for current user
     * @return adminValue*/
    public static String getAdminValue() {
        return adminValue;
    }

    /** method for setting client socket
     * @param client client socket*/
    public static void setClient(Socket client) {
        Controller.client = client;
    }
}

