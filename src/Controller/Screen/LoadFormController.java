/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Screen;

import Controller.Dialog.AlertController;
import Controller.Dialog.ConfirmationController;
import Controller.Dialog.TextInputController;
import Model.Core.Field;
import Model.Core.Response;
import Model.Service.FieldService;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FillTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author user
 */
public class LoadFormController implements Initializable {

	private static File currentFile;

	private static int lengthOriginal;

	private static Response storedResponse;

	private static List<Control> fieldAnswers;

	private static boolean isEdit;

	@FXML
	private Button backButton;

	@FXML
	private Button answerButton;

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

	private void loadFields(File file) {
		try {
			currentFile = file;

			fieldAnswers = new ArrayList<>();

			storedResponse = new Response(FieldService.readFieldsFromFile(currentFile, true));
			lengthOriginal = FieldService.getLengthOriginal(currentFile);

			titleText.setText(Paths.get(currentFile.getAbsolutePath()).getFileName().toString().split("\\.")[0]);

			isEdit = false;
		} catch (FileNotFoundException ex) {
			AlertController.showAlert("Error", "Cannot open that file",
					"We cannot open that file. It might be of the wrong format.", Alert.AlertType.ERROR);
		}
	}

	private void drawFields() {
		// Clear previous controls
		fieldAnswers.clear();

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
				fieldAnswers.add(options);
			} else {
				TextField value = new TextField();

				GridPane.setConstraints(value, 1, row);

				fieldsGrid.getChildren().add(value);
				fieldAnswers.add(value);
			}

			// If the length of the original has been exceeded, all fields are now custom
			// from now on
			// so add a delete button
			final int finalRow = row;

			if (row >= lengthOriginal && isEdit) {
				// Add its delete button
				Button deleteButton = new Button("Delete");

				deleteButton.setOnAction(e -> deleteAction(finalRow));

				GridPane.setConstraints(deleteButton, 2, finalRow);

				fieldsGrid.getChildren().add(deleteButton);
			}

			row++;
		}
	}

	public void setParameters(File file) {
		loadFields(file);
		drawFields();
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@FXML
	public void backAction() {
		StageController.activate("main");
	}

	@FXML
	public void answerAction() {
		// Clear menu bar
		menuBar.getChildren().clear();

		// Add clear and done buttons as well as the title in the menu bar
		Button clearButton = new Button("Clear");

		clearButton.setPrefHeight(43.0);
		clearButton.setPrefWidth(100.0);
		clearButton.setOnAction(e -> clearAction());

		Button endButton = new Button("End answering");

		endButton.setPrefHeight(43.0);
		endButton.setPrefWidth(120.0);
		endButton.setOnAction(e -> endAction());

		menuBar.getChildren().addAll(clearButton, titleText, endButton);

		// Clear edit bar
		editBar.getChildren().clear();

		// Add submit response to the edit bar
		Button submitButton = new Button("Submit your reponse");

		submitButton.setOnAction(e -> submitAction());

		editBar.getChildren().add(submitButton);

		// Change color scheme
		new FillTransition(Duration.millis(250), menuRectangle, (Color) menuRectangle.getFill(),
				(Color) Paint.valueOf(Main.ANSWER_THEME)).play();

		new FillTransition(Duration.millis(250), editRectangle, (Color) editRectangle.getFill(),
				(Color) Paint.valueOf(Main.ANSWER_THEME)).play();

		// Redraw fields
		drawFields();
	}

	@FXML
	public void editAction() {
		// Clear menu bar
		menuBar.getChildren().clear();

		// Add save and done buttons as well as the title in the menu bar
		Button saveButton = new Button("Save");

		saveButton.setPrefHeight(43.0);
		saveButton.setPrefWidth(100.0);
		saveButton.setOnAction(e -> saveAction());

		Button doneButton = new Button("Done editing, save");

		doneButton.setPrefHeight(43.0);
		doneButton.setPrefWidth(150.0);
		doneButton.setOnAction(e -> doneAction());

		menuBar.getChildren().addAll(titleText, saveButton, doneButton);

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
		try {
			// Save the changes to the current file
			FieldService.writeFieldsToFile(currentFile.getAbsolutePath(), storedResponse.getFields(), lengthOriginal);

			// TODO [1]: Perform the following:
			// 1) Update the fields of the excel (.xlsx) file corresponding to the current
			// file,
			// 2) If the additional output file doesn't exist yet, create an additional
			// excel (.xlsx) file containing the original
			// preset fields + the custom fields added (if the current file was made from a
			// preset form)
			// If it already exists, update that file's fields

			// Show success dialog
			AlertController.showAlert("Information", "Form successfully saved", "The form was successfully saved.",
					Alert.AlertType.INFORMATION);
		} catch (FileNotFoundException ex) {
			AlertController.showAlert("Error", "Form save failed",
					"The form was not saved. Make sure the file isn't open in another program.", Alert.AlertType.ERROR);
		}

		// Reload current file
		loadFields(currentFile);
	}

	@FXML
	public void doneAction() {
		if (ConfirmationController.showConfirmation("Are you sure?", "You need to save any changes first",
				"Do you want to save your changes?")) {
			// Save changes
			saveAction();

			// Clear menu bar
			menuBar.getChildren().clear();

			// Restore original menu bar
			menuBar.getChildren().addAll(backButton, titleText, answerButton);

			// Clear edit bar
			editBar.getChildren().clear();

			// Restore original edit bar
			editBar.getChildren().addAll(editButton);

			// Change color scheme
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

	private void clearAction() {
		drawFields();
	}

	private void submitAction() {
		// Retrieve all answers
		int answerIndex = 0;

		for (Control control : fieldAnswers) {
			String answer;

			if (control instanceof TextField) {
				answer = ((TextField) control).getText();
			} else if (control instanceof ChoiceBox) {
				answer = (String) ((ChoiceBox) control).getSelectionModel().getSelectedItem();
			} else {
				answer = "";
			}

			if (answer == null || answer.isEmpty()) {
				AlertController.showAlert("Error", "Some fields are empty", "All fields must be filled out.",
						Alert.AlertType.ERROR);

				return;
			}

			// Add to answers
			storedResponse.getFields().get(answerIndex).setAnswer(answer);

			answerIndex++;
		}

		// TODO [2]:
		// Save response into the associated excel (.xlsx) file (or files, if preset
		// form was used) of the current file.

		// Show save success message
		AlertController.showAlert("Success", "Response submitted",
				"Your response has been successfully submitted. Thank you!", Alert.AlertType.INFORMATION);

		// Clear the current response
		clearAnswers();

		// Redraw fields
		drawFields();
	}

	private void endAction() {
		// Clear menu bar
		menuBar.getChildren().clear();

		// Restore original menu bar
		menuBar.getChildren().addAll(backButton, titleText, answerButton);

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

	private void deleteAction(int row) {
		// Preset fields can't be deleted!
		// Only allow deletion of non-preset fields
		if (row >= lengthOriginal) {
			storedResponse.getFields().remove(row);
		}

		// Redraw the grid
		drawFields();
	}

	private void trimChoices(String[] choices) {
		for (int choiceIndex = 0; choiceIndex < choices.length; choiceIndex++) {
			choices[choiceIndex] = choices[choiceIndex].trim();
		}
	}

	private void clearAnswers() {
		for (Field field : storedResponse.getFields()) {
			field.setAnswer("");
		}
	}
}
