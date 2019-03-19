package com.company.lab2;

import com.company.lab2.controllers.MainController;
import com.company.lab2.model.ArrayTaskList;
import com.company.lab2.model.Task;
import com.company.lab2.model.TaskIO;
import com.company.lab2.model.Tasks;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import com.company.lab2.controllers.WindowMaker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

public class Controller extends Application {
    public final static Logger logger = Logger.getLogger(MainController.class);
    private static File file = new File("file.txt");

    @Override
    public void start(Stage primaryStage) throws Exception{
        final String path = "/view/Main.fxml";
        final String header = "Task Manager";
        WindowMaker.makeWindow(path, header);
    }

    public static void main(String[] args) {
        launch(args);

    }
    /**Method for reading tasks from file
     * if file not found make this file
     */
    public static ObservableList<Task> getFromFileToTaskList() {
        ArrayTaskList list = new ArrayTaskList();
        ObservableList<Task> TaskList = FXCollections.observableArrayList();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            TaskIO.readBinary(list, file);
        } catch (IOException e) {
            logger.error("Не найден файл для чтения",e);
            return TaskList;
        }
        if (list.size() != 0) {
            for (Task t: list) {
                TaskList.add(t);
            }
        }
        return TaskList;
    }

    /**Method for writing tasks in file
     * if file not found make this file
     */
    public static void saveInFile(ObservableList<Task> TaskList) {
        final String title = "Saving";
        String content;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            ArrayTaskList list = new ArrayTaskList();
            for (Task t:TaskList) {
                list.add(t);
            }
            TaskIO.writeBinary(list, file);
            MainController.setSaved(true);
            content = "Successful";
            WindowMaker.alertWindowInf(title, title, content);
        } catch (IOException e) {
            logger.error("Не найден файл для сохранения",e);
            content = "File not found";
            WindowMaker.alertWindowWarning(title, title, content);
        }
    }
    /**Method for making calendar
     */
    public static SortedMap<Date, Set<Task>> calendar(Date start, Date end){
        return Tasks.calendar(MainController.getTaskList(), start, end);
    }

    /**Method for formatting ListView Items
     */
    public static String formatListViewItems(Task item) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        if (item.isRepeated()) {
            return "Title: '" + item.getTitle() +
                    "', startTime: " + format.format(item.getStartTime()) +
                    ", endTime: " +  format.format(item.getEndTime()) +
                    ", repeatInterval: " + TaskIO.getStringFromRepeatInterval(item.getRepeatInterval()) +
                    ", active: " + item.isActive() + ";";
        }
        else {
            return"Title: '" + item.getTitle() +
                    "', time: " +  format.format(item.getTime()) +
                    ", active: " + item.isActive() + ";";
        }
    }
}
