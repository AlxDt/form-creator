/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import static Controller.Screen.Main.TEMPORARY_FILE_INDICATOR;
import Model.Core.Field;
import Model.Service.QuestionsService;
import Model.Service.ResponsesService;
import Controller.Dialog.AlertController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
public class UpdateInterfaceController implements Initializable {

    // The owner stage of this window
    private Stage stage;

    // The currently loaded questions file
    private File currentFile;

    // The questions of this form
    private List<Field> fields;

    // The number of official (non-custom) fields in the form
    private int lengthOriginal;

    // Custom (user-made) flag
    private boolean isCustom;

    // Used template (presets) flag
    private boolean isTemplate;

    // Official copy existence flag
    private boolean isOfficialExists;

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

    public void setParameters(
            Stage stage,
            File currentFile,
            List<Field> fields,
            int lengthOriginal,
            boolean isTemplate) throws FileNotFoundException {
        this.stage = stage;

        this.currentFile = currentFile;
        this.fields = fields;
        this.lengthOriginal = lengthOriginal;

        this.isCustom = fields.size() > lengthOriginal;
        this.isTemplate = isTemplate;

        try {
            String officialFilename;

            officialFilename
                    = QuestionsService.getOutputFilenames(currentFile)[1];

            officialLabel.setText(officialFilename);

            this.isOfficialExists = new File(officialFilename).exists();
        } catch (ArrayIndexOutOfBoundsException ex) {
            this.isOfficialExists = false;
        }

        // If an official copy of a customized preset form hasn't been made
        // yet, show the option to create it
        if (isCustom && isTemplate && !isOfficialExists) {
            officialText.setVisible(true);
            officialLabel.setVisible(true);

            officialButton.setVisible(true);
            officialButton.setDisable(false);
        }

        questionsLabel.setText(currentFile.getAbsolutePath());

        responsesLabel.setText(
                QuestionsService.getOutputFilenames(currentFile)[0]
        );
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
                        "Excel file (*.xlsx)",
                        "*.xlsx"
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

        File file = fileChooser.showSaveDialog(stage);

        // Change label to the selected file
        if (file != null) {
            responsesLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void saveAction() {
        if (!officialLabel.getText().equals("No location chosen")
                && officialLabel.isVisible()
                || !officialLabel.isVisible()) {
            if (responsesLabel.getText().equals(officialLabel.getText())) {
                AlertController.showAlert(
                        "Error",
                        "Duplicate save location",
                        "The responses file and its official copy cannot have"
                        + " the same name at the same location.",
                        Alert.AlertType.ERROR
                );
            } else {
                File questionsFile;
                File responsesFile;
                File officialFile = null;

                File questionsTempFile = null;
                File responsesTempFile = null;
                File officialTempFile = null;

                try {
                    try {
                        // Create a temporary copy of the questions file,
                        // responses file, and the the official file
                        // (if available)
                        questionsFile = new File(questionsLabel.getText());
                        responsesFile = new File(responsesLabel.getText());

                        questionsTempFile
                                = new File(
                                        questionsLabel.getText()
                                        + TEMPORARY_FILE_INDICATOR
                                );

                        responsesTempFile
                                = new File(
                                        responsesLabel.getText()
                                        + TEMPORARY_FILE_INDICATOR
                                );

                        Files.copy(
                                questionsFile.toPath(),
                                questionsTempFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                        );

                        Files.copy(
                                responsesFile.toPath(),
                                responsesTempFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                        );

                        if (isOfficialExists) {
                            officialFile = new File(officialLabel.getText());

                            officialTempFile
                                    = new File(
                                            officialLabel.getText()
                                            + TEMPORARY_FILE_INDICATOR
                                    );

                            Files.copy(
                                    officialFile.toPath(),
                                    officialTempFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING
                            );
                        }

                        try {
                            // Save a questions (.form) file containing the
                            // format of the questions
                            QuestionsService.writeFieldsToFile(
                                    questionsLabel.getText(),
                                    fields,
                                    responsesLabel.getText(),
                                    isCustom
                                    && isTemplate
                                            ? officialLabel.getText()
                                            : null,
                                    lengthOriginal,
                                    isTemplate
                            );

                            // TODO [1]: Perform the following:
                            // 1) Update the fields of the excel (.xlsx) file
                            // corresponding to the current file containing all
                            // preset fields + custom fields,
                            ResponsesService.updateForm(
                                    currentFile,
                                    responsesFile,
                                    true
                            );

                            // 2) If the file was a preset file and it was
                            // modified, and if the official copy doesn't exist
                            // yet, create it and update it with the responses;
                            // but if the official copy already exists, then
                            // nothing needs to be updated at all, as the
                            // official copy only concerns preset fields
                            if (isCustom && isTemplate && !isOfficialExists) {
                                // Make a copy of the responses file and turn it
                                // into the official copy
                                officialFile
                                        = new File(
                                                officialLabel.getText()
                                        );

                                Files.copy(
                                        responsesFile.toPath(),
                                        officialFile.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING
                                );

                                // Then update it with the latest responses
                                ResponsesService.updateForm(
                                        currentFile,
                                        officialFile,
                                        false
                                );
                            }

                            // Show success dialog
                            AlertController.showAlert(
                                    "Information",
                                    "Form successfully saved",
                                    "The form was successfully saved.",
                                    Alert.AlertType.INFORMATION
                            );

                            // Close this stage
                            stage.close();
                        } catch (IOException ex) {
                            // Roll all changes back in case of failure to
                            // ensure transaction atomicity
                            try {
                                Files.copy(
                                        questionsTempFile.toPath(),
                                        questionsFile.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING
                                );
                            } catch (IOException ex2) {
                                // Do nothing
                            }

                            try {
                                Files.copy(
                                        responsesTempFile.toPath(),
                                        responsesFile.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING
                                );
                            } catch (IOException ex2) {
                                // Do nothing
                            }

                            if (isOfficialExists
                                    && officialTempFile != null
                                    && officialFile != null) {
                                try {
                                    Files.copy(
                                            officialTempFile.toPath(),
                                            officialFile.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING
                                    );
                                } catch (IOException ex2) {
                                    // Do nothing
                                }
                            }

                            AlertController.showAlert(
                                    "Error",
                                    "Form save failed",
                                    "The form was not saved. Make sure the file"
                                    + " isn't open in another program.",
                                    Alert.AlertType.ERROR
                            );
                        }
                    } catch (IOException ex) {
                        AlertController.showAlert(
                                "Error",
                                "Form save failed",
                                "The form was not saved. Make sure the file"
                                + " isn't open in another program.",
                                Alert.AlertType.ERROR
                        );
                    }
                } catch (Exception ex) {
                    AlertController.showAlert(
                            "Error",
                            "Internal error",
                            "An internal error occurred.",
                            Alert.AlertType.ERROR,
                            ex
                    );
                } finally {
                    // Whatever happens, always delete temporary files, if 
                    // available
                    if (questionsTempFile != null) {
                        questionsTempFile.delete();
                    }

                    if (responsesTempFile != null) {
                        responsesTempFile.delete();
                    }

                    if (officialTempFile != null) {
                        officialTempFile.delete();
                    }
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
    }
}
