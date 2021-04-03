package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.utils.ConnectionHandler;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class goCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public goCart() {
		super();
		// TODO Auto-generated constructor stub
	}


	public void init() throws ServletException {
		servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	@SuppressWarnings({ "null" })
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("user");
		
		// Get params
		ArrayList<Integer> items = new ArrayList<Integer>();
		
		
		try {
			items.add(1);
			items.add(2);
			// items = (ArrayList<Integer>) session.getAttribute("items");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}

		ItemDAO IDAO = new ItemDAO(connection);
		List<Item> cartItem = new ArrayList<Item>();
		
		try {
			
			cartItem = IDAO.findItemsById(items);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String path = "cart";
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("cartItem", cartItem);
		webContext.setVariable("user", user);
		templateEngine.process(path, webContext, response.getWriter());
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
