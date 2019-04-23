package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

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
                                                streamWrite("connected\n" + gson.toJson(taskArrayList) + "\n");
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
                                        streamWrite("connected\n" + gson.toJson(taskArrayList) + "\n");
                                    }
                                }
                            }
                            break;

                        case "Add:":
                            taskArrayList.add(gson.fromJson(in.readLine(), new TypeToken<Task>(){}.getType()));
                            break;

                        case "Delete:":
                            Task task = gson.fromJson(in.readLine(), new TypeToken<Task>(){}.getType());
                            taskArrayList.remove(task);
                            break;

                        case "Change:":
                            Task oldT = gson.fromJson(in.readLine(), new TypeToken<Task>(){}.getType());
                            Task newT = gson.fromJson(in.readLine(), new TypeToken<Task>(){}.getType());
                            taskArrayList.set(taskArrayList.indexOf(oldT), newT);
                            break;

                        case "Users list:":
                            synchronized (usersList) {
                                streamWrite("Users list:\n" + gson.toJson(usersList) + "\n");
                            }
                            break;

                        case "isAdmin:":
                            if (currentUser.getAdmin().equals("false"))
                                streamWrite("false\n");
                            else streamWrite("true\n");
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
                    /* else if ("Calendar:".equals(title)) {
                        Gson gson = new Gson();
                        String login = response.substring(0, response.indexOf(" "));
                        response = response.substring(response.indexOf(" ") + 1);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
                        Date date1 = simpleDateFormat.parse(response.substring(0, response.indexOf(" ")));
                        Date date2 = simpleDateFormat.parse(response.substring(response.indexOf(" ") + 1));

                        synchronized (tasksList) {
                            SortedMap<Date, Set<Task>> sortedMap = Tasks.calendar(tasksList.get(login), date1, date2);
                            streamWrite("Calendar:\n" + gson.toJson(sortedMap) + "\n");
                        }
                    }*/
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

