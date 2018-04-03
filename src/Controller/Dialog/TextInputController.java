/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Dialog;

import java.util.Optional;
import javafx.scene.control.TextInputDialog;

/**
 *
 * @author user
 */
public class TextInputController {

    public static Optional<String> showTextInput(
            String title,
            String header,
            String content) {
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(header);
        textInputDialog.setContentText(content);

        textInputDialog.getDialogPane().getStylesheets().add(
                "/View/Interface/material-fx-v0_3.css"
        );

        textInputDialog.getDialogPane().getStylesheets().add(
                "/View/Interface/materialfx-toggleswitch.css"
        );

        return textInputDialog.showAndWait();
    }
}
