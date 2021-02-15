package com.Diachenko.services;

import com.Diachenko.tables.enums.ProductsStatuses;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ProductService {
    public static void createNewProduct(Connection connection, String name, int price, ProductsStatuses status){
        LocalDateTime created_at = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        String query = "INSERT INTO products (name, price, status, created_at) VALUES (?,?,?,?);";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,name);
            statement.setInt(2,price);
            statement.setString(3, String.valueOf(status));
            statement.setObject(4,created_at);
            statement.executeUpdate();
            System.out.println("The product was created successfully");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void showAllProducts(Connection connection){
        String query = "SELECT * FROM products ORDER BY id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)){
            System.out.printf("%3s %20s %20s %20s%n", "Id", "Product Name", "Product Price", "Product Status");
            System.out.printf("%s%n", "------------------------------------------------------------------");
            while(resultSet.next()){
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int price = resultSet.getInt(3);
                String status = resultSet.getString(4);
                System.out.printf("%3s %20s %20s %20s%n", id, name, price,status);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void showAllOrderedProducts(Connection connection){
        int order_id;
        String name;
        String status;
        int quantity;
        int price;
        int product_id;
        String created_at;
        String query ="SELECT order_id, name, status, quantity, price, product_id,created_at from products left join order_items oi on products.id = oi.product_id where order_id >= 1 order by order_id;";
        System.out.printf("%5s %20s %25s %25s %20s %20s %25s%n", "Order Id", "Name", "Status", "Products Quantity", "Price","Product Id", "Created Date");
        System.out.printf("%s%n", "-----------------------------------------------------------------------------------------------------------------------------------------------------");
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()){
            order_id = resultSet.getInt(1);
            name = resultSet.getString(2);
            status = resultSet.getString(3);
            quantity = resultSet.getInt(4);
            price = resultSet.getInt(5);
            product_id = resultSet.getInt(6);
            created_at = resultSet.getString(7);
                System.out.printf("%5s %23s %27s %18s %24s %18s %28s%n", order_id, name, status, quantity, price, product_id,created_at);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void removeProductById(Connection connection,int productId){
        String query = "DELETE order_items,products FROM order_items LEFT JOIN products on product_id=id and product_id=?;";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,productId);
            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void removeAllProducts(Connection connection){
        String queryForOrderItems = "DELETE FROM order_items;";
        String queryForProducts = "DELETE FROM products;";
        try(PreparedStatement statement = connection.prepareStatement(queryForOrderItems);
            PreparedStatement productsStatement = connection.prepareStatement(queryForProducts)){
            statement.executeUpdate();
            productsStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
