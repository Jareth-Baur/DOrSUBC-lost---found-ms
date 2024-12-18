package lostandfound.transactions;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class Transaction extends Application {

    private TableView<TransactionData> transactTable;
    private TextField searchField;

    @Override
    public void start(Stage stage) {
        // Create the AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(934, 430);

        // Create the label at the top
        Label label1 = new Label("Campus Lost And Found");
        label1.setLayoutX(380);
        label1.setLayoutY(28);
        label1.setFont(new Font(18));

        Label label2 = new Label("Management System");
        label2.setLayoutX(394);
        label2.setLayoutY(54);
        label2.setFont(new Font(18));

        // Create the Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(40);
        backButton.setLayoutY(377);
        backButton.setPrefHeight(26);
        backButton.setPrefWidth(44);
        backButton.setOnAction((ActionEvent e) -> {
            Stage stage1 = (Stage) backButton.getScene().getWindow(); // Get the current window (Stage)
            stage1.close(); // Close the window
            System.out.println("Back button clicked.");
        });

        // Create the Transactions label
        Label label3 = new Label("Transactions");
        label3.setLayoutX(401);
        label3.setLayoutY(95);
        label3.setFont(new Font(18));

        // Create the TableView for displaying transactions
        transactTable = new TableView<>();
        transactTable.setLayoutX(51);
        transactTable.setLayoutY(122);
        transactTable.setPrefSize(831, 235);

        // Create columns for the TableView
        TableColumn<TransactionData, String> transactIDColumn = new TableColumn<>("Transaction ID");
        transactIDColumn.setPrefWidth(121.6);
        transactIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactID()));

        TableColumn<TransactionData, String> itemNameColumn = new TableColumn<>("Item ID");
        itemNameColumn.setPrefWidth(157.6);
        itemNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemID()));

        TableColumn<TransactionData, String> transactTypeColumn = new TableColumn<>("Transaction Type");
        transactTypeColumn.setPrefWidth(184.8);
        transactTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactType()));

        TableColumn<TransactionData, String> transactDateColumn = new TableColumn<>("Date of Transact");
        transactDateColumn.setPrefWidth(183.2);
        transactDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactDate()));

        TableColumn<TransactionData, String> foundByColumn = new TableColumn<>("Student ID");
        foundByColumn.setPrefWidth(184.0);
        foundByColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFoundBy()));

        // Add columns to the TableView
        transactTable.getColumns().addAll(transactIDColumn, itemNameColumn, transactTypeColumn, transactDateColumn, foundByColumn);

        // Create the search field
        searchField = new TextField();
        searchField.setLayoutX(51);
        searchField.setLayoutY(83);
        searchField.setPrefSize(141, 26);

        // Create the Search button
        Button searchButton = new Button("Search");
        searchButton.setLayoutX(202);
        searchButton.setLayoutY(84);
        searchButton.setOnAction(e -> searchTransactions());

        // Create the View Details button
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setLayoutX(797);
        viewDetailsButton.setLayoutY(82);
        viewDetailsButton.setPrefHeight(26);
        viewDetailsButton.setPrefWidth(85);
        viewDetailsButton.setOnAction(e -> viewTransactionDetails());

        // Create the Add button (Newly Added)
        Button addButton = new Button("Add");
        addButton.setLayoutX(798); // Position it on the bottom-right
        addButton.setLayoutY(377); // Bottom of the window
        addButton.setPrefWidth(85);
        addButton.setPrefHeight(26);
        addButton.setOnAction(e -> showAddTransactionWindow());

        // Add all components to the root
        root.getChildren().addAll(label1, label2, label3, transactTable, searchField, searchButton, backButton, viewDetailsButton, addButton);

        // Populate the table with transaction data
        populateTransactionList();

        // Create and show the scene
        Scene scene = new Scene(root);
        stage.setTitle("Transaction Management");
        stage.setScene(scene);
        stage.show();
    }

    // Populate the transaction table from the database
    private void populateTransactionList() {
        transactTable.getItems().clear();

        String url = "jdbc:mysql://localhost:3306/lostandfounddb";
        String user = "root";
        String password = "";

        String sql = "SELECT transaction_id, item_id, transaction_type, date_of_transaction, student_idNumber FROM Transaction";

        try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String transactionID = rs.getString("transaction_id");
                String itemID = rs.getString("item_id");
                String transactionType = rs.getString("transaction_type");
                String transactionDate = rs.getString("date_of_transaction");
                String studentID = rs.getString("student_idNumber");

                transactTable.getItems().add(new TransactionData(transactionID, itemID, transactionType, transactionDate, studentID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search transactions based on the search field
    private void searchTransactions() {
        String searchQuery = searchField.getText().toLowerCase();

        if (searchQuery.isEmpty()) {
            populateTransactionList();
        } else {
            transactTable.getItems().removeIf(transaction
                    -> !transaction.getItemID().toLowerCase().contains(searchQuery)
                    && !transaction.getTransactType().toLowerCase().contains(searchQuery)
                    && !transaction.getTransactDate().toLowerCase().contains(searchQuery)
                    && !transaction.getFoundBy().toLowerCase().contains(searchQuery)
            );
        }
    }

    private void viewTransactionDetails() {
        TransactionData selectedTransaction = transactTable.getSelectionModel().getSelectedItem();

        if (selectedTransaction == null) {
            showAlert("No Transaction Selected", "Please select a transaction to view details.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "SELECT t.transaction_id, t.item_id, t.transaction_type, t.date_of_transaction, t.student_idNumber, "
                + "i.item_name, i.description, s.student_name "
                + "FROM Transaction t "
                + "JOIN Item i ON t.item_id = i.item_id "
                + "JOIN Student s ON t.student_idNumber = s.student_idNumber "
                + "WHERE t.transaction_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", ""); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selectedTransaction.getTransactID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Transaction Details");
                dialog.setHeaderText(null);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(5);
                grid.setPadding(new Insets(10));

                // Use TextArea for description to handle long text
                TextArea descriptionArea = new TextArea(rs.getString("description"));
                descriptionArea.setEditable(false); // Make it non-editable
                descriptionArea.setPrefRowCount(5); //set the number of rows to display
                descriptionArea.setWrapText(true); // Enable text wrapping
                descriptionArea.setMaxWidth(300); // Set a maximum width
                grid.add(createLabel("Transaction ID:", rs.getString("transaction_id")), 0, 0);
                grid.add(createLabel("Item ID:", rs.getString("item_id")), 0, 1);
                grid.add(createLabel("Transaction Type:", rs.getString("transaction_type")), 0, 2);
                grid.add(createLabel("Date:", rs.getString("date_of_transaction")), 0, 3);
                grid.add(createLabel("Student ID:", rs.getString("student_idNumber")), 0, 4);
                grid.add(createLabel("Student Name:", rs.getString("student_name")), 0, 5);
                grid.add(createLabel("Item Name:", rs.getString("item_name")), 0, 6);
                grid.add(descriptionArea, 1, 6, 1, 2); // Span two rows

                // Set column constraints
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setPercentWidth(40);
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setPercentWidth(60);
                grid.getColumnConstraints().addAll(col1, col2);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.show();
            } else {
                showAlert("Transaction Not Found", "The selected transaction could not be found.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch transaction details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Label createLabel(String labelText, String value) {
        Label label = new Label(labelText + ": " + value); // Added a colon and space for better readability
        label.setFont(new Font(14));
        return label;
    }

    // Show alert for errors or warnings
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAddTransactionWindow() {
        AddTransaction addTransaction = new AddTransaction();
        Stage transactionStage = new Stage();
        addTransaction.start(transactionStage);
    }

    // TransactionData class to represent each row in the TableView
    public static class TransactionData {

        private String transactID;
        private String itemID;
        private String transactType;
        private String transactDate;
        private String foundBy;

        public TransactionData(String transactID, String itemID, String transactType, String transactDate, String foundBy) {
            this.transactID = transactID;
            this.itemID = itemID;
            this.transactType = transactType;
            this.transactDate = transactDate;
            this.foundBy = foundBy;
        }

        public String getTransactID() {
            return transactID;
        }

        public String getItemID() {
            return itemID;
        }

        public String getTransactType() {
            return transactType;
        }

        public String getTransactDate() {
            return transactDate;
        }

        public String getFoundBy() {
            return foundBy;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
