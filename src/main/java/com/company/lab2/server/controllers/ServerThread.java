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

                String title = in.readLine();
                System.out.println(title);

                if ("Login:".equals(title)) {
                    String login = in.readLine();
                    System.out.println(login);
                    String password = in.readLine();
                    System.out.println(password);

                    int i = 0;

                    synchronized (usersList) {
                        for (User user : usersList) {
                            i++;
                            if (user.getLogin().equals(login)) {
                                if (user.getPassword().equals(password)) {
                                    if (user.isBanned()) {
                                        printWriter.println("banned");
                                        printWriter.flush();
                                        break;
                                    } else {
                                        Gson gson = new Gson();
                                        synchronized (tasksList) {
                                            printWriter.println("connected");
                                            printWriter.flush();
                                            printWriter.println(gson.toJson(tasksList.get(login)));
                                            printWriter.flush();
                                            break;
                                        }
                                    }
                                } else {
                                    printWriter.println("wrong password");
                                    printWriter.flush();
                                    break;
                                }
                            }
                        }
                    }

                    if (i == usersList.size()) {
                        printWriter.println("login not exist");
                        printWriter.flush();
                    }
                } else if ("Registration:".equals(title)) {
                    String login = in.readLine();
                    String password = in.readLine();

                    synchronized (usersList) {
                        for (User user : usersList) {
                            if (user.getLogin().equals(login)) {
                                printWriter.println("already exist login");
                                printWriter.flush();
                            }
                        }
                        usersList.add(new User(login, password, false, "false"));
                        tasksList.put(login, new ArrayList<>());
                        synchronized (tasksList) {
                            printWriter.println("registration accept");
                            printWriter.flush();
                        }
                    }
                } else if ("Delete:".equals(title)) {
                    Gson gson = new Gson();
                    String login = in.readLine();
                    Task task = gson.fromJson(in.readLine(), new TypeToken<Task>() {
                    }.getType());

                    synchronized (tasksList) {
                        tasksList.get(login).remove(task);
                    }
                } else if ("Add:".equals(title)) {
                    Gson gson = new Gson();
                    String login = in.readLine();
                    Task task = gson.fromJson(in.readLine(), new TypeToken<Task>() {
                    }.getType());

                    synchronized (tasksList) {
                        tasksList.get(login).add(task);
                    }
                } else if ("Change:".equals(title)) {
                    String login = in.readLine();
                    Gson gson = new Gson();
                    int index = Integer.parseInt(in.readLine());

                    Task task = gson.fromJson(in.readLine(), Task.class);

                    synchronized (tasksList) {
                        tasksList.get(login).set(index, task);
                    }
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
                    String login = in.readLine();
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
