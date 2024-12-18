package lostandfound.items;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static javafx.application.Application.launch;

public class EditItem extends Application {

    // Declare the form fields as instance variables
    private TextField itemNameField;
    private TextField descriptionField;
    private ComboBox<String> statusComboBox;
    private TextField locationField;
    private String itemId; // Store the item ID for editing

    // Add a constructor that accepts item details and item ID
    public EditItem(String itemId, String itemName, String description, String status, String location) {
        this.itemId = itemId; // Set the item ID
        // Initialize the TextField and ComboBox with passed data
        this.itemNameField = new TextField(itemName);
        this.descriptionField = new TextField(description);
        this.statusComboBox = new ComboBox<>();
        this.statusComboBox.getItems().addAll("Lost", "Found", "Returned");
        this.statusComboBox.setValue(status);
        this.locationField = new TextField(location);
    }

    @Override
    public void start(Stage stage) {
        // Create the AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(666, 376);

        // Create Labels
        Label label1 = new Label("Campus Lost And Found");
        label1.setLayoutX(233);
        label1.setLayoutY(22);
        label1.setFont(new Font(18));

        Label label11 = new Label("Management System");
        label11.setLayoutX(254);
        label11.setLayoutY(48);
        label11.setFont(new Font(18));

        Label label = new Label("Item Details");
        label.setLayoutX(296);
        label.setLayoutY(88);
        label.setFont(new Font(16));

        Label label2 = new Label("Item Name:");
        label2.setLayoutX(114);
        label2.setLayoutY(127);
        label2.setFont(new Font(16));

        Label label21 = new Label("Status:");
        label21.setLayoutX(425);
        label21.setLayoutY(127);
        label21.setFont(new Font(16));

        Label label22 = new Label("Description:");
        label22.setLayoutX(114);
        label22.setLayoutY(210);
        label22.setFont(new Font(16));

        Label label221 = new Label("Location:");
        label221.setLayoutX(430);
        label221.setLayoutY(210);
        label221.setFont(new Font(16));

        // Create TextFields and ComboBox
        itemNameField.setLayoutX(114);
        itemNameField.setLayoutY(167);
        itemNameField.setPrefHeight(27);
        itemNameField.setPrefWidth(164);

        descriptionField.setLayoutX(114);
        descriptionField.setLayoutY(243);
        descriptionField.setPrefHeight(27);
        descriptionField.setPrefWidth(164);

        locationField.setLayoutX(425);
        locationField.setLayoutY(243);
        locationField.setPrefHeight(27);
        locationField.setPrefWidth(164);

        // Create ComboBox and set its value
        statusComboBox.setLayoutX(431);
        statusComboBox.setLayoutY(167);
        statusComboBox.setPrefHeight(27);
        statusComboBox.setPrefWidth(87);

        // Create Buttons
        Button saveButton = new Button("Save Changes");
        saveButton.setLayoutX(268);
        saveButton.setLayoutY(293);
        saveButton.setPrefHeight(49);
        saveButton.setPrefWidth(129);

        Button backButton = new Button("Cancel");
        backButton.setLayoutX(28);
        backButton.setLayoutY(20);

        // Add all elements to the root AnchorPane
        root.getChildren().addAll(label1, label11, label, label2, label21, label22, label221,
                itemNameField, locationField, descriptionField, saveButton, backButton, statusComboBox);

        // Create Scene and set it to the stage
        Scene scene = new Scene(root);
        stage.setTitle("Campus Lost and Found");
        stage.setScene(scene);
        stage.show();

        // Add functionality to "Back" button
        backButton.setOnAction(e -> {
            stage.close(); // Close the current window
        });

        // Add functionality to "Save Changes" button (update data in DB)
        saveButton.setOnAction(e -> {
            // Get the updated data from the form
            String itemNameText = itemNameField.getText();
            String descriptionText = descriptionField.getText();
            String statusText = statusComboBox.getValue();
            String locationText = locationField.getText();

            // Update the item data in the database
            boolean success = updateItemData(itemId, itemNameText, descriptionText, statusText, locationText);

            // Show appropriate alert based on success or failure
            if (success) {
                showAlert("Success", "Item updated successfully!", AlertType.INFORMATION);
                stage.close(); // Close the current window
            } else {
                showAlert("Error", "Failed to update item in the database.", AlertType.ERROR);
            }
        });
    }

    private boolean updateItemData(String itemId, String itemName, String description, String status, String location) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/lostandfounddb"; // Change to your DB name
        String user = "root"; // Change to your DB username
        String password = ""; // Change to your DB password

        String sql = "UPDATE Item SET item_name = ?, description = ?, status = ?, location = ? WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for the SQL query
            stmt.setString(1, itemName);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setString(4, location);
            stmt.setString(5, itemId); // Set the item ID for updating

            // Execute the SQL statement
            int rowsUpdated = stmt.executeUpdate();

            return rowsUpdated > 0; // Return true if the item was updated successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurred
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
