package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.Tasks;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    public ServerThread(Socket socket, ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList) {
        this.socket = socket;
        this.usersList = usersList;
        this.tasksList = tasksList;
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
                    String login = response.substring(0, response.indexOf(" "));
                    String password = response.substring(response.indexOf(" ") + 1);

                    int i = 0;

                    synchronized (usersList) {
                        for (User user : usersList) {
                            i++;
                            if (user.getLogin().equals(login)) {
                                if (user.getPassword().equals(password)) {
                                    if (user.isBanned()) {
                                        printWriter.println("Login: You are banned");
                                        printWriter.flush();
                                        break;
                                    } else {
                                        Gson gson = new Gson();
                                        synchronized (tasksList) {
                                            printWriter.println("Login: " + user.getAdmin()+ " " + gson.toJson(tasksList.get(login)));
                                            printWriter.flush();
                                            break;
                                        }
                                    }
                                } else {
                                    printWriter.println("Login: Password entered incorrectly");
                                    printWriter.flush();
                                    break;
                                }
                            }
                        }
                    }

                    if (i == usersList.size()) {
                        printWriter.println("Login: Login entered incorrectly");
                        printWriter.flush();
                    }
                } else if ("Registration:".equals(title)) {
                    String login = response.substring(0, response.indexOf(" "));
                    String password = response.substring(response.indexOf(" ") + 1);

                    synchronized (usersList) {
                        for (User user : usersList) {
                            if (user.getLogin().equals(login)) {
                                printWriter.println("Registration: Such a login is already busy");
                                printWriter.flush();
                            }
                        }
                        usersList.add(new User(login, password, false, "false"));
                        tasksList.put(login, new ArrayList<>());
                        Gson gson = new Gson();
                        synchronized (tasksList) {
                            printWriter.println("Registration: " + gson.toJson(tasksList.get(login)));
                            printWriter.flush();
                        }
                    }
                } else if ("Delete:".equals(title)) {
                    Gson gson = new Gson();
                    String login = response.substring(0, response.indexOf(" "));
                    Task task = gson.fromJson(response.substring(response.indexOf(" ") + 1), new TypeToken<Task>() {
                    }.getType());

                    synchronized (tasksList) {
                        tasksList.get(login).remove(task);
                    }
                } else if ("Add:".equals(title)) {
                    Gson gson = new Gson();
                    String login = response.substring(0, response.indexOf(" "));
                    Task task = gson.fromJson(response.substring(response.indexOf(" ") + 1), new TypeToken<Task>() {
                    }.getType());

                    synchronized (tasksList) {
                        tasksList.get(login).add(task);
                    }
                } else if ("Change:".equals(title)) {
                    String login = response.substring(0, response.indexOf(" "));
                    response = response.substring(response.indexOf(" ") + 1);
                    Gson gson = new Gson();
                    int index = Integer.parseInt(response.substring(0, response.indexOf(" ")));
                    response = response.substring(response.indexOf(" "));

                    Task task = gson.fromJson(response, Task.class);

                    synchronized (tasksList) {
                        tasksList.get(login).set(index, task);
                    }
                } else if ("Calendar:".equals(title)) {
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
                } else if ("Banned list:".equals(title)) {
                    Gson gson = new Gson();
                    synchronized (usersList) {
                        printWriter.println(gson.toJson(usersList));
                    }
                } else if ("Exit work:".equals(title)) {
                    socket.close();
                    break;
                }

                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }
}
