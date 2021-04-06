package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

import it.polimi.tiw.bigbang.beans.ExtendedItem;
import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.Price;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.dao.VendorDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class doSearch extends HttpServlet {
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
		PriceDAO priceDAO = new PriceDAO(connection);
		VendorDAO vendorDAO = new VendorDAO(connection);
		ArrayList<Item> searchItems = new ArrayList<>();
		ArrayList<Integer> idItems = new ArrayList<>();
		ArrayList<Integer> idVendor = new ArrayList<>();
		ArrayList<Price> priceOfItems = new ArrayList<>();
		ArrayList<Vendor> vendorOfItems = new ArrayList<>();
		ArrayList<ExtendedItem> finalItemsSearch = new ArrayList<>();
		HashMap<Vendor, Price> association = new HashMap<>();
		try {
			searchItems = itemDAO.findItemsByWord(itemSearch);
			for (Item item: searchItems) {
				idItems.add(item.getId());
			}
			priceOfItems = priceDAO.findLowerPriceByItemId(idItems);
			for (Price price : priceOfItems) {
				idVendor.add(price.getIdVendor());
			}
			vendorOfItems = (ArrayList<Vendor>) vendorDAO.findById(idVendor);
			for (int i=0; i<searchItems.size();i++) {
				ExtendedItem extendedItem = new ExtendedItem();
				extendedItem.setItem(searchItems.get(i));
				association.put(vendorOfItems.get(i), priceOfItems.get(i));
				extendedItem.setValue(association);
				finalItemsSearch.add(extendedItem);
			}
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to find items");
			e.printStackTrace();
			return;
		}

		// Redirect to the search Page with the items found
		String path = "search";
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("searchItem", finalItemsSearch);
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
