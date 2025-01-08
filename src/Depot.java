import java.io.FileWriter;
import java.sql.*;
import java.io.IOException;

public class Depot {
	private Connection connection;
	private ParcelMap parcelMap;

	public Depot() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:depot.db");
			initializeDatabase();
			System.out.println("Connected to the SQLite database.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		parcelMap = new ParcelMap();
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
		Parcel newParcel = new Parcel(weight, length, width, height, daysInDepot, ownerName);

		// Add the new Parcel to the ParcelMap
		parcelMap.addParcel(newParcel);

		// Logging the addition of the new Parcel
		System.out.printf("Parcel added with Parcel ID: %s\n", newParcel.getParcelId());
		Log.getInstance().addEvent("Parcel added: " + newParcel.getParcelId());
	}


	public void removeProduct(String parcelId) {
		if (parcelMap.containsParcel(parcelId)) {
			parcelMap.removeParcel(parcelId);
			System.out.printf("Parcel with Parcel ID %s has been removed successfully.\n", parcelId);
			Log.getInstance().addEvent("Parcel removed: " + parcelId);
		} else {
			System.out.printf("Parcel with Parcel ID %s was not found.\n", parcelId);
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
		Parcel parcel = parcelMap.getParcel(parcelId);
		if (parcel != null) {
			parcel.setWeight(weight);
			parcel.setLength(length);
			parcel.setWidth(width);
			parcel.setHeight(height);
			parcel.setDaysInDepot(daysInDepot);
			parcel.updateCollectionFee();
			System.out.printf("Parcel with Parcel ID %s updated successfully.\n", parcelId);
			Log.getInstance().addEvent("Parcel Updated: " + parcelId);
		} else {
			System.out.printf("Parcel with Parcel ID %s not found.\n", parcelId);
		}
	}



	public void updateProduct(String parcelId, int daysInDepot) {
		String sql = "UPDATE Products SET daysInDepot = ?, collectionFee = ?, status = 'in depot' WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			double newCollectionFee = (daysInDepot * 0.2);
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

	public void markProductAsPickedUp(String parcelId) {
		Parcel parcel = parcelMap.getParcel(parcelId);
		if (parcel != null) {
			parcel.setStatus("picked up");
			System.out.printf("Parcel with Parcel ID %s marked as picked up.\n", parcelId);
			Log.getInstance().addEvent("Parcel picked up: " + parcelId);
		} else {
			System.out.printf("Parcel with Parcel ID %s not found.\n", parcelId);
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


	public void displayProducts() {
		System.out.println("All Parcels:");
		for (Parcel parcel : parcelMap.getAllParcels().values()) {
			System.out.println(parcel);
		}
	}

	public boolean isProductPresent(String parcelId) {
		String sql = "SELECT 1 FROM Products WHERE parcelId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, parcelId);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isProductPresentFromUrl(String parcelId) {
		return parcelMap.containsParcel(parcelId);
	}


	public String displayProductsForUrl() {
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<div>");  // Start of the container div for better HTML management
		htmlContent.append("<h1>All Parcels:</h1>");

		// Check if parcelMap is not empty
		if (!parcelMap.getAllParcels().isEmpty()) {
			for (Parcel parcel : parcelMap.getAllParcels().values()) {
				// Append each parcel's details in HTML format
				htmlContent.append("<p>");
				htmlContent.append("Parcel ID: ").append(parcel.getParcelId()).append(", ");
				htmlContent.append("Owner: ").append(parcel.getOwnerName()).append(", ");
				htmlContent.append("Weight: ").append(parcel.getWeight()).append("kg, ");
				htmlContent.append("Dimensions: ").append(parcel.getDimensions()).append(", ");
				htmlContent.append("Days in Depot: ").append(parcel.getDaysInDepot()).append(", ");
				htmlContent.append("Status: ").append(parcel.getStatus()).append(", ");
				htmlContent.append("Collection Fee: $").append(String.format("%.2f", parcel.getCollectionFee()));
				htmlContent.append("</p>");
			}
		} else {
			// If no parcels are available
			htmlContent.append("<p>No parcels currently stored.</p>");
		}

		htmlContent.append("</div>");  // End of the container div
		return htmlContent.toString();
    }


	public double calculateCumulativeValue() {
		double totalFees = 0.0;
		for (Parcel parcel : parcelMap.getAllParcels().values()) {
			if ("in depot".equals(parcel.getStatus())) {
				totalFees += parcel.getCollectionFee();
			}
		}
		return totalFees;
	}

	public void displayCumulativeValue() {
		double totalValue = calculateCumulativeValue();
		System.out.printf("Total collection fees for all products in depot: $%.2f\n", totalValue);
	}

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