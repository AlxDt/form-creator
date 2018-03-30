/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FormInterfaceController implements Initializable {

    @FXML
    private Button arwButton;

    @FXML
    private Button attendanceButton;

    @FXML
    private Button newFormButton;

    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void arwButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Interface/PresetARWInterface.fxml"));
            BorderPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add("/View/Interface/material-fx-v0_3.css");
            scene.getStylesheets().add("/View/Interface/materialfx-toggleswitch.css");

            // Extract the ARW interface controller from the FXML
            PresetARWController presetARWController = loader.getController();

            // Set the parameters of the save dialog
            presetARWController.setParameters();

            StageController.addScreen("arw", scene);
            StageController.activate("arw");
        } catch (IOException ex) {
        }
    }

    @FXML
    private void attendanceButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Interface/PresetAttendanceInterface.fxml"));
            BorderPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add("/View/Interface/material-fx-v0_3.css");
            scene.getStylesheets().add("/View/Interface/materialfx-toggleswitch.css");

            // Extract the attendance interface controller from the FXML
            PresetAttendanceController presetAttendanceController = loader.getController();

            // Set the parameters of the save dialog
            presetAttendanceController.setParameters();

            StageController.addScreen("attendance", scene);
            StageController.activate("attendance");
        } catch (IOException ex) {
        }
    }

    @FXML
    private void newFormButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Interface/NewFormInterface.fxml"));
            BorderPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add("/View/Interface/material-fx-v0_3.css");
            scene.getStylesheets().add("/View/Interface/materialfx-toggleswitch.css");

            // Extract the ARW interface controller from the FXML
            NewFormController newFormController = loader.getController();

            // Set the parameters of the save dialog
            newFormController.setParameters();

            StageController.addScreen("new", scene);
            StageController.activate("new");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void backButtonAction() {
        StageController.activate("main");
    }

}
