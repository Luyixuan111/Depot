import java.io.FileWriter;
import java.sql.*;
import java.io.IOException;

public class Depot {
//	private Connection connection;
	private ParcelMap parcelMap;

	public Depot() {
//		try {
//			connection = DriverManager.getConnection("jdbc:sqlite:depot.db");
//			initializeDatabase();
//			System.out.println("Connected to the SQLite database.");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		parcelMap = new ParcelMap();
	}
//
//	private void initializeDatabase() throws SQLException {
//		String createTableQuery = """
//                CREATE TABLE IF NOT EXISTS Products (
//                    parcelId TEXT PRIMARY KEY,
//                    weight REAL,
//                    length REAL,
//                    width REAL,
//                    height REAL,
//                    daysInDepot INTEGER,
//                    ownerName TEXT,
//                    dimensions TEXT,
//                    status TEXT,
//                    collectionFee REAL
//                );
//                """;
//		try (Statement stmt = connection.createStatement()) {
//			stmt.execute(createTableQuery);
//		}
//	}

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

//	public void removeProductFromUrl(String parcelId) {
//		String sql = "DELETE FROM Products WHERE parcelId = ?";
//		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//			pstmt.setString(1, parcelId);
//
//			int rowsDeleted = pstmt.executeUpdate();
//			if (rowsDeleted > 0) {
//				System.out.printf("Product with Parcel ID %s has been removed successfully.\n", parcelId);
//			} else {
//				System.out.printf("Product with Parcel ID %s was not found in the database.\n", parcelId);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}


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


	public void markProductAsPickedUpFromUrl(String parcelId) {
		Parcel parcel = parcelMap.getParcel(parcelId);
		if (parcel != null) {
			parcel.setStatus("picked up");
			System.out.printf("Parcel with Parcel ID %s marked as picked up.\n", parcelId);
			Log.getInstance().addEvent("Parcel picked up: " + parcelId);
		} else {
			System.out.printf("Parcel with Parcel ID %s not found.\n", parcelId);
		}
	}


	public boolean isProductPresentFromUrl(String parcelId) {
		return parcelMap.containsParcel(parcelId);
	}


	public String displayProductsForUrl() {
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<div>");  // Start of the container div for better HTML management
		htmlContent.append("<h1>All Parcels:</h1>");

		if (!parcelMap.getAllParcels().isEmpty()) {
			for (Parcel parcel : parcelMap.getAllParcels().values()) {
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
			htmlContent.append("<p>No parcels currently stored.</p>");
		}

		htmlContent.append("</div>");
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
		try (FileWriter writer = new FileWriter("DepotDetails.txt")) {
			writer.write("Depot Products:\n");
			writer.write("------------------------------------------------------------\n");

			// Check if parcelMap has entries
			if (!parcelMap.getAllParcels().isEmpty()) {
				for (Parcel parcel : parcelMap.getAllParcels().values()) {
					String line = String.format(
							"Parcel ID: %s, Owner: %s, Weight: %.2fkg, Dimensions: %s, Days in Depot: %d, Status: %s, Collection Fee: $%.2f\n",
							parcel.getParcelId(), parcel.getOwnerName(), parcel.getWeight(), parcel.getDimensions(),
							parcel.getDaysInDepot(), parcel.getStatus(), parcel.getCollectionFee()
					);
					writer.write(line);
				}
			} else {
				writer.write("No parcels currently stored.\n");
			}

			writer.write("------------------------------------------------------------\n");
			System.out.println("Depot details exported successfully to DepotDetails.txt.");
		} catch (IOException e) {
			System.out.println("An error occurred while writing to the file.");
			e.printStackTrace();
		}
	}

}