/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.phury.expenses;

import be.phury.expenses.model.Expense;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phury
 */
public class ExpensesDaoFileSystem implements ExpensesDao {
    private static final String FILENAME = "expenses_fs.db";

    private Map<String, Expense> map = new HashMap<>();
    
    public ExpensesDaoFileSystem() {
        load();
    }
    
    private void load() {
        File file = new File(FILENAME);
        if (file.exists()) {
            ObjectInputStream s = null;
            try {
                FileInputStream f = new FileInputStream(file);
                s = new ObjectInputStream(f);
                map.putAll((Map<String,Expense>)s.readObject());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExpensesDaoFileSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ExpensesDaoFileSystem.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    s.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExpensesDaoFileSystem.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void save() {
        ObjectOutputStream s = null;
        try {
            FileOutputStream f = new FileOutputStream(new File(FILENAME));
            s = new ObjectOutputStream(f);
            s.writeObject(map);
            s.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExpensesDaoFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExpensesDaoFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(ExpensesDaoFileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public Expense delete(String uuid) {
        final Expense expense = map.remove(uuid);
        save();
        return expense;
    }

    @Override
    public List<Expense> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Expense findById(String uuid) {
        return map.get(uuid);
    }

    @Override
    public List<Expense> findByType(String... types) {
        return findByType(Arrays.asList(types));
    }

    @Override
    public List<Expense> findByType(List<String> types) {
        final List<Expense> byType = new ArrayList<>();
        Expense candidate;
        for (String uuid : map.keySet()) {
            candidate = map.get(uuid);
            if (!Collections.disjoint(types, candidate.getTypes())) {
                byType.add(candidate);
            }
        }
        return byType;
    }

    @Override
    public Expense save(Expense expense) {
        map.put(expense.getUuid(), expense);
        save();
        return expense;
    }
    
}
