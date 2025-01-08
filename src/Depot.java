import java.io.FileWriter;
import java.sql.*;
import java.io.IOException;

public class Depot {
	private Connection connection;

	// Constructor to establish SQLite connection and initialize the database
	public Depot() {
		try {
			// Connect to the SQLite database
			connection = DriverManager.getConnection("jdbc:sqlite:depot.db");
			initializeDatabase();
			System.out.println("Connected to the SQLite database.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method to create the Products table if it doesn't exist
	private void initializeDatabase() throws SQLException {
		String createTableQuery = """
                CREATE TABLE IF NOT EXISTS Products (
                    parcelId TEXT PRIMARY KEY,
                    weight REAL,
                    length REAL,
                    width REAL,
                    height REAL,
                    daysInDepot INTEGER,
                    ownerName TEXT,
                    dimensions TEXT,
                    status TEXT,
                    collectionFee REAL
                );
                """;
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(createTableQuery);
		}
	}

	public void addProduct(double weight, double length, double width, double height, int daysInDepot, String ownerName) {
		String sql = """
        INSERT INTO Products (parcelId, weight, length, width, height, daysInDepot, ownerName, dimensions, status, collectionFee)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'in depot', ?);
        """;
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			String parcelId = Parcel.generateParcelId(); // Generate unique Parcel ID
			String dimensions = length + "x" + width + "x" + height;
			double collectionFee = (weight * 0.5) + (daysInDepot * 0.2);

			pstmt.setString(1, parcelId);
			pstmt.setDouble(2, weight);
			pstmt.setDouble(3, length);
			pstmt.setDouble(4, width);
			pstmt.setDouble(5, height);
			pstmt.setInt(6, daysInDepot);
			pstmt.setString(7, ownerName);  // Correct position according to SQL
			pstmt.setString(8, dimensions);
			pstmt.setDouble(9, collectionFee);

			pstmt.executeUpdate();
			System.out.printf("Product added with Parcel ID: %s\n", parcelId);
			Log.getInstance().addEvent("Product added: " + parcelId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	// Remove a product from the database by parcelId
	public void removeProduct(String parcelId) {
		String sql = "DELETE FROM Products WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);

			int rowsDeleted = pstmt.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.printf("Product with Parcel ID %s has been removed successfully.\n", parcelId);
				Log.getInstance().addEvent("Product removed: " + parcelId);
			} else {
				System.out.printf("Product with Parcel ID %s was not found in the database.\n", parcelId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeProductFromUrl(String parcelId) {
		String sql = "DELETE FROM Products WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);

			int rowsDeleted = pstmt.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.printf("Product with Parcel ID %s has been removed successfully.\n", parcelId);
			} else {
				System.out.printf("Product with Parcel ID %s was not found in the database.\n", parcelId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void updateProductFromUrl(String parcelId, double weight, double length, double width, double height, int daysInDepot) {
		String sql = "UPDATE Products SET weight = ?, length = ?, width = ?, height = ?, dimensions = ?, daysInDepot = ?, collectionFee = ?, status = 'in depot' WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			double newCollectionFee = (daysInDepot * 0.2);  // Adjust fee logic if needed
			String newDimensions = length + "x" + width + "x" + height;  // Recalculate the dimensions

			pstmt.setDouble(1, weight);
			pstmt.setDouble(2, length);
			pstmt.setDouble(3, width);
			pstmt.setDouble(4, height);
			pstmt.setString(5, newDimensions);  // Set the new dimensions string
			pstmt.setInt(6, daysInDepot);
//			pstmt.setString(7, ownerName);

			pstmt.setDouble(8, newCollectionFee);
			pstmt.setString(9, parcelId);

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.printf("Product with Parcel ID %s updated successfully.\n", parcelId);
				Log.getInstance().addEvent("Product Updated: " + parcelId);
			} else {
				System.out.printf("Product with Parcel ID %s not found.\n", parcelId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}




	// Update an existing product in the database
	public void updateProduct(String parcelId, int daysInDepot) {
		String sql = "UPDATE Products SET daysInDepot = ?, collectionFee = ?, status = 'in depot' WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			double newCollectionFee = (daysInDepot * 0.2); // Adjust fee logic if needed
			pstmt.setInt(1, daysInDepot);
			pstmt.setDouble(2, newCollectionFee);
			pstmt.setString(3, parcelId);

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.printf("Product with Parcel ID %s updated successfully.\n", parcelId);
			} else {
				System.out.printf("Product with Parcel ID %s not found.\n", parcelId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Mark a product as picked up
	public void markProductAsPickedUp(String parcelId) {
		String sql = "UPDATE Products SET status = 'picked up' WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.printf("Product with Parcel ID %s marked as picked up.\n", parcelId);
				Log.getInstance().addEvent("Product picked up : " + parcelId);
			} else {
				System.out.printf("Product with Parcel ID %s not found.\n", parcelId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void markProductAsPickedUpFromUrl(String parcelId) {
		String sql = "UPDATE Products SET status = 'picked up' WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.printf("Product with Parcel ID %s marked as picked up.\n", parcelId);
			} else {
				System.out.printf("Product with Parcel ID %s not found.\n", parcelId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	// Display all products in the database
	public void displayProducts() {
		String sql = "SELECT * FROM Products";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				System.out.printf(
						"Parcel ID: %s, Weight: %.2fkg, Dimensions: %s, Days in Depot: %d, Collection Fee: $%.2f, Status: %s, OwnerName: %s\n",
						rs.getString("parcelId"), rs.getDouble("weight"), rs.getString("dimensions"),
						rs.getInt("daysInDepot"), rs.getDouble("collectionFee"), rs.getString("status"), rs.getString("ownerName")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Check if a product exists in the database
	public boolean isProductPresent(String parcelId) {
		String sql = "SELECT 1 FROM Products WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next(); // Returns true if the product exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isProductPresentFromUrl(String parcelId) {
		String sql = "SELECT 1 FROM Products WHERE parcelId = ?, status = 'in depot'";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();  // Returns true if the product exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	public String displayProductsForUrl() {
		StringBuilder htmlContent = new StringBuilder();
		String sql = "SELECT * FROM Products";
		try (Statement stmt = this.connection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				htmlContent.append(String.format("<p>Parcel ID: %s, Weight: %.2fkg, Dimensions: %s, Days in Depot: %d, Collection Fee: $%.2f, Status: %s, OwnerName: %s </p>",
						rs.getString("parcelId"), rs.getDouble("weight"), rs.getString("dimensions"), rs.getInt("daysInDepot"), rs.getDouble("collectionFee"), rs.getString("status"), rs.getString("ownerName")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			htmlContent.append("<p>Error accessing database.</p>");
		}
		return htmlContent.toString();
	}


	// Calculate the cumulative value of all products
	public double calculateCumulativeValue() {
		String sql = "SELECT SUM(collectionFee) AS total FROM Products WHERE status = 'in depot'";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			return rs.getDouble("total");
		} catch (SQLException e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	// Display the cumulative value of all products
	public void displayCumulativeValue() {
		double totalValue = calculateCumulativeValue();
		System.out.printf("Total collection fees for all products in depot: $%.2f\n", totalValue);
	}

	// Export depot data to a text file
	public void printDepotToFile() {
		String sql = "SELECT * FROM Products";
		try (FileWriter writer = new FileWriter("DepotDetails.txt");
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			writer.write("Depot Products:\n");
			writer.write("------------------------------------------------------------\n");
			while (rs.next()) {
				String line = String.format(
						"Parcel ID: %s, Weight: %.2fkg, Dimensions: %s, Days in Depot: %d, Collection Fee: $%.2f, Status: %s\n",
						rs.getString("parcelId"), rs.getDouble("weight"), rs.getString("dimensions"),
						rs.getInt("daysInDepot"), rs.getDouble("collectionFee"), rs.getString("status")
				);
				writer.write(line);
			}
			writer.write("------------------------------------------------------------\n");
			System.out.println("Depot details exported successfully to DepotDetails.txt.");
		} catch (IOException e) {
			System.out.println("An error occurred while writing to the file.");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("An error occurred while querying the database.");
			e.printStackTrace();
		}
	}
}