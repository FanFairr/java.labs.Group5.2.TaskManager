package com.company.lab2.server.model;

import java.util.*;

public class Tasks {
    /** метод, що повертає підмножину задач,
     *які заплановані на виконання хоча б раз
     *після часу from і не пізніше ніж to.
     *@param tasks итератор по задачам
     *@param start время от которого ищем задачу
     *@param end время до которого ищем задачу
     *@return итератор по списку задач
     **/
    public static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end){
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

    /** метод, будувати календар задач на заданий період.
     *@param tasks итератор по задачам
     *@param start время от которого ищем задачу
     *@param end время до которого ищем задачу
     *@return связанный список задач
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
