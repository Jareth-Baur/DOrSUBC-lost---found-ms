package lostandfound.items;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import java.sql.*;

public class ViewItems extends Application {

    @FXML
    private Label label1;

    @FXML
    private Label label11;

    @FXML
    private Button back;

    @FXML
    private Label label111;

    @FXML
    private TableView<String[]> ItemTable;

    @FXML
    private TableColumn<String[], String> itemID;

    @FXML
    private TableColumn<String[], String> itemName;

    @FXML
    private TableColumn<String[], String> description;

    @FXML
    private TableColumn<String[], String> status;

    @FXML
    private TableColumn<String[], String> location;

    @FXML
    private Button editButton;

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/lostandfounddb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // JDBC variables for opening, closing, and managing connection
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    @Override
    public void start(Stage primaryStage) {
        // Create the root AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(830, 456);

        // Create and set labels and buttons
        label1 = new Label("Campus Lost And Found");
        label1.setLayoutX(315);
        label1.setLayoutY(30);
        label1.setFont(new Font(18));

        label11 = new Label("Management System");
        label11.setLayoutX(329);
        label11.setLayoutY(56);
        label11.setFont(new Font(18));

        label111 = new Label("View Item Details");
        label111.setLayoutX(43);
        label111.setLayoutY(96);
        label111.setFont(new Font(18));

        back = new Button("Back");
        back.setLayoutX(26);
        back.setLayoutY(394);
        back.setPrefWidth(60);
        back.setOnAction(event -> handleBackAction(primaryStage));

        // Create the TableView and its columns
        ItemTable = new TableView<>();
        ItemTable.setLayoutX(48);
        ItemTable.setLayoutY(140);
        ItemTable.setPrefWidth(734);
        ItemTable.setPrefHeight(235);

        itemID = new TableColumn<>("Item ID");
        itemID.setPrefWidth(104);
        itemID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));

        itemName = new TableColumn<>("Item Name");
        itemName.setPrefWidth(149.6);
        itemName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));

        description = new TableColumn<>("Description");
        description.setPrefWidth(150.4);
        description.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));

        status = new TableColumn<>("Status");
        status.setPrefWidth(172);
        status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));

        location = new TableColumn<>("Location");
        location.setPrefWidth(156.8);
        location.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4]));

        ItemTable.getColumns().addAll(itemID, itemName, description, status, location);

        // Create and set edit button
        editButton = new Button("Edit");
        editButton.setLayoutX(691);
        editButton.setLayoutY(92);
        editButton.setPrefWidth(90);
        editButton.setOnAction(event -> handleEditAction());

        // Add all components to the root AnchorPane
        root.getChildren().addAll(label1, label11, label111, back, ItemTable, editButton);

        // Fetch and populate the data
        fetchDataFromDatabase();

        // Create and show the scene
        Scene scene = new Scene(root);
        primaryStage.setTitle("View Items");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchDataFromDatabase() {
        ObservableList<String[]> data = FXCollections.observableArrayList();

        try {
            // Establish connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Create SQL query
            String query = "SELECT item_id, item_name, description, status, location FROM Item";

            // Execute query
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Loop through the result set and add to data
            while (rs.next()) {
                String[] row = {
                    rs.getString("item_id"),
                    rs.getString("item_name"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("location")
                };
                data.add(row);
            }

            // Set the table's items with the fetched data
            ItemTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBackAction(Stage primaryStage) {
        // Close the current window (back action)
        primaryStage.close();
        System.out.println("Back button clicked! Window closed.");
    }

    private void handleEditAction() {
        // Get the selected row data from the table
        String[] selectedRow = ItemTable.getSelectionModel().getSelectedItem();

        if (selectedRow != null) {
            // Print the data of the selected row to the console
            System.out.println("Selected Item ID: " + selectedRow[0]);
            System.out.println("Selected Item Name: " + selectedRow[1]);
            System.out.println("Selected Description: " + selectedRow[2]);
            System.out.println("Selected Status: " + selectedRow[3]);
            System.out.println("Selected Location: " + selectedRow[4]);

            // Pass the data to the EditItem class and open it
            EditItem editItem = new EditItem(
                    selectedRow[0], // itemid
                    selectedRow[1], // itemName
                    selectedRow[2], // description
                    selectedRow[3], // status
                    selectedRow[4] // location
            );

            editItem.start(new Stage());  // Open the edit item window
        } else {
            // Handle case when no row is selected
            System.out.println("No item selected for editing.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
