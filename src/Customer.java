public class Customer {
    private String fullName; // Private variable to store the full name of the customer

    // Constructor to initialize the Customer object with a full name
    public Customer(String fullName) {
        this.fullName = fullName;
    }

    // Getter method to retrieve the customer's full name
    public String getFullName() {
        return this.fullName;
    }

    // Setter method to set the customer's full name
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Override the toString method to provide a string representation of the Customer object
    @Override
    public String toString() {
        return "Customer{" +
                "fullName='" + fullName + '\'' +
                '}';
    }
}
