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
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.dao.VendorDAO;
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

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		//Get items added to cart from session
		//Rebuilt cart from vendor id and list of item id
		HashMap<Integer, HashMap<Integer,Integer>> cartSession = new HashMap<Integer, HashMap<Integer,Integer>>();
		try {
			cartSession = (HashMap<Integer, HashMap<Integer,Integer>>) session.getAttribute("cartSession");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}
		
		//Built Cart
				HashMap<Vendor, List<SelectedItem>> cart = null;
				HashMap<Vendor, float[]> shipping =null;
		
		if(cartSession.isEmpty()) {
			
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
			
			// catch information about vendor
			VendorDAO vendorDAO = new VendorDAO(connection);
			Vendor vendorCurrent = new Vendor();
			try {
				
				vendorCurrent = vendorDAO.findFullBySingleId(vendor);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			Set<Integer> itemsForVendorSet = cartSession.get(vendor).keySet();
			List<SelectedItem> selectedItemList = new ArrayList<SelectedItem>();
			
			for(int item: itemsForVendorSet) {
				
				//catch information about item
				ItemDAO itemDAO = new ItemDAO(connection);
				Item itemCurrent = new Item();
				try {
					itemCurrent = itemDAO.findItemsBySingleId(item);
				} catch (SQLException e) {
					e.printStackTrace();
				}
						
				// catch price
				PriceDAO priceDAO = new PriceDAO(connection);
				Price price = new Price();
				try {
					price = priceDAO.findPriceBySingleItemId(item,vendor);
				} catch (SQLException e) {
					e.printStackTrace();
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
		try {
			cart = (HashMap<Vendor, List<SelectedItem>>) session.getAttribute("cart");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}
		 */
		
		//Calculate shipping cost and total expenses
		shipping = new HashMap<Vendor, float[]>(); // <Vendor, [ShippingPrice , Total]>
		
		for (Vendor v : cart.keySet()) {

			float shippingPrice = OrderUtils.calculateShipping(v, cart.get(v));
			float subtotal = 0;
			for (SelectedItem s : cart.get(v)) {
				subtotal = subtotal + (s.getCost() * s.getQuantity());
			}

			float[] costs = new float[3];
			costs[0] = shippingPrice;
			costs[1] = subtotal;
			costs[2] = shippingPrice + subtotal;

			shipping.put(v, costs);
		}

		String path = "cart";
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
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
