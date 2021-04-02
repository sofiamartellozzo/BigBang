package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.utils.ConnectionHandler;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;
import it.polimi.tiw.bigbang.beans.User;

public class goHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
	private ServletContext servletContext;
	
	public void init() throws ServletException {
		servletContext = getServletContext();
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
		connection = ConnectionHandler.getConnection(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		User user = (User)session.getAttribute("user");
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("user", user);
		templateEngine.process("/index.html", webContext, response.getWriter());
	}
}
