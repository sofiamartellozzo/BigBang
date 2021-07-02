package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.ErrorMessage;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.UserDAO;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;
import it.polimi.tiw.bigbang.utils.AuthUtils;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class doLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private ServletContext servletContext;

	@Override
	public void init() throws ServletException {
		servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		String path = "login";
		templateEngine.process(path, ctx, response.getWriter());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ErrorMessage errorMessage = null;

		// obtain and escape params
		String email = null;
		String pwd = null;
		try {
			// usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			// pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
			email = request.getParameter("email");
			pwd = request.getParameter("pwd");
			if (email == null || pwd == null || email.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		// query db to authenticate for user
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkCredentials(email, AuthUtils.encryptString(pwd));
		} catch (DatabaseException e) {
			errorMessage = new ErrorMessage("Database Error", e.getBody());
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("error", errorMessage);
			String path = "login";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message
		String path;
		if (user == null) {
			errorMessage = new ErrorMessage("Invalid Credentials",
					"The credentials you entered are not valid, please try again.");
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("error", errorMessage);
			path = "login";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		} else {
			request.getSession().setAttribute("user", user);

			// [VendorId || ItemId, Quantity]
			request.getSession().setAttribute("cartSession", new HashMap<Integer, HashMap<Integer, Integer>>());
			path = getServletContext().getContextPath() + "/home";
			response.sendRedirect(path);
		}
	}

	@Override
	public void destroy() {
		try {
			DBConnectionProvider.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
