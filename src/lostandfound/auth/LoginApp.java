package lostandfound.auth;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lostandfound.db.DBConnection;
import lostandfound.main.MainApp;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(435.0);
        anchorPane.setPrefWidth(653.0);

        // Labels
        Label label1 = new Label("Campus Lost And Found");
        label1.setLayoutX(234.0);
        label1.setLayoutY(38.0);
        label1.setPrefHeight(26.0);
        label1.setPrefWidth(200.0);
        label1.setFont(new Font(18.0));

        Label label11 = new Label("Management System");
        label11.setLayoutX(248.0);
        label11.setLayoutY(64.0);
        label11.setPrefHeight(26.0);
        label11.setPrefWidth(170.0);
        label11.setFont(new Font(18.0));

        Label label111 = new Label("Log In Form");
        label111.setLayoutX(271.0);
        label111.setLayoutY(114.0);
        label111.setPrefHeight(26.0);
        label111.setPrefWidth(106.0);
        label111.setFont(new Font(18.0));

        Label label21 = new Label("Email");
        label21.setLayoutX(301.0);
        label21.setLayoutY(204.0);
        label21.setPrefHeight(26.0);
        label21.setPrefWidth(46.0);
        label21.setFont(new Font(16.0));

        Label label221 = new Label("Password");
        label221.setLayoutX(289.0);
        label221.setLayoutY(270.0);
        label221.setPrefHeight(26.0);
        label221.setPrefWidth(75.0);
        label221.setFont(new Font(16.0));

        Label label2211 = new Label("Don't have an account yet? ");
        label2211.setLayoutX(202.0);
        label2211.setLayoutY(316.0);
        label2211.setPrefHeight(26.0);
        label2211.setPrefWidth(161.0);

        // TextFields
        TextField emailField = new TextField();
        emailField.setLayoutX(224.0);
        emailField.setLayoutY(167.0);
        emailField.setPrefHeight(27.0);
        emailField.setPrefWidth(214.0);

        TextField passwordField = new TextField();
        passwordField.setLayoutX(226.0);
        passwordField.setLayoutY(235.0);
        passwordField.setPrefHeight(27.0);
        passwordField.setPrefWidth(214.0);

        // Button
        Button logInButton = new Button("Log In");
        logInButton.setLayoutX(273.0);
        logInButton.setLayoutY(353.0);
        logInButton.setPrefHeight(45.0);
        logInButton.setPrefWidth(106.0);

        // Hyperlink
        Hyperlink regisLink = new Hyperlink("Register here");
        regisLink.setLayoutX(364.0);
        regisLink.setLayoutY(317.0);
        regisLink.setFont(new Font("System Bold", 12.0));

        // Add all components to the AnchorPane
        anchorPane.getChildren().addAll(label1, label11, label111, label21, label221, label2211, emailField, passwordField, logInButton, regisLink);

        // Button Action - Login Validation
        logInButton.setOnAction(event -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String hashedPassword = hashPassword(password);
            if (validateLogin(email, hashedPassword)) {
                showAlert(AlertType.INFORMATION, "Login Successful", "You have successfully logged in.");

                // Close the current login window
                Stage currentStage = (Stage) logInButton.getScene().getWindow();
                currentStage.close();

                // Launch the MainApp window
                MainApp mainApp = new MainApp();
                mainApp.start(new Stage()); // Open the MainApp in a new stage
            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Invalid credentials. Please try again.");
            }
        });

        // Hyperlink Action - Open Registration (SignUpApp)
        regisLink.setOnAction(event -> {
            // Close the current window (stage)
            Stage currentStage = (Stage) regisLink.getScene().getWindow();
            currentStage.close();

            // Open the SignUpApp
            SignUpApp signUpApp = new SignUpApp();
            signUpApp.start(new Stage()); // Open the SignUpApp
        });

        // Create the scene and set it
        Scene scene = new Scene(anchorPane);
        primaryStage.setTitle("Login Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to validate login credentials
    private boolean validateLogin(String email, String password) {
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            System.out.println("Database connection failed.");
            return false;
        }

        String query = "SELECT * FROM Admin WHERE email = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true; // Admin found, login successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // No matching admin, login failed
    }

    // Method to hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to show alerts
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
