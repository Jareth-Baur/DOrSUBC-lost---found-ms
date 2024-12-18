package lostandfound.students;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class ViewStudents extends Application {

    private TableView<Student> studentList;
    private TextField searchField;

    @Override
    public void start(Stage stage) {
        // Create the AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(563, 426);

        // Create the label at the top
        Label label1 = new Label("Campus Lost And Found");
        label1.setLayoutX(197);
        label1.setLayoutY(29);
        label1.setFont(new Font(18));

        Label label2 = new Label("Management System");
        label2.setLayoutX(206);
        label2.setLayoutY(56);
        label2.setFont(new Font(18));

        Label label3 = new Label("List of Students");
        label3.setLayoutX(212);
        label3.setLayoutY(97);
        label3.setFont(new Font(18));

        // Create the TableView for displaying students
        studentList = new TableView<>();
        studentList.setLayoutX(38);
        studentList.setLayoutY(130);
        studentList.setPrefSize(487, 235);

        // Create columns for the TableView
        TableColumn<Student, String> studentIDColumn = new TableColumn<>("Student ID");
        studentIDColumn.setPrefWidth(110.4);
        studentIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentID()));

        TableColumn<Student, String> studentNameColumn = new TableColumn<>("Student Name");
        studentNameColumn.setPrefWidth(376);
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentName()));

        studentList.getColumns().addAll(studentIDColumn, studentNameColumn);

        // Create the search field
        searchField = new TextField();
        searchField.setLayoutX(35);
        searchField.setLayoutY(97);
        searchField.setPrefSize(78, 26);

        // Create the Search button
        Button searchButton = new Button("Search");
        searchButton.setLayoutX(127);
        searchButton.setLayoutY(96);
        searchButton.setOnAction(e -> searchStudents());

        // Create the Edit button
        Button editButton = new Button("Edit");
        editButton.setLayoutX(467);
        editButton.setLayoutY(97);
        editButton.setOnAction(e -> editStudent());

        // Create the Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(32);
        backButton.setLayoutY(379);
        backButton.setOnAction(e -> stage.close());

        // Create the Add Student button
        Button addStudentButton = new Button("Add Student");
        addStudentButton.setLayoutX(423);
        addStudentButton.setLayoutY(379);
        addStudentButton.setOnAction(e -> addStudent());

        // Add all components to the root
        root.getChildren().addAll(label1, label2, label3, studentList, searchField, searchButton, editButton, backButton, addStudentButton);

        // Fetch and populate the student list from the database
        populateStudentList();

        // Set up the scene and stage
        Scene scene = new Scene(root);
        stage.setTitle("View Students");
        stage.setScene(scene);
        stage.show();
    }

    // Populate the student list from the database
    private void populateStudentList() {
        studentList.getItems().clear();

        String url = "jdbc:mysql://localhost:3306/lostandfounddb";
        String user = "root";
        String password = "";

        String sql = "SELECT student_idNumber, student_name FROM student";

        try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String studentID = rs.getString("student_idNumber");
                String studentName = rs.getString("student_name");
                studentList.getItems().add(new Student(studentID, studentName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search students based on the search field
    private void searchStudents() {
        String searchQuery = searchField.getText().toLowerCase();

        if (searchQuery.isEmpty()) {
            populateStudentList();
        } else {
            studentList.getItems().removeIf(student -> !student.getStudentName().toLowerCase().contains(searchQuery));
        }
    }

    // Add a new student
    // Add a new student
    private void addStudent() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField studentIDField = new TextField();
        studentIDField.setPromptText("Student ID");

        TextField studentNameField = new TextField();
        studentNameField.setPromptText("Student Name");

        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(studentIDField, 1, 0);
        grid.add(new Label("Student Name:"), 0, 1);
        grid.add(studentNameField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String studentID = studentIDField.getText().trim();
                String studentName = studentNameField.getText().trim();

                // Validation for empty or blank fields
                if (studentID.isEmpty() || studentName.isEmpty()) {
                    showAlert("Validation Error", "Both Student ID and Student Name are required.", Alert.AlertType.WARNING);
                    return null;
                }
                return new Student(studentID, studentName);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(student -> {
            String sql = "INSERT INTO student (student_idNumber, student_name) VALUES (?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", ""); PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, student.getStudentID());
                pstmt.setString(2, student.getStudentName());
                pstmt.executeUpdate();

                // Show success popup
                showAlert("Success", "Student added successfully!", Alert.AlertType.INFORMATION);
                populateStudentList();
            } catch (SQLException e) {
                // Show failure popup
                showAlert("Error", "Failed to add student: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Edit the selected student
    // Edit the selected student
    private void editStudent() {
        Student selectedStudent = studentList.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert("No Student Selected", "Please select a student to edit.", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField studentIDField = new TextField(selectedStudent.getStudentID());
        studentIDField.setDisable(true); // Disable editing of the ID

        TextField studentNameField = new TextField(selectedStudent.getStudentName());

        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(studentIDField, 1, 0);
        grid.add(new Label("Student Name:"), 0, 1);
        grid.add(studentNameField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String studentName = studentNameField.getText().trim();

                // Validation for empty or blank fields
                if (studentName.isEmpty()) {
                    showAlert("Validation Error", "Student Name is required.", Alert.AlertType.WARNING);
                    return null;
                }
                return new Student(studentIDField.getText(), studentName);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(student -> {
            String sql = "UPDATE student SET student_name = ? WHERE student_idNumber = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", ""); PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, student.getStudentName());
                pstmt.setString(2, student.getStudentID());
                pstmt.executeUpdate();

                // Show success popup
                showAlert("Success", "Student updated successfully!", Alert.AlertType.INFORMATION);
                populateStudentList();
            } catch (SQLException e) {
                // Show failure popup
                showAlert("Error", "Failed to update student: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Show alert for errors or warnings
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Student class to represent each row in the TableView
    public static class Student {

        private String studentID;
        private String studentName;

        public Student(String studentID, String studentName) {
            this.studentID = studentID;
            this.studentName = studentName;
        }

        public String getStudentID() {
            return studentID;
        }

        public String getStudentName() {
            return studentName;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
