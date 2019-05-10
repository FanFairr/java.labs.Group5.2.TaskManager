package com.company.lab2.server.model;

import com.company.lab2.server.controllers.StringConverterController;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/** task class */
public class Task implements Cloneable, Serializable {

    /** task title */
    private String title;
    /** task time */
    private Date time;
    /** task start time */
    private Date startTime;
    /** task end time */
    private Date endTime;
    /** task repeat interval */
    private int repeatInterval;
    /** task active status */
    private boolean active;

    /**
     * constructs an inactive task that runs
     * at a given time without
     * repeating with a given name
     *@param title task title
     *@param time task time
     **/
    public Task(String title, Date time) {
        this.title = title;
        if (time == null) {
            this.time = null;
        } else this.time = (Date) time.clone();
    }

    /**
     * constructs an inactive task that runs
     * at a given start and end time with
     * repeating with a given name
     *@throws IllegalArgumentException
     * task repetition interval should not be null
     *@throws IllegalArgumentException
     * The start and end time of the task must be
     * at least zero and the end time of
     * the task must be greater than the start time
     *@param title task title
     *@param start task start time
     *@param end task end time
     *@param interval task repeat interval
     **/
    public Task(String title, Date start, Date end, int interval)
            throws IllegalArgumentException {
        if (interval <= 0) {
            throw new IllegalArgumentException("task repetition interval must be greater than zero!!");
        } else if (start == null || end == null || end.getTime() < start.getTime() + interval * 1000) {
            throw new IllegalArgumentException("The start and end time of the task must be "
                    + "at least zero and the end time of "
                    + "the task must be greater than the start time");
        } else {
            this.title = title;
            this.time = null;
            this.startTime = (Date) start.clone();
            this.endTime = (Date) end.clone();
            this.repeatInterval = interval;
        }
    }

    /**
     * Method for reading the task title
     *@return task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method for setting the task name
     *@param title task title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Method for reading the state of the task
     *@return task active status
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Method for determining the status of a task
     *@param active task active status
     **/
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * A method for reading execution time
     * for non-repetitive tasks, if the
     * task repeats the method,
     * returns the start time of the repetition
     *@return task time
     **/
    public Date getTime() {
        if (repeatInterval > 0) {
            return startTime;
        } else {
            return time;
        }
    }

    /**
     * A method for changing the execution time
     * for non-repetitive tasks, if the task
     * is repeated, it should become non repetitive
     *@param time task time
     **/
    public void setTime(Date time) {
        repeatInterval = 0;
        startTime = null;
        endTime = null;
        this.time = (Date) time.clone();
    }

    /**
     * A method for reading start-up times
     * for repetitive tasks ..
     * if the task is not repeated,
     * the method must return
     * the execution time of the task.
     *@return time or startTime
     **/
    public Date getStartTime() {
        if (repeatInterval == 0) {
            return time;
        } else {
            return startTime;
        }
    }

    /**
     * A method for reading end-time
     * execution for repetitive tasks..
     * if the task is not repeated,
     * the method should return
     * the execution time of the task
     *@return time or endTime
     */
    public Date getEndTime() {
        if (repeatInterval == 0) {
            return time;
        } else {
            return endTime;
        }
    }

    /**
     * A method for reading repetitive
     * execution times for repetitive tasks..
     * if the task does not repeat it returns 0
     *@return repeatInterval
     */
    public int getRepeatInterval() {
        return repeatInterval;
    }

    /**
     * A method for changing the execution
     * time for repetitive tasks ..
     * if the task is not repeated,
     * the quench should become repetitive
     *@throws IllegalArgumentException
     * if the repetition interval of the task <= 0.
     *@param start task start time
     *@param end task end time
     *@param interval task interval
     **/
    public void setTime(Date start, Date end, int interval)
            throws IllegalArgumentException {
        if (interval <= 0) {
            throw new IllegalArgumentException("task repetition interval must be greater than zero!!");
        }else if (start == null || end == null || end.getTime() < start.getTime() + interval) {
            throw new IllegalArgumentException("The start and end time "
                    + "of the task should not be null "
                    + "and the end time of the task should"
                    + "be greater than the start time + interval");
        } else {
            repeatInterval = interval;
            startTime = (Date) start.clone();
            endTime = (Date) end.clone();
            time = null;
        }
    }

    /**
     * Method for checking the repetition of a task
     *@return the repetition of a task
     */
    public boolean isRepeated() {
        return repeatInterval != 0;
    }

    /**
     * returns the next time the task is executed
     * after the specified current time,
     * if the task is not executed after the specified time,
     * then the method has to rotate -1
     *@param current specified time
     *@return time or null
     **/
    Date nextTimeAfter(Date current) {
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


    /**
     * a method that compares the task and
     * returns the truth if the tasks are the same
     *@param o Object of type Object
     *@return the truth if the tasks are the same
     */
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

    /**
     * method returning the hash task code
     *@return the value of the hash code
     */
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

    /**
     * a method that returns the value field of a given object.
     *@return task information
     */
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        if (this.isRepeated()) {
            return "Title: '" + this.getTitle() +
                    "', startTime: " + format.format(this.getStartTime()) +
                    ", endTime: " +  format.format(this.getEndTime()) +
                    ", repeatInterval: " + StringConverterController.getStringFromRepeatInterval(this.getRepeatInterval()) +
                    ", active: " + this.isActive() + ";";
        }
        else {
            return"Title: '" + this.getTitle() +
                    "', time: " +  format.format(this.getTime()) +
                    ", active: " + this.isActive() + ";";
        }
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