package com.company.lab2.server.model;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * class to work with xml files
 */
public class TaskIO {
    private Logger LOGGER = Logger.getLogger(TaskIO.class);

    /** file name */
    private final String FILENAME = "information.xml";

    /**
     * data readout method of the file
     * @param information - data store with key - login and value - list of tasks related to this client
     * @param usersList - collection to which you want to record data about customers
     */
    public void readData(TreeMap<String, ArrayList<Task>> information, ArrayList<User> usersList) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(FILENAME);
            NodeList userList = document.getElementsByTagName("user");

            for (int i = 0; i < userList.getLength(); i++) {
                Node node = userList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element user = (Element) node;
                    String login = user.getAttribute("login");
                    String password = user.getAttribute("password");
                    String isBanned = user.getAttribute("isBanned");
                    String admin = user.getAttribute("grantAdmin");
                    usersList.add(new User(login, password, Boolean.parseBoolean(isBanned), admin));

                    NodeList tasks = user.getChildNodes();
                    ArrayList<Task> taskArrayList = new ArrayList<>();
                    for (int j = 0; j < tasks.getLength(); j++) {
                        Node node1 = tasks.item(j);

                        if (node1.getNodeType() == Node.ELEMENT_NODE) {
                            Element task = (Element) node1;

                            NodeList attributes = task.getChildNodes();
                            StringBuilder attributesString = new StringBuilder();

                            for (int k = 0; k < attributes.getLength(); k++) {
                                Node node2 = attributes.item(k);

                                if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                    Element attribute = (Element) node2;

                                    attributesString.append(attribute.getAttribute("value")).append(" ");
                                }
                            }

                            taskArrayList.add(parserStroki(attributesString.toString()));
                        }
                    }
                    information.put(login, taskArrayList);
                }

            }
        } catch (ParserConfigurationException | ParseException | IOException | SAXException e) {
            LOGGER.error("error while reading from the xml file");
        }
    }

    /**
     * method to cast a string to an object of type Task
     * @param attr - task information
     * @return - task object
     * @throws ParseException - incorrectly stored data
     */
    private Task parserStroki(String attr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
        Task task;

        String title = attr.substring(1, attr.indexOf("]"));
        attr = attr.substring(attr.indexOf("]", 1) + 2);

        String time = attr.substring(0, attr.indexOf(" "));
        attr = attr.substring(attr.indexOf(" ") + 1);

        String start = attr.substring(0, attr.indexOf(" "));
        attr = attr.substring(attr.indexOf(" ") + 1);

        String end = attr.substring(0, attr.indexOf(" "));
        attr = attr.substring(attr.indexOf(" ") + 1);

        String interval = attr.substring(0, attr.indexOf(" "));
        attr = attr.substring(attr.indexOf(" ") + 1);

        String active = attr.substring(0, attr.indexOf(" "));

        if ("".equals(time)) {
            task = new Task(title, dateFormat.parse(start), dateFormat.parse(end), Integer.parseInt(interval));
        } else {
            task = new Task(title, dateFormat.parse(time));
        }
        task.setActive(Boolean.parseBoolean(active));
        return task;
    }

    /**
     * write method to xml file
     * @param information - data store with key - login and value - list of tasks related to this client
     * @param usersList - collection to which you want to record data about customers
     */
    public void writeData(TreeMap<String, ArrayList<Task>> information, ArrayList<User> usersList) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element users = document.createElement("users");
            document.appendChild(users);

            for (User user : usersList){
                Element user1 = document.createElement("user");
                users.appendChild(user1);
                user1.setAttribute("login", user.getLogin());
                user1.setAttribute("password", user.getPassword());
                user1.setAttribute("isBanned", String.valueOf(user.isBanned()));
                user1.setAttribute("grantAdmin", user.getAdmin());
                ArrayList<Task> list1 = information.get(user.getLogin());
                addElements(user1, list1, document);
            }

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(document), new StreamResult(new FileOutputStream(FILENAME)));
        } catch (ParserConfigurationException | FileNotFoundException | TransformerException e) {
            LOGGER.error("error while writing to the xml file");
        }
    }

    /**
     * method of building dependent elements
     * @param user - client to which tasks will be associated
     * @param list - tasks list
     * @param document - xml document
     */
    private void addElements(Element user, ArrayList<Task> list, Document document) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
        for (Task task : list) {
            Element task1 = document.createElement("task");

            Element title = document.createElement("title");
            user.appendChild(task1);
            task1.appendChild(title);
            title.setAttribute("value", "[" +  task.getTitle() + "]");

            if (task.isRepeated()) {
                Element time = document.createElement("time");
                user.appendChild(task1);
                task1.appendChild(time);
                if (task.getTime() != null)
                    time.setAttribute("value", null);
                else
                    time.setAttribute("value", "");

                Element start = document.createElement("start");
                user.appendChild(task1);
                task1.appendChild(start);
                if (task.getStartTime() != null)
                    start.setAttribute("value", dateFormat.format(task.getStartTime()));
                else
                    start.setAttribute("value", "");

                Element end = document.createElement("end");
                user.appendChild(task1);
                task1.appendChild(end);
                if (task.getEndTime() != null)
                    end.setAttribute("value", dateFormat.format(task.getEndTime()));
                else
                    end.setAttribute("value", "");
            } else {
                Element time = document.createElement("time");
                user.appendChild(task1);
                task1.appendChild(time);
                if (task.getTime() != null)
                    time.setAttribute("value", dateFormat.format(task.getTime()));
                else
                    time.setAttribute("value", "");

                Element start = document.createElement("start");
                user.appendChild(task1);
                task1.appendChild(start);
                if (task.getStartTime() != null)
                    start.setAttribute("value", null);
                else
                    start.setAttribute("value", "");

                Element end = document.createElement("end");
                user.appendChild(task1);
                task1.appendChild(end);
                if (task.getEndTime() != null)
                    end.setAttribute("value", null);
                else
                    end.setAttribute("value", "");
            }

            Element interval = document.createElement("interval");
            user.appendChild(task1);
            task1.appendChild(interval);
            interval.setAttribute("value", String.valueOf(task.getRepeatInterval()));

            Element active = document.createElement("active");
            user.appendChild(task1);
            task1.appendChild(active);
            active.setAttribute("value", String.valueOf(task.isActive()));
        }
    }
}
