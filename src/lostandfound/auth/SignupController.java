import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupController {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField userName;

    @FXML
    private TextField pWord;

    @FXML
    private Button signUp;

    @FXML
    private Button back;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/YOUR_DATABASE_NAME";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Handle Sign-Up Button Action
    @FXML
    void handleSignUp(ActionEvent event) {
        String firstNameInput = firstName.getText().trim();
        String lastNameInput = lastName.getText().trim();
        String userNameInput = userName.getText().trim();
        String passwordInput = pWord.getText().trim();

        if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || userNameInput.isEmpty() || passwordInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all fields.");
            return;
        }

        // Combine first and last name for 'name' column
        String fullName = firstNameInput + " " + lastNameInput;
        insertAdmin(fullName, userNameInput, passwordInput);
    }

    private void insertAdmin(String name, String email, String password) {
        String sql = "INSERT INTO Admin (name, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success!", "Admin account created successfully.");
                clearForm();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error!", "An error occurred: " + e.getMessage());
        }
    }

    private void clearForm() {
        firstName.clear();
        lastName.clear();
        userName.clear();
        pWord.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleButtonAction(ActionEvent event) {
        // Add your back navigation logic here
        System.out.println("Back button clicked");
    }
}
