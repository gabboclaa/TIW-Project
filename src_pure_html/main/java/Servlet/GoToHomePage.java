package Servlet;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import Dao.CategoryDAO;
import utils.ConnectionHandler;
import beans.Category;

@WebServlet("/Home")
public class GoToHomePage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;

    public GoToHomePage() {
        super();
    }

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If the user is not logged in (not present in session) redirect to the login
        String loginpath = getServletContext().getContextPath() + "/Index.html";
        HttpSession session = request.getSession();
        int success = 1;
        int checkCatchable = 1;
        
        
        if( request.getParameter("checkCatchable") == null) { 
        	checkCatchable = 1;
        }else {
        	checkCatchable = Integer.parseInt(request.getParameter("checkCatchable"));
        }
        
        if(request.getParameter("insertSuccess") != null) {
        	
        	success = Integer.parseInt(request.getParameter("insertSuccess"));
        
        }else {
        	//non dovrebbe succedere in quanto lo comunico sempre
        	
        }
        
        
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }
        
       
        List<Category> allcategories = null;
		List<Category> topcategories = null;
		
		CategoryDAO bService = new CategoryDAO(connection);
		try {
			topcategories = bService.findTopLevelCategories();
			allcategories = bService.findAllCategories();
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in retrieving categories from the database");
			return;
		}

        // Redirect to the Home page
        String path = "/WEB-INF/Home.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        
        ctx.setVariable("checkCatchable", checkCatchable);
        ctx.setVariable("success", success);
        ctx.setVariable("allcategories", allcategories);
		ctx.setVariable("topcategories", topcategories);
		templateEngine.process(path, ctx, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
