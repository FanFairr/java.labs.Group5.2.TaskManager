package com.company.lab2.controllers;

import com.company.lab2.Controller;
import com.company.lab2.model.Task;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

import static com.company.lab2.Controller.logger;

/**Thread class for checking taskList
 * and make alarm signal when it's time to do Task
 */
public class Notification extends Thread {
    private ObservableList<Task> TaskList;
    private Date now;
    private Date endDayTime;
    private Date nextAlarmDate;
    private SortedMap<Date, Set<Task>> calendarByTime;
    private boolean notFirstTimeHereAfterMonitor;
    private boolean checkForWhile = true;
    private final String title = "Task Time!";
    private final String header = "Time to do this Task";

    /**Method for making Calendar for one day
     * from current time to 02:00:00 next day
     */
    private void makeCalendarForDay() {
        if (!notFirstTimeHereAfterMonitor) {
            TaskList = Controller.getTaskList();
            now = new Date(System.currentTimeMillis() / 1000 * 1000);
            endDayTime = new Date((now.getTime() +(86400000 - now.getTime() % 86400000)));
            calendarByTime = Controller.getCalendar(now, endDayTime);
            notFirstTimeHereAfterMonitor = false;
        }
    }

    @Override
    public void run() {
        logger.debug("new Thread");
        mark:
        while (checkForWhile){
            makeCalendarForDay();
            TaskList = null;
            try {
                while ((calendarByTime == null ? 0 : calendarByTime.size()) > 0) {
                    Thread.sleep(200);
                    if (notFirstTimeHereAfterMonitor) {
                        now = new Date(System.currentTimeMillis() / 1000 * 1000);
                    } else notFirstTimeHereAfterMonitor = true;
                    //зайдем если текущее время = времени зади
                    if (nextAlarmDate != null && now.getTime() == nextAlarmDate.getTime()) {
                        //то походи по сету задач этого ключа и выводи все задачи сета
                        if (calendarByTime.get(now) != null ) {
                            for (Task task : calendarByTime.get(now)) {
                                logger.debug("alarm!!!");
                                Platform.runLater(() -> WindowMaker.alertWindowInf(title, header, task.getTitle()));
                            }
                            //удаляем объекты мапы
                            calendarByTime.remove(now);
                        }
                    }
                    //зайдем если текущее время > времени зади или время задачи еще не задано
                    if (nextAlarmDate == null || now.getTime() != nextAlarmDate.getTime()) {
                        //проходим по сету ключей тримапы
                        for (Date date : calendarByTime.keySet()) {
                            //если текущее время соответствует ключу сета
                            if (date.getTime() == now.getTime()) {
                                //то проходим по сету задач этого ключа и выводи все задачи сета
                                if (calendarByTime.get(now) != null ) {
                                    for (Task task : calendarByTime.get(now)) {
                                        logger.debug("alarm!!!");
                                        Platform.runLater(() -> WindowMaker.alertWindowInf(title, header, task.getTitle()));
                                    }
                                    //удаляем объекты мапы
                                    calendarByTime.remove(now);
                                    break;
                                }
                            //если текущее время меньше ключа сета
                            } else if (now.getTime() < date.getTime()) {
                                //запоминаем время
                                nextAlarmDate = date;
                                logger.debug("sleep to alarm " +(nextAlarmDate.getTime() - now.getTime()));
                                //время на которое засыпаем
                                long sleep = nextAlarmDate.getTime() - now.getTime();
                                Thread.sleep(sleep);
                                break;
                            } else break mark;
                        }
                    }
                }
                //если календарь на сегодня пуст - спим до завтра
                logger.debug("sleep to the next day");
                Thread.sleep(endDayTime.getTime() - now.getTime());
                break;
            } catch (InterruptedException e) {
                logger.debug("exception");
                checkForWhile = false;
                break;
            }
        }
        logger.debug("end Thread");
    }
}