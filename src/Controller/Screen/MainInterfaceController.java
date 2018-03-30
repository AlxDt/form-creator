/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 *
 * @author user
 */
public class MainInterfaceController implements Initializable {

    @FXML
    private Button formButton;

    @FXML
    private Button loadButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void formButtonAction() {
        // Load the form FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Interface/FormInterface.fxml"));
            StackPane window = loader.load();

            Scene scene = new Scene(window);

            // Style the scene
            scene.getStylesheets().add("/View/Interface/material-fx-v0_3.css");
            scene.getStylesheets().add("/View/Interface/materialfx-toggleswitch.css");

            StageController.addScreen("form", scene);
            StageController.activate("form");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void loadButtonAction() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ARW questions (*.arwq)", "*.arwq");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(Main.primaryStage);

        if (file != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Interface/LoadFormInterface.fxml"));
                BorderPane window = loader.load();

                Scene scene = new Scene(window);

                // Style the scene
                scene.getStylesheets().add("/View/Interface/material-fx-v0_3.css");
                scene.getStylesheets().add("/View/Interface/materialfx-toggleswitch.css");

                // Extract the attendance interface controller from the FXML
                LoadFormController loadFormController = loader.getController();

                // Set the parameters of the save dialog
                loadFormController.setParameters(file);

                StageController.addScreen("load", scene);
                StageController.activate("load");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
