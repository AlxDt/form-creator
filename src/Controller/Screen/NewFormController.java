/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Model.Core.Field;
import Model.Core.Response;
import Controller.Dialog.AlertController;
import Controller.Dialog.ConfirmationController;
import Controller.Dialog.TextInputController;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FillTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author user
 */
public class NewFormController implements Initializable {

    // The responses of the currently loaded questions file
    private static Response storedResponse;

    // Edit mode flag
    private static boolean isEdit;

    @FXML
    private Button backButton;

    @FXML
    private Button okButton;

    @FXML
    private Button editButton;

    @FXML
    private GridPane fieldsGrid;

    @FXML
    private HBox menuBar;

    @FXML
    private Text titleText;

    @FXML
    private HBox editBar;

    @FXML
    private Rectangle menuRectangle;

    @FXML
    private Rectangle editRectangle;

    private void drawFields() {
        // Clear the grid
        fieldsGrid.getChildren().clear();

        // Retrieve the fields from application memory
        Response response = storedResponse;

        // Add each field to the fields grid
        int row = 0;

        for (Field field : response.getFields()) {
            // At the label to the first column
            Text fieldLabel = new Text(field.getLabel());

            GridPane.setConstraints(fieldLabel, 0, row);

            fieldsGrid.getChildren().add(fieldLabel);

            // And if the field is a multi-option one, add a choicebox
            // containing the options
            // If not, put a dummy text field
            if (field.getMultiOption() != null) {
                ChoiceBox options
                        = new ChoiceBox(
                                FXCollections.observableList(
                                        field.getMultiOption()
                                )
                        );

                GridPane.setConstraints(options, 1, row);

                fieldsGrid.getChildren().add(options);
            } else {
                TextField value = new TextField();

                value.setDisable(true);

                GridPane.setConstraints(value, 1, row);

                fieldsGrid.getChildren().add(value);
            }

            // If the length of the original has been exceeded, all fields are
            // now custom from now on so add a delete button
            final int finalRow = row;

            // Add its delete button
            if (isEdit) {
                Button deleteButton = new Button("Delete");

                deleteButton.setOnAction(e -> deleteAction(finalRow));

                GridPane.setConstraints(deleteButton, 2, finalRow);

                fieldsGrid.getChildren().add(deleteButton);
            }

            row++;
        }
    }

    public void setParameters() {
        // Draw the (blank) fields
        drawFields();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        storedResponse = new Response(new ArrayList<>());
    }

    @FXML
    public void backAction() {
        // Prompt the user before leaving
        if (ConfirmationController.showConfirmation(
                "Are you sure?",
                "Unsaved changes will be discarded",
                "Are you sure you want to go back?")) {
            StageController.activate("create");
        }
    }

    @FXML
    public void okAction() {
        saveAction();
    }

    @FXML
    public void editAction() {
        // Clear menu bar
        menuBar.getChildren().clear();

        // Add save and done buttons as well as the title in the menu bar
        Button saveButton = new Button("Save as");

        saveButton.setPrefHeight(43.0);
        saveButton.setPrefWidth(100.0);
        saveButton.setOnAction(e -> saveAction());

        Button doneButton = new Button("Preview");

        doneButton.setPrefHeight(43.0);
        doneButton.setPrefWidth(137.0);
        doneButton.setOnAction(e -> doneAction());

        // Reset the title
        titleText.setText("Edit New Form");

        menuBar.getChildren().addAll(doneButton, titleText, saveButton);

        // Clear edit bar
        editBar.getChildren().clear();

        // Add add question and add multiple choice buttons to the edit bar
        Button addQuestionButton = new Button("Add question");

        addQuestionButton.setOnAction(e -> addQuestionAction());

        Button addMultipleChoiceButton = new Button("Add multiple choice");

        addMultipleChoiceButton.setOnAction(e -> addMultipleChoiceAction());

        editBar.getChildren().addAll(
                addQuestionButton,
                addMultipleChoiceButton
        );

        // Change color scheme
        new FillTransition(
                Duration.millis(250),
                menuRectangle,
                (Color) menuRectangle.getFill(),
                (Color) Paint.valueOf(Main.EDIT_THEME)
        ).play();

        new FillTransition(
                Duration.millis(250),
                editRectangle,
                (Color) editRectangle.getFill(),
                (Color) Paint.valueOf(Main.EDIT_THEME)
        ).play();

        // Turn on edit mode
        isEdit = true;

        // Redraw fields
        drawFields();
    }

