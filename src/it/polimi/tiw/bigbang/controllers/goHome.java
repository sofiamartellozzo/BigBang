package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
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
import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.ItemDAO;

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

		User user = (User) session.getAttribute("user");

		ItemDAO itemDAO = new ItemDAO(connection);
		ArrayList<Item> items = null;
		try {
			items = itemDAO.findLastViewedItemsByUser(user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (items.isEmpty() || items.size() < 5) {
			ArrayList<Item> fillerItems = null;

			try {
				fillerItems = itemDAO.findNItemsByCategory("Books", 5);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			for (Item fillerItem : fillerItems) {
				boolean found = false;
				for (Item item : items) {
					if (item.getId() == fillerItem.getId()) {
						found = true;
						break;
					}
				}
				if (!found && items.size() < 5) {
					items.add(fillerItem);
				}
				if (items.size() == 5)
					break;
			}
		}
		;

		String path = "home";
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("lastViewedItems", items);
		webContext.setVariable("user", user);
		templateEngine.process(path, webContext, response.getWriter());
	}
}
