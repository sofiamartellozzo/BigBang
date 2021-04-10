package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.Price;
import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.dao.VendorDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;

public class doAddCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		
		ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@SuppressWarnings({ "unchecked", "unused" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		// Get items added to cart
		HashMap<Vendor, List<SelectedItem>> cart = new HashMap<Vendor, List<SelectedItem>>(); // items added to cart
																								// from session

		try {
			cart = (HashMap<Vendor, List<SelectedItem>>) session.getAttribute("cart");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}

		Integer vendorAdd = 0;
		String sub = null;
		Integer itemAdd = 0;
		Integer quantity = 1;
		boolean decrement = false;

		try {
			vendorAdd = Integer.parseInt(request.getParameter("vendorId"));
			if (vendorAdd == null) {
				throw new Exception("Missing or empty credential value vendor");
			}

			itemAdd = Integer.parseInt(request.getParameter("itemId"));
			if (itemAdd == null) {
				throw new Exception("Missing or empty credential value item");
			}

			// from search or home
			if (request.getParameter("quantity") != null) {
				quantity = Integer.parseInt(request.getParameter("quantity"));
			}

			// from cart if decrement
			if (request.getParameter("sub") != null) {
				decrement = true;
			}

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value quantity total");
			return;
		}

		// catch information about vendor
		ItemDAO itemDAO = new ItemDAO(connection);
		Item item = new Item();
		try {
			item = itemDAO.findItemsBySingleId(itemAdd);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// catch price
		PriceDAO priceDAO = new PriceDAO(connection);
		Price price = new Price();
		try {
			price = priceDAO.findPriceBySingleItemId(itemAdd,vendorAdd);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// catch information about item
		VendorDAO vendorDAO = new VendorDAO(connection);
		Vendor vendor = new Vendor();
		try {
			vendor = vendorDAO.findFullBySingleId(vendorAdd);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		boolean vendorIsPresent = false;
		Vendor selectedVendor = null;
		for (Vendor v : cart.keySet()) { // search item
			if (v.getId()==vendorAdd) {
				vendorIsPresent = true;
				selectedVendor = v;
			}
		}
			
		if (vendorIsPresent) { // search vendor

			boolean isPresent = false;
			SelectedItem selectedItem = null;
			for (SelectedItem s : cart.get(selectedVendor)) { // search item
				if (s.getItem().getId() == itemAdd) {
					isPresent = true;
					selectedItem = s;
				}
			}

			if (!isPresent) { // if not present create it
				selectedItem = new SelectedItem();
				selectedItem.setItem(item);
				selectedItem.setQuantity(quantity);
				selectedItem.setCost(price.getPrice());
				cart.get(selectedVendor).add(selectedItem);
			} else { // if present yet
				int actualQuantity = selectedItem.getQuantity();
				if (decrement) {
					if (selectedItem.getQuantity() < 2) { // quantity == 0
						cart.get(selectedVendor).remove(selectedItem);
						if(cart.get(selectedVendor).isEmpty()) {
							cart.remove(selectedVendor);
						}
					} else { // simply decrement quantity
						actualQuantity = actualQuantity - quantity;
						selectedItem.setQuantity(actualQuantity);
					}
				} else { // increment
					actualQuantity = actualQuantity + quantity;
					selectedItem.setQuantity(actualQuantity);
				}
			}
		}
		
		else { //vendor is not present
			
			SelectedItem selectedItem = new SelectedItem();
			selectedItem.setItem(item);
			selectedItem.setQuantity(quantity);
			selectedItem.setCost(price.getPrice());
			List<SelectedItem> ls = new ArrayList<SelectedItem>();
			ls.add(selectedItem);
			cart.put(selectedVendor, ls);
		}
		
		request.getSession().setAttribute("cart", cart);
		response.sendRedirect(getServletContext().getContextPath() + "/cart");
		

	}
}
