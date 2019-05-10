package com.company.lab2.server.model;

import java.util.*;

/**
 * class for finding tasks by criterion
 */
public class Tasks {
    /**
     * a method that returns a subset of tasks
     * that are scheduled to execute at least once
     * after the time from and no later than that
     *@param tasks task iterator
     *@param start time from which we are looking for a task
     *@param end time to which we are looking for a task
     *@return task list iterator
     **/
    private static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end){
        if (tasks == null)
            return null;
        Iterator<Task> iter = tasks.iterator();
        ArrayList<Task> to_return = new ArrayList<>();
        Iterable<Task> iter_return;
        Task current;
        while (iter.hasNext()){
            current = iter.next();
            if (current.nextTimeAfter(start) != null
                    && (current.nextTimeAfter(start).compareTo(end) < 0
                    || current.nextTimeAfter(start).compareTo(end) == 0)) {
                to_return.add(current);
            }
        }
        iter_return = to_return;
        return iter_return;
    }

    /**
     * method, build a task calendar for a given period
     *@param tasks task iterator
     *@param start time from which we are looking for a task
     *@param end time to which we are looking for a task
     *@return linked task list
     **/
    public static SortedMap<Date, Set<String>> calendar(Iterable<Task> tasks, Date start, Date end) {
        if (tasks == null)
            return null;
        Iterator<Task> iter = incoming(tasks, start, end).iterator();
        SortedMap<Date,Set<String>> map = new TreeMap<>();
        Set<String> set;
        Task task;
        Date date;
        while (iter.hasNext()){
            task = iter.next();
            if (task.isActive()) {
                if (task.isRepeated()) {
                    int interval = task.getRepeatInterval();
                    for (long i = start.getTime(); i < end.getTime(); i += (interval*1000)) {
                        Date d = new Date(i);
                        date = task.nextTimeAfter(d);
                        if (date != null) {
                            Task t = new Task(task.getTitle(), date);
                            t.setActive(true);
                            if (map.get(date) == null) {
                                set = new HashSet<>();
                                set.add(t.toString());
                                map.put(date,set);
                            } else {
                                set = map.get(date);
                                set.add(t.toString());
                            }
                        }
                    }
                } else {
                    date = task.nextTimeAfter(start);
                    if (map.get(date) == null) {
                        set = new HashSet<>();
                        set.add(task.toString());
                        map.put(date,set);
                    } else {
                        set = map.get(date);
                        set.add(task.toString());
                    }
                }
            }
        }
        return map;
    }
}
