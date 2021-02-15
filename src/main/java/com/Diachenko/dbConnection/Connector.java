package com.Diachenko.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Connector {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", "root");
        connectionProperties.put("password", "jdbc.root");

        ResourceBundle resourceBundle = ResourceBundle.getBundle("local");
        String url = resourceBundle.getString("jdbc.url");
        String username = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        String driver = resourceBundle.getString("jdbc.driver");
        Class.forName(driver);

        return DriverManager.getConnection(url,username,password);
    }
}