    @FXML
    public void saveAction() {
        if (!storedResponse.getFields().isEmpty()) {
            // Load the form FXML
            try {
                FXMLLoader loader
                        = new FXMLLoader(
                                getClass().getResource(
                                        "/View/Interface/SaveInterface.fxml"
                                )
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

                // Extract the save interface controller from the FXML
                SaveInterfaceController saveInterfaceController
                        = loader.getController();

                Stage saveStage = new Stage();

                // Window settings
                saveStage.setTitle("Save changes");
                saveStage.setResizable(false);
                saveStage.setScene(scene);
                saveStage.initModality(Modality.APPLICATION_MODAL);

                // Set the parameters of the save dialog
                saveInterfaceController.setParameters(
                        saveStage,
                        storedResponse.getFields(),
                        0,
                        false
                );

                saveStage.showAndWait();
            } catch (Exception ex) {
                AlertController.showAlert("Error",
                        "Could not load resources",
                        "The application could not load the required"
                        + " internal resources.",
                        Alert.AlertType.ERROR,
                        ex
                );
            }
        } else {
            AlertController.showAlert(
                    "Error",
                    "Empty form",
                    "You cannot save an empty form.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void doneAction() {
        // Reset the title
        titleText.setText("Preview New Form");

        // Clear menu bar
        menuBar.getChildren().clear();

        // Restore original menu bar
        menuBar.getChildren().addAll(backButton, titleText, okButton);

        // Clear edit bar
        editBar.getChildren().clear();

        // Restore original edit bar
        editBar.getChildren().addAll(editButton);

        // Change color scheme
        new FillTransition(
                Duration.millis(250),
                menuRectangle,
                (Color) menuRectangle.getFill(),
                (Color) Paint.valueOf(Main.PREVIEW_THEME)
        ).play();

        new FillTransition(
                Duration.millis(250),
                editRectangle,
                (Color) editRectangle.getFill(),
                (Color) Paint.valueOf(Main.PREVIEW_THEME)
        ).play();

        // Turn off edit mode
        isEdit = false;

        // Redraw fields
        drawFields();
    }

    @FXML
    public void addQuestionAction() {
        Optional<String> result = TextInputController.showTextInput(
                "Add a question",
                "Add a simple question field",
                "Enter your question: "
        );

        // If the user input something, add it to the fields grid
        result.ifPresent(question -> {
            question = question.trim();

            if (question.isEmpty()
                    || question.contains(">")
                    || question.contains(",")) {
                AlertController.showAlert(
                        "Error",
                        "Invalid label",
                        "Make sure your label isn't blank and doesn't contain"
                        + " the characters '>' or ','.",
                        Alert.AlertType.ERROR
                );
            } else if (isLabelExists(question)) {
                AlertController.showAlert(
                        "Error",
                        "Invalid label",
                        "That label already exists.",
                        Alert.AlertType.ERROR
                );
            } else {
                // Add a new field
                storedResponse.getFields().add(new Field(question, null));

                // Redraw the grid
                drawFields();
            }
        });
    }

    @FXML
    public void addMultipleChoiceAction() {
        Optional<String> label
                = TextInputController.showTextInput(
                        "Add a multiple choice field",
                        "Set the label of the multiple choice field",
                        "Enter the label"
                );

        label.ifPresent(titleString -> {
            titleString = titleString.trim();

            if (titleString.isEmpty()
                    || titleString.contains(">")
                    || titleString.contains(",")) {
                AlertController.showAlert(
                        "Error",
                        "Invalid label",
                        "Make sure your label isn't blank and doesn't contain"
                        + " the characters '>' or ','.",
                        Alert.AlertType.ERROR
                );
            } else if (isLabelExists(titleString)) {
                AlertController.showAlert(
                        "Error",
                        "Invalid label",
                        "That label already exists.",
                        Alert.AlertType.ERROR
                );
            } else {
                Optional<String> result
                        = TextInputController.showTextInput(
                                "Add a multiple choice field",
                                "Add a multiple choice field",
                                "Enter your choices, separate with commas: "
                        );

                // If the user input something, add it to the fields grid
                final String finalTitleString = titleString;

                result.ifPresent(choiceString -> {
                    choiceString = choiceString.trim();

                    // Get the choices
                    String[] choices = choiceString.split(",");

                    if (choices.length < 2 || choiceString.contains(">")) {
                        AlertController.showAlert(
                                "Error",
                                "Invalid choices",
                                "Make sure you've entered at least two valid"
                                + " choices.",
                                Alert.AlertType.ERROR
                        );
                    } else {
                        // Trim any leading whitespace
                        trimChoices(choices);

                        // Add a new field
                        storedResponse.getFields().add(
                                new Field(
                                        finalTitleString,
                                        Arrays.asList(choices)
                                )
                        );

                        // Redraw the grid
                        drawFields();
                    }
                });
            }
        });
    }

    private void deleteAction(int row) {
        // Preset fields can't be deleted!
        // Only allow deletion of non-preset fields
        storedResponse.getFields().remove(row);

        // Redraw the grid
        drawFields();
    }

    private void trimChoices(String[] choices) {
        for (int choiceIndex = 0; choiceIndex < choices.length; choiceIndex++) {
            choices[choiceIndex] = choices[choiceIndex].trim();
        }
    }

    private boolean isLabelExists(String label) {
        List<Field> fields = storedResponse.getFields();

        for (Field field : fields) {
            if (field.getLabel().equals(label)) {
                return true;
            }
        }

        return false;
    }
}
