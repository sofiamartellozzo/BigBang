package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.Price;
import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.beans.ErrorMessage;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.dao.VendorDAO;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.OrderUtils;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class goCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	
	/**
	 * ERRORI GESTITI: 
	 * -errore propagato da doAddCart
	 * -cartSession non presente nella sessione
	 * -errori di interrogazioni al db
	 */
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get active user
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// space for errors
		ErrorMessage errorMessage;
		errorMessage = (ErrorMessage) session.getAttribute("error");

		// Rebuilt cart from vendor id and list of item id
		HashMap<Vendor, List<SelectedItem>> cart = null;
		HashMap<Vendor, float[]> shipping = null;

		//error added to session from doAddCart
		if (errorMessage != null) {
			
			//rebuilt old cart and shipping
			cart = (HashMap<Vendor, List<SelectedItem>>) session.getAttribute("cartOld");
			shipping = (HashMap<Vendor, float[]>) session.getAttribute("shippingOld");
			
			String path = "cart";
			final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
			webContext.setVariable("cart", cart);
			webContext.setVariable("shipping", shipping);
			webContext.setVariable("user", user);
			webContext.setVariable("error", errorMessage);
			templateEngine.process(path, webContext, response.getWriter());
			return;
			
		}else {
			errorMessage=null;
		}

		// Get items added to cart from session
		HashMap<Integer, HashMap<Integer, Integer>> cartSession = new HashMap<Integer, HashMap<Integer, Integer>>();
		try {
			cartSession = (HashMap<Integer, HashMap<Integer, Integer>>) session.getAttribute("cartSession");
		} catch (NumberFormatException | NullPointerException e) {

			errorMessage= new ErrorMessage("Session Error", "resources not found");
			String path = "cart";
			final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
			webContext.setVariable("cart", cart);
			webContext.setVariable("shipping", shipping);
			webContext.setVariable("user", user);
			webContext.setVariable("error", errorMessage);
			templateEngine.process(path, webContext, response.getWriter());
			return;
		}

		if (cartSession.isEmpty()) {
			
			String path = "cart";
			final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
			webContext.setVariable("cart", cart);
			webContext.setVariable("shipping", shipping);
			webContext.setVariable("user", user);
			templateEngine.process(path, webContext, response.getWriter());
			return;
		}

		cart = new HashMap<Vendor, List<SelectedItem>>();
		Set<Integer> vendorSet = cartSession.keySet();

		for (int vendor : vendorSet) {

			// collect information about vendor
			VendorDAO vendorDAO = new VendorDAO(connection);
			Vendor vendorCurrent = new Vendor();
			try {

				vendorCurrent = vendorDAO.fineOneByVendorId(vendor);
			} catch (DatabaseException e) {
				errorMessage = new ErrorMessage("Database Error", e.getBody());
				String path = "cart";
				final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
				webContext.setVariable("cart", cart);
				webContext.setVariable("shipping", shipping);
				webContext.setVariable("user", user);
				webContext.setVariable("error", errorMessage);
				templateEngine.process(path, webContext, response.getWriter());
				return;	
			}

			Set<Integer> itemsForVendorSet = cartSession.get(vendor).keySet();
			List<SelectedItem> selectedItemList = new ArrayList<SelectedItem>();

			for (int item : itemsForVendorSet) {

				// collect information about item
				ItemDAO itemDAO = new ItemDAO(connection);
				Item itemCurrent = new Item();
				try {
					itemCurrent = itemDAO.findOneByItemId(item);
				} catch (DatabaseException e) {
					errorMessage = new ErrorMessage("Database Error", e.getBody());
					String path = "cart";
					final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
					webContext.setVariable("cart", cart);
					webContext.setVariable("shipping", shipping);
					webContext.setVariable("user", user);
					webContext.setVariable("error", errorMessage);
					templateEngine.process(path, webContext, response.getWriter());
					return;	
				}

				// collect price
				PriceDAO priceDAO = new PriceDAO(connection);
				Price price = new Price();
				try {
					price = priceDAO.findOneByItemIdAndVendorId(item, vendor);
				} catch (DatabaseException e) {
					errorMessage = new ErrorMessage("Database Error", e.getBody());
					String path = "cart";
					final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
					webContext.setVariable("cart", cart);
					webContext.setVariable("shipping", shipping);
					webContext.setVariable("user", user);
					webContext.setVariable("error", errorMessage);
					templateEngine.process(path, webContext, response.getWriter());
					return;	
				}

				SelectedItem selectedItem = new SelectedItem();
				selectedItem.setItem(itemCurrent);
				selectedItem.setQuantity(cartSession.get(vendor).get(item));
				selectedItem.setCost(price.getPrice());
				selectedItemList.add(selectedItem);
			}
			cart.put(vendorCurrent, selectedItemList);
		}

		/*
		 * try { cart = (HashMap<Vendor, List<SelectedItem>>)
		 * session.getAttribute("cart"); } catch (NumberFormatException |
		 * NullPointerException e) {
		 * response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
		 * return; }
		 */

		// Calculate shipping cost and total expenses
		shipping = new HashMap<Vendor, float[]>(); // <Vendor, [ShippingPrice , Total]>

		for (Vendor vendor : cart.keySet()) {

			float shippingPrice = OrderUtils.calculateShipping(vendor, cart.get(vendor));
			float subtotal = 0;
			for (SelectedItem s : cart.get(vendor)) {
				subtotal = subtotal + (s.getCost() * s.getQuantity());
			}

			float[] costs = new float[3];
			costs[0] = shippingPrice;
			costs[1] = subtotal;
			costs[2] = shippingPrice + subtotal;

			shipping.put(vendor, costs);
		}

		String path = "cart";
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		
		//necessary just for manage errors
		request.getSession().setAttribute("cartOld", cart);
		request.getSession().setAttribute("shippingOld", shipping);
		
		webContext.setVariable("error", errorMessage);
		webContext.setVariable("cart", cart);
		webContext.setVariable("shipping", shipping);
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
