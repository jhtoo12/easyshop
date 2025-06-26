package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;

@Repository
public class MySqlShoppingCartDao implements ShoppingCartDao {

    private DataSource dataSource;

    public MySqlShoppingCartDao(DataSource dataSource) {
        this.dataSource = dataSource;
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
        return null;
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
}
