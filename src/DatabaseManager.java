import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:depot.db");
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() throws SQLException {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS Products (
                parcelId TEXT PRIMARY KEY,
                weight REAL,
                length REAL,
                width REAL,
                height REAL,
                daysInDepot INTEGER,
                dimensions TEXT,
                status TEXT,
                collectionFee REAL,
                ownerName TEXT
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableQuery);
        }

    }

    public void addProduct(Parcel parcel) {
        String sql = """
        INSERT INTO Products (parcelId, weight, length, width, height, daysInDepot, dimensions, status, collectionFee, ownerName)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parcel.getParcelId());
            pstmt.setDouble(2, parcel.getWeight());
            pstmt.setDouble(3, parcel.getLength());
            pstmt.setDouble(4, parcel.getWidth());
            pstmt.setDouble(5, parcel.getHeight());
            pstmt.setInt(6, parcel.getDaysInDepot());
            pstmt.setString(7, parcel.getDimensions());
            pstmt.setString(8, parcel.getStatus());
            pstmt.setDouble(9, parcel.getCollectionFee());
            pstmt.setString(10, parcel.getOwnerName()); // Set owner's name

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeProduct(String parcelId) {
        String sql = "DELETE FROM Products WHERE parcelId =?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parcelId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Parcel parcel) {
        String sql = "UPDATE Products SET weight =?, length =?, width =?, height =?, daysInDepot =?, dimensions =?, status =?, collectionFee =?, ownerName =? WHERE parcelId =?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, parcel.getWeight());
            pstmt.setDouble(2, parcel.getLength());
            pstmt.setDouble(3, parcel.getWidth());
            pstmt.setDouble(4, parcel.getHeight());
            pstmt.setInt(5, parcel.getDaysInDepot());
            pstmt.setString(6, parcel.getDimensions());
            pstmt.setString(7, parcel.getStatus());
            pstmt.setDouble(8, parcel.getCollectionFee());
            pstmt.setString(9, parcel.getOwnerName()); // Update owner's name
            pstmt.setString(10, parcel.getParcelId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Parcel getProduct(String parcelId) {
        String sql = "SELECT * FROM Products WHERE parcelId =?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parcelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double weight = rs.getDouble("weight");
                    double length = rs.getDouble("length");
                    double width = rs.getDouble("width");
                    double height = rs.getDouble("height");
                    int daysInDepot = rs.getInt("daysInDepot");
                    String dimensions = rs.getString("dimensions");
                    String status = rs.getString("status");
                    double collectionFee = rs.getDouble("collectionFee");
                    String ownerName = rs.getString("ownerName"); // Retrieve owner's name
                    return new Parcel(weight, length, width, height, daysInDepot, ownerName); // Include owner's name in the constructor
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void closeConnection() {
        try {
            if (connection!= null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}