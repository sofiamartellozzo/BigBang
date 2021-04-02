package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.utils.ConnectionHandler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class doSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	private TemplateEngine templateEngine;

	public doSearch() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		// Get the search paramether, so the items asked
		String itemSearch = null;
		try {
			itemSearch = request.getParameter("keyword");
			if (itemSearch == null || itemSearch.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		ItemDAO itemDAO = new ItemDAO(connection);
		ArrayList<Item> searchItems = new ArrayList<>();
		try {
			searchItems = itemDAO.findItemsByWord(itemSearch);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to find items");
			return;
		}

		// Redirect to the search Page with the items found
		String path = "/search.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("items", searchItems);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
