package lostandfound.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lostandfound.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Label;

public class LoginController {

    private TextField userEmail;

    @FXML
    private TextField pWord;

    @FXML
    private Button logIn;

    @FXML
    private Hyperlink regisLink;
    @FXML
    private Label label1;
    @FXML
    private Label label11;
    @FXML
    private Label label111;
    @FXML
    private Label label21;
    @FXML
    private Label label221;
    @FXML
    private TextField email;
    @FXML
    private Label label2211;

    private void handleLogInButton(ActionEvent event) {
        String email = userEmail.getText();
        String password = pWord.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both email and password.");
            return;
        }

        if (authenticateAdmin(email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Login Successful!");
            System.out.println("Admin Login Successful!");
            // Load the dashboard or main screen
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Email or Password.");
            System.out.println("Login Failed: Invalid credentials.");
        }
    }

    private void handleRegisterLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            Stage currentStage = (Stage) regisLink.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the Sign-Up form.");
        }
    }

    // Authenticate admin user against the database
    private boolean authenticateAdmin(String email, String password) {
        boolean isValidAdmin = false;
        String query = "SELECT * FROM Admin WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isValidAdmin = true; // Admin exists with matching credentials
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while connecting to the database.");
        }
        return isValidAdmin;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
