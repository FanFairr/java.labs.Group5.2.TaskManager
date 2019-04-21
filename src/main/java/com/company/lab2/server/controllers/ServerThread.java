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

    public ServerThread(Socket socket, ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList, ArrayList<User> adminList) {
        this.socket = socket;
        this.usersList = usersList;
        this.tasksList = tasksList;
        this.adminList = adminList;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

            while (true) {

                String response = in.readLine();
                System.out.println(response);

                String title = response.substring(0, response.indexOf(":") + 1);
                response = response.substring(response.indexOf(":") + 2);

                if ("Login:".equals(title)) {
                    String login = in.readLine();
                    String password = in.readLine();
                    synchronized (tasksList) {
                        taskArrayList = tasksList.get(login);
                    }

                    int i = 0;

                    synchronized (usersList) {
                        for (User user : usersList) {
                            if (user.getLogin().equals(login)) {
                                if (user.getPassword().equals(password)) {
                                    if (user.isBanned()) {
                                        printWriter.println("banned");
                                        printWriter.flush();
                                        break;
                                    } else {
                                        i = 1;
                                        Gson gson = new Gson();

                                        printWriter.println("connected:\n" + gson.toJson(taskArrayList));
                                        printWriter.flush();
                                        break;
                                    }
                                } else {
                                    printWriter.println("wrong password");
                                    printWriter.flush();
                                    break;
                                }
                            }
                        }
                    }

                    if (i == 0) {
                        printWriter.println("login not exist");
                        printWriter.flush();
                    }
                } else if ("Registration:".equals(title)) {
                    Gson gson = new Gson();
                    String login = response.substring(0, response.indexOf(" "));
                    String password = response.substring(response.indexOf(" ") + 1);
                    int k = 0;

                    synchronized (usersList) {
                        for (User user : usersList) {
                            if (user.getLogin().equals(login)) {
                                printWriter.println("already exist login");
                                printWriter.flush();
                                k = 1;
                            }
                        }
                        if (k == 0) {
                            usersList.add(new User(login, password, false, "false"));

                            synchronized (tasksList) {
                                tasksList.put(login, new ArrayList<>());

                                taskArrayList = tasksList.get(login);
                                printWriter.println("connected\n" + gson.toJson(taskArrayList));
                                printWriter.flush();
                            }
                        }
                    }
                } else if ("Delete:".equals(title)) {
                    Gson gson = new Gson();
                    Task task = gson.fromJson(response, new TypeToken<Task>() {
                    }.getType());

                    taskArrayList.remove(task);
                } else if ("Add:".equals(title)) {
                    Gson gson = new Gson();
                    Task task = gson.fromJson(response, new TypeToken<Task>() {
                    }.getType());

                    taskArrayList.add(task);
                } else if ("Change:".equals(title)) {

                    String oldTask = response.substring(0, response.indexOf(" "));
                    String newTask = response.substring(response.indexOf(" ") + 1);
                    Gson gson = new Gson();

                    Task oldTask1 = gson.fromJson(oldTask, Task.class);
                    Task newTask1 = gson.fromJson(newTask, Task.class);

                    taskArrayList.set(taskArrayList.indexOf(oldTask1), newTask1);
                }/* else if ("Calendar:".equals(title)) {
                    Gson gson = new Gson();
                    String login = response.substring(0, response.indexOf(" "));
                    response = response.substring(response.indexOf(" ") + 1);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
                    Date date1 = simpleDateFormat.parse(response.substring(0, response.indexOf(" ")));
                    Date date2 = simpleDateFormat.parse(response.substring(response.indexOf(" ") + 1));

                    synchronized (tasksList) {
                        SortedMap<Date, Set<Task>> sortedMap = Tasks.calendar(tasksList.get(login), date1, date2);
                        printWriter.println("Calendar: " + gson.toJson(sortedMap));
                        printWriter.flush();
                    }
                }*/ else if ("Users list:".equals(title)) {
                    Gson gson = new Gson();
                    synchronized (usersList) {
                        printWriter.println("Users list: " + gson.toJson(usersList));
                    }
                } else if ("Become adm:".equals(title)) {
                    String login = response.substring(0, response.indexOf(" "));
                    int k = 0;
                    synchronized (adminList) {
                        for (User user1 : usersList)
                            if (login.equals(user1.getLogin())) {
                                for (User user : adminList) {
                                    if (user.getLogin().equals(login)) {
                                        k = 1;
                                        break;
                                    }
                                }

                                if (k == 0) {
                                    adminList.add(user1);
                                    break;
                                } else {
                                    printWriter.println("become adm");
                                    printWriter.flush();
                                }
                            }
                    }
                } else if ("Spisok adminov:".equals(title)) {
                    Gson gson = new Gson();
                    synchronized (adminList) {
                        String list = gson.toJson(adminList);
                        printWriter.println("Spisok adminov: \n" + list);
                        printWriter.flush();
                    }
                } else if ("Exit work:".equals(title)) {
                    socket.close();
                    break;
                }

                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
