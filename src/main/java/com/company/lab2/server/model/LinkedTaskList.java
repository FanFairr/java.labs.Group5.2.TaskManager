package com.company.lab2.server.model;

import java.util.Iterator;

/** Класс реализации списка задач**/
public class LinkedTaskList extends TaskList implements Cloneable {

    private int size;
    private Node first;
    private Node last;

    private static class Node implements Cloneable {
        Node prev;
        Task item;
        Node next;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;

            Node node = (Node) o;
            if (item != null ? !item.equals(node.item) : node.item != null) return false;
            if (next != null ? !next.equals(node.next) : node.next != null) return false;
            return prev != null ? prev.equals(node.prev) : node.prev == null;
        }

        @Override
        public int hashCode() {
            return item != null ? item.hashCode() : 0;
        }

        @Override
        public String toString() {
            String s;
            if (prev == null) {
                if (item == null) {
                    if (next == null) {
                        return "Node{prev=null, item=null, next=null}";
                    } else {
                        s = "Node{prev=null, item=null, next=" +
                                next.toString() + '}';
                    }
                } else {
                    if (next == null) {
                        s = "Node{prev=null" +
                                ", item=" + item.toString() +
                                ", next=null}";
                    } else {
                        s = "Node{prev=null" +
                                ", item=" + item.toString() +
                                ", next=" + next.toString() + '}';
                    }
                }
            } else {
                if (prev.item != null) {
                    if (item == null) {
                        if (next == null) {
                            s = "Node{prev=" + prev.item.toString() +
                                    ", item=null, next=null}";
                        } else {
                            s = "Node{prev=" + prev.item.toString() +
                                    ", item=null, next=" +
                                    next.toString() + '}';
                        }
                    } else {
                        if (next == null) {
                            s = "Node{prev=" + prev.item.toString() +
                                    ", item=" + item.toString() +
                                    ", next=null}";
                        } else {
                            s = "Node{prev=" + prev.item.toString() +
                                    ", item=" + item.toString() +
                                    ", next=" + next.toString() + '}';
                        }
                    }
                } else {
                    if (item == null) {
                        if (next == null) {
                            s = "Node{prev=nill, item=null, next=null}";
                        } else {
                            s = "Node{prev=null, item=null, next=" +
                                    next.toString() + '}';
                        }
                    } else {
                        if (next == null) {
                            s = "Node{prev=null, item=" + item.toString() +
                                    ", next=null}";
                        } else {
                            s = "Node{prev=null, item=" + item.toString() +
                                    ", next=" + next.toString() + '}';
                        }
                    }
                }
            }
            return s;
        }

        @Override
        protected Node clone() throws CloneNotSupportedException {
            return (Node) super.clone();
        }
    }

    /**
     * метод додає до списку вказану задачу.
     *
     * @param task объект типа Task
     * @throws IllegalArgumentException ошибка
     *                                  при передаче пустого объекта типа Task
     **/
    public void add(Task task) throws IllegalArgumentException {
        if (task.equals(new Task(null, null))) {
            throw new IllegalArgumentException("Ошибка!"
                    + " Нельзя добавлять пустые"
                    + " задачи в список!");
        } else {
            if (size == 0) {
                first = new Node(null, task, null);
                last = first;
            } else {
                last = new Node(last, task, null);
                last.prev.next = last;
            }
            size++;
        }
    }

    /**
     * метод видаляє задачу із
     * списку повертає
     * істину якщо задача була у списку
     * Якщо у списку було декілька таких задач,
     * необхідно видалити одну будь-яку.
     *
     * @param task объект типа Task
     * @return boolean
     **/
    public boolean remove(Task task) {
        Node get = first;
        if (size == 0) {
            return false;
        } else {
            for (int i = 0; i < size; i++) {
                if (get.item.equals(task)) {
                    if (i == 0 && size == 1) {
                        first = null;
                        last = null;
                    } else if (i == 0) {
                        get.next.prev = null;
                        first = get.next;
                    } else if (i == size - 1) {
                        get.prev.next = null;
                        last = get.prev;
                    } else {
                        get.next.prev = get.prev;
                        get.prev.next = get.next;
                    }
                    size--;
                    return true;
                } else if (get.next == null) {
                    return false;
                } else {
                    get = get.next;
                }
            }
            return false;
        }
    }

    /**
     * метод, повертає кількість задач у списку
     *
     * @return size
     **/
    public int size() {
        return size;
    }

    /**
     * метод, що повертає
     * задачу, яка знаходиться
     * на вказаному місці у списку,
     * перша задача має індекс 0.
     *
     * @param index индекс нужной задачи
     * @return задачу по индексу или null если её нет
     **/
    public Task getTask(int index) {

        if (index < 0 || size <= index) {
            return null;
        } else {
            if (index < (size + 1) / 2) {
                Node get = first;
                for (int i = 0; i < index; i++) {
                    get = get.next;
                }
                return get.item;
            } else {
                Node get = last;
                for (int i = size - 1; i > index; i--) {
                    get = get.prev;
                }
                return get.item;
            }
        }
    }

     /**
     * метод, що порівнюе задачі
     * і повертає істину, якщо така
     * задачі однакові.
     *
     * @param o объект типа Object
     * @return boolean
     * **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedTaskList)) return false;

        LinkedTaskList that = (LinkedTaskList) o;

        if (size != that.size) return false;

        if (size < 0) return false;
        else {
            for (int i = 0; i < size; i++) {
                if (!this.getTask(i).equals(that.getTask(i))) return false;
            }
        }
        return true;
    }

    /**
     * метод, що повертає хешкод задачі.
     *
     * @return значення хешКода
     **/
    @Override
    public int hashCode() {
        int result = size;
        if (first != null) {
            Node getThis;
            getThis = first;
            for (int i = 0; i < size; i++) {
                result = 31 * result + (getThis != null ? getThis.hashCode() : 0);
                assert getThis != null;
                getThis = getThis.next;
            }
        }
        return result;
    }

    /**
     * метод, що повертає строку значень
     * полів данного об'єкту.
     *
     * @return String
     **/
    @Override
    public String toString() {
        return "LinkedTaskList{" +
                "size=" + size +
                ", first=" + first.toString() +
                '}';
    }

    @Override
    protected LinkedTaskList clone() throws CloneNotSupportedException {
        super.clone();
        LinkedTaskList clone = new LinkedTaskList();
        for (Task a: this) {
            clone.add(a);
        }
        return clone;
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
            private Node get = first;

            @Override
            public boolean hasNext() {
                return index == 0 && size == 1 || 0 <= index && index < size - 1;
            }

            @Override
            public Task next() {
                //if (hasNext()) {
                    if (next_not_used) next_not_used = false;
                    else {
                        get = get.next;
                        index++;
                    }
                    return get.item;
                //} else throw new NoSuchElementException("No more elements in list");
            }

            @Override
            public void remove() {
                if (size == 0 ) throw new IllegalStateException("Список пуст, нечего удалять");
                else if (next_not_used) throw new IllegalStateException("Метод remove() нельзя использовать не использовав до этого метод next()");
                else {
                    if (index == 0 && size == 1) {
                        first = null;
                        last = null;
                        get = null;
                    } else if (index == 0) {
                        get.next.prev = null;
                        first = get.next;
                        get = first;
                        next_not_used = true;
                    } else if (index == size - 1) {
                        get.prev.next = null;
                        last = get.prev;
                        get = last;
                    } else {
                        get.next.prev = get.prev;
                        get.prev.next = get.next;
                        get = get.prev;
                        index--;
                    }
                    size --;
                }
            }
        };
    }
}

