package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.ErrorMessage;
import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.OrderDAO;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.dao.VendorDAO;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.OrderUtils;

public class doOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private ServletContext servletContext;

	@Override
	public void init() throws ServletException {
		servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get the cart from the session
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Map<Integer, HashMap<Integer, Integer>> cart = (Map<Integer, HashMap<Integer, Integer>>) session
				.getAttribute("cartSession");
		
		ErrorMessage errorMessage;

		// get the vendor id from the request
		//int vendorID = Integer.parseInt(request.getParameter("vendorId"));
		Integer vendorID;
		String vendorString = request.getParameter("vendorId");
		if (vendorString != null && !vendorString.equals("")) {
			try {
				vendorID = Integer.parseInt(vendorString);
			} catch (NumberFormatException e) {
				errorMessage= new ErrorMessage("Vendor Parameter Error", "incorret format");
				request.getSession().setAttribute("error", errorMessage);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}
		} else {
			errorMessage = new ErrorMessage("Vendor Parameter Error", "incorret format");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}
		if (vendorID == null || vendorID < 0) {
			errorMessage = new ErrorMessage("Vendor Parameter Error", "missing or empty value");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		// get the vendor details
		VendorDAO vendorDAO = new VendorDAO(connection);
		Vendor vendor = new Vendor();
		try {
			vendor = vendorDAO.fineOneByVendorId(vendorID);
		} catch (DatabaseException e1) {
			errorMessage = new ErrorMessage("Vendor Parameter Error", e1.getBody());
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		// get all the items with their respective quantities for this vendor
		HashMap<Integer, Integer> items = cart.get(vendorID);
		
		// check if items is empty
		if (items == null || items.isEmpty()) {
			errorMessage = new ErrorMessage("Vendor Parameter Error", "you don't have any items from this vendor in your cart");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		// get details for all items
		ItemDAO itemDAO = new ItemDAO(connection);
		List<Item> fullItems = new ArrayList<>();
		try {
			fullItems = itemDAO.findManyByItemsId(new ArrayList<>(items.keySet()));
		} catch (DatabaseException e1) {
			errorMessage = new ErrorMessage("Database Error", e1.getBody());
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		PriceDAO priceDAO = new PriceDAO(connection);

		List<SelectedItem> selectedItems = new ArrayList<>();
		for (Item i : fullItems) {
			SelectedItem selectedItem = new SelectedItem();
			selectedItem.setItem(i);
			selectedItem.setQuantity(items.get(i.getId()));
			try {
				selectedItem.setCost(priceDAO.findOneByItemIdAndVendorId(i.getId(), vendorID).getPrice());
			} catch (DatabaseException e) {
				errorMessage = new ErrorMessage("Database Error", e.getBody());
				request.getSession().setAttribute("error", errorMessage);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}
			selectedItems.add(selectedItem);
		}

		float shipping_cost = OrderUtils.calculateShipping(vendor, selectedItems);

		OrderDAO orderDAO = new OrderDAO(connection);
		try {
			orderDAO.createOrder(user.getId(), vendorID, shipping_cost, selectedItems);
		} catch (DatabaseException e) {
			errorMessage = new ErrorMessage("Database Error", e.getBody());
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}
		
		cart.remove(vendorID);
		session.setAttribute("cartSession", cart);

		String path = getServletContext().getContextPath() + "/orders";
		response.sendRedirect(path);
	}
}
