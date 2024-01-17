package Servlet;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

import Dao.UserDAO;
import beans.MyLoginData;
import beans.User;
import utils.ConnectionHandler;


@WebServlet("/login")
public class LoginServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public LoginServlet() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// obtain and escape params
		String usrn = null;
		String pwd = null;
		String errorMsg=null;
		
		try {
			// Leggi il corpo della richiesta come stringa JSON
		    String jsonBody = request.getReader().lines().collect(Collectors.joining());

		    // Crea un oggetto Gson per la deserializzazione JSON
		    Gson gson = new Gson();

		    // Deserializza il JSON in un oggetto Java
		    MyLoginData formData = gson.fromJson(jsonBody, MyLoginData.class);

		    // Accedi ai dati dell'oggetto Java
		    usrn = formData.getUsername();
		    pwd = formData.getPassword();
			
			System.out.println("LOGIN");
			System.out.println("user:"+usrn);
			System.out.println("pw:"+pwd);
			
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
				response.getWriter().println("Missing or empty credential value");
				
				return;
			}

		} catch (Exception e) {

			e.printStackTrace();
			errorMsg="Missing credential value";
		}

		// query db to authenticate for user
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkUser(usrn, pwd);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			errorMsg="Not Possible to check credentials";
			response.getWriter().println(errorMsg);
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message
		System.out.println(user);

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//Code 401
			response.getWriter().println("Wrong UserName or Password");
		} else {
			request.getSession(true).setAttribute("user", usrn);
			response.setStatus(HttpServletResponse.SC_OK);//Code 200
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(usrn);
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
