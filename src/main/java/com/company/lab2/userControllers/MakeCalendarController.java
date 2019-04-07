package com.company.lab2.userControllers;

import com.company.lab2.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Date;

/**
 * Class controller for MakeCalendar.fxml view
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

    private static Date start;
    private static Date end;
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
            Long fromTime = ConvertController.makeLongFromTimeFields(fromH, fromM, fromS);
            Long toTime = ConvertController.makeLongFromTimeFields(toH, toM, toS);
            if (From.getValue() == null || To.getValue() == null) {
                text = "Date field should be filled";
            } else if (fromTime == null || toTime == null) {
                text = "At least one of time fields in one row should be filled";
            } else {
                start = ConvertController.makeDate(From, fromTime);
                end = ConvertController.makeDate(To, toTime);
                if (end.getTime() < start.getTime()) {
                    text = "The end time of the task must be greater than the start time";
                } else {
                    alertMade = false;
                    //TODO
                    if (Controller.calendarTreeViewGetRequest(start, end)) {
                        title = "Calendar not found";
                        header = "Sorry :-(";
                        text = "There aren't any Tasks is such period of time";
                        WindowMaker.alertWindowInf(title, header, text);
                    } else {
                        final String path = "/view/Calendar.fxml";
                        header = "Calendar";
                        WindowMaker.makeWindow(path, header);
                    }
                }
            }
            if (alertMade) WindowMaker.alertWindowWarning(title, header, text);
        });
        CloseBtn.setOnAction(event -> WindowMaker.closeWindow(stage));
    }

    public static Date getStart() {
        return start;
    }

    static Date getEnd() {
        return end;
    }
}