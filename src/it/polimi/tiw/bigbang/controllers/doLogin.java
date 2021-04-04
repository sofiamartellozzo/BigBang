package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.UserDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class doLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		String path = "login";
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
			user = userDao.checkCredentials(email, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check credentials");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (user == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect email or password");
			path = "login";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("items", new ArrayList<Integer>(){{add(1); add(2);}});
			path = getServletContext().getContextPath() + "/home";
			response.sendRedirect(path);
		}

	}

	public void destroy() {
		try {
			DBConnectionProvider.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
