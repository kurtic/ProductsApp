package com.Diachenko.mainAppMenu;

import com.Diachenko.authentication.UserAuthentication;
import com.Diachenko.dbConnection.Connector;
import com.Diachenko.tables.enums.OrdersStatuses;
import com.Diachenko.tables.enums.ProductsStatuses;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static com.Diachenko.services.OrderService.*;
import static com.Diachenko.services.ProductService.*;
import static com.Diachenko.tables.enums.OrdersStatuses.ORDERED;
import static com.Diachenko.tables.enums.ProductsStatuses.*;

public class MainMenu {
    private Connection connection;
    private Statement statement;
    private final UserAuthentication userAuthentication;
    private static final Scanner scanner = new Scanner(new InputStreamReader(System.in));

    public MainMenu() {
        userAuthentication = new UserAuthentication();
        createTables();
    }

    public void showMainMenu() {
        passwordChecking();
        try  {
            String menu = "Main application menu:\n" +
                    "1) Create product\n" +
                    "2) Create order\n" +
                    "3) Update order\n" +
                    "4) Show all products\n" +
                    "5) Show all orders\n" +
                    "6) Show ordered products\n" +
                    "7) Remove product\n" +
                    "8) Exit from the application";
            System.out.println(menu);
            switch (scanner.nextInt()) {
                case 1:
                    createProduct();
                    break;
                case 2:
                    createOrder();
                    break;
                case 3:
                    updateOrder();
                    break;
                case 4:
                    showAllProducts(connection);
                    mainMenuOrExit();
                    break;
                case 5:
                    showAllOrders(connection);
                    mainMenuOrExit();
                    break;
                case 6:
                    showAllOrderedProducts(connection);
                    mainMenuOrExit();
                    break;
                case 7:
                    removeMenu();
                case 8:
                    System.exit(0);
                default:
                    showMainMenu();
            }
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (
                    SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    public void removeMenu() {
        int value;
        int id;
        String password;
        System.out.println("For the removing data you need to enter your password:");
        password = scanner.next();
        if (userAuthentication.userMap.containsValue(password)) {
            System.out.println("If you want to remove only one product, enter - 1\n" +
                    "If you want to remove all products, enter - 2\n" +
                    "Enter - 3 if you want back to the main menu");
            value = scanner.nextInt();
            if (value == 1) {
                showAllProducts(connection);
                System.out.println("Enter product id which you want to remove:");
                id = scanner.nextInt();
                removeProductById(connection, id);
                showAllProducts(connection);
            } else if (value == 2) {
                removeAllProducts(connection);
                showAllProducts(connection);
            } else if (value == 3) {
                showMainMenu();
            } else {
                System.out.println("Wrong input!");
            }
        } else {
            System.out.println("Wrong password!You can't remove data");
            mainMenuOrExit();
        }
    }

    public void mainMenuOrExit() {
        System.out.println("If you want back to the main menu, enter - 1\n" +
                "If you want to exit from application, enter - 2");
        if (scanner.nextInt() == 1) {
            showMainMenu();
        } else {
            System.exit(0);
        }
    }

    public void updateOrder() {
        int id;
        int newQuantity;
        boolean updateOrderComleted = false;
        while (!updateOrderComleted) {
            showAllOrders(connection);
            System.out.println("Enter product id for change quantity:");
            id = scanner.nextInt();
            System.out.println("Enter new value of quantity:");
            newQuantity = scanner.nextInt();
            updateOrderById(connection, id, newQuantity);
            System.out.println("Your order was updated successfully!");
            showAllOrders(connection);
            System.out.println("Do you want to change another order?" +
                    "Yes-1,No-2");
            if (scanner.nextInt() == 2) {
                updateOrderComleted = true;
                showMainMenu();
            }
        }
    }

    public void createOrder() {
        int order_id = 0;
        int user_id;
        int product_id;
        int quantity;
        boolean orderCompleted = false;
        String password;
        OrdersStatuses status;
        System.out.println("Enter your user id:");
        user_id = scanner.nextInt();
        if (userAuthentication.userMap.containsKey(user_id)) {
            System.out.println("Please enter your password:");
            password = scanner.next();
            if (userAuthentication.userMap.containsValue(password)) {
                showAllProducts(connection);
                while (!orderCompleted) {
                    System.out.println("Enter product id that you want to order:");
                    product_id = scanner.nextInt();
                    System.out.println("Enter quantity:");
                    quantity = scanner.nextInt();
                    status = ORDERED;
                    createNewOrder(connection, userAuthentication.getUserId(), order_id, product_id, status, quantity);
                    System.out.println("Do you want to order something else?" +
                            "If yes, enter 1, if no, enter 2");
                    if (scanner.nextInt() == 2) {
                        orderCompleted = true;
                    }
                }
                showMainMenu();
            } else {
                System.out.println("Wrong password,try again");
            }
        } else {
            System.out.println("Wrong user id, try again");
        }


    }

    public void createTables() {
        try {
            connection = Connector.getConnection();
            statement = connection.createStatement();
            String orderTable = "CREATE TABLE IF NOT EXISTS orders "
                    + "(id INTEGER not NULL AUTO_INCREMENT,"
                    + "user_id INTEGER not NULL,"
                    + "status VARCHAR(20),"
                    + "created_at VARCHAR(20),"
                    + "PRIMARY KEY (id))";
            String productTable = "CREATE TABLE IF NOT EXISTS products "
                    + "(id INTEGER not NULL AUTO_INCREMENT,"
                    + "name VARCHAR(20),"
                    + "price INTEGER,"
                    + "status VARCHAR(20),"
                    + "created_at VARCHAR(20),"
                    + "PRIMARY KEY (id))";
            String orderItemsTable = "CREATE TABLE IF NOT EXISTS order_items "
                    + "(order_id INTEGER NOT NULL AUTO_INCREMENT, "
                    + "product_id INTEGER NOT NULL,"
                    + "quantity INTEGER, "
                    + "CONSTRAINT FK_orders "
                    + "FOREIGN  KEY (order_id) REFERENCES orders(id),"
                    + "CONSTRAINT FK_product "
                    + "FOREIGN  KEY (product_id) REFERENCES products(id))";
            statement.executeUpdate(productTable);
            statement.executeUpdate(orderTable);
            statement.executeUpdate(orderItemsTable);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createProduct() {
        boolean createProductCompleted = false;
        while (!createProductCompleted) {
            System.out.println("Enter the product name:");
            String name = scanner.next();
            System.out.println("Enter the product price");
            int price = scanner.nextInt();
            System.out.println("Select the status of the product:\n" +
                    "1) Out of stock\n" +
                    "2) In stock\n" +
                    "3) Running low");
            int enteredStatus = scanner.nextInt();
            ProductsStatuses status;
            if (enteredStatus == 1) {
                status = OUT_OF_STOCK;
            } else if (enteredStatus == 2) {
                status = IN_STOCK;
            } else if (enteredStatus == 3) {
                status = RUNNING_LOW;
            } else {
                status = OUT_OF_STOCK;
                System.out.println("Your number is incorrect." +
                        "So product status automatically set as out of stock");
            }
            createNewProduct(connection, name, price, status);
            System.out.println("Do you want create the next product?" +
                    "If yes, enter 1, " +
                    "if no, enter 2");
            if (scanner.nextInt() == 2) {
                createProductCompleted = true;
                showMainMenu();
            }
        }
    }

    public void passwordChecking() {
        if (!userAuthentication.isAuth())
            userAuthentication.authentication();
    }

}
