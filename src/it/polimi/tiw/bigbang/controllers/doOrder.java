package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.OrderDAO;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.dao.VendorDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.OrderUtils;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class doOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	@Override
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
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

		// get the vendor id from the request
		int vendorID = Integer.parseInt(request.getParameter("vendorId"));

		// get the vendor details
		VendorDAO vendorDAO = new VendorDAO(connection);
		Vendor vendor = new Vendor();
		try {
			vendor = vendorDAO.findFullBySingleId(vendorID);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		// get all the items with their respective quantities for this vendor
		HashMap<Integer, Integer> items = cart.get(vendorID);

		// get details for all items
		ItemDAO itemDAO = new ItemDAO(connection);
		List<Item> fullItems = new ArrayList<>();
		try {
			fullItems = itemDAO.findItemsById(new ArrayList<>(items.keySet()));
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		PriceDAO priceDAO = new PriceDAO(connection);

		List<SelectedItem> selectedItems = new ArrayList<>();
		for (Item i : fullItems) {
			SelectedItem selectedItem = new SelectedItem();
			selectedItem.setItem(i);
			selectedItem.setQuantity(items.get(i.getId()));
			try {
				selectedItem.setCost(priceDAO.findPriceBySingleItemId(i.getId(), vendorID).getPrice());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			selectedItems.add(selectedItem);
		}

		float shipping_cost = OrderUtils.calculateShipping(vendor, selectedItems);

		OrderDAO orderDAO = new OrderDAO(connection);
		orderDAO.createNewOrder(user.getId(), vendorID, shipping_cost, selectedItems);

		String path = getServletContext().getContextPath() + "/orders";
		response.sendRedirect(path);
	}
}
