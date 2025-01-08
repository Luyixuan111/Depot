import java.util.Random;

public class Parcel {
	private String parcelId;
	private double weight;
	private double length;
	private double width;
	private double height;
	private int daysInDepot;
	private String dimensions;
	private String status; // "picked up" or "in depot"
	private double collectionFee;
	private String ownerName; // Owner's full name

	// Default Constructor
	public Parcel() {
		this.parcelId = generateParcelId();
		this.weight = 0.0;
		this.length = 0.0;
		this.width = 0.0;
		this.height = 0.0;
		this.daysInDepot = 0;
		this.dimensions = "";
		this.status = "in depot"; // Default status
		this.collectionFee = 0.0;
		this.ownerName = ""; // Initialize with no owner
	}

	// Parameterized Constructor
	public Parcel(double weight, double length, double width, double height, int daysInDepot, String ownerName) {
		this.parcelId = generateParcelId(); // Generate random parcelId
		this.weight = weight;
		this.length = length;
		this.width = width;
		this.height = height;
		this.daysInDepot = daysInDepot;
		this.dimensions = length + "x" + width + "x" + height;
		this.status = "in depot"; // Default status
		this.collectionFee = calculateCollectionFee();
		this.ownerName = ownerName; // Set owner's name
	}

	// Generate a random parcel ID (e.g., "PX1234")
	public static String generateParcelId() {
		Random random = new Random();
		int number = 1000 + random.nextInt(9000); // Generates a random 4-digit number
		char letter = (char) (random.nextInt(26) + 'A'); // Random uppercase letter
		return "P" + letter + number;
	}

	// Getters
	public String getParcelId() {
		return parcelId;
	}

	public double getWeight() {
		return weight;
	}

	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public int getDaysInDepot() {
		return daysInDepot;
	}

	public String getDimensions() {
		return dimensions;
	}

	public String getStatus() {
		return status;
	}

	public double getCollectionFee() {
		return collectionFee;
	}

	public String getOwnerName() {
		return ownerName;
	}

	// Setters
	public void setWeight(double weight) {
		this.weight = weight;
		updateCollectionFee();
	}

	public void setLength(double length) {
		this.length = length;
		updateDimensions();
	}

	public void setWidth(double width) {
		this.width = width;
		updateDimensions();
	}

	public void setHeight(double height) {
		this.height = height;
		updateDimensions();
	}

	public void setDaysInDepot(int daysInDepot) {
		this.daysInDepot = daysInDepot;
		updateCollectionFee();
	}

	public void setStatus(String status) {
		if (status.equals("picked up") || status.equals("in depot")) {
			this.status = status;
		} else {
			System.out.println("Invalid status. Use 'picked up' or 'in depot'.");
		}
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	// Private methods to update dimensions and collection fee
	private void updateDimensions() {
		this.dimensions = length + "x" + width + "x" + height;
	}

	private double calculateCollectionFee() {
		return (weight * 0.5) + (daysInDepot * 0.2);
	}

	public void updateCollectionFee() {
		this.collectionFee = calculateCollectionFee();
	}

	@Override
	public String toString() {
		return "Parcel ID: " + parcelId +
				", Owner: " + ownerName +
				", Weight: " + weight + "kg" +
				", Dimensions: " + dimensions +
				", Days in Depot: " + daysInDepot +
				", Status: " + status +
				", Collection Fee: $" + String.format("%.2f", collectionFee);
	}
}
