package be.phury.expenses;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;


/**
 *
 */
public class ExpensesApp {
    
    public static void main( String[] args ) {
        new ExpensesApp();
    }
    
    private ExpensesApp() {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                //bind(ExpensesResource.class).to(ExpensesResource.class);
                bind(ExpensesDao.class).to(ExpensesDaoSqlite.class);
            }
        }).getInstance(ExpensesResource.class);
    }
        
}
