/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Controller.Dialog.AlertController;
import Controller.Dialog.TextInputController;
import Model.Core.Field;
import Model.Core.Response;
import Model.Service.FieldService;
import Model.Service.ResponseService;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FillTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author user
 */
public class NewFormController implements Initializable {

	private static Response storedResponse;

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

			// And if the field is a multi-option one, add a choicebox containing the
			// options
			// If not, put a dummy text field
			if (field.getMultiOption() != null) {
				ChoiceBox options = new ChoiceBox(FXCollections.observableList(field.getMultiOption()));

				GridPane.setConstraints(options, 1, row);

				fieldsGrid.getChildren().add(options);
			} else {
				TextField value = new TextField();

				GridPane.setConstraints(value, 1, row);

				fieldsGrid.getChildren().add(value);
			}

			// If the length of the original has been exceeded, all fields are now custom
			// from now on
			// so add a delete button
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
		StageController.activate("form");
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
		Button saveButton = new Button("Save As");

		saveButton.setPrefHeight(43.0);
		saveButton.setPrefWidth(100.0);
		saveButton.setOnAction(e -> saveAction());

		Button doneButton = new Button("Preview");

		doneButton.setPrefHeight(43.0);
		doneButton.setPrefWidth(137.0);
		doneButton.setOnAction(e -> doneAction());

		// Reset the title
		titleText.setText("Edit New Form");
		
		menuBar.getChildren().addAll(saveButton, titleText, doneButton);

		// Clear edit bar
		editBar.getChildren().clear();

		// Add add question and add multiple choice buttons to the edit bar
		Button addQuestionButton = new Button("Add question");

		addQuestionButton.setOnAction(e -> addQuestionAction());

		Button addMultipleChoiceButton = new Button("Add multiple choice");

		addMultipleChoiceButton.setOnAction(e -> addMultipleChoiceAction());

		editBar.getChildren().addAll(addQuestionButton, addMultipleChoiceButton);

		// Change color scheme
		new FillTransition(Duration.millis(250), menuRectangle, (Color) menuRectangle.getFill(),
				(Color) Paint.valueOf(Main.EDIT_THEME)).play();

		new FillTransition(Duration.millis(250), editRectangle, (Color) editRectangle.getFill(),
				(Color) Paint.valueOf(Main.EDIT_THEME)).play();

		// Turn on edit mode
		isEdit = true;

		// Redraw fields
		drawFields();
	}

	@FXML
	public void saveAction() {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Choose where to save the questions file");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ARW questions (*.arwq)", "*.arwq");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog(Main.primaryStage);

		if (file != null) {
			fileChooser.setTitle("Choose where to save the responses file");
			fileChooser.getExtensionFilters().clear();
			extFilter = new FileChooser.ExtensionFilter("Excel file (*.xlsx)", "*.xlsx");
			fileChooser.getExtensionFilters().add(extFilter);

			File outputFile = fileChooser.showSaveDialog(Main.primaryStage);

			if (outputFile != null) {
				try {
					// Save an ARW questions (.arwq) file containing the format of the questions
					FieldService.writeFieldsToFile(file.getAbsolutePath(), storedResponse.getFields(), 0);

					// TODO [3]: Save an excel (.xlsx) file containing the custom fields made by the
					// user
					ResponseService.createForm(file, outputFile.getAbsolutePath(), true);

					// Show success dialog
					AlertController.showAlert("Information", "Form successfully saved",
							"The form was successfully saved.", Alert.AlertType.INFORMATION);
				} catch (FileNotFoundException ex) {
					AlertController.showAlert("Error", "Form save failed",
							"The form was not saved. Make sure the file isn't open in another program.",
							Alert.AlertType.ERROR);
				}
			}
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
		new FillTransition(Duration.millis(250), menuRectangle, (Color) menuRectangle.getFill(),
				(Color) Paint.valueOf(Main.NORMAL_THEME)).play();

		new FillTransition(Duration.millis(250), editRectangle, (Color) editRectangle.getFill(),
				(Color) Paint.valueOf(Main.NORMAL_THEME)).play();

		// Turn off edit mode
		isEdit = false;

		// Redraw fields
		drawFields();
	}

	@FXML
	public void addQuestionAction() {
		Optional<String> result = TextInputController.showTextInput("Add a question", "Add a simple question field",
				"Enter your question: ");

		// If the user input something, add it to the fields grid
		result.ifPresent(question -> {
			question = question.trim();

			if (question.isEmpty() || question.contains(">") || question.contains(",")) {
				AlertController.showAlert("Error", "Invalid label", "You did not enter a valid label.",
						Alert.AlertType.ERROR);
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
		Optional<String> label = TextInputController.showTextInput("Add a multiple choice field",
				"Set the label of the multiple choice field", "Enter the label");

		label.ifPresent(titleString -> {
			titleString = titleString.trim();

			if (titleString.isEmpty() || titleString.contains(">") || titleString.contains(",")) {
				AlertController.showAlert("Error", "Invalid label", "You did not enter a valid label.",
						Alert.AlertType.ERROR);
			} else {
				Optional<String> result = TextInputController.showTextInput("Add a multiple choice field",
						"Add a multiple choice field", "Enter your choices, separate with commas: ");

				// If the user input something, add it to the fields grid
				final String finalTitleString = titleString;

				result.ifPresent(choiceString -> {
					choiceString = choiceString.trim();

					// Get the choices
					String[] choices = choiceString.split(",");

					if (choices.length < 2 || choiceString.contains(">")) {
						AlertController.showAlert("Error", "Invalid choices",
								"Make sure you've entered at least two valid choices.", Alert.AlertType.ERROR);
					} else {
						// Trim any leading whitespace
						trimChoices(choices);

						// Add a new field
						storedResponse.getFields().add(new Field(finalTitleString, Arrays.asList(choices)));

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
}
