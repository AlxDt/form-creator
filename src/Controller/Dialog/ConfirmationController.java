/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Dialog;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author user
 */
public class ConfirmationController {

    public static boolean showConfirmation(
            String title,
            String header,
            String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.getDialogPane().getStylesheets().add(
                "/View/Interface/material-fx-v0_3.css"
        );

        alert.getDialogPane().getStylesheets().add(
                "/View/Interface/materialfx-toggleswitch.css"
        );

        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }
}
