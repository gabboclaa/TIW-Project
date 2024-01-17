package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import Dao.CategoryDAO;
import beans.Category;
import utils.ConnectionHandler;

@WebServlet("/MoveCategory")
public class InsertCategory extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public InsertCategory() {
		super();
	}
	
	public void init() throws ServletException {
        
        connection = ConnectionHandler.getConnection(getServletContext());
    }
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		int precid=-1;
		int id = -1;
		boolean badRequest = false;
		
		
		try {
			
			precid = Integer.parseInt(request.getParameter("categoryId"));
			
			id = Integer.parseInt(request.getParameter("targetCategoryId"));
			
			if( id==-1 || precid==-1) badRequest=true;
			
			
		}catch(NullPointerException | NumberFormatException e) {
			badRequest=true;
		}
		
		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters");
			return;
		}
		
		boolean success = insertCategories(precid , id);
		
		if(success) {
		// Imposta la risposta come JSON di conferma
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Inserimento categorie avvenuto con successo.\"}");
		}else {
			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//Code 401
			response.getWriter().println("{\"message\": \"Non puoi inserire qui.\"}");
		
		}
		
    }

    private boolean insertCategories(int precid, int id) {
    	Category category = null;
		
		List<Category> settedCategories = null;
		
		List<Category> ToInsertCategories = null;

		boolean insertSuccess = false;
		boolean check = false;
    	
		CategoryDAO bService = new CategoryDAO(connection);
		
		
		try {
			category = bService.findCategoryById(precid);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		
		try {
			settedCategories = bService.setFlag(category);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		ToInsertCategories = bService.categoriesToInsert(settedCategories);
		
		try {
			check = bService.checkOrder(id);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(check) {
			
			try {
				bService.insertListCategory(id, ToInsertCategories);
				insertSuccess = true;
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
			}
		
		return insertSuccess;
		}
		
    
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
