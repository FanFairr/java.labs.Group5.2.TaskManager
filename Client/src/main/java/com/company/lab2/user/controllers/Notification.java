package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.application.Platform;

import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

import static com.company.lab2.user.Controller.LOGGER;

/**
 * Thread class for checking taskList
 * and make alarm signal when it's time to do Task
 */
public class Notification extends Thread {
    /**current time*/
    private Date now;
    /**end of current day time*/
    private Date endDayTime;
    /**next alarm time */
    private Date nextAlarmDate;
    /**calendar map by time*/
    private SortedMap<Date, Set<String>> calendarByTime;
    /**boolean statement for checking usage of
     * makeCalendarForDay() method*/
    private boolean notFirstTimeHereAfterMonitor;
    /**boolean statement for while loop*/
    private boolean checkForWhile = true;
    /**TITLE of notification*/
    private final String TITLE = "Task Time!";
    /**HEADER of notification*/
    private final String HEADER = "Time to do this Task";


    /**Method for making Calendar for one day
     * from current time to 03:00:00 next day
     */
    private void makeCalendarForDay() {
        if (!notFirstTimeHereAfterMonitor) {
            now = new Date(System.currentTimeMillis() / 1000 * 1000);
            endDayTime = new Date((now.getTime() + (86400000 - now.getTime() % 86400000)));
            calendarByTime = Controller.calendar(now, endDayTime);
            notFirstTimeHereAfterMonitor = false;
        }
    }

    @Override
    public void run() {
        LOGGER.debug("new Thread");
        while (checkForWhile){
            makeCalendarForDay();
            try {
                while ((calendarByTime == null ? 0 : calendarByTime.size()) > 0) {
                    if (notFirstTimeHereAfterMonitor) {
                        now = new Date(System.currentTimeMillis() / 1000 * 1000);
                    } else notFirstTimeHereAfterMonitor = true;
                    if (nextAlarmDate != null && now.getTime() == nextAlarmDate.getTime()) {
                        if (calendarByTime.get(now) != null ) {
                            for (String task : calendarByTime.get(now)) {
                                LOGGER.debug("alarm!!!");
                                Platform.runLater(() -> WindowMaker.alertWindowInf(TITLE, HEADER, task));
                            }
                            calendarByTime.remove(now);
                        }
                    }
                    if (nextAlarmDate == null || !now.equals(nextAlarmDate)) {
                        if (calendarByTime != null) {
                            for (Date date : calendarByTime.keySet()) {
                                if (date.getTime() == now.getTime()) {
                                    if (calendarByTime.get(now) != null ) {
                                        for (String task : calendarByTime.get(now)) {
                                            LOGGER.debug("alarm!!!");
                                            Platform.runLater(() -> WindowMaker.alertWindowInf(TITLE, HEADER, task));
                                        }
                                        calendarByTime.remove(now);
                                        break;
                                    }
                                } else if (now.getTime() < date.getTime()) {
                                    nextAlarmDate = date;
                                    if (nextAlarmDate.getTime() - now.getTime() > 1) {
                                        long sleep = nextAlarmDate.getTime() - now.getTime() - 1;
                                        LOGGER.debug("sleep to alarm " + sleep + " mils");
                                        sleep(sleep);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                LOGGER.debug("sleep to the next day");
                sleep(endDayTime.getTime() - now.getTime());
                break;
            } catch (InterruptedException e) {
                LOGGER.debug("exception");
                checkForWhile = false;
                break;
            }
        }
        LOGGER.debug("end Thread");
    }
}