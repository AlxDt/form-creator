/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import javafx.application.Application;
import static javafx.application.Application.launch;

import Controller.Dialog.AlertController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class Main extends Application {

    // Where to look for the template forms
    public static final String PRESET_ARW_FIELDS_PATH
            = "defaults/arw_fields.form";
    public static final String PRESET_ATTENDANCE_FIELDS_PATH
            = "defaults/attendance_fields.form";

    // Theme colors for different modes
    public static final String PREVIEW_THEME = "#2196f3";
    public static final String EDIT_THEME = "#f57c00";
    public static final String ANSWER_THEME = "#2e7d32";

    // Temporary file indicator
    public static final String TEMPORARY_FILE_INDICATOR = "tmp_";

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            primaryStage = stage;

            // Load the main FXML
            Parent root
                    = FXMLLoader.load(
                            getClass().getResource(
                                    "/View/Interface/MainInterface.fxml"
                            )
                    );

            // Extract the scene
            Scene scene = new Scene(root);

            // Use the Roboto font
            Font.loadFont(
                    Main.class.getResource(
                            "/View/Interface/Roboto-Medium.ttf")
                            .toExternalForm(),
                    10
            );

            // Style the scene
            scene.getStylesheets().add(
                    "/View/Interface/material-fx-v0_3.css"
            );
            scene.getStylesheets().add(
                    "/View/Interface/materialfx-toggleswitch.css"
            );

            // Initialize the scene map
            StageController.setStage(stage);

            // Set this as the main scene
            StageController.addScreen("main", scene);

            // Window settings
            stage.setTitle("Form Creator");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            AlertController.showAlert("Error",
                    "Could not load resources",
                    "The application could not load the required internal"
                    + " resources.",
                    Alert.AlertType.ERROR, ex
            );
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
