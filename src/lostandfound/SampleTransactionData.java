package lostandfound;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleTransactionData {

    public static void generateAndInsertTransactions(Connection connection, int numTransactions) {
        Random random = new Random();
        LocalDate currentDate = LocalDate.now();

        try {
            String selectStudentQuery = "SELECT student_idNumber FROM Student";
            String selectItemQuery = "SELECT item_id FROM Item WHERE status = 'Found'"; //Only include found items
            String insertTransactionQuery = "INSERT INTO Transaction (item_id, transaction_type, date_of_transaction, student_idNumber) VALUES (?, ?, ?, ?)";
            String updateItemQuery = "UPDATE Item SET status = 'Returned' WHERE item_id = ?";

            try (PreparedStatement selectStudentStmt = connection.prepareStatement(selectStudentQuery); PreparedStatement selectItemStmt = connection.prepareStatement(selectItemQuery); PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery); PreparedStatement updateItemStmt = connection.prepareStatement(updateItemQuery)) {

                ResultSet studentRS = selectStudentStmt.executeQuery();
                ResultSet itemRS = selectItemStmt.executeQuery();

                List<String> studentIDs = new ArrayList<>();
                List<Integer> itemIDs = new ArrayList<>();

                while (studentRS.next()) {
                    studentIDs.add(studentRS.getString("student_idNumber"));
                }
                while (itemRS.next()) {
                    itemIDs.add(itemRS.getInt("item_id"));
                }

                for (int i = 0; i < numTransactions; i++) {
                    //Randomly select student and item
                    String studentID = studentIDs.get(random.nextInt(studentIDs.size()));
                    int itemID = itemIDs.get(random.nextInt(itemIDs.size()));

                    //Simulate a claim transaction
                    String transactionType = "Claimed";
                    LocalDate transactionDate = currentDate.minusDays(random.nextInt(30)); //Transaction date within last 30 days

                    insertTransactionStmt.setInt(1, itemID);
                    insertTransactionStmt.setString(2, transactionType);
                    insertTransactionStmt.setDate(3, Date.valueOf(transactionDate));
                    insertTransactionStmt.setString(4, studentID);
                    insertTransactionStmt.executeUpdate();

                    //Update item status to returned
                    updateItemStmt.setInt(1, itemID);
                    updateItemStmt.executeUpdate();
                }

                System.out.println("Sample transactions added successfully.");

            }
        } catch (SQLException e) {
            System.err.println("Error adding sample transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", "")) {
            generateAndInsertTransactions(connection, 12); // Generate and insert 12 sample transactions
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
