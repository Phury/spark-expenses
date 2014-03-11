package be.phury.expenses;

import be.phury.expenses.model.Expense;
import be.phury.expenses.spark.HandlebarsRoute;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import spark.template.velocity.VelocityRoute;

/**
 *
 * @author phury
 */
public class ExpensesResource {
    
    private ExpensesDao expenses;
    
    @Inject
    public ExpensesResource(ExpensesDao expensesDao) {
        this.expenses = expensesDao;
        
        staticFileLocation("/static"); 
        
        get(new HandlebarsRoute("/hbs/:tplName") {
            @Override
            public Object handle(final Request request, final Response response) {
                return modelAndView(null, request.params(":tplName"));
            }
        });
        
        get(new VelocityRoute("/wm/:tplName") {
            @Override
            public Object handle(final Request request, final Response response) {
                return modelAndView(null, request.params(":tplName"));
            }
        });
        
        get(new VelocityRoute("/expense") {
            @Override
            public Object handle(final Request request, final Response response) {
                Map<String, Object> model = new HashMap<>();
                model.put("title", "List of all expenses");
                model.put("expenses", expenses.findAll());
                model.put("route", "expense_list.wm");
                return modelAndView(model, "index.wm");
            }
        });
        
        get(new VelocityRoute("/expense/find/type/:type") {
            @Override
            public Object handle(final Request request, final Response response) {
                Map<String, Object> model = new HashMap<>();
                model.put("title", "List of all expenses");
                model.put("expenses", expenses.findByType(request.params(":type")));
                model.put("route", "expense_list.wm");
                return modelAndView(model, "index.wm");
            }
        });
        
        get(new VelocityRoute("/expense/add") {
            @Override
            public Object handle(final Request request, final Response response) {
                Map<String, Object> model = new HashMap<>();
                model.put("route", "expense_add.wm");
                return modelAndView(model, "index.wm");
            }
        });
        
        post(new Route("/expense/add"){
            @Override
            public Object handle(final Request request, final Response response) {
                final Expense expense = new Expense(
                        Double.parseDouble(request.queryParams("amount.value")),
                        request.queryParams("details"),
                        parseDate(request.queryParams("timestamp")),
                        parseStringList(request.queryParams("types.list")));
                expenses.save(expense);
                response.redirect("/expense/" + expense.getUuid()); 
                return null;
            }
        });
        
        post(new Route("/expense/delete"){
            @Override
            public Object handle(final Request request, final Response response) {
                for (String uuid : request.queryMap("expense").values()) {
                    expenses.delete(uuid);
                }
                response.redirect("/expense"); 
                return null;
            }
        });
        
        get(new VelocityRoute("/expense/:uuid") {
            @Override
            public Object handle(final Request request, final Response response) {
                Map<String, Object> model = new HashMap<>();
                model.put("title", "Expense");
                model.put("expense", expenses.findById(request.params(":uuid")));
                model.put("route", "expense.wm");
                return modelAndView(model, "index.wm");
            }
        });
    }
    
    private Long parseDate(String str) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(str).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(ExpensesResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new TechnicalException(ex);
        }
    }
    
    private List<String> parseStringList(String str) {
        return Arrays.asList(str.split(", "));
    }
}
