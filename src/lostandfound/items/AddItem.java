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
import java.sql.SQLException;

public class AddItem extends Application {

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

        // Create TextFields
        TextField itemName = new TextField();
        itemName.setLayoutX(114);
        itemName.setLayoutY(167);
        itemName.setPrefHeight(27);
        itemName.setPrefWidth(164);

        TextField location = new TextField();
        location.setLayoutX(425);
        location.setLayoutY(243);
        location.setPrefHeight(27);
        location.setPrefWidth(164);

        TextField description = new TextField();
        description.setLayoutX(114);
        description.setLayoutY(243);
        description.setPrefHeight(27);
        description.setPrefWidth(164);

        // Create Buttons
        Button addItemButton = new Button("Add Item");
        addItemButton.setLayoutX(268);
        addItemButton.setLayoutY(293);
        addItemButton.setPrefHeight(49);
        addItemButton.setPrefWidth(129);

        Button backButton = new Button("Back");
        backButton.setLayoutX(28);
        backButton.setLayoutY(20);

        // Create ComboBox for Status
        ComboBox<String> status = new ComboBox<>();
        status.setLayoutX(431);
        status.setLayoutY(167);
        status.setPrefHeight(27);
        status.setPrefWidth(87);

        // Add items to ComboBox
        status.getItems().addAll("Lost", "Found", "Returned");

        // Add all elements to the root AnchorPane
        root.getChildren().addAll(label1, label11, label, label2, label21, label22, label221,
                itemName, location, description, addItemButton, backButton, status);

        // Create Scene and set it to the stage
        Scene scene = new Scene(root);
        stage.setTitle("Campus Lost and Found");
        stage.setScene(scene);
        stage.show();

        // Add functionality to "Back" button
        backButton.setOnAction(e -> {
            // Close the current window
            stage.close();
        });

        // Add functionality to "Add Item" button (insert data into DB)
        addItemButton.setOnAction(e -> {
            // Get the data from the form
            String itemNameText = itemName.getText();
            String descriptionText = description.getText();
            String statusText = status.getValue();
            String locationText = location.getText();

            // Insert the item data into the database
            boolean success = insertItemData(itemNameText, descriptionText, statusText, locationText);

            // Show appropriate alert based on success or failure
            if (success) {
                showAlert("Success", "Item added successfully!", AlertType.INFORMATION);
                // Close the current window
                stage.close();
            } else {
                showAlert("Error", "Failed to add item to the database.", AlertType.ERROR);
            }
        });
    }

    private boolean insertItemData(String itemName, String description, String status, String location) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/lostandfounddb"; // Change to your DB name
        String user = "root"; // Change to your DB username
        String password = ""; // Change to your DB password

        String sql = "INSERT INTO Item (item_name, description, status, location) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for the SQL query
            stmt.setString(1, itemName);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setString(4, location);

            // Execute the SQL statement
            int rowsInserted = stmt.executeUpdate();

            return rowsInserted > 0; // Return true if the item was inserted successfully
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
