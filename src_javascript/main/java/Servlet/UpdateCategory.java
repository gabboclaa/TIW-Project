package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import Dao.CategoryDAO;
import utils.ConnectionHandler;

@WebServlet("/Update")
public class UpdateCategory extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	
	
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		// Recupera i parametri dalla richiesta POST
        String categoryId = request.getParameter("categoryId");
        String newName = request.getParameter("newName");

        // Esegui la logica per salvare il nuovo nome nel database
        updateDatabase(categoryId, newName);

        // Imposta la risposta come JSON di conferma
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Nome categoria salvato con successo.\"}");
    }
	
	private void updateDatabase(String categoryId, String newName) {
		
		int id = Integer.parseInt(categoryId);
		
		CategoryDAO bService = new CategoryDAO(connection);
		
		try {
			bService.updateCategoryName(id, newName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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