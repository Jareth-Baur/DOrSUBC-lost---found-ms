package lostandfound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.sql.*;

public class SampleStudentData {

    private static final String[] firstNames = {"Alice", "Bob", "Charlie", "David", "Eve", "Jona", "Frank", "Grace", "Henry", "Ivy", "Jack", "Karen", "Liam", "Mia", "Noah", "Olivia", "Peter", "Quinn", "Robert", "Sophia", "Thomas", "Ursula", "Victor", "Wendy", "Xavier", "Yara", "Zachary", "Ava", "Benjamin", "Chloe", "Daniel", "Ella", "Gabriel", "Harper", "Isabella", "Jackson", "Kylie", "Logan", "Mia", "Nathan", "Olivia", "Owen", "Penelope", "Quinn", "Ryan", "Sophia", "Tyler", "Violet", "William", "Xena", "Zoe"};
    private static final String[] lastNames = {"Smith", "Jones", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen", "Young", "King", "Wright", "Lopez", "Hill", "Scott", "Green", "Adams", "Baker", "Gonzalez", "Nelson", "Carter", "Mitchell", "Perez", "Roberts", "Turner", "Phillips", "Campbell", "Parker", "Evans", "Edwards", "Collins", "Stewart", "Sanchez", "Morris", "Rogers"};

    public static List<String> generateStudentData(int numStudents) {
        List<String> studentData = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numStudents; i++) {
            int year = 2020 + random.nextInt(5); // Year between 2020 and 2024
            int randomNumber = random.nextInt(10000); // Random number between 0 and 9999
            String studentID = String.format("%d-%04d", year, randomNumber);

            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String studentName = firstName + " " + lastName;

            studentData.add(studentID + "," + studentName);
        }
        return studentData;
    }

    public static void main(String[] args) {
        List<String> studentDataList = generateStudentData(50); // Generate 50 sample students
        for (String student : studentDataList) {
            System.out.println(student);
        }

        //Example of inserting into database (replace with your database connection details)
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", "")) {
            String sql = "INSERT INTO Student (student_idNumber, student_name) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (String student : studentDataList) {
                    String[] parts = student.split(",");
                    statement.setString(1, parts[0]);
                    statement.setString(2, parts[1]);
                    statement.executeUpdate();
                }
                System.out.println("Data inserted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
