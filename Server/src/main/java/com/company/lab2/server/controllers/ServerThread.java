package com.company.lab2.server.controllers;

import com.company.lab2.server.model.Patterns;
import com.company.lab2.server.model.Task;
import com.company.lab2.server.model.Tasks;
import com.company.lab2.server.model.User;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * class of allocation of a separate stream for processing client requests
 */
public class ServerThread extends Thread {
    private Logger LOGGER = Logger.getLogger(ServerThread.class);

    /** message flow */
    private BufferedReader in;
    /** message flow */
    private PrintWriter printWriter;

    /** client socket */
    private Socket socket;
    /** current client */
    private User currentUser;
    /** current client login */
    private String login;

    /** list of all clients */
    private final ArrayList<User> usersList;
    /** information of client tasks */
    private final TreeMap<String, ArrayList<Task>> tasksList;
    /** current client task list */
    private ArrayList<Task> taskArrayList;
    /** a list of customers that want to get the admin rights */
    private final ArrayList<User> adminList;
    /** list of active client */
    private LinkedList<User> activeUsers;

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

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            while (whileCondition) {
                String response = in.readLine();
                if (response != null && !response.isEmpty()) {
                    switch (response) {
                        case Patterns.LOGIN:
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

                        case Patterns.REGISTRATION:
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

                        case Patterns.TASK:
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

                        case Patterns.ADD:
                            taskArrayList.add(StringConverterController.makeTaskFromString(in.readLine()));
                            streamWrite("doneADCH\n");
                            break;

                        case Patterns.DELETE:
                            taskArrayList.remove(StringConverterController.makeTaskFromString(in.readLine()));
                            streamWrite("doneADCH\n");
                            break;

                        case Patterns.CHANGE:
                            Task oldT = StringConverterController.makeTaskFromString(in.readLine());
                            Task newT = StringConverterController.makeTaskFromString(in.readLine());
                            taskArrayList.set(taskArrayList.indexOf(oldT), newT);
                            streamWrite("doneADCH\n");
                            break;

                        case Patterns.ISADMIN:
                            if (currentUser.getAdmin().equals("false")) {
                                synchronized (adminList) {
                                    if (adminList.contains(currentUser)) {
                                        streamWrite("isAdmin\nfalse\nwaiting\n");
                                    } else streamWrite("isAdmin\nfalse\nnotWaiting\n");
                                }
                            } else streamWrite("isAdmin\n"+currentUser.getAdmin()+"\n");
                            break;

                        case Patterns.BECOMEADM:
                            String code = in.readLine();
                            if (code.equals("123")) {
                                synchronized (adminList) {
                                    if (!adminList.contains(currentUser)) {
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

                        case Patterns.USERSLIST:
                            synchronized (adminList) {
                                streamWrite("usersList\n" + gson.toJson(StringConverterController.convertUserList(usersList)) + "\n");
                            }
                            break;

                        case Patterns.ADMINLIST:
                            synchronized (adminList) {
                                streamWrite("adminsList\n" + gson.toJson(StringConverterController.convertUserList(adminList)) + "\n");
                            }
                            break;

                        case Patterns.CALENDAR:
                            Date date1 = null;
                            Date date2 = null;
                            try {
                                date1 = format.parse(in.readLine());
                                date2 = format.parse(in.readLine());
                            } catch (ParseException e) {
                                LOGGER.error("class ServerThread line 226 Parsing error");
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

                        case Patterns.BANNED:
                            synchronized (usersList) {
                                strUser = in.readLine();
                                newUser = StringConverterController.makeUserFromString(strUser);
                                for (User user : usersList) {
                                    if (user.equals(newUser)){
                                        if (user.getAdmin().equals("SuperAdmin"))
                                            break;
                                        else {
                                            user.setBanned(!newUser.isBanned());
                                            break;
                                        }
                                    }
                                }
                            }
                            break;

                        case Patterns.ADMINKA:
                            synchronized (usersList) {
                                strUser = in.readLine();
                                newUser = StringConverterController.makeUserFromString(strUser);
                                for (User user : usersList) {
                                    if (user.equals(newUser)){
                                        if (user.getAdmin().equals("SuperAdmin"))
                                            break;
                                        else {
                                            adminList.remove(user);
                                            user.setAdmin("false".equals(newUser.getAdmin()) ? "admin" : "false");
                                            break;
                                        }
                                    }
                                }
                            }
                            break;

                        case Patterns.REBUT:
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

                        case Patterns.EXIT:
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
                            LOGGER.error("smth wrong with response");
                            break;
                    }
                    Thread.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("class ServerThread IOException or InterruptedException");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error("error when BufferedReader closed " + e.getMessage());
            } finally {
                printWriter.close();
            }
        }
    }

    /**
     * message sending method
     * @param write - message
     */
    private void streamWrite(String write) {
        printWriter.print(write);
        printWriter.flush();
    }
}

