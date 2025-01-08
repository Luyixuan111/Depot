import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;

public class Interface {
	private Depot depot;
	private boolean isStaff;

	public Interface() {
		this.depot = new Depot();
		customerQueue = new CustomerQueue();

	}
	private CustomerQueue customerQueue;


	private int printMenuAndGetChoice(Scanner scannerObj) {
		int choice = -1;
		while (choice < 0 || choice > 8) {
			System.out.println("\nMenu");
			System.out.println("--------------");

			if (isStaff) {
				System.out.println("1. Add a product to the depot");
				System.out.println("2. Update an existing product in the depot");
				System.out.println("3. Mark a product as picked up");
				System.out.println("4. Remove a product from the depot");
				System.out.println("5. Display list of products in the depot");
				System.out.println("6. Check if a product exists in the depot");
				System.out.println("7. Cumulative value of all products in the depot");
				System.out.println("8. Export depot and product information to a text file");
				System.out.println("0. EXIT");
				System.out.print("\t\tCHOICE: ");
				choice = scannerObj.nextInt();
			} else {
				System.out.println("1. Add a product to the depot");
				System.out.println("2. Mark a product as picked up");
				System.out.println("0. EXIT");
				System.out.print("\t\tCHOICE: ");
				choice = scannerObj.nextInt();
				if (choice > 2) {
					System.out.println("Invalid choice for customer role.");
					choice = -1;
				}
			}
		}
		return choice;
	}

	private void addProductFromUrl(double weight, double length, double width, double height, int daysInDepot, String ownerName) {
		depot.addProduct(weight, length, width, height, daysInDepot, ownerName);
		System.out.println("Product added successfully to the depot.");
	}


	private void addProduct(Scanner scannerObj) {
		System.out.print("\nWeight (kg): ");
		double weight = scannerObj.nextDouble();
		System.out.print("Length (cm): ");
		double length = scannerObj.nextDouble();
		System.out.print("Width (cm): ");
		double width = scannerObj.nextDouble();
		System.out.print("Height (cm): ");
		double height = scannerObj.nextDouble();
		System.out.print("Days in Depot: ");
		int daysInDepot = scannerObj.nextInt();
		scannerObj.nextLine();
		System.out.print("Owner's Name: ");
		String ownerName = scannerObj.nextLine();

		Parcel newParcel = new Parcel(weight, length, width, height, daysInDepot, ownerName);
		depot.addProduct(weight, length, width, height, daysInDepot, ownerName);
		System.out.println("Product added successfully to the depot.");
		printDepotToFile();
	}




	private void cumulativeValueOfDepot() {
		double totalValue = depot.calculateCumulativeValue();
		System.out.printf("Total collection fees of all products: $%.2f\n", totalValue);
	}

	private void printDepotToFile() {
		depot.printDepotToFile();
	}


	private void authenticateUser(Scanner scannerObj) {
		System.out.print("Enter role (staff/customer): ");
		String role = scannerObj.next().toLowerCase();
		isStaff = role.equals("staff");
		if (!isStaff && !role.equals("customer")) {
			System.out.println("Invalid role. Defaulting to customer role.");
			isStaff = false;
		}
	}

