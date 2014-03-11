/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.phury.expenses;

import be.phury.expenses.model.Expense;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phury
 */
public class ExpensesDaoSqlite implements ExpensesDao{

    private static final String DBNAME = "expenses.db";
    private Gson gson = new Gson();
    
    public ExpensesDaoSqlite() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            logError("Unalble to load sqlite drivers", ex);
        }
        
        initializeDatabase();
    }
    
    private static void logError(String message, Throwable cause) {
        Logger.getLogger(ExpensesDaoSqlite.class.getName()).log(Level.SEVERE, message, cause);
    }
    
    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + DBNAME);
        } catch (SQLException ex) {
            logError("Unable to get an sqlite connnection", ex);
            throw new TechnicalException(ex);
        }
    }
    
    private void initializeDatabase() {
        final String table = executeWithPreparedStatment("SELECT name FROM sqlite_master WHERE type='table' AND name=?;", new Template<String>() {
            @Override
            public String execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException{
                stmt.setString(1, "JSON_ENTITY");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("name");
                } else {
                    return null;
                }
            }
        });
        
        if (table == null) {
            executeWithPreparedStatment("create table JSON_ENTITY("
                + "uuid char(36) primary key not null, "
                + "json text not null,"
                + "type text not null);", new Template<Boolean>() {
            @Override
            public Boolean execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException{
                stmt.executeUpdate();
                return true;
            }
        });
        }
    }
    
    @Override
    public Expense delete(final String uuid) {
        return executeWithPreparedStatment("delete from JSON_ENTITY where uuid = ?", new Template<Expense>() {
            @Override
            public Expense execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException{
                final Expense expense = dao.findById(uuid);
                stmt.setString(1, uuid);
                stmt.executeUpdate();
                return expense;
            }
        });
    }

    @Override
    public List<Expense> findAll() {
        return executeWithPreparedStatment("select * from JSON_ENTITY;", new Template<List<Expense>>() {
            @Override
            public List<Expense> execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException{
                ResultSet rs = stmt.executeQuery();
                List<Expense> result = new ArrayList<>();
                while ( rs.next() ) {
                    result.add(new ExpenseBuilder().fromResultSet(rs));
                }
                return result;
            }
        });
    }

    @Override
    public Expense findById(final String uuid) {
        return executeWithPreparedStatment("select * from JSON_ENTITY where uuid = ?;", new Template<Expense>() {
            @Override
            public Expense execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException{
                stmt.setString(1, uuid);
                return new ExpenseBuilder().fromResultSet(stmt.executeQuery());
            }
        });
    }

    @Override
    public List<Expense> findByType(String... types) {
        return findByType(Arrays.asList(types));
    }

    @Override
    public List<Expense> findByType(List<String> types) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expense save(final Expense expense) {
        return executeWithPreparedStatment("insert into JSON_ENTITY (uuid, type, json) values (?, ?, ?);", new Template<Expense>() {
            @Override
            public Expense execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException{
                stmt.setString(1, expense.getUuid());
                stmt.setString(2, Expense.class.getSimpleName());
                stmt.setString(3, gson.toJson(expense));
                stmt.executeUpdate();
                return expense;
            }
        });
    }
    
    private <T> T executeWithPreparedStatment(String sql, Template<T> callback) {
        try (Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql)) {
            T result = callback.execute(this, stmt);
            //con.commit();
            return result;
        } catch (SQLException ex) {
            //logError("Unable to delete expense with id " + uuid, ex);
            throw new TechnicalException(ex);
        }
    }
    
    static interface Template<T> {
        T execute(ExpensesDao dao, PreparedStatement stmt) throws SQLException;
    }
    
    static class ExpenseBuilder {
        private Gson gson = new Gson();
        
        public Expense fromResultSet(ResultSet rs) {
            try {
                return gson.fromJson(rs.getString("json"), Expense.class);
            } catch (SQLException ex) {
                ExpensesDaoSqlite.logError("unable to convert from json data", ex);
                throw new TechnicalException(ex);
            }
        }
    }
}
