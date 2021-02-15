package com.Diachenko.services;

import com.Diachenko.tables.enums.OrdersStatuses;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OrderService {
    public static void createNewOrder(Connection connection, int userId, int orderId, int productId, OrdersStatuses status, int quantity) {
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        String query = "INSERT INTO orders (user_id,status,created_at) values (?,?,?)";
        String queryToOrderItems = "INSERT INTO Order_items (product_id, quantity) VALUES (?,?)";
        try (PreparedStatement statementForOrders = connection.prepareStatement(query)) {
            statementForOrders.setInt(1, userId);
            statementForOrders.setString(2, status.toString());
            statementForOrders.setObject(3, localDateTime);
            statementForOrders.executeUpdate();
            try (PreparedStatement statementForOrderItems = connection.prepareStatement(queryToOrderItems)) {
                statementForOrderItems.setInt(1, productId);
                statementForOrderItems.setInt(2, quantity);
                statementForOrderItems.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void showAllOrders(Connection connection) {
        int orderId;
        int totalPrice;
        String productName;
        int productQuantity;
        String query ="SELECT order_id, quantity*price, name, quantity FROM order_items, products order by quantity;";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)){
            System.out.printf("%10s %25s %20s %20s%n", "Order Id", "Products total price", "Product Name", "Products Quantity");
            System.out.printf("%s%n", "------------------------------------------------------------------------------");
            while(resultSet.next()){
                orderId = resultSet.getInt(1);
                totalPrice = resultSet.getInt(2);
                productName = resultSet.getString(3);
                productQuantity = resultSet.getInt(4);
                System.out.printf("%10s %25s %20s %20s%n", orderId, totalPrice, productName, productQuantity);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void updateOrderById(Connection connection, int id, int newQuantity){
        String query = "UPDATE order_items set quantity = ? WHERE product_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,newQuantity);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
