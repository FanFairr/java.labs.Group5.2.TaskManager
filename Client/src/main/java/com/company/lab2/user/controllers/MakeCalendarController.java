
package com.company.lab2.user.controllers;

import com.company.lab2.user.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Date;

/**
 * Class controller for MakeCalendar.fxml view.user
 */

public class MakeCalendarController {
    @FXML
    private DatePicker To;
    @FXML
    private TextField toH;
    @FXML
    private TextField toM;
    @FXML
    private TextField toS;

    @FXML
    private DatePicker From;
    @FXML
    private TextField fromH;
    @FXML
    private TextField fromM;
    @FXML
    private TextField fromS;

    @FXML
    private Button CloseBtn;
    @FXML
    private Button SearchBtn;

    /**start date of calendar selected py user*/
    private static Date start;
    /**end date of calendar selected py user*/
    private static Date end;
    /**makeCalendar.fxml stage*/
    private static Stage stage;

    @FXML
    void initialize() {
        stage = WindowMaker.getStage();
        ValidateController.toolTipForDateAndTime(fromH, fromM, fromS, From);
        ValidateController.toolTipForDateAndTime(toH, toM, toS, To);
        ValidateController.hoursMinSecondsValidate(fromH, fromM, fromS);
        ValidateController.hoursMinSecondsValidate(toH, toM, toS);
        SearchBtn.setOnAction(event -> {
            String title = "Warning";
            String header = "Action mistake";
            String text = "";
            boolean alertMade = true;
            Long fromTime = Convert.makeLongFromTimeFields(fromH, fromM, fromS);
            Long toTime = Convert.makeLongFromTimeFields(toH, toM, toS);
            if (From.getValue() == null || To.getValue() == null) {
                text = "Date field should be filled";
            } else if (fromTime == null || toTime == null) {
                text = "At least one of time fields in one row should be filled";
            } else {
                start = Convert.makeDate(From, fromTime);
                end = Convert.makeDate(To, toTime);
                if (end.getTime() < start.getTime()) {
                    text = "The end time of the task must be greater than the start time";
                } else {
                    alertMade = false;
                    if (Controller.calendar(start, end) == null) {
                        title = "Calendar not found";
                        header = "Sorry :-(";
                        text = "There aren't any Tasks is such period of time";
                        WindowMaker.alertWindowInf(title, header, text);
                    } else {
                        final String path = "/view/user/Calendar.fxml";
                        header = "Calendar";
                        WindowMaker.makeWindow(path, header);
                    }
                }
            }
            if (alertMade) WindowMaker.alertWindowWarning(title, header, text);
        });
        CloseBtn.setOnAction(event -> WindowMaker.closeWindow(stage));
    }

    /**method for getting start date
     * @return start*/
    static Date getStart() {
        return start;
    }

    /**method for getting end date
     * @return end*/
    static Date getEnd() {
        return end;
    }
}
