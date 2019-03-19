package com.company.lab2.model;

import java.util.Date;
import java.io.Serializable;

/** класс задачи**/
public class Task implements Cloneable, Serializable {

    private String title;
    private Date time;
    private Date startTime;
    private Date endTime;
    private int repeatInterval;
    private boolean active;

    /**конструює неактивну задачу,
     *яка виконується у заданий час
     *без повторення із заданою назвою
     *@param title заголовок
     *@param time час
     **/
    public Task(String title, Date time) {
        this.title = title;
        if (time == null) {
            this.time = null;
        } else this.time = (Date) time.clone();
    }

    /**конструює неактивну задачу,
     *що виконується у заданому
     *проміжку часу
     *(і початок і кінець включно)
     *із заданим інтервалом
     *і має задану назву.
     *@throws IllegalArgumentException
     *Интервал поторения задачи должен
     *быть не null!!.
     *@throws IllegalArgumentException
     *Время начала и конца выполнения задачи
     *должны быть не меньше ноля и время
     *конца выполнения задачи должно быть
     *больше времени начала интервал
     *@param title заголовок
     *@param start початок
     *@param end кінець
     *@param interval інтервал
     **/
    public Task(String title, Date start, Date end, int interval)
            throws IllegalArgumentException {
        if (interval <= 0) {
            throw new IllegalArgumentException("Интервал поторения задачи должен быть больше ноля!!");
        } else if (start == null || end == null || end.getTime() < start.getTime() + interval) {
            throw new IllegalArgumentException("Время начала и "
                    + "конца выполнения задачи должны быть не null "
                    + "и время конца выполнения задачи должно быть "
                    + "больше времени начала + интервал");
        } else {
            this.title = title;
            this.time = null;
            this.startTime = (Date) start.clone();
            this.endTime = (Date) end.clone();
            this.repeatInterval = interval;
        }
    }

    /**Метод для зчитування назви задачі
     *active title
     *@return title**/
    public String getTitle() {
        return title;
    }

    /**Метод для встановлення назви задачі
     *@param title заголовок
     **/
    public void setTitle(String title) {
        this.title = title;
    }

    /**Метод для зчитування стану задачі
     *@return active**/
    public boolean isActive() {
        return active;
    }

    /**Метод для встановлення стану задачі
     *@param active стан
     **/
    public void setActive(boolean active) {
        this.active = active;
    }

    /**Метод для зчитування часу
     *виконання для задач, що не повторюються,
     *у разі, якщо задача повторюється
     *метод має повертати
     *час початку повторення
     *@return time
     **/
    public Date getTime() {
        if (repeatInterval > 0) {
            return startTime;
        } else {
            return time;
        }
    }

    /**Метод для зміни часу виконання
     *для задач, що не повторюються, у разі,
     *якщо задача повторювалась,
     *вона має стати такою, що не повторюється.
     *@param time час виконання задачі
     **/
    public void setTime(Date time) {
        repeatInterval = 0;
        startTime = null;
        endTime = null;
        this.time = (Date) time.clone();
    }

    /**Метод для зчитування часу початку
     *виконання для задач, що повторюються..
     *у разі, якщо задача не
     *повторюється метод
     *має повертати час виконання задачі.
     *@return time or startTime
     **/
    public Date getStartTime() {
        if (repeatInterval == 0) {
            return time;
        } else {
            return startTime;
        }
    }

    /**Метод для зчитування часу кінця
     *виконання для задач, що повторюються..
     *у разі, якщо задача не
     *повторюється метод
     *має повертати час виконання задачі.
     *@return time or endTime**/
    public Date getEndTime() {
        if (repeatInterval == 0) {
            return time;
        } else {
            return endTime;
        }
    }

    /**Метод для зчитування часу
     *повторення виконання для задач,
     *що повторюються.. у разі, якщо
     *задача не повторюється повертає 0.
     *@return repeatInterval**/
    public int getRepeatInterval() {
        return repeatInterval;
    }

    /**Метод для зміни часу виконання
     *для задач, що повторюються.. у разі,
     *якщо задача не повторювалася
     *воня має стати такою, що повторюється.
     @throws IllegalArgumentException якщо
      *інтервал повтору задачі <=0.
     @param start початок
     @param end кінець
     @param interval інтевал
     **/
    public void setTime(Date start, Date end, int interval)
            throws IllegalArgumentException {
        if (interval <= 0) {
            throw new IllegalArgumentException("Интервал повторения задачи должен быть больше ноля!!");
        }else if (start == null || end == null || end.getTime() < start.getTime() + interval) {
            throw new IllegalArgumentException("Время начала и "
                    + "конца выполнения задачи должны быть не null "
                    + "и время конца выполнения задачи должно быть "
                    + "больше времени начала + интервал");
        } else {
            repeatInterval = interval;
            startTime = (Date) start.clone();
            endTime = (Date) end.clone();
            time = null;
        }
    }

    /**Метод для перевірки повторюваності
     *задачі
     *@return boolean**/
    public boolean isRepeated() {
        return repeatInterval != 0;
    }

    /**повертає час наступного виконання
     *задачі після вказаного часу current,
     *якщо після вказаного часу задача не
     *виконується, то метод має повертати -1.
     *@param current указаний час
     *@return time or null
     **/
    public Date nextTimeAfter(Date current) {
        if (active) {
            if (this.isRepeated()) {
                if (startTime.getTime() <= current.getTime() && current.getTime() <= endTime.getTime()) {
                    long varTime = startTime.getTime();
                    while (varTime <= current.getTime()) {
                        varTime += (repeatInterval*1000);
                    }
                    Date time = new Date(varTime);
                    if (time.compareTo(endTime) > 0) {
                        return null;
                    } else {
                        return time;
                    }
                } else if (startTime.getTime() > current.getTime()) {
                    return startTime;
                } else if (current.getTime() > endTime.getTime()) {
                    return null;
                }
            } else if (current.compareTo(time) < 0) {
                return time;
            } else {
                return null;
            }
        } else {
            return null;
        }
        return null;
    }


    /** метод, що порівнюе задачі
     *і повертає істину, якщо така
     *задачі однакові.
     *@param o объект типа Object
     *@return boolean**/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (repeatInterval != task.repeatInterval) return false;
        if (active != task.active) return false;
        if (title != null ? !title.equals(task.title) : task.title != null) return false;
        if (time != null ? !time.equals(task.time) : task.time != null) return false;
        if (startTime != null ? !startTime.equals(task.startTime) : task.startTime != null) return false;
        return endTime != null ? endTime.equals(task.endTime) : task.endTime == null;
    }

    /** метод, що повертає хешкод задачі.
     *@return значення хешКода**/
    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + repeatInterval;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
    /** метод, що повертає строку значень
     * полів данного об'єкту.
     *@return String**/
    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", repeatInterval=" + repeatInterval +
                ", active=" + active +
                '}';
    }


    @Override
    public Task clone() throws CloneNotSupportedException {
        Task clone = (Task) super.clone();
        clone.title = title;
        if (time == null) {
            clone.time = null;
        } else clone.time = (Date) time.clone();
        if (startTime == null) {
            clone.startTime = null;
        } else clone.startTime = (Date) startTime.clone();
        if (endTime == null) {
            clone.endTime = null;
        } else  clone.endTime = (Date) endTime.clone();
        clone.repeatInterval = repeatInterval;
        clone.active = active;
        return clone;
    }
}