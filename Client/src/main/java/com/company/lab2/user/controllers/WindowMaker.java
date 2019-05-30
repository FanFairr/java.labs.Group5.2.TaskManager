package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static com.company.lab2.user.Controller.logger;

/**
 * Class for making new windows
 */
public class WindowMaker {

    /**last created stage*/
    private static Stage stage;

    /**Method for making new window
     * which is not allowed to use others windows
     * while this one is open
     * @param path .fxml file path
     * @param header header
     */
    public static void makeWindow(String path, String header) {
        try {
            stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(header);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource(path));
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            e.printStackTrace();
        }
    }

    /**Method for making new window
     * with different modality
     * @param path .fxml file path
     * @param header header
     * @param modality window modality
     */
    public static void makeWindow(String path, String header, Modality modality) {
        try {
            stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(header);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource(path));
            stage.setScene(new Scene(loader.load()));
            stage.initModality(modality);
            stage.showAndWait();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            e.printStackTrace();
        }
    }

    /**Method for making new alert warning window
     * which is not allowed to use others windows
     * while this one is open
     * @param title title of window
     * @param header header of window
     * @param contentText content text
     */
    static void alertWindowWarning(String title, String header, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**Method for making new alert information window
     * which is not allowed to use others windows
     * while this one is open
     * @param title title of window
     * @param header header of window
     * @param contentText content text
     */
    public static void alertWindowInf(String title, String header, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**method for closing stage
     * @param stageClose stage to close*/
    public static void closeWindow(Stage stageClose){
        stageClose.close();
    }

    /**method for gettind current stage
     * @return  current stage*/
    public static Stage getStage() {
        return stage;
    }
}
