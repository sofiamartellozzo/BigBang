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

import it.polimi.tiw.bigbang.beans.ExtendedItem;
import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.ExtendedItemDAO;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class doSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		List<ExtendedItem> viewItem = (List<ExtendedItem>) session.getAttribute("itemViewed");
		List<ExtendedItem> finalItemSearch = (List<ExtendedItem>) session.getAttribute("itemSearch");
		
		boolean clearViewedItemList = true;
		if(session.getAttribute("clearViewItemList")!=null) {
			clearViewedItemList = (boolean)session.getAttribute("clearViewItemList");
		}
	
		//check if is a new search 
		if (clearViewedItemList) {
			//each time I do a search, remove all old items searched and visualized
			request.getSession().removeAttribute("itemSearch");
			request.getSession().removeAttribute("itemViewed");
		
		

		// Get the search parameter, so the items asked
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
		ExtendedItemDAO extendedItemDAO = new ExtendedItemDAO(connection);
		List<Item> searchItems = new ArrayList<>();
		finalItemSearch = new ArrayList<>();
		try {
			searchItems = itemDAO.findItemsByWord(itemSearch);
			finalItemSearch = extendedItemDAO.findAllItemDetails(searchItems);
			
			//put the serch items in the session, so they now can be find by doView when redirect to serch page
			request.getSession().setAttribute("itemSearch", finalItemSearch);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to find items");
			e.printStackTrace();
			return;
		}
		
		
		viewItem = new ArrayList<>();
		
		}
		
		session.setAttribute("clearViewItemList", true);

		// Redirect to the search Page with the items found
		String path = "search";
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("itemViewed", viewItem);
		webContext.setVariable("searchItem", finalItemSearch);
		webContext.setVariable("user", user);
		templateEngine.process(path, webContext, response.getWriter());
	}
	


	public void destroy() {
		try {
			DBConnectionProvider.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
