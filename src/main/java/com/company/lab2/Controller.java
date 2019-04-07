package com.company.lab2;

import com.company.lab2.userControllers.MainController;
import com.company.lab2.userControllers.WindowMaker;
import com.company.lab2.userModel.Task;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

public class Controller extends Application {
    private static String login;
    private static TreeView<Task> calendarTreeView;
    public static ObservableList<Task> TaskList;
    public final static Logger logger = Logger.getLogger(MainController.class);

    @Override
    public void start(Stage primaryStage) throws Exception{
        final String path = "/view/Main.fxml";
        final String header = "Task Manager";
        WindowMaker.makeWindow(path, header);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void registration(String email, String password) {

    }

    public static void signIn(String email, String password) {

    }

    /**Method for GETTING  ListView tasks from server*/
    public static ListView<Task> getTaskListView() {
        return null;
    }

    /**Method for GETTING tasks from server*/
    //запрос на ObservableList тасок
    public static ObservableList<Task> getTaskList() {
        return null;
    }

    public static void parseJsonResponse() {}

    public static void addTask(Task task) {


        MainController.notificationInterrupt();
    }

    public static void deleteTask(Task task) {


        MainController.notificationInterrupt();
    }

    public static void changeTask(Task oldT, Task newT) {


        MainController.notificationInterrupt();
    }

    /**Method for getting TreeView calendar from server
     */
    public static boolean calendarTreeViewGetRequest(Date start, Date end){
        calendarTreeView = null;
        //тип response вернет что-то вроде строки после анмаршалинга которой сперва узнаем были ли найдены задачи
        //если да(true), то парсим их
        boolean isEmpty = false;
        return isEmpty;
    }

    //запрос календарь по таскам данного пользователя
    public static SortedMap<Date, Set<Task>> getCalendar(Date start, Date end) {


        return null;
    }


    public static TreeView<Task> getCalendarTreeView() {
        return calendarTreeView;
    }

}
