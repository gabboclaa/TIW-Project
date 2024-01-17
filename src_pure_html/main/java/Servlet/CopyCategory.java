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
import beans.Category;
import utils.ConnectionHandler;


@WebServlet("/CopyCategory")
public class CopyCategory extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	private TemplateEngine templateEngine;
	
	
	public CopyCategory() {
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
		String loginpath = getServletContext().getContextPath() + "/Index.html";
        HttpSession session = request.getSession();
		
		
		String HomePath = request.getServletContext().getContextPath() + "/Home?generalError=id+not+found";
		
		List<Category> settedCategories = null;
		
		
		Category category = null;
		
		int id = -1;
		boolean badRequest = false;
		
		if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }
		
		try {
			
			id = Integer.parseInt(request.getParameter("categoryid"));
			
			if(id==-1) badRequest=true;
			
			
		}catch(NullPointerException | NumberFormatException e) {
			badRequest=true;
		}
		
		if (badRequest) {
			response.sendRedirect(HomePath);
			return;
		}
		
		CategoryDAO bService = new CategoryDAO(connection);
		
		try {
			category = bService.findCategoryById(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in finding the category in the database");
			return;
		}
		
		try {
			settedCategories = bService.setFlag(category);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in getting the categories from the database");
			return;
			
		}
		
		
		 // Redirect to the Home page
        String path = "/WEB-INF/HomeCopy.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("precid", id);
        ctx.setVariable("copiedcategory", category);
		ctx.setVariable("topcategories", settedCategories);
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
