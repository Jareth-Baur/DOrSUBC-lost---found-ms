package lostandfound.main;

import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lostandfound.auth.LoginApp;
import lostandfound.items.AddItem;
import lostandfound.items.ViewItems;
import lostandfound.students.ViewStudents;
import lostandfound.transactions.Transaction;

public class MainApp extends Application {

    private Stage mainAppStage;

    @Override
    public void start(Stage primaryStage) {
        mainAppStage = primaryStage;
        // Create the AnchorPane
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(406.0);
        anchorPane.setPrefWidth(671.0);

        // Create Label
        Label label = new Label("Campus Lost And Found Management System");
        label.setLayoutX(183.0);
        label.setLayoutY(18.0);
        label.setMinHeight(16);
        label.setMinWidth(69);
        label.setPrefHeight(74.0);
        label.setPrefWidth(332.0);
        label.setFont(new Font(16.0));

        // Create Buttons
        Button addItem = new Button("Add Item");
        addItem.setLayoutX(223.0);
        addItem.setLayoutY(139.0);
        addItem.setPrefHeight(59.0);
        addItem.setPrefWidth(108.0);
        addItem.setOnAction(event -> handleButtonAction("Add Item"));

        Button logOut = new Button("Log Out");
        logOut.setLayoutX(586.0);
        logOut.setLayoutY(349.0);
        logOut.setOnAction(event -> handleButtonAction("Log Out"));

        Button students = new Button("Students");
        students.setLayoutX(223.0);
        students.setLayoutY(207.0);
        students.setPrefHeight(59.0);
        students.setPrefWidth(108.0);
        students.setOnAction(event -> handleButtonAction("Students"));

        Button viewItems = new Button("View Items");
        viewItems.setLayoutX(341.0);
        viewItems.setLayoutY(139.0);
        viewItems.setPrefHeight(59.0);
        viewItems.setPrefWidth(108.0);
        viewItems.setOnAction(event -> handleButtonAction("View Items"));

        Button transactions = new Button("Transactions");
        transactions.setLayoutX(341.0);
        transactions.setLayoutY(207.0);
        transactions.setPrefHeight(59.0);
        transactions.setPrefWidth(108.0);
        transactions.setOnAction(event -> handleButtonAction("Transactions"));

        // Add all components to the AnchorPane
        anchorPane.getChildren().addAll(label, addItem, logOut, students, viewItems, transactions);

        // Create the Scene and set it
        Scene scene = new Scene(anchorPane);
        primaryStage.setTitle("Campus Lost and Found");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Handle button click actions
    private void handleButtonAction(String action) {
        System.out.println(action + " button clicked");

        // Open the AddItem window if the Add Item button is clicked
        if ("Add Item".equals(action)) {
            AddItem addItemWindow = new AddItem();
            Stage addItemStage = new Stage();
            addItemWindow.start(addItemStage);  // Open AddItem window
        } // Open the ViewItems window if the View Items button is clicked
        else if ("View Items".equals(action)) {
            ViewItems viewItemsWindow = new ViewItems();
            Stage viewItemsStage = new Stage();
            viewItemsWindow.start(viewItemsStage);  // Open ViewItems window
        } else if ("Students".equals(action)) {
            ViewStudents viewStudentsWindow = new ViewStudents();
            Stage viewStudentsStage = new Stage();
            viewStudentsWindow.start(viewStudentsStage);
        } else if ("Transactions".equals(action)) {
            Transaction transactions = new Transaction();
            Stage transactionsStage = new Stage();
            transactions.start(transactionsStage);
        } else if ("Log Out".equals(action)) {
            // Create a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Log Out Confirmation");
            alert.setHeaderText("Are you sure you want to log out?");
            //alert.setContentText("If you log out, you will need to log in again to access the application.");

            // Show the dialog and wait for the response
            Optional<ButtonType> result = alert.showAndWait();

            // Check the user's response
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // If confirmed, proceed with logging out
                LoginApp loginApp = new LoginApp();
                Stage loginAppStage = new Stage();
                loginApp.start(loginAppStage);
                this.mainAppStage.close();
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
