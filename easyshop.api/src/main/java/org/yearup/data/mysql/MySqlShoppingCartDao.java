package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MySqlShoppingCartDao implements ShoppingCartDao {

    private final DataSource dataSource;
    private final ProductDao productDao;

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        this.dataSource = dataSource;
        this.productDao = productDao;
    }

    @Override
    public boolean exists(int userId, int productId) {
        return false;
    }

    @Override
    public int getQuantity(int userId, int productId) {
        return 0;
    }

    @Override
    public void addItem(int userId, int productId, int quantity) {

    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity) {

    }

    @Override
    public void removeItem(int userId, int productId) {

    }

    @Override
    public void clearCart(int userId) {

    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        return getCart(userId);
    }

    @Override
    public ShoppingCart addProductToCart(int id, int productId) {
        return null;
    }

    @Override
    public void updateProductQuantity(int id, int productId, int quantity) {

    }

    @Override
    public ShoppingCartItem getCartItemByUserIdAndProductId(int id, int productId) {
        return null;
    }

    @Override
    public ShoppingCart getCart(int userId) {
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ?";
        ShoppingCart cart = new ShoppingCart();
        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");
                Product product = productDao.getById(productId);
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                items.put(productId, item);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cart.setItems(items);
        return cart;
    }

    @Override
    public ShoppingCart addProduct(int userId, int productId) {
        String sql = """
                    INSERT INTO shopping_cart (user_id, product_id, quantity)
                    VALUES (?, ?, 1);
                """;

        if (getCart(userId).getItems().containsKey(productId)) {
            return updateProduct(userId, productId, getCart(userId).getItems().get(productId).getQuantity() + 1);
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getCart(userId);
    }

    @Override
    public ShoppingCart updateProduct(int userId, int productId, int quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getCart(userId);
    }
}