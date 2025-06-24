package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery())
        {
            while (row.next())
            {
                categories.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving categories", e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, categoryId);
            ResultSet row = statement.executeQuery();

            if (row.next())
            {
                return mapRow(row);
            }
        }
        catch (SQLException e)
        {
            // Log error instead of throwing, so controller can handle null gracefully
            System.err.println("Error retrieving category by ID: " + e.getMessage());
            // Optionally log stacktrace e.printStackTrace();
        }

        // Return null if not found or error, so controller can return 404 or handle error properly
        return null;
    }


    @Override
    public Category create(Category category)
    {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
            {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next())
                {
                    int newId = keys.getInt(1);
                    return getById(newId); // fetch newly inserted category
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating category", e);
        }

        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId)
    {

        String deleteCategorySql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection())
        {
            // Disable auto-commit for transaction control
            connection.setAutoCommit(false);

            try (PreparedStatement deleteCategoryStmt = connection.prepareStatement(deleteCategorySql))
            {
                // Then delete the category itself
                deleteCategoryStmt.setInt(1, categoryId);
                int rowsDeleted = deleteCategoryStmt.executeUpdate();

                if (rowsDeleted == 0) {
                    connection.rollback();
                    throw new RuntimeException("No category found with id: " + categoryId);
                }

                // Commit transaction
                connection.commit();
            }
            catch (SQLException e)
            {
                // Rollback if any exception happens during deletion
                connection.rollback();
                throw new RuntimeException("Error deleting category and related products", e);
            }
            finally
            {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Database error during delete operation", e);
        }
    }


    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        return new Category(categoryId, name, description);
    }
}
