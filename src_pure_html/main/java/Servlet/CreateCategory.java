package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import Dao.CategoryDAO;
import utils.ConnectionHandler;

@WebServlet("/CreateCategory")
public class CreateCategory extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	private TemplateEngine templateEngine;
	
	public CreateCategory() {
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String HomePath = request.getServletContext().getContextPath() + "/Home?generalError=createCategoryError";
		
		String loginpath = getServletContext().getContextPath() + "/Index.html";
        HttpSession session = request.getSession();
		
		String name = null;
		int father = -1;
		boolean badRequest = false;
		boolean check = false;
		boolean checkName = false;
		boolean insertSuccess = false;
		
		if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }
		
		try {
			
			name = request.getParameter("categoryName");
			father = Integer.parseInt(request.getParameter("parentCategory"));
			
			if(name.isEmpty() || father==-1) badRequest=true;
			
			
		}catch(NullPointerException | NumberFormatException e) {
			badRequest=true;
		}
		
		if (badRequest) {
			response.sendRedirect(HomePath);
			return;
		}
		
		CategoryDAO bService = new CategoryDAO(connection);
		
		try {
			check = bService.checkOrder(father);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		checkName = bService.checkName(name);
		
		if(check && checkName) {
			
			try {
			
				bService.createCategory(name, father);
				insertSuccess = true;
			
			}catch (Exception e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Error in creating the category in the database");
				return;
			}
			
		}
		
		//Redirect to Servlet InsertSuccess
		String path;
		request.getSession().setAttribute("insertSuccess", insertSuccess);			
		path = getServletContext().getContextPath() + "/InsertSuccess";
		response.sendRedirect(path);
	
	}
	
	
	public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
}
