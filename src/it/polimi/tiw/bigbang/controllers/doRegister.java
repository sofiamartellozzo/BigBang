package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.ErrorMessage;
import it.polimi.tiw.bigbang.dao.UserDAO;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;
import it.polimi.tiw.bigbang.utils.AuthUtils;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

/**
 * Servlet implementation class doRegister
 */

public class doRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private TemplateEngine templateEngine;
       
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "register";
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, webContext, response.getWriter());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//create the variable to store a possible error
    	ErrorMessage errorMessage;
    	
    	//create the variables 
		String name = null;
		String surname = null;
		String email = null;
		String pwd = null;
		String confirmPwd = null;
		String address = null;
		
		try {
			name = request.getParameter("name");
			surname = request.getParameter("surname");
			email = request.getParameter("email");
			pwd = request.getParameter("pwd");
			confirmPwd = request.getParameter("confirmPwd");
			address = request.getParameter("address");
			if (name == null || surname == null || email == null || pwd == null || address == null || name.isEmpty() || surname.isEmpty() || email.isEmpty() || pwd.isEmpty() || address.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		} catch (Exception e) {
			errorMessage = new ErrorMessage("Input Error","Missing credential value");
			String path = "register";
			ServletContext servletContext = getServletContext();
			final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
			webContext.setVariable("error", errorMessage);
			templateEngine.process(path, webContext, response.getWriter());
			return;
		}
		
		if (!pwd.equals(confirmPwd)) {
			errorMessage = new ErrorMessage("Input Error","No matching passwords");
			String path = "register";
			ServletContext servletContext = getServletContext();
			final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
			webContext.setVariable("error", errorMessage);
			templateEngine.process(path, webContext, response.getWriter());
			return;
		}
		
		//create new user in DB
		UserDAO userDAO = new UserDAO(connection);
		try {
			userDAO.createUser(name, surname, email, AuthUtils.encryptString(pwd), address);
		} catch (DatabaseException e) {
			errorMessage = new ErrorMessage("Database Error", e.getBody());
			String path = "register";
			ServletContext servletContext = getServletContext();
			final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
			webContext.setVariable("error", errorMessage);
			templateEngine.process(path, webContext, response.getWriter());
			return;
		}
		
		//redirect to the login page
		String loginPath = request.getServletContext().getContextPath() + "/login";
		response.sendRedirect(loginPath);
		
		
		
	}

}
