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


import com.google.gson.Gson;

import Dao.CategoryDAO;
import beans.Category;
import utils.ConnectionHandler;


@WebServlet("/AskCategory")
public class AskCategory extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	
	
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		String categoryId = request.getParameter("categoryId");
		
		int id = Integer.parseInt(categoryId);
		
		
		// Recupera i dati dal database
	    Category category = retrieveDataFromDatabase(id);
	    
	    //System.out.println(category.getName());


	 // Converte gli oggetti Java in formato JSON utilizzando Gson
	    Gson gson = new Gson();
	    String json = gson.toJson(category);

	    // Imposta l'intestazione della risposta come JSON
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    // Scrive il JSON come risposta
	    response.getWriter().write(json);
	    

	}
	
	private Category retrieveDataFromDatabase(int id) {
		
	    // Logica per recuperare i dati dal database e restituire una lista di oggetti Item
		List<Category> categories = null;
	    Category category = null;
	    
	    CategoryDAO bService = new CategoryDAO(connection);
	    
	    try {
			categories = bService.findTopLevelCategories();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    category = findCategory(id, categories);
	    
	    
	    return category;
	}
	
	private Category findCategory(int id, List<Category> categories) {
		Category category = null;
		
		for(Category c: categories) {
	    	
	    	if(c.getId()==id) {
	    		
	    		category = c;
	    		break;
	    			    	
	    	}else {
	    		
	    		category = findCategory(id, c.getSubcategories());
	    		if(category!=null) {
	    			break;
	    		}
	    		
	    	}
	    	
	    }
		
		return category;
	}
	
	public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
