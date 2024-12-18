package lostandfound.auth;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lostandfound.db.DBConnection;

public class SignUpApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the AnchorPane container
        AnchorPane root = new AnchorPane();
        root.setPrefSize(600, 400);

        // Create labels
        Label label1 = new Label("Campus Lost And Found");
        label1.setFont(new Font(18));
        label1.setLayoutX(200);
        label1.setLayoutY(30);

        Label label11 = new Label("Management System");
        label11.setFont(new Font(18));
        label11.setLayoutX(214);
        label11.setLayoutY(56);

        Label label111 = new Label("Sign Up Form");
        label111.setFont(new Font(18));
        label111.setLayoutX(241);
        label111.setLayoutY(106);

        // Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(24);
        backButton.setLayoutY(28);
        backButton.setOnAction(event -> {
            // Close the current window (stage)
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close(); // Close the current window

            // Open the login screen in a new window
            LoginApp loginApp = new LoginApp(); // Create a new instance of LoginApp
            try {
                loginApp.start(new Stage()); // Start the login app on a new stage
            } catch (Exception e) {
                e.printStackTrace(); // Handle potential exceptions
            }
        });

        // First Name field
        Label label2 = new Label("First Name");
        label2.setFont(new Font(16));
        label2.setLayoutX(118);
        label2.setLayoutY(195);

        TextField firstName = new TextField();
        firstName.setLayoutX(78);
        firstName.setLayoutY(159);
        firstName.setPrefSize(164, 27);

        // Last Name field
        Label label22 = new Label("Last Name");
        label22.setFont(new Font(16));
        label22.setLayoutX(116);
        label22.setLayoutY(264);

        TextField lastName = new TextField();
        lastName.setLayoutX(78);
        lastName.setLayoutY(226);
        lastName.setPrefSize(164, 27);

        // Username field
        Label label21 = new Label("Email");
        label21.setFont(new Font(16));
        label21.setLayoutX(380);
        label21.setLayoutY(195);

        TextField userName = new TextField();
        userName.setLayoutX(333);
        userName.setLayoutY(159);
        userName.setPrefSize(164, 27);

        // Password field
        Label label221 = new Label("Password");
        label221.setFont(new Font(16));
        label221.setLayoutX(380);
        label221.setLayoutY(264);

        PasswordField pWord = new PasswordField();
        pWord.setLayoutX(333);
        pWord.setLayoutY(228);
        pWord.setPrefSize(164, 27);

        // Sign Up button
        Button signUpButton = new Button("Sign up");
        signUpButton.setLayoutX(241);
        signUpButton.setLayoutY(316);
        signUpButton.setPrefSize(106, 45);
        signUpButton.setOnAction(event -> {
            // Capture the input data
            String firstNameText = firstName.getText();
            String lastNameText = lastName.getText();
            String usernameText = userName.getText();
            String passwordText = pWord.getText();

            // Check if all fields are filled
            if (firstNameText.isEmpty() || lastNameText.isEmpty() || usernameText.isEmpty() || passwordText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "All fields must be filled!");
                return;
            }

            // Hash the password
            String hashedPassword = hashPassword(passwordText);

            // Insert data into the database
            if (insertUserData(firstNameText, lastNameText, usernameText, hashedPassword)) {
                showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "User signed up successfully!");
                // Optionally, switch to a login screen
                LoginApp loginApp = new LoginApp();
                try {
                    loginApp.start(new Stage());
                    Stage currentStage = (Stage) signUpButton.getScene().getWindow();
                    currentStage.close(); // Close the sign-up window
                } catch (Exception e) {
                    e.printStackTrace(); // Handle potential exceptions
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Sign up failed. Please try again.");
            }
        });

        // Add all components to the root AnchorPane
        root.getChildren().addAll(
                label1, label11, label111, backButton,
                label2, firstName, label22, lastName,
                label21, userName, label221, pWord,
                signUpButton
        );

        // Create the Scene
        Scene scene = new Scene(root);

        // Set the stage
        primaryStage.setTitle("Sign Up Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Hash the password
    private String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert byte array to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // Insert user data into the database
    private boolean insertUserData(String firstName, String lastName, String username, String password) {
        Connection connection = DBConnection.getConnection(); // Get connection from DBConnection class
        if (connection == null) {
            System.out.println("Database connection failed.");
            return false;
        }

        String query = "INSERT INTO Admin (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName + " " + lastName); // Full name
            statement.setString(2, username); // Username or email
            statement.setString(3, password); // Hashed password

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Show an alert popup
    private void showAlert(Alert.AlertType type, String title, String message) {
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
