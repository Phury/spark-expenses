/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.phury.expenses;

import be.phury.expenses.model.Expense;
import java.util.List;

/**
 *
 * @author phury
 */
public interface ExpensesDao {

    Expense delete(String uuid);

    List<Expense> findAll();

    Expense findById(String uuid);

    List<Expense> findByType(String... types);

    List<Expense> findByType(List<String> types);

    Expense save(Expense expense);
    
}
