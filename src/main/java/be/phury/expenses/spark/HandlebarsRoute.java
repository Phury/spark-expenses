package be.phury.expenses.spark;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.ModelAndView;
import spark.TemplateViewRoute;

/**
 *
 * @author phury
 */
public abstract class HandlebarsRoute extends TemplateViewRoute {
    
    private Handlebars handlebars = new Handlebars();
    
    public HandlebarsRoute(String path) {
        super(path);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String render(ModelAndView modelAndView) {
        try {
            Template template = handlebars.compile(modelAndView.getViewName());
            return template.apply(modelAndView.getModel());
        } catch (IOException ex) {
            Logger.getLogger(HandlebarsRoute.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException("Unable to convert [" + modelAndView.getViewName() + "]", ex);
        }
    }
}
