package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

public class ServerThread extends Thread {
    private Socket socket = null;
    private final ArrayList<User> usersList;
    private final TreeMap<String, ArrayList<Task>> tasksList;
    private ArrayList<Task> taskArrayList;
    private final ArrayList<User> adminList;
    private BufferedReader in;
    private Gson gson = new Gson();
    private String login;
    private boolean whileCondition = true;

    public ServerThread(Socket socket, ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList, ArrayList<User> adminList) {
        this.socket = socket;
        this.usersList = usersList;
        this.tasksList = tasksList;
        this.adminList = adminList;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            while (whileCondition) {
                System.out.println("hi");
                String response = null;
                if (in != null)
                    response = in.readLine();
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
                                        if (user.getPassword().equals(password)) {
                                            if (user.isBanned()) {
                                                printWriter.write("banned\n");
                                                printWriter.flush();
                                                loginNotExist = false;
                                                break;
                                            } else {
                                                loginNotExist = false;
                                                printWriter.write("connected\n" + gson.toJson(taskArrayList) + "\n");
                                                printWriter.flush();
                                                break;
                                            }
                                        } else {
                                            printWriter.write("wrong password\n");
                                            printWriter.flush();
                                            loginNotExist = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (loginNotExist) {
                                printWriter.write("login not exist\n");
                                printWriter.flush();
                            }
                            break;

                        case "Registration:":
                            login = in.readLine();
                            password = in.readLine();
                            loginNotExist = true;
                            synchronized (usersList) {
                                for (User user : usersList) {
                                    if (user.getLogin().equals(login)) {
                                        printWriter.write("already exist login\n");
                                        printWriter.flush();
                                        loginNotExist = false;
                                        break;
                                    }
                                }
                                if (loginNotExist) {
                                    usersList.add(new User(login, password, false, "false"));
                                    synchronized (tasksList) {
                                        tasksList.put(login, new ArrayList<>());
                                        taskArrayList = tasksList.get(login);
                                        printWriter.write("connected\n" + gson.toJson(taskArrayList) + "\n");
                                        printWriter.flush();
                                    }
                                }
                            }
                            break;

                        case "Add:":
                            taskArrayList.add(getTaskFromInputStream());
                            break;

                        case "Delete:":
                            taskArrayList.remove(getTaskFromInputStream());
                            break;

                        case "Change:":
                            Task oldT = getTaskFromInputStream();
                            Task newT = getTaskFromInputStream();
                            taskArrayList.set(taskArrayList.indexOf(oldT), newT);
                            break;

                        case "Users list:":
                            synchronized (usersList) {
                                printWriter.write("Users list:\n" + gson.toJson(usersList) + "\n");
                            }
                            break;

                        case "Become adm:":
                            boolean notAdmin = true;
                            synchronized (adminList) {
                                for (User user1 : usersList)
                                    if (login.equals(user1.getLogin())) {
                                        for (User user : adminList) {
                                            if (user.getLogin().equals(login)) {
                                                notAdmin = false;
                                                break;
                                            }
                                        }
                                        //todo лучше сделать не арейлист по админам хеш сет, тогда не нужна проверка на наличие чела в списке
                                        if (notAdmin) {
                                            adminList.add(user1);
                                            printWriter.write("congratulations\n");
                                            break;
                                        } else {
                                            printWriter.write("become adm\n");
                                            printWriter.flush();
                                        }
                                    }
                            }
                            break;
                        case "Spisok adminov:":
                            synchronized (adminList) {
                                String list = gson.toJson(adminList);
                                printWriter.write("Spisok adminov:\n" + list + "\n");
                                printWriter.flush();
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
                            printWriter.write("Calendar:\n" + gson.toJson(sortedMap) + "\n");
                            printWriter.flush();
                        }
                    }*/
                    Thread.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Task getTaskFromInputStream() throws IOException {
        String strTask = in.readLine();
        Type type = new TypeToken<Task>(){}.getType();
        /*while (strTask == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        Task task = gson.fromJson(strTask, type);
        strTask = null;
        return task;
    }
}

