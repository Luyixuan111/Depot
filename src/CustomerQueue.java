import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

public class CustomerQueue {
    private Queue<Customer> queue;

    public CustomerQueue() {
        this.queue = new LinkedList<>();
    }

    public void enqueue(Customer customer) {
        queue.add(customer);
    }

    public String displayQueue() {
        StringBuilder htmlOutput = new StringBuilder("<ul>");
        for (Customer customer : queue) {
            htmlOutput.append("<li>").append(customer.getFullName()).append("</li>");
        }
        htmlOutput.append("</ul>");
        if (queue.isEmpty()) {
            return "<p>No customers in the queue.</p>";
        }
        return htmlOutput.toString();
    }

    public Customer dequeue() {
        return queue.poll();
    }

    public Customer peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    // Checks if a customer with the given full name is already in the queue
    public boolean contains(String fullName) {
        for (Customer customer : queue) {
            if (customer.getFullName().equals(fullName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "CustomerQueue{" +
                "queue=" + queue +
                '}';
    }
}
