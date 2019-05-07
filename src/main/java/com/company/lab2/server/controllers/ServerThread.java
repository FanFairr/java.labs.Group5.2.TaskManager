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
    private Socket socket;
    private PrintWriter printWriter;
    private User currentUser;
    private String login;

    private final ArrayList<User> usersList;
    private final TreeMap<String, ArrayList<Task>> tasksList;
    private ArrayList<Task> taskArrayList;
    private final ArrayList<User> adminList;
    private LinkedList<User> activeUsers = new LinkedList<>();

    public ServerThread(Socket socket, ArrayList<User> usersList, TreeMap<String, ArrayList<Task>> tasksList, ArrayList<User> adminList, LinkedList<User> activeUsers) {
        this.socket = socket;
        this.usersList = usersList;
        this.tasksList = tasksList;
        this.adminList = adminList;
        this.activeUsers = activeUsers;
    }

    @Override
    public void run() {
        try {
            int becomeAdmTry = 0;
            boolean whileCondition = true;
            String strUser;
            User newUser;
            Gson gson = new Gson();

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
                                            synchronized (activeUsers) {
                                                System.out.println(activeUsers.indexOf(user));
                                                if (user.isBanned()) {
                                                    streamWrite("banned\n");
                                                    loginNotExist = false;
                                                    break;
                                                } else if (activeUsers.indexOf(user) != -1) {
                                                    loginNotExist = false;
                                                    streamWrite("active user\n");
                                                    break;
                                                } else{
                                                    activeUsers.add(user);
                                                    loginNotExist = false;
                                                    streamWrite("connected\n" + gson.toJson(StringConverterController.formatTaskArr(taskArrayList)) + "\n");
                                                    break;
                                                }
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
                                        synchronized (activeUsers) {
                                            activeUsers.add(currentUser);
                                        }
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
                            taskArrayList.add(StringConverterController.makeTaskFromString(in.readLine()));
                            streamWrite("doneADCH\n");
                            break;

                        case "Delete:":
                            taskArrayList.remove(StringConverterController.makeTaskFromString(in.readLine()));
                            streamWrite("doneADCH\n");
                            break;

                        case "Change:":
                            Task oldT = StringConverterController.makeTaskFromString(in.readLine());
                            Task newT = StringConverterController.makeTaskFromString(in.readLine());
                            taskArrayList.set(taskArrayList.indexOf(oldT), newT);
                            streamWrite("doneADCH\n");
                            break;

                        case "Users list:":
                            synchronized (usersList) {
                                streamWrite(gson.toJson(usersList) + "\n");
                            }
                            break;

                        case "isAdmin:":
                            if (currentUser.getAdmin().equals("false"))
                                streamWrite("isAdmin\nfalse\n");
                            else streamWrite("isAdmin\ntrue\n");
                            break;

                        case "Become adm:":
                            String code = in.readLine();
                            if (code.equals("123")) {
                                synchronized (adminList) {
                                    if (!adminList.contains(currentUser)) {
                                        currentUser.setAdmin("admin");
                                        adminList.add(currentUser);
                                        streamWrite("congratulations\n");
                                        break;
                                    } else {
                                        streamWrite("already admin\n");
                                    }
                                }
                            } else {
                                becomeAdmTry++;
                                int i = 3 - becomeAdmTry;
                                streamWrite("wrong code\n"+ i + "\n");
                            }


                            break;

                        case "AdminList:":
                            synchronized (adminList) {
                                streamWrite("adminList\n" + gson.toJson(StringConverterController.convertUserList(usersList)) + "\n");
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
                                SortedMap<Date, Set<String>> sortedMap = Tasks.calendar(tasksList.get(login), date1, date2);
                                if (sortedMap.isEmpty())
                                    streamWrite("calendar\n" + "empty\n");
                                else {
                                    streamWrite("calendar\n");// + new Gson().toJson(sortedMap)
                                    int i = 0;
                                    for (Date date:sortedMap.keySet()) {
                                        Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
                                        Date endDayTime = new Date((now.getTime() +(86400000 - now.getTime() % 86400000)));
                                        if (date.before(endDayTime) || date.equals(endDayTime)) {
                                            streamWrite(new Gson().toJson(date) + "\n" + new Gson().toJson(sortedMap.get(date)) + "\n");
                                            i++;
                                        } else if (i < 96) {
                                            streamWrite(new Gson().toJson(date) + "\n" + new Gson().toJson(sortedMap.get(date)) + "\n");
                                            i++;
                                        }
                                    }
                                    streamWrite("end\n");
                                    streamWrite(new Gson().toJson(sortedMap.lastKey()) + "\n");
                                }
                            }
                            break;

                        case "Banned:":
                            synchronized (usersList) {
                                strUser = in.readLine();
                                newUser = StringConverterController.makeUserFromString(strUser);
                                for (User user : usersList) {
                                    if (user.equals(newUser)){
                                        System.out.println("ban");
                                        user.setBanned(!newUser.isBanned());
                                        break;
                                    }
                                }
                            }
                            break;

                        case "Adminka:":
                            synchronized (usersList) {
                                strUser = in.readLine();
                                newUser = StringConverterController.makeUserFromString(strUser);
                                System.out.println(newUser);
                                for (User user : usersList) {
                                    if (user.equals(newUser)){
                                        System.out.println("adm");
                                        user.setAdmin("false".equals(newUser.getAdmin()) ? "admin" : "false");
                                        break;
                                    }
                                }
                            }
                            break;

                        case "Rebut:":
                            ServerSceneController controller = new ServerSceneController();
                            controller.rebut(null);

                            synchronized (activeUsers) {
                                for (User user : usersList) {
                                    if (user.getLogin().equals(login)) {
                                        activeUsers.remove(user);
                                        break;
                                    }
                                }
                            }

                            whileCondition = false;
                            break;

                        case "Exit:":
                            synchronized (tasksList) {
                                if (login != null)
                                    tasksList.put(login, taskArrayList);
                            }
                            socket.close();
                            synchronized (activeUsers) {
                                for (User user : usersList) {
                                    if (user.getLogin().equals(login)) {
                                        activeUsers.remove(user);
                                        break;
                                    }
                                }

                            }
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
        printWriter.print(write);
        printWriter.flush();
    }
}

