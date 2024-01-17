package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

@WebServlet("/AllCategories")
public class AllCategories extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	
	
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		// Recupera i dati dal database
	    List<Category> categories = retrieveDataFromDatabase();

	    // Converte gli oggetti Java in formato JSON utilizzando Gson
	    Gson gson = new Gson();
	    String json = gson.toJson(categories);

	    // Imposta l'intestazione della risposta come JSON
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    // Scrive il JSON come risposta
	    response.getWriter().write(json);
	}
	
	private List<Category> retrieveDataFromDatabase() {
		
	    // Logica per recuperare i dati dal database e restituire una lista di oggetti Item
	    
	    List<Category> categories = new ArrayList<>();
	    
	    
	    CategoryDAO bService = new CategoryDAO(connection);
	    
	    try {
			categories = bService.findAllCategories();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return categories;
	}
	
	public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
