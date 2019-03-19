package com.company.lab2.model;

import java.util.Arrays;
import java.util.Iterator;

/** Класс реализации списка задач**/
public class ArrayTaskList extends TaskList implements Cloneable {

    /** массив объектов типа таск.**/
    private Task[] taskArr = new Task[0];

    /** счетчик объэктов в массиве taskArr.**/
    private int size = 0;


    /** метод додає до списку вказану задачу.
     *@param task объект типа Task
     *@throws IllegalArgumentException ошибка при передачи
    пустого объекта типа Task
     **/
    public void add(Task task) throws IllegalArgumentException {
        if (task.equals(new Task(null,null))) {
            throw new  IllegalArgumentException("Ошибка!"
                    + " Нельзя добавлять пустые"
                    + " задачи в список!");
        } else {
            if (size < taskArr.length) {
                taskArr[size] = task;
                size++;
            } else {
                Task []newtaskArr = new Task[((taskArr.length + 2) / 2) + taskArr.length];
                System.arraycopy(taskArr, 0, newtaskArr, 0, size);
                newtaskArr[taskArr.length] = task;
                taskArr = newtaskArr;
                size++;
            }
        }
    }

    /**метод видаляє задачу із списку повертає
     істину якщо задача була у списку
     Якщо у списку було декілька таких задач,
     необхідно видалити одну будь-яку.
     *@param task объект типа Task
     *@return ret
     **/
    public boolean remove(Task task) {
        boolean ret = false;
        for (int i = 0; i < size; i++) {
            if (taskArr[i].equals(task)) {
                for (int j = i; j < size; j++) {
                    if (j == size - 1) {
                        taskArr[j] = null;
                    } else {
                        taskArr[j] = taskArr[j + 1];
                    }
                }
                size--;
                ret = true;
                break;
            } else ret = false;
        }
        return ret;
    }

    /**метод, повертає кількість задач у списку
     *@return size
     **/
    public int size() {
        return size;
    }

    /** метод, що повертає задачу,
     *яка знаходиться на вказаному
     *місці у списку,
     *перша задача має індекс 0.
     *@param index -индекс нужной задачи
     *@return задачу по индексу или null если её нет
     **/
    public Task getTask(int index) {
        if (index < size && index>=0) {
            return taskArr[index];
        } else {
            return null;
        }
    }

    /** метод, що порівнюе задачі
     *і повертає істину, якщо така
     *задачі однакові.
     *@param o объект типа Object
     *@return boolean**/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayTaskList)) return false;

        ArrayTaskList that = (ArrayTaskList) o;

        if (size != that.size) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(taskArr, that.taskArr);
    }

    /** метод, що повертає хешкод задачі.
     *@return значення хешКода**/
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(taskArr);
        result = 31 * result + size;
        return result;
    }

    /** метод, що повертає строку значень
     * полів данного об'єкту.
     *@return String**/
    @Override
    public String toString() {
        return "ArrayTaskList{" +
                "taskArr=" + Arrays.toString(taskArr) +
                ", size=" + size +
                '}';
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return new Iterator<Task>() {

            private boolean next_not_used = true;
            private int index = 0;


            @Override
            public boolean hasNext() {
                return (index == 0 && size == 1 || index < size) && taskArr[index] != null;
            }

            @Override
            public Task next() {
                if (next_not_used) next_not_used = false;
                return taskArr[index++];
            }

            @Override
            public void remove() {
                if (size == 0 ) throw new IllegalStateException("Список пуст, нечего удалять");
                else if (next_not_used) throw new IllegalStateException("Метод remove() нельзя использовать не использовав до этого метод next()");
                else {
                    if (index != 0 || size != 1) {
                        if (index == size - 1 && index != 0) {
                            index--;
                            taskArr[--size] = null;
                        } else {
                            int numMoved = size - index - 1;
                            System.arraycopy(taskArr, index + 1, taskArr, index, numMoved);
                            index--;
                        }
                    } else taskArr[--size] = null;
                }
            }
        };
    }

    @Override
    protected ArrayTaskList clone() throws CloneNotSupportedException {
        super.clone();
        ArrayTaskList clone = new ArrayTaskList();
        clone.taskArr = taskArr.clone();
        clone.size = size;
        return clone;
    }
}