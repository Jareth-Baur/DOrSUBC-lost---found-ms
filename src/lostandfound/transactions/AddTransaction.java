package lostandfound.transactions;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class AddTransaction extends Application {

    private TableView<ObservableList<String>> itemTable;
    private TableView<ObservableList<String>> studentTable;
    private ComboBox<String> transactionTypeComboBox;

    @Override
    public void start(Stage primaryStage) {
        // UI elements with precise layout values from FXML
        itemTable = createItemTable();
        studentTable = createStudentTable();
        transactionTypeComboBox = new ComboBox<>(FXCollections.observableArrayList("Report Lost", "Report Found", "Claimed"));
        transactionTypeComboBox.setPromptText("Select a Transaction Type");

        Button addTransactionButton = new Button("Add Transaction");
        Button cancelButton = new Button("Cancel");
        Label transactionTypeLabel = new Label("Transaction Type");
        transactionTypeLabel.setFont(new Font(17));

        Label studentDetailsLabel = new Label("Student Details");
        studentDetailsLabel.setFont(new Font(20));

        Label studentIDLabel = new Label("Student ID");
        studentIDLabel.setFont(new Font(17));
        studentIDLabel.setStyle("-fx-font-weight: bold;");
        Label studentNameLabel = new Label("Student Name");
        studentNameLabel.setFont(new Font(17));
        studentNameLabel.setStyle("-fx-font-weight: bold;");

        Label itemDetailsLabel = new Label("Item Details");
        itemDetailsLabel.setFont(new Font(20));

        Label itemNameLabel = new Label("Item Name");
        itemNameLabel.setFont(new Font(17));
        itemNameLabel.setStyle("-fx-font-weight: bold;");
        Label locationLabel = new Label("Location");
        locationLabel.setFont(new Font(17));
        locationLabel.setStyle("-fx-font-weight: bold;");

        // Create the AnchorPane for the layout
        AnchorPane root = new AnchorPane();

        // Set item table properties
        itemTable.setPrefHeight(235);
        itemTable.setPrefWidth(734);
        itemTable.setLayoutX(14);
        itemTable.setLayoutY(14);

        // Set student table properties
        studentTable.setPrefHeight(235);
        studentTable.setPrefWidth(381);
        studentTable.setLayoutX(14);
        studentTable.setLayoutY(263);

        // Set transaction type combo box properties
        transactionTypeComboBox.setPrefHeight(28);
        transactionTypeComboBox.setPrefWidth(157);
        transactionTypeComboBox.setLayoutX(556);
        transactionTypeComboBox.setLayoutY(500);

        // Set Add Transaction button properties
        addTransactionButton.setPrefHeight(28);
        addTransactionButton.setPrefWidth(118);
        addTransactionButton.setLayoutX(423);
        addTransactionButton.setLayoutY(544);

        // Set Cancel button properties
        cancelButton.setPrefHeight(28);
        cancelButton.setPrefWidth(118);
        cancelButton.setLayoutX(630);
        cancelButton.setLayoutY(544);

        // Set Transaction Type label properties
        transactionTypeLabel.setLayoutX(408);
        transactionTypeLabel.setLayoutY(500);
        transactionTypeLabel.setPrefHeight(28);
        transactionTypeLabel.setPrefWidth(133);

        // Set Student Details and Student ID/Name labels properties
        studentDetailsLabel.setLayoutX(424);
        studentDetailsLabel.setLayoutY(265); // Move down a bit to create space for label
        studentDetailsLabel.setPrefHeight(36);
        studentDetailsLabel.setPrefWidth(187);

        studentIDLabel.setLayoutX(424);
        studentIDLabel.setLayoutY(295); // Adjusted Y position to move down
        studentIDLabel.setPrefHeight(36);
        studentIDLabel.setPrefWidth(187);

        studentNameLabel.setLayoutX(424);
        studentNameLabel.setLayoutY(331); // Adjusted Y position to move down
        studentNameLabel.setPrefHeight(36);
        studentNameLabel.setPrefWidth(187);

        // Set Item Details and Item Name/Location labels properties
        itemDetailsLabel.setLayoutX(622);
        itemDetailsLabel.setLayoutY(265); // Move down a bit to create space for label
        itemDetailsLabel.setPrefHeight(36);
        itemDetailsLabel.setPrefWidth(202);

        itemNameLabel.setLayoutX(622);
        itemNameLabel.setLayoutY(295); // Adjusted Y position to move down
        itemNameLabel.setPrefHeight(36);
        itemNameLabel.setPrefWidth(202);

        locationLabel.setLayoutX(622);
        locationLabel.setLayoutY(331); // Adjusted Y position to move down
        locationLabel.setPrefHeight(36);
        locationLabel.setPrefWidth(202);

        // Add all components to the root AnchorPane
        root.getChildren().addAll(studentTable, itemTable, transactionTypeComboBox, addTransactionButton, cancelButton, transactionTypeLabel,
                studentDetailsLabel, studentIDLabel, studentNameLabel, itemDetailsLabel, itemNameLabel, locationLabel);

        // Create the scene
        Scene scene = new Scene(root, 765, 587);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Transaction");
        primaryStage.show();

        // Database connection and population
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", "")) {
            populateTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error connecting to the database or retrieving data.");
        }

        // Add listeners for row selection on the student table
        studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String studentID = newValue.get(0); // First column: student ID
                String studentName = newValue.get(1); // Second column: student name
                studentIDLabel.setText(studentID);
                studentNameLabel.setText(studentName);
            }
        });

        // Add listeners for row selection on the item table
        itemTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String itemName = newValue.get(1); // Second column: item name
                String location = newValue.get(4); // Fifth column: location
                itemNameLabel.setText(itemName);
                locationLabel.setText(location);
            }
        });

        addTransactionButton.setOnAction(event -> {
            String transactionType = transactionTypeComboBox.getValue();
            String studentID = studentIDLabel.getText();
            String itemName = itemNameLabel.getText();

            if (transactionType == null || studentID.isEmpty() || itemName.isEmpty()) {
                showAlert("Error", "Please select a transaction type, student, and item.");
                return;
            }

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", "")) {
                // Get item_id based on itemName
                String getItemIDQuery = "SELECT item_id FROM Item WHERE item_name = ?";
                try (PreparedStatement getItemIDStmt = connection.prepareStatement(getItemIDQuery)) {
                    getItemIDStmt.setString(1, itemName);
                    try (ResultSet getItemIDRS = getItemIDStmt.executeQuery()) {
                        if (!getItemIDRS.next()) {
                            showAlert("Error", "Item not found.");
                            return;
                        }
                        int itemID = getItemIDRS.getInt("item_id");

                        // Check if student exists (same as before)
                        String studentCheckQuery = "SELECT COUNT(*) FROM Student WHERE student_idNumber = ?";
                        try (PreparedStatement studentCheckStmt = connection.prepareStatement(studentCheckQuery)) {
                            studentCheckStmt.setString(1, studentID);
                            try (ResultSet studentCheckRS = studentCheckStmt.executeQuery()) {
                                if (studentCheckRS.next() && studentCheckRS.getInt(1) == 0) {
                                    showAlert("Error", "Selected student does not exist.");
                                    return;
                                }
                            }
                        }

                        // Correct INSERT statement
                        String insertTransactionQuery = "INSERT INTO Transaction (item_id, transaction_type, date_of_transaction, student_idNumber) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                            insertTransactionStmt.setInt(1, itemID);
                            insertTransactionStmt.setString(2, transactionType);
                            insertTransactionStmt.setDate(3, Date.valueOf(LocalDate.now())); // Use current date
                            insertTransactionStmt.setString(4, studentID);
                            insertTransactionStmt.executeUpdate();

                            //Update Item status to 'Returned' if transaction type is 'Claimed'
                            if (transactionType.equals("Claimed")) {
                                String updateItemQuery = "UPDATE Item SET status = 'Returned' WHERE item_id = ?";
                                try (PreparedStatement updateItemStmt = connection.prepareStatement(updateItemQuery)) {
                                    updateItemStmt.setInt(1, itemID);
                                    updateItemStmt.executeUpdate();
                                }
                            }

                            showAlert("Success", "Transaction added successfully.");
                            // Clear the labels and selections (same as before)
                            studentIDLabel.setText("Student ID");
                            studentNameLabel.setText("Student Name");
                            itemNameLabel.setText("Item Name");
                            locationLabel.setText("Location");
                            studentTable.getSelectionModel().clearSelection();
                            itemTable.getSelectionModel().clearSelection();
                            primaryStage.close();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to add transaction.");
            }
        });

        // Add functionality to the "Cancel" button
        cancelButton.setOnAction(event -> primaryStage.close());
    }

    private TableView<ObservableList<String>> createItemTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        TableColumn<ObservableList<String>, String> itemIDCol = new TableColumn<>("Item ID");
        itemIDCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        itemIDCol.setPrefWidth(104);
        TableColumn<ObservableList<String>, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));
        itemNameCol.setPrefWidth(149.6);
        TableColumn<ObservableList<String>, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(2)));
        descriptionCol.setPrefWidth(200);
        TableColumn<ObservableList<String>, String> itemLocationCol = new TableColumn<>("Location");
        itemLocationCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(3)));
        itemLocationCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> itemStatusCol = new TableColumn<>("Status");
        itemStatusCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(4)));
        itemStatusCol.setPrefWidth(50);
        table.getColumns().addAll(itemIDCol, itemNameCol, descriptionCol, itemLocationCol, itemStatusCol);
        return table;
    }

    private TableView<ObservableList<String>> createStudentTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        TableColumn<ObservableList<String>, String> studentIDCol = new TableColumn<>("Student ID");
        studentIDCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        studentIDCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> studentNameCol = new TableColumn<>("Student Name");
        studentNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));
        studentNameCol.setPrefWidth(250);
        table.getColumns().addAll(studentIDCol, studentNameCol);
        return table;
    }

    private void populateTables(Connection connection) {
        ObservableList<ObservableList<String>> studentData = FXCollections.observableArrayList();
        ObservableList<ObservableList<String>> itemData = FXCollections.observableArrayList();

        try {
            String studentQuery = "SELECT student_idNumber, student_name FROM Student";
            String itemQuery = "SELECT item_id, item_name, description, location, status FROM Item";

            try (Statement stmt = connection.createStatement()) {
                try (ResultSet studentRS = stmt.executeQuery(studentQuery)) {
                    while (studentRS.next()) {
                        ObservableList<String> row = FXCollections.observableArrayList(studentRS.getString("student_idNumber"), studentRS.getString("student_name"));
                        studentData.add(row);
                    }
                }

                try (ResultSet itemRS = stmt.executeQuery(itemQuery)) {
                    while (itemRS.next()) {
                        ObservableList<String> row = FXCollections.observableArrayList(itemRS.getString("item_id"), itemRS.getString("item_name"), itemRS.getString("description"), itemRS.getString("location"), itemRS.getString("status"));
                        itemData.add(row);
                    }
                }
            }

            studentTable.setItems(studentData);
            itemTable.setItems(itemData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
