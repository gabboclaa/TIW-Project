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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import Dao.CategoryDAO;
import beans.Category;
import utils.ConnectionHandler;

@WebServlet("/InsertCategory")
public class InsertCategory extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	public InsertCategory() {
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
		
		doPost(request, response);
		
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
String HomePath = request.getServletContext().getContextPath() + "/Home?generalError=id+not+found+precid+not+found";
		
		Category category = null;
		
		List<Category> settedCategories = null;
		
		List<Category> ToInsertCategories = null;

		int precid=-1;
		int id = -1;
		boolean badRequest = false;
		boolean insertSuccess = false;
		boolean check = false;
		boolean checkCatchable = false;
		
		try {
			
			precid = Integer.parseInt(request.getParameter("precid"));
			
			id = Integer.parseInt(request.getParameter("categoryid"));
			
			if( id==-1 || precid==-1) badRequest=true;
			
			
		}catch(NullPointerException | NumberFormatException e) {
			badRequest=true;
		}
		
		if (badRequest) {
			response.sendRedirect(HomePath);
			return;
		}
		
		CategoryDAO bService = new CategoryDAO(connection);
		
		
		try {
			category = bService.findCategoryById(precid);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
					"Error in finding the category in the database");
			return;
		}
		
		ToInsertCategories = bService.categoriesToInsert(settedCategories);
		
		checkCatchable = bService.checkCatchableId(id, ToInsertCategories);
		
		if(checkCatchable) {
		
		try {
			check = bService.checkOrder(id);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in getting the categories in the database");
			return;
		}
		
			if(check) {
			
				try {
					bService.insertListCategory(id, ToInsertCategories);
					insertSuccess = true;
			
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Error in creating the categories in the database");
					return;
				}
			}
		}
		
		if(checkCatchable && insertSuccess) {
		
			//Redirect to Servlet InsertSuccess
			String path;
			request.getSession().setAttribute("insertSuccess", insertSuccess);				
			path = getServletContext().getContextPath() + "/InsertSuccess";
			response.sendRedirect(path);
		
		}else if(!insertSuccess){
			
			//Redirect to the Home page with an error
			String path;
			request.getSession().setAttribute("insertSuccess", insertSuccess);				
			path = getServletContext().getContextPath() + "/InsertSuccess";
			response.sendRedirect(path);
			
		}else {
			//Redirect to the Home page with an error
			String path;
			request.getSession().setAttribute("checkCatchable", checkCatchable);				
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}
    
    
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
