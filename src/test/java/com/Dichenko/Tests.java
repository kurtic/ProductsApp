package com.Dichenko;

import com.Diachenko.authentication.UserAuthentication;
import com.Diachenko.dbConnection.Connector;
import com.Diachenko.mainAppMenu.MainMenu;
import com.Diachenko.services.OrderService;
import com.Diachenko.tables.enums.ProductsStatuses;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import static com.Diachenko.services.ProductService.createNewProduct;
import static com.Diachenko.services.ProductService.showAllOrderedProducts;

public class Tests {

    private Connection connection;
    private final UserAuthentication userAuthentication = Mockito.mock(UserAuthentication.class);
    private MainMenu mainMenu = Mockito.mock(MainMenu.class);


    @Before
    public void connection() throws SQLException, ClassNotFoundException {
        connection = Connector.getConnection();

    }

    @Test
    public void testForConnection() {
        try {
            Connector.getConnection();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void authTest() {
       try( ByteArrayInputStream in = new ByteArrayInputStream("Password".getBytes())) {
           System.setIn(in);
           userAuthentication.authentication();
           Mockito.verify(userAuthentication).authentication();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    @Test
    public void createProductTest() {
       createNewProduct(connection,"banana",32,ProductsStatuses.OUT_OF_STOCK);
    }

    @Test
    public void showAllOrderedProductsTest(){
        showAllOrderedProducts(connection);
    }

    @Test
    public void showAllOrdersTest(){
        OrderService.showAllOrders(connection);
    }

    @Test
    public void showMainMenuTest() {
        mainMenu.showMainMenu();
        Mockito.verify(mainMenu).showMainMenu();
    }

    @Test
    public void createTablesTest() {
        mainMenu.createTables();
        Mockito.verify(mainMenu).createTables();
    }

}
