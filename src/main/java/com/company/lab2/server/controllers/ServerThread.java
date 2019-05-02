package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.Tasks;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerThread extends Thread {
    private Socket socket = null;
    private final ArrayList<User> usersList;
    private final TreeMap<String, ArrayList<Task>> tasksList;
    private ArrayList<Task> taskArrayList;
    private final ArrayList<User> adminList;
    private PrintWriter printWriter;
    private Gson gson = new Gson();
    private String login;
    private boolean whileCondition = true;
    private User currentUser;

    public ServerThread(Socket socket, ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList, ArrayList<User> adminList) {
        this.socket = socket;
        this.usersList = usersList;
        this.tasksList = tasksList;
        this.adminList = adminList;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            while (whileCondition) {
                System.out.println("hi");
                String response = in.readLine();
                if (response != null && !response.isEmpty()) {
                    System.out.println(response);
                    switch (response) {
                        case "Login:":
                            login = in.readLine();
                            String password = in.readLine();
                            synchronized (tasksList) {
                                taskArrayList = tasksList.get(login);
                            }
                            boolean loginNotExist = true;
                            synchronized (usersList) {
                                for (User user : usersList) {
                                    if (user.getLogin().equals(login)) {
                                        currentUser = user;
                                        if (user.getPassword().equals(password)) {
                                            if (user.isBanned()) {
                                                streamWrite("banned\n");
                                                loginNotExist = false;
                                                break;
                                            } else {
                                                loginNotExist = false;
                                                streamWrite("connected\n" + gson.toJson(StringConverterController.formatTaskArr(taskArrayList)) + "\n");
                                                break;
                                            }
                                        } else {
                                            streamWrite("wrong password\n");
                                            loginNotExist = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (loginNotExist) {
                                streamWrite("login not exist\n");
                            }
                            break;

                        case "Registration:":
                            login = in.readLine();
                            password = in.readLine();
                            loginNotExist = true;
                            synchronized (usersList) {
                                for (User user : usersList) {
                                    if (user.getLogin().equals(login)) {
                                        streamWrite("already exist login\n");
                                        loginNotExist = false;
                                        break;
                                    }
                                }
                                if (loginNotExist) {
                                    currentUser = new User(login, password, false, "false");
                                    usersList.add(currentUser);
                                    synchronized (tasksList) {
                                        tasksList.put(login, new ArrayList<>());
                                        taskArrayList = tasksList.get(login);
                                        streamWrite("connected\n" + gson.toJson(StringConverterController.formatTaskArr(taskArrayList)) + "\n");
                                    }
                                }
                            }
                            break;

                        case "Task:":
                            String taskStr = in.readLine();
                            Task task = StringConverterController.makeTaskFromString(taskStr);
                            StringBuilder builder = new StringBuilder();
                            if (task.isRepeated()) {
                                builder.append("taskRep\n").append(task.getTitle()).append("\n");
                                builder.append(format.format(task.getStartTime())).append("\n");
                                builder.append(format.format(task.getEndTime())).append("\n");
                                builder.append(StringConverterController.getStringFromRepeatInterval(task.getRepeatInterval())).append("\n");
                                builder.append(task.getRepeatInterval()).append("\n");
                            } else {
                                builder.append("task\n").append(task.getTitle()).append("\n");
                                builder.append(format.format(task.getTime())).append("\n");
                            }
                            if (task.isActive()) builder.append("Active\n");
                            else builder.append("NotActive\n");
                            streamWrite(new String(builder));
                            break;

                        case "Add:":
                            String s =in.readLine();
                            System.out.println(s);
                            taskArrayList.add(StringConverterController.makeTaskFromString(s));
                            streamWrite("doneADCH\n");
                            for (Task t:taskArrayList) {
                                System.out.println(t);
                            }
                            break;

                        case "Delete:":
                            String ss =in.readLine();
                            System.out.println(ss);
                            taskArrayList.remove(StringConverterController.makeTaskFromString(ss));
                            for (Task t:taskArrayList) {
                                System.out.println(t);
                            }
                            streamWrite("doneADCH\n");
                            break;

                        case "Change:":
                            String sss =in.readLine();
                            System.out.println(sss);
                            String ssss =in.readLine();
                            System.out.println(ssss);
                            Task oldT = StringConverterController.makeTaskFromString(sss);
                            Task newT = StringConverterController.makeTaskFromString(ssss);
                            taskArrayList.set(taskArrayList.indexOf(oldT), newT);
                            streamWrite("doneADCH\n");
                            break;

                        case "Users list:":
                            synchronized (usersList) {
                                streamWrite("Users list:\n" + gson.toJson(usersList) + "\n");
                            }
                            break;

                        case "isAdmin:":
                            if (currentUser.getAdmin().equals("false"))
                                streamWrite("isAdmin\nfalse\n");
                            else streamWrite("isAdmin\ntrue\n");
                            break;

                        case "Become adm:":
                            synchronized (adminList) {
                                if (!adminList.contains(currentUser)) {
                                    adminList.add(currentUser);
                                    streamWrite("congratulations\n");
                                    break;
                                } else {
                                    streamWrite("already admin\n");
                                }
                            }
                            break;

                        case "AdminList:":
                            synchronized (adminList) {
                                String list = gson.toJson(adminList);
                                streamWrite("Spisok adminov:\n" + list + "\n");
                            }
                            break;

                        case "Calendar:":
                            Date date1 = null;
                            Date date2 = null;
                            try {
                                date1 = format.parse(in.readLine());
                                date2 = format.parse(in.readLine());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            synchronized (tasksList) {
                                SortedMap<Date, Set<Task>> sortedMap = Tasks.calendar(tasksList.get(login), date1, date2);
                                if (sortedMap.isEmpty())
                                    streamWrite("calendar\n" + "empty\n");
                                else streamWrite("calendar\n" + gson.toJson(sortedMap) + "\n");
                            }
                            break;

                        case "Exit:":
                            synchronized (tasksList) {
                                tasksList.put(login, taskArrayList);
                            }
                            socket.close();
                            whileCondition = false;
                            break;
                        default:
                            System.out.println("smth wrong");
                    }
                    Thread.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void streamWrite(String write) {
        printWriter.write(write);
        printWriter.flush();
    }
}

