/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Dialog;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * @author user
 */
public class AlertController {

    public static void showAlert(
            String title,
            String header,
            String content,
            Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.getDialogPane().getStylesheets().add(
                "/View/Interface/material-fx-v0_3.css"
        );

        alert.getDialogPane().getStylesheets().add(
                "/View/Interface/materialfx-toggleswitch.css"
        );

        alert.showAndWait();
    }

    public static void showAlert(
            String title,
            String header,
            String content,
            Alert.AlertType alertType,
            Exception exception) {
        Alert alert = new Alert(alertType);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.getDialogPane().getStylesheets().add(
                "/View/Interface/material-fx-v0_3.css"
        );

        alert.getDialogPane().getStylesheets().add(
                "/View/Interface/materialfx-toggleswitch.css"
        );

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        exception.printStackTrace(pw);

        String exceptionText = sw.toString();

        Label label = new Label("Error details:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();

        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable exception into the dialog pane
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
