/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Controller.Dialog.AlertController;
import Model.Core.Field;
import Model.Service.FieldService;
import Model.Service.ResponseService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author user
 */
public class SaveInterfaceController implements Initializable {

    private Stage stage;

    private List<Field> fields;

    private int lengthOriginal;

    private boolean isCustom;

    private boolean isTemplate;

    @FXML
    private Rectangle menuRectangle;

    @FXML
    private Text titleText;

    @FXML
    private Rectangle editRectangle;

    @FXML
    private Button saveButton;

    @FXML
    private Label questionsLabel;

    @FXML
    private Button questionsButton;

    @FXML
    private Label responsesLabel;

    @FXML
    private Button responseButton;

    @FXML
    private Text officialText;

    @FXML
    private Label officialLabel;

    @FXML
    private Button officialButton;

    public void setParameters(Stage stage, List<Field> fields, int lengthOriginal, boolean isTemplate) {
        this.stage = stage;

        this.fields = fields;
        this.lengthOriginal = lengthOriginal;

        this.isCustom = fields.size() > lengthOriginal;
        this.isTemplate = isTemplate;

        // If the form in question is custom, enable relevant fields
        if (isCustom && isTemplate) {
            officialText.setVisible(true);
            officialLabel.setVisible(true);

            officialButton.setVisible(true);
            officialButton.setDisable(false);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    public void officialAction() {
        // Prompt user
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save the official responses file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel file (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        // Change label to the selected file
        if (file != null) {
            officialLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void questionAction() {
        // Prompt user
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save the questions file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ARW questions (*.arwq)", "*.arwq");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        // Change label to the selected file
        if (file != null) {
            questionsLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void responseAction() {
        // Prompt user
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save the responses file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel file (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        // Change label to the selected file
        if (file != null) {
            responsesLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void saveAction() {
        if (!questionsLabel.getText().equals("No location chosen")
                && !responsesLabel.getText().equals("No location chosen")
                && (!officialLabel.getText().equals("No location chosen") && officialLabel.isVisible()
                || !officialLabel.isVisible())) {
            if (!responsesLabel.getText().equals(officialLabel.getText())) {
                try {
                    // Save an ARW questions (.arwq) file containing the format of the questions
                    FieldService.writeFieldsToFile(
                            questionsLabel.getText(),
                            fields,
                            responsesLabel.getText(),
                            isCustom && isTemplate ? officialLabel.getText() : null,
                            lengthOriginal,
                            isTemplate
                    );

                    // TODO [3, 4, 5]: Save an excel (.xlsx) file containing:
                    // 1) The original fields,
                    ResponseService.createForm(
                            new File(questionsLabel.getText()),
                            responsesLabel.getText(),
                            true
                    );

                    // 2) The original fields + the custom fields added by the user (if any)
                    if (isCustom) {
                        ResponseService.createForm(
                                new File(questionsLabel.getText()),
                                officialLabel.getText(),
                                false
                        );
                    }

                    // Show success dialog
                    AlertController.showAlert("Information", "Form successfully saved",
                            "The form was successfully saved.", Alert.AlertType.INFORMATION);

                    // Close this stage
                    stage.close();
                } catch (IOException ex) {
                    AlertController.showAlert("Error", "Form save failed",
                            "The form was not saved. Make sure the file isn't open in another program.",
                            Alert.AlertType.ERROR);
                }
            } else {
                AlertController.showAlert("Error", "Duplicate save location",
                        "The responses file and its official copy cannot have the same name at the same location.",
                        Alert.AlertType.ERROR
                );
            }
        } else {
            AlertController.showAlert("Error", "Invalid save configuration",
                    "You may have forgotten to choose where to save some files.",
                    Alert.AlertType.ERROR);
        }
    }
}
