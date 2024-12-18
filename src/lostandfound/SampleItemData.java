package lostandfound;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleItemData {

    private static final String[] itemNames = {"Laptop", "Textbook", "Phone", "Wallet", "Keys", "Bag", "Watch", "Jewelry", "Sunglasses", "Headphones", "Notebook", "Pencil Case", "Charger", "Umbrella", "Jacket", "Water Bottle", "Backpack", "Camera", "Tablet", "Power Bank", "Book", "Pencil", "Eraser", "Ruler", "Calculator", "Flash Drive", "Mouse", "Keyboard", "Monitor", "Printer", "Scanner", "Stapler", "Tape", "Scissors", "Paper", "Pen", "Highlighter", "Sticky Notes", "Folder", "Binder", "Notebook", "Pencil Sharpener", "Desk Lamp", "Chair", "Whiteboard", "Marker", "Eraser", "Dry Erase Board", "Projector", "Screen", "Remote", "Cable", "Adapter"};
    private static final String[] descriptions = {"Generic description", "Slightly more detailed description", "A very long and detailed description of the item", "Short description", "Another generic description"};
    private static final String[] locations = {"Library", "Classroom", "Cafeteria", "Hallway", "Parking Lot", "Lab", "Office", "Dormitory", "Gym", "Stadium"};
    private static final String[] statuses = {"Lost", "Found", "Returned"};


    public static List<String> generateItemData(int numItems) {
        List<String> itemData = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numItems; i++) {
            String itemName = itemNames[random.nextInt(itemNames.length)];
            String description = descriptions[random.nextInt(descriptions.length)];
            String status = statuses[random.nextInt(statuses.length)];
            String location = locations[random.nextInt(locations.length)];

            itemData.add(itemName + "," + description + "," + status + "," + location);
        }
        return itemData;
    }

    public static void main(String[] args) {
        List<String> itemDataList = generateItemData(60); // Generate 60 sample items

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostandfounddb", "root", "")) {
            String sql = "INSERT INTO Item (item_name, description, status, location) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (String item : itemDataList) {
                    String[] parts = item.split(",");
                    statement.setString(1, parts[0]);
                    statement.setString(2, parts[1]);
                    statement.setString(3, parts[2]);
                    statement.setString(4, parts[3]);
                    statement.executeUpdate();
                }
                System.out.println("Item data inserted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting item data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
