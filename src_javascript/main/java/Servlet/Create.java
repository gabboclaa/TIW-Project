package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import Dao.CategoryDAO;
import beans.MyFormData;
import utils.ConnectionHandler;

@WebServlet("/Create")
public class Create extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	
	
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		boolean success;
		// Leggi il corpo della richiesta come stringa JSON
	    String jsonBody = request.getReader().lines().collect(Collectors.joining());

	    // Crea un oggetto Gson per la deserializzazione JSON
	    Gson gson = new Gson();

	    // Deserializza il JSON in un oggetto Java
	    MyFormData formData = gson.fromJson(jsonBody, MyFormData.class);

	    // Accedi ai dati dell'oggetto Java
	    String name = formData.getName();
	    String parentCategory = formData.getParentCategory();
        
        int father = Integer.parseInt(parentCategory);

        
        success = createCategoryDatabase(father, name);

        if(success) {
        // Imposta la risposta come JSON di conferma
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Nuova categoria creata con successo.\"}");
        }else {
        	
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//Code 401
			response.getWriter().println("{\"message\": \"Non puoi crearex qui.\"}");
		
        }
    }
	
	private boolean createCategoryDatabase(int fatherId, String Name) {
		
		boolean check = false;
		boolean success = false;
		
		CategoryDAO bService = new CategoryDAO(connection);
		
		try {
			check = bService.checkOrder(fatherId);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(check) {
		
		try {
			bService.createCategory(Name, fatherId);
			success = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		return success;
	   
	}
	
	public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