	private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
		Map<String, String> map = new HashMap<>();
		String[] pairs = formData.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			map.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return map;
	}

	private void startHttpServer() {
		try {
			HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8000), 0);
			server.createContext("/depot", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String htmlResponse = "<html>" +
							"<head><title>depot Page</title></head>" +
							"<body>" +
							"<h1>Welcome to the Depot System</h1>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/customer';\">Customers</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Staff</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/manager';\">Manager</button>" +
							"</body>" +
							"</html>";
					t.sendResponseHeaders(200, htmlResponse.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(htmlResponse.getBytes());
					os.close();
				}
			});

			server.createContext("/manager", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String htmlResponse = "<html>" +
							"<head><title>Manager Menu</title></head>" +
							"<body>" +
							"<h1>Manager Menu</h1>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/calculate_fees';\">Calculate Fees</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/customer_queue';\">Customer Queue</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/display_parcels';\">Display Parcels</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/delete_customer_form';\">Delete Customer</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/depot';\">Go Back</button>" +
							"</body>" +
							"</html>";
					t.sendResponseHeaders(200, htmlResponse.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(htmlResponse.getBytes());
					os.close();
				}
			});

			server.createContext("/delete_customer_form", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String formHtml = "<html><head><title>Delete Customer</title></head><body>" +
							"<h1>Delete a Customer from Queue</h1>" +
							"<form action='/delete_customer' method='post'>" +
							"Customer Name: <input type='text' name='fullName'><br>" +
							"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Delete Customer'>" +
							"</form>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/manager';\">Back to Manager</button>" +
							"</body></html>";
					t.sendResponseHeaders(200, formHtml.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(formHtml.getBytes());
					os.close();
				}
			});

			server.createContext("/delete_customer", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("POST".equals(t.getRequestMethod())) {
						InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String query = br.readLine();
						Map<String, String> formData = parseFormData(query);
						String fullName = formData.get("fullName");

						synchronized (customerQueue) {
							if (customerQueue.contains(fullName)) {
								customerQueue.remove(fullName);
								Log.getInstance().addEvent("Customer removed from queue: " + fullName);
								String response = "<html><head><script>alert('Customer removed successfully.');window.location='/manager';</script></head><body></body></html>";
								t.sendResponseHeaders(200, response.getBytes().length);
								OutputStream os = t.getResponseBody();
								os.write(response.getBytes());
							} else {
								String response = "<html><head><script>alert('Customer not found.');window.location='/delete_customer_form';</script></head><body></body></html>";
								t.sendResponseHeaders(200, response.getBytes().length);
								OutputStream os = t.getResponseBody();
								os.write(response.getBytes());
							}
						}
						t.close();
					}
				}
			});



			server.createContext("/display_parcels", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String response = "<html><head><title>Parcel List</title></head><body>";
					response += "<h1>Parcel List</h1>";
					response += depot.displayProductsForUrl();
					response += "<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/manager';\">Back to Manager Menu</button>";
					response += "</body></html>";

					t.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}
			});

			server.createContext("/customer_queue", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String response = "<html><head><title>Customer Queue</title></head><body>";
					response += "<h1>Customer Queue</h1>";
					response += customerQueue.displayQueue();
					response += "<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/manager';\">Back to Manager Menu</button>";
					response += "</body></html>";

					t.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}
			});

			server.createContext("/calculate_fees", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					double totalFees = depot.calculateCumulativeValue();
					String response = "<html><head><title>Calculate Fees</title></head><body>";
					response += "<h1>Total Fees Calculated</h1>";
					response += "<p>Total Fees: $" + totalFees + "</p>";
					response += "<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/manager';\">Back to Manager Menu</button>";
					response += "</body></html>";

					t.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}
			});





			server.createContext("/display_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String response = "<html><head><title>Product List</title></head><body>";
					response += "<h1>Product List</h1>";
					response += depot.displayProductsForUrl();
					response += "<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Back to Main</button>";
					response += "</body></html>";

					t.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}
			});


			server.createContext("/customer_menu", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String htmlResponse = "<html>" +
							"<head><title>Customer Menu</title></head>" +
							"<body>" +
							"<h1>Customer Menu</h1>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/cus_add_product';\">Add Product</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/cus_pickup_product';\">Mark Product Picked Up</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/depot';\">EXIT</button>" +
							"</body>" +
							"</html>";
					t.sendResponseHeaders(200, htmlResponse.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(htmlResponse.getBytes());
					os.close();
				}
			});

			server.createContext("/add_customer_to_queue", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("POST".equals(t.getRequestMethod())) {
						InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String query = br.readLine();
						Map<String, String> formData = parseFormData(query);
						String fullName = formData.get("fullName");

						synchronized (customerQueue) {
							if (!customerQueue.contains(fullName)) {
								customerQueue.enqueue(new Customer(fullName));
								Log.getInstance().addEvent("Customer added to queue: " + fullName);
								alertAndRedirect(t, "You have been added to the queue.");
							} else {
								alertAndRedirect(t, "You are already in the queue.");
							}
						}
					}
				}

				private void alertAndRedirect(HttpExchange t, String message) throws IOException {
					String response = "<html>" +
							"<head><script type='text/javascript'>alert('" + message + "'); window.location='/customer_menu';</script></head>" +
							"<body></body></html>";
					t.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}


				private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
					Map<String, String> map = new HashMap<>();
					String[] pairs = formData.split("&");
					for (String pair : pairs) {
						int idx = pair.indexOf("=");
						map.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
					}
					return map;
				}
			});

			server.createContext("/customer", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						String formHtml = "<html>" +
								"<head><title>Customer Menu</title></head>" +
								"<body>" +
								"<h1>Enter your full name to access the Customer Menu</h1>" +
								"<form action='/add_customer_to_queue' method='post'>" +
								"Full Name: <input type='text' name='fullName'><br>" +
								"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
								"</form>" +
								"</body>" +
								"</html>";
						t.sendResponseHeaders(200, formHtml.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(formHtml.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/add_product", (HttpExchange t) -> {
				String formHtml = "<html>" +
						"<head><title>Add Product</title></head>" +
						"<body>" +
						"<h1>Add a Product to the Depot</h1>" +
						"<form action='/submit_product' method='post'>" +
						"Weight (kg): <input type='text' name='weight'><br>" +
						"Length (cm): <input type='text' name='length'><br>" +
						"Width (cm): <input type='text' name='width'><br>" +
						"Height (cm): <input type='text' name='height'><br>" +
						"Days in Depot: <input type='text' name='daysInDepot'><br>" +
						"Owner's Name: <input type='text' name='ownerName'><br>" +
				"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
						"</form>" +
						"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;'  onclick=\"window.location.href='/staff';\">Go Back</button>" +
						"</body>" +
						"</html>";
				t.sendResponseHeaders(200, formHtml.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(formHtml.getBytes());
				os.close();
			});

			server.createContext("/cus_add_product", (HttpExchange t) -> {
				String formHtml = "<html>" +
						"<head><title>Add Product</title></head>" +
						"<body>" +
						"<h1>Add a Product to the Depot</h1>" +
						"<form action='/submit_product' method='post'>" +
						"Weight (kg): <input type='text' name='weight'><br>" +
						"Length (cm): <input type='text' name='length'><br>" +
						"Width (cm): <input type='text' name='width'><br>" +
						"Height (cm): <input type='text' name='height'><br>" +
						"Days in Depot: <input type='text' name='daysInDepot'><br>" +
						"Owner's Name: <input type='text' name='ownerName'><br>" +
						"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
						"</form>" +
						"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;'  onclick=\"window.location.href='/customer_menu';\">Go Back</button>" +
						"</body>" +
						"</html>";
				t.sendResponseHeaders(200, formHtml.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(formHtml.getBytes());
				os.close();
			});

			server.createContext("/submit_product", new HttpHandler() {
				public void handle(HttpExchange t) {
					try {
						if ("POST".equals(t.getRequestMethod())) {
							InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
							BufferedReader br = new BufferedReader(isr);
							String query = br.readLine();
							Map<String, String> formData = parseFormData(query);

							double weight = Double.parseDouble(formData.get("weight"));
							double length = Double.parseDouble(formData.get("length"));
							double width = Double.parseDouble(formData.get("width"));
							double height = Double.parseDouble(formData.get("height"));
							int daysInDepot = Integer.parseInt(formData.get("daysInDepot"));
							String ownerName = formData.get("ownerName");

							depot.addProduct(weight, length, width, height, daysInDepot, ownerName);

							String response = "Product added successfully!";
							t.sendResponseHeaders(200, response.getBytes().length);
							OutputStream os = t.getResponseBody();
							os.write(response.getBytes());
							os.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
						try {
							t.sendResponseHeaders(500, 0);
							OutputStream os = t.getResponseBody();
							os.write(("Failed due to: " + e.getMessage()).getBytes());
							os.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});



			server.createContext("/update_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						String formHtml = "<html>" +
								"<head><title>Update Product</title></head>" +
								"<body>" +
								"<h1>Update a Product</h1>" +
								"<form action='/submit_update' method='post'>" +
								"Parcel ID: <input type='text' name='parcelId'><br>" +
								"Weight (kg): <input type='text' name='weight'><br>" +
								"Length (cm): <input type='text' name='length'><br>" +
								"Width (cm): <input type='text' name='width'><br>" +
								"Height (cm): <input type='text' name='height'><br>" +
								"Days in Depot: <input type='text' name='daysInDepot'><br>" +
								"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
								"</form>" +
								"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
								"</body>" +
								"</html>";
						t.sendResponseHeaders(200, formHtml.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(formHtml.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/submit_update", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("POST".equals(t.getRequestMethod())) {
						InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String query = br.readLine();
						Map<String, String> formData = parseFormData(query);

						String parcelId = formData.get("parcelId");
						double weight = Double.parseDouble(formData.get("weight"));
						double length = Double.parseDouble(formData.get("length"));
						double width = Double.parseDouble(formData.get("width"));
						double height = Double.parseDouble(formData.get("height"));
						int daysInDepot = Integer.parseInt(formData.get("daysInDepot"));

						depot.updateProductFromUrl(parcelId, weight, length, width, height, daysInDepot);

						String response = "<html><head><script type='text/javascript'>alert('Product updated successfully!'); window.history.go(-1);</script></head><body></body></html>";
						t.sendResponseHeaders(200, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					} else {
						String response = "Invalid request method!";
						t.sendResponseHeaders(405, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}
				}
			});


			server.createContext("/pickup_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						String formHtml = "<html>" +
								"<head><title>Pick Up Product</title></head>" +
								"<body>" +
								"<h1>Pick Up a Product</h1>" +
								"<form action='/submit_pickup' method='post'>" +
								"Parcel ID: <input type='text' name='parcelId'><br>" +
								"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
								"</form>" +
								"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
								"</body>" +
								"</html>";
						t.sendResponseHeaders(200, formHtml.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(formHtml.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/cus_pickup_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						String formHtml = "<html>" +
								"<head><title>Pick Up Product</title></head>" +
								"<body>" +
								"<h1>Pick Up a Product</h1>" +
								"<form action='/submit_pickup' method='post'>" +
								"Parcel ID: <input type='text' name='parcelId'><br>" +
								"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
								"</form>" +
								"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/customer_menu';\">Go Back</button>" +
								"</body>" +
								"</html>";
						t.sendResponseHeaders(200, formHtml.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(formHtml.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/submit_pickup", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("POST".equals(t.getRequestMethod())) {
						InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String query = br.readLine();
						Map<String, String> formData = parseFormData(query);

						String parcelId = formData.get("parcelId");

						depot.markProductAsPickedUpFromUrl(parcelId);

						t.getResponseHeaders().set("Location", "/staff");
						t.sendResponseHeaders(303, -1);
						t.close();
					} else {
						String response = "Invalid request method!";
						t.sendResponseHeaders(405, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/remove_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						String formHtml = "<html>" +
								"<head><title>Remove Product</title></head>" +
								"<body>" +
								"<h1>Remove a Product</h1>" +
								"<form action='/submit_remove' method='post'>" +
								"Parcel ID: <input type='text' name='parcelId'><br>" +
								"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
								"</form>" +
								"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
								"</body>" +
								"</html>";
						t.sendResponseHeaders(200, formHtml.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(formHtml.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/submit_remove", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("POST".equals(t.getRequestMethod())) {
						InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String query = br.readLine();
						Map<String, String> formData = parseFormData(query);

						String parcelId = formData.get("parcelId");

						depot.removeProduct(parcelId);

						t.getResponseHeaders().set("Location", "/staff");
						t.sendResponseHeaders(303, -1);
						t.close();
					} else {
						String response = "Invalid request method!";
						t.sendResponseHeaders(405, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}
				}
			});


			server.createContext("/check_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						String formHtml = "<html>" +
								"<head><title>Check Product Existence</title></head>" +
								"<body>" +
								"<h1>Check if a Product Exists</h1>" +
								"<form action='/submit_check' method='post'>" +
								"Parcel ID: <input type='text' name='parcelId'><br>" +
								"<input style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' type='submit' value='Submit'>" +
								"</form>" +
								"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
								"</body>" +
								"</html>";
						t.sendResponseHeaders(200, formHtml.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(formHtml.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/submit_check", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("POST".equals(t.getRequestMethod())) {
						InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String query = br.readLine();
						Map<String, String> formData = parseFormData(query);

						String parcelId = formData.get("parcelId");
						boolean exists = depot.isProductPresentFromUrl(parcelId);

						String responseMessage = exists ? "Product with Parcel ID " + parcelId + " exists in the depot." : "Product with Parcel ID " + parcelId + " does not exist in the depot.";
						String response = "<html><head><script type='text/javascript'>alert('" + responseMessage + "'); window.location='/staff';</script></head><body></body></html>";

						t.sendResponseHeaders(200, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					} else {
						String response = "Invalid request method!";
						t.sendResponseHeaders(405, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/value_product", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						double totalValue = depot.calculateCumulativeValue();
						String pageContent = "<html>" +
								"<head><title>Cumulative Value</title></head>" +
								"<body>" +
								"<h1>Cumulative Value of Products</h1>" +
								"<p>The total cumulative value of products currently in the depot is: $" + String.format("%.2f", totalValue) + "</p>" +
								"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
								"</body>" +
								"</html>";

						t.sendResponseHeaders(200, pageContent.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(pageContent.getBytes());
						os.close();
					}
				}
			});

			server.createContext("/download_file", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					File file = new File("DepotDetails.txt");
					if (file.exists()) {
						t.sendResponseHeaders(200, file.length());
						OutputStream os = t.getResponseBody();
						Files.copy(file.toPath(), os);
						os.close();
					} else {
						String response = "File not found.";
						t.sendResponseHeaders(404, response.getBytes().length);
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}
				}
			});


			server.createContext("/export_data", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					if ("GET".equals(t.getRequestMethod())) {
						try {
							depot.printDepotToFile();
							String response = "<html><head><title>Export Successful</title></head><body>" +
									"<h1>Export Successful</h1>" +
									"<p>The depot details have been successfully exported to 'DepotDetails.txt'.</p>" +
									"<a href=\"/download_file\" download=\"DepotDetails.txt\">Download File</a>\n" +
									"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
									"</body></html>";
							t.sendResponseHeaders(200, response.getBytes().length);
							OutputStream os = t.getResponseBody();
							os.write(response.getBytes());
							os.close();
						} catch (Exception e) {
							String response = "<html><head><title>Error</title></head><body>" +
									"<h1>Error</h1>" +
									"<p>There was an error while exporting the data: " + e.getMessage() + "</p>" +
									"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/staff';\">Go Back</button>" +
									"</body></html>";
							t.sendResponseHeaders(500, response.getBytes().length);
							OutputStream os = t.getResponseBody();
							os.write(response.getBytes());
							os.close();
						}
					}
				}
			});


			server.createContext("/staff", new HttpHandler() {
				public void handle(HttpExchange t) throws IOException {
					String htmlResponse = "<html>" +
							"<head><title>Staff Menu</title></head>" +
							"<body>" +
							"<h1>Staff Menu</h1>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/add_product';\">Add Product</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/update_product';\">Update Product</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/pickup_product';\">Mark Product Picked Up</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/remove_product';\">Remove Product</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/display_product';\">Display Products</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/check_product';\">Check Product Existence</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/value_product';\">Cumulative Value</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/export_data';\">Export Data</button>" +
							"<button style='background-color: black; color: white; font-size: 20px; padding: 10px 20px;' onclick=\"window.location.href='/depot';\">EXIT</button>" +
							"</body>" +
							"</html>";
					t.sendResponseHeaders(200, htmlResponse.getBytes().length);
					OutputStream os = t.getResponseBody();
					os.write(htmlResponse.getBytes());
					os.close();
				}
			});

			server.start();
			System.out.println("HTTP Server started on port 8000");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Interface intFace = new Interface();
		intFace.startHttpServer();

//		intFace.run();
	}

}