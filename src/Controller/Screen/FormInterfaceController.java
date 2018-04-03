/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Controller.Dialog.AlertController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FormInterfaceController implements Initializable {

    @FXML
    private Button templateButton;

    @FXML
    private Button startButton;

    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void startButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/View/Interface/NewFormInterface.fxml")
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
            NewFormController newFormController = loader.getController();

            // Set the parameters of the save dialog
            newFormController.setParameters();

            StageController.addScreen("new", scene);
            StageController.activate("new");
        } catch (IOException ex) {
            AlertController.showAlert("Error",
                    "Could not load resources",
                    "The application could not load the required"
                    + " internal resources.",
                    Alert.AlertType.ERROR, ex
            );
        }
    }

    @FXML
    private void templateButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader
                    = new FXMLLoader(
                            getClass().getResource(
                                    "/View/Interface/TemplateInterface.fxml"
                            )
                    );

            StackPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add(
                    "/View/Interface/material-fx-v0_3.css"
            );

            scene.getStylesheets().add(
                    "/View/Interface/materialfx-toggleswitch.css"
            );

            StageController.addScreen("template", scene);
            StageController.activate("template");
        } catch (IOException ex) {
            AlertController.showAlert("Error",
                    "Could not load resources",
                    "The application could not load the required"
                    + " internal resources.",
                    Alert.AlertType.ERROR, ex
            );
        }
    }

    @FXML
    private void backButtonAction() {
        StageController.activate("main");
    }
}
