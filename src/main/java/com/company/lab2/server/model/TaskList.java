package com.company.lab2.server.model;

import java.io.Serializable;

/**Класс реализации связанного списка задач**/
public abstract class TaskList implements Iterable<Task>, Cloneable, Serializable {

    /** метод, що додає до
    *списку вказану задачу.
    *@param task объект типа Task
    *@throws IllegalArgumentException ошибка
    *при передачи пустого объекта типа Task
    **/
    public abstract void add(Task task) throws IllegalArgumentException;

    /** метод, що видаляє задачу із
    *списку і повертає істину, якщо така
    *задача була у списку. Якщо у списку
    *було декілька таких задач, необхідно
    *видалити одну будь-яку.
    *@param task объект типа Task
    *@return boolean**/
    public abstract boolean remove(Task task);

    /** метод, що повертає кількість
    *задач у списку.
    *@return size**/
    public abstract int size();

    /** метод, що повертає задачу, яка
    *знаходиться на вказаному місці у
    *списку, перша задача має індекс 0.
    *@param index индекс нужной задачи
    *@return задачу по индексу или null если её нет
    **/
    public abstract Task getTask(int index);

    /** метод, що повертає хешкод задачі.
     *@return значення хешКода**/
    @Override
    public abstract int hashCode();

    /** метод, що порівнюе задачі
     *і повертає істину, якщо
     *задачі однакові.
     *@return boolean**/
    @Override
    public abstract boolean equals(Object obj);

    /** метод, що повертає строку значень
     * полів данного об'єкту.
     *@return String**/
    @Override
    public abstract String toString();
}
