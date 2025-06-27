package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao {

    boolean exists(int userId, int productId);
    int getQuantity(int userId, int productId);
    void addItem(int userId, int productId, int quantity);
    void updateQuantity(int userId, int productId, int quantity);
    void removeItem(int userId, int productId);  // optional for PUT with quantity = 0
    void clearCart(int userId);

    ShoppingCart getByUserId(int userId);

    ShoppingCart addProductToCart(int id, int productId);

    void updateProductQuantity(int id, int productId, int quantity);

    ShoppingCartItem getCartItemByUserIdAndProductId(int id, int productId);

    ShoppingCart getCart(int userId);

    ShoppingCart addProduct(int userId, int productId);

    ShoppingCart updateProduct(int userId, int productId, int quantity);

    // add additional method signatures here
}
