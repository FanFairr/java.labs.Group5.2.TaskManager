package com.company.lab2.model;

import java.io.OutputStream;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.company.lab2.Controller.logger;

public class TaskIO implements Serializable{

    /**метод, що записує задачі із
     *списку у потік у бінарному форматі.
     *@param tasks список задач
     *@param out поток вывода
     */
    public static void write(TaskList tasks, OutputStream out) throws IOException {
        try (DataOutputStream data = new DataOutputStream(out)) {
            data.writeInt(tasks.size());
            for (Task task : tasks) {
                data.writeInt(task.getTitle().length());
                data.writeChars(task.getTitle());
                data.writeBoolean(task.isActive());
                data.writeInt(task.getRepeatInterval());
                if (task.isRepeated()) {
                    data.writeLong(task.getStartTime().getTime());
                    data.writeLong(task.getEndTime().getTime());
                } else data.writeLong(task.getTime().getTime());
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при записи в OutputStream из колекции");
        }
    }

    /**метод, що зчитує задачі  із
     *потоку у даний список задач.
     *@param tasks список задач
     *@param in поток ввода
     **/
    public static void read(TaskList tasks, InputStream in) throws IOException {
        DataInputStream data = null;
        Task task;
        String title;
        int taskListSize;
        int titleSize;
        char []title_in_chars;
        int repInt;
        boolean active;
        try{
            data = new DataInputStream(in);
            taskListSize = data.readInt();
            for (int i = 0; i < taskListSize; i++) {
                titleSize = data.readInt();
                title_in_chars = new char[titleSize];
                for (int j = 0; j < titleSize ; j++) {
                    title_in_chars[j] = data.readChar();
                }
                title = new String(title_in_chars);
                active = data.readBoolean();
                repInt = data.readInt();
                if (repInt == 0) task = new Task(title, new Date(data.readLong()));
                else task = new Task(title, new Date(data.readLong()), new Date(data.readLong()), repInt);
                task.setActive(active);
                tasks.add(task);
            }
        } catch(IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при чтении в InputStream из колекции");
        } finally {
                if(data != null)
                    data.close();
        }
    }

    /**метод, що записує задачі
     *із списку у файл.
     *@param tasks список задач
     *@param file файл для вывода
     */
    public static void writeBinary(TaskList tasks, File file) throws IOException {
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            write(tasks, out);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(),ex);
            throw new FileNotFoundException("Файл не был найден!");
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при записи задач в файл");
        }
    }

    /**метод, що записує задачі
     *із файлу у список задач.
     *@param tasks список задач
     *@param file файл для ввода
     */
    public static void readBinary(TaskList tasks, File file) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            read(tasks, in);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(),ex);
            throw new FileNotFoundException("Файл не был найден!");
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при чтении задач из файла");
        }
    }

    /**метод, що записує задачі зі списку
     *у потік в текстовому форматі.
     *@param tasks список задач
     *@param out поток вывода
     */
    public static void write(TaskList tasks, Writer out) throws IOException {
        BufferedWriter wr = null;
        StringBuilder toWrite;
        SimpleDateFormat format = new SimpleDateFormat("[yyyy-MM-dd kk:mm:ss.SSS]");
        int index = 0;
        try {
            wr = new BufferedWriter(out);
            for (Task task:tasks) {
                toWrite = new StringBuilder();
                toWrite.append("\"");
                toWrite.append(task.getTitle().replace("\"", "\"\""));
                toWrite.append("\"");

                if (!task.isRepeated()) {
                    toWrite.append(" at ");
                    toWrite.append(format.format(task.getTime()));
                } else {
                    toWrite.append(" from ");
                    toWrite.append(format.format(task.getStartTime()));
                    toWrite.append(" to ");
                    toWrite.append(format.format(task.getEndTime()));
                    toWrite.append(" every ");
                    toWrite.append("[");
                    toWrite.append(getStringFromRepeatInterval(task.getRepeatInterval()));
                    toWrite.append("]");
                }
                if (!task.isActive()) toWrite.append(" inactive");
                if (index == tasks.size() - 1) toWrite.append('.');
                else toWrite.append(";");
                wr.write(new String(toWrite));
                wr.newLine();
                index++;
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при записи в поток из колекции");
        } finally {
                if(wr != null)
                    wr.close();
        }
    }

    /**метод, що зчитує задачі із потоку у список.
     *@param tasks список задач
     *@param in поток ввода
     */
    public static void read(TaskList tasks, Reader in) throws IOException {
        BufferedReader rd = null;
        String taskString;
        String toIdentified;
        String toDate;
        String toEndDate;
        String toInterval;
        Task task;
        String title;
        Date date;
        Date endDate;
        int interval;
        Pattern pt = Pattern.compile("\".*\"");
        try {
            rd = new BufferedReader(in);
            while ((taskString = rd.readLine()) != null) {
                Matcher mt = pt.matcher(taskString);
                if (mt.find()) {
                    title = mt.group();
                    taskString = taskString.replace(title,"");
                    title = title.replace("\"\"","\"");
                    title = title.substring(1, title.length() - 1);
                } else throw new ParseException("Задачи должны быть заключины в двойные скобки \"Task\"!", 0);

                toIdentified = taskString.substring(0, taskString.indexOf('['));

                switch (toIdentified) {
                    case " at ":
                        taskString = taskString.replace(" at ", "");
                        toDate = taskString.substring(1, taskString.lastIndexOf("]"));
                        taskString = taskString.replace(taskString.substring(0, taskString.lastIndexOf("]")), "");
                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(toDate);
                        task = new Task(title, date);
                        if (taskString.lastIndexOf("]") + 1 == taskString.lastIndexOf(";")
                                || taskString.lastIndexOf("]") + 1 == taskString.lastIndexOf(".")) {
                            task.setActive(true);
                        } else {
                            task.setActive(false);
                        }
                        break;
                    case " from ":
                        taskString = taskString.replace(" from ", "");
                        toDate = taskString.substring(1, taskString.lastIndexOf("] t"));
                        taskString = taskString.replace(taskString.substring(0, taskString.lastIndexOf("] t") + 1), "");
                        taskString = taskString.replace(" to ", "");
                        toEndDate = taskString.substring(1, taskString.lastIndexOf("] e"));
                        taskString = taskString.replace(taskString.substring(0, taskString.lastIndexOf("] e") + 1), "");
                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(toDate);
                        endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(toEndDate);
                        taskString = taskString.replace(" every ", "");
                        toInterval = taskString.substring(1, taskString.lastIndexOf("]"));
                        interval = parseRepeatInterval(toInterval);
                        taskString = taskString.replace(taskString.substring(0, taskString.lastIndexOf("]")), "");
                        task = new Task(title, date, endDate, interval);
                        if (taskString.lastIndexOf("]") + 1 == taskString.lastIndexOf(";")
                                || taskString.lastIndexOf("]") + 1 == taskString.lastIndexOf(".")) {
                            task.setActive(true);
                        } else {
                            task.setActive(false);
                        }
                        break;
                    default:
                        throw new ParseException("Неизвестный формат в reader при чтении из потока", 1);
                }
                tasks.add(task);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при чтении задач из потока в колекцию");
        } finally {
                if(rd != null)
                    rd.close();
        }
    }

    /**метод, що записує задачі у
     *файл у текстовому форматі.
     *@param tasks список задач
     *@param file файл вывода
     */
    public static void writeText(TaskList tasks, File file) throws IOException {
        try (Writer out = new BufferedWriter(new FileWriter(file))) {
            write(tasks, out);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(),ex);
            throw new FileNotFoundException("Файл не был найден!");
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при записи задач в файл");
        }
    }

    /**метод, що зчитує задачі із
     *файлу у текстовому вигляді.
     *@param tasks список задач
     *@param file файл ввода
     */
    public static void readText(TaskList tasks, File file) throws IOException, ParseException {
        try (Reader in = new BufferedReader(new FileReader(file))) {
            read(tasks, in);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(),ex);
            throw new FileNotFoundException("Файл не был найден!");
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new IOException("IOException, при чтении задач из файла");
        }
    }

    /**метод, что записывает интервал повторения
     *задачи в текстовый формат(строку).
     *@param repeatInterval интервал повторения задачи
     */
    public static String getStringFromRepeatInterval(final int repeatInterval) {
        StringBuilder builderString = new StringBuilder();
        int days = repeatInterval / 86400;
        int hours =  (repeatInterval / 3600) % 24;
        int minutes = (repeatInterval / 60) % 60;
        int seconds = repeatInterval % 60;
        builderString.append((days == 0? "": days + (days > 1 ? " days " : " day ")));
        builderString.append((hours == 0? "": hours + (hours > 1 ? " hours " : " hour ")));
        builderString.append((minutes == 0? "": minutes + (minutes > 1 ? " minutes " : " minute ")));
        builderString.append((seconds == 0? "": seconds + (seconds > 1 ? " seconds " : " second ")));
        String s = new String(builderString);
        return s.trim();
    }

    /**метод, что парсит строку в int
     * значение интервала повторение задачи.
     *@param data строчное представление интервала повторения задачи
     */
    private static int parseRepeatInterval(String data) throws NumberFormatException {
        int days = 0, hours = 0, minutes = 0, seconds = 0;
        String parsed [] = new String[4];
        Matcher matcher = Pattern.compile("\\d+\\s\\w").matcher(data);
        int counter = 0;
        while (matcher.find()) {
            parsed[counter] = matcher.group();
            counter++;
        }
        try {
            for (int i=0; i<counter; i++)  {
                switch (parsed[i].substring(parsed[i].length() - 1)) {
                    case "d":
                        days = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                    case "h":
                        hours = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                    case "m":
                        minutes = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                    case "s":
                        minutes = Integer.parseInt(parsed[i].substring(0, parsed[i].indexOf(" ")));
                        break;
                }
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Integer parse error при парсеровке repeatInterval");
        }
        return ((days * 86400) + (hours * 3600) + (minutes * 60) + seconds);
    }
}
