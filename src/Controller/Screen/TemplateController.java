/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Controller.Dialog.AlertController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author user
 */
public class TemplateController implements Initializable {

    @FXML
    private Button arwButton;

    @FXML
    private Button attendanceButton;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void arwButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/View/Interface/PresetARWInterface.fxml")
            );

            BorderPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add(
                    "/View/Interface/material-fx-v0_3.css"
            );

            scene.getStylesheets().add(
                    "/View/Interface/materialfx-toggleswitch.css"
            );

            // Extract the ARW interface controller from the FXML
            PresetARWController presetARWController = loader.getController();

            // Set the parameters of the save dialog
            presetARWController.setParameters();

            StageController.addScreen("arw", scene);
            StageController.activate("arw");
        } catch (Exception ex) {
            AlertController.showAlert(
                    "Error",
                    "Could not read the response file",
                    "Could not read the specified response file. Make sure the"
                    + " file isn't open in another program, or that it"
                    + " even exists at all.",
                    Alert.AlertType.ERROR,
                    ex
            );
        }
    }

    @FXML
    public void attendanceButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/View/Interface/PresetAttendanceInterface.fxml")
            );

            BorderPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add(
                    "/View/Interface/material-fx-v0_3.css"
            );

            scene.getStylesheets().add(
                    "/View/Interface/materialfx-toggleswitch.css"
            );

            // Extract the attendance interface controller from the FXML
            PresetAttendanceController presetAttendanceController
                    = loader.getController();

            // Set the parameters of the save dialog
            presetAttendanceController.setParameters();

            StageController.addScreen("attendance", scene);
            StageController.activate("attendance");
        } catch (Exception ex) {
            AlertController.showAlert(
                    "Error",
                    "Could not read the response file",
                    "Could not read the specified response file. Make sure the"
                    + " file isn't open in another program, or that it"
                    + " even exists at all.",
                    Alert.AlertType.ERROR,
                    ex
            );
        }
    }

    @FXML
    public void backButtonAction() {
        StageController.activate("create");
    }
}
