/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Model.Core.Field;
import Model.Service.QuestionsService;
import Model.Service.ResponsesService;
import Controller.Dialog.AlertController;
import Controller.Dialog.ConfirmationController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

    // The owner stage of this window
    private Stage stage;

    // The questions of this form
    private List<Field> fields;

    // The number of official (non-custom) fields in the form
    private int lengthOriginal;

    // Custom (user-made) flag
    private boolean isCustom;

    // Used template (presets) flag
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

    @FXML
    private CheckBox useExistingCheckBox;

    public void setParameters(
            Stage stage,
            List<Field> fields,
            int lengthOriginal,
            boolean isTemplate) {
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

        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter(
                        "Excel file (*.xlsx)", "*.xlsx"
                );

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

        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter(
                        "Form questions (*.form)",
                        "*.form"
                );

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

        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter(
                        "Excel file (*.xlsx)",
                        "*.xlsx"
                );

        fileChooser.getExtensionFilters().add(extFilter);

        File file = useExistingCheckBox.isSelected()
                ? fileChooser.showOpenDialog(stage)
                : fileChooser.showSaveDialog(stage);

        // Change label to the selected file
        if (file != null) {
            responsesLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void saveAction() {
        try {
            if (!questionsLabel.getText().equals("No location chosen")
                    && !responsesLabel.getText().equals("No location chosen")
                    && (!officialLabel.getText().equals("No location chosen")
                    && officialLabel.isVisible()
                    || !officialLabel.isVisible())) {
                if (responsesLabel.getText().equals(officialLabel.getText())) {
                    AlertController.showAlert(
                            "Error",
                            "Duplicate save location",
                            "The responses file and its official copy cannot"
                            + " have the same name at the same"
                            + " location.",
                            Alert.AlertType.ERROR
                    );
                } else if (useExistingCheckBox.isSelected()
                        && !isHeadersEqual(
                                ResponsesService.getHeaders(
                                        new File(responsesLabel.getText())
                                ),
                                fields)) {
                    AlertController.showAlert(
                            "Error",
                            "Fields mismatch",
                            "The questions of the response file you have chosen"
                            + " to use do not match your form.",
                            Alert.AlertType.ERROR
                    );
                } else {
                    try {
                        // Save a questions (.form) file containing the
                        // format of the questions
                        QuestionsService.writeFieldsToFile(
                                questionsLabel.getText(),
                                fields,
                                responsesLabel.getText(),
                                isCustom && isTemplate
                                        ? officialLabel.getText()
                                        : null,
                                lengthOriginal,
                                isTemplate
                        );

                        // Only create new forms when the user says so
                        if (!useExistingCheckBox.isSelected()) {
                            // TODO [3, 4, 5]: Create an excel (.xlsx) file
                            // containing all preset fields + custom fields
                            ResponsesService.createForm(
                                    new File(questionsLabel.getText()),
                                    responsesLabel.getText(),
                                    true
                            );

                            // 2) If a custom form was made, create an excel
                            // file containing only the official fields
                            if (isCustom) {
                                ResponsesService.createForm(
                                        new File(questionsLabel.getText()),
                                        officialLabel.getText(),
                                        false
                                );
                            }
                        }

                        // Show success dialog
                        if (ConfirmationController.showConfirmation(
                                "Information",
                                "Form successfully saved",
                                "The form was successfully saved. Would you"
                                + " like to answer it now?")) {
                            new MainInterfaceController().loadForm(
                                    new File(questionsLabel.getText())
                            );
                        }

                        // Close this stage
                        stage.close();
                    } catch (IOException ex) {
                        // Roll all changes back in case of failure to ensure
                        // transaction atomicity
                        File questionsFile = new File(questionsLabel.getText());
                        File responsesFile = new File(responsesLabel.getText());
                        File officialFile = new File(officialLabel.getText());

                        questionsFile.delete();
                        responsesFile.delete();
                        officialFile.delete();

                        AlertController.showAlert(
                                "Error",
                                "Form save failed",
                                "The form was not saved. Make sure the file"
                                + " isn't open in another program.",
                                Alert.AlertType.ERROR
                        );
                    }
                }
            } else {
                AlertController.showAlert(
                        "Error",
                        "Invalid save configuration",
                        "You may have forgotten to choose where to save some"
                        + " files.",
                        Alert.AlertType.ERROR
                );
            }
        } catch (IOException ex) {
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
    public void toggleExisting() {
        responsesLabel.setText("No location chosen");
    }

    public static boolean isHeadersEqual(
            List<String> headersFromResponsesFile,
            List<Field> headersFromQuestionsFile) {
        if (headersFromResponsesFile.size() != headersFromQuestionsFile.size()) {
            return false;
        } else {
            for (int column = 0;
                    column < headersFromResponsesFile.size();
                    column++) {
                if (!headersFromResponsesFile.get(column).equals(
                        headersFromQuestionsFile.get(column).getLabel()
                )) {
                    return false;
                }
            }

            return true;
        }
    }
}
