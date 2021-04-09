package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.ShippingRange;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
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

		// Get items added to cart
		HashMap<Vendor, List<SelectedItem>> cart = new HashMap<Vendor, List<SelectedItem>>(); // items added to cart
																								// from session

		try {
			cart = (HashMap<Vendor, List<SelectedItem>>) session.getAttribute("cart");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}

		// calcolo delle policy di spedizione

		HashMap<Vendor, float[]> shipping = new HashMap<Vendor, float[]>(); // Vendor, [Shipping Price][Total]

		for (Vendor v : cart.keySet()) {

			float shippingPrice = 0;

			int numberOfItems = 0;
			for (SelectedItem s : cart.get(v)) {
				numberOfItems = numberOfItems + s.getQuantity();
			}

			if (numberOfItems >= v.getFree_limit()) {
				shippingPrice = 0;
			} else {
				for (ShippingRange s : v.getRanges()) {
					if ((s.getMin() <= numberOfItems) && (s.getMax() >= numberOfItems)) {
						shippingPrice = s.getCost();
					}
				}
			}
			float total = shippingPrice;
			for (SelectedItem s : cart.get(v)) {
				total = total + (s.getCost() * s.getQuantity());
			}

			float[] costs = new float[2];
			costs[0] = shippingPrice;
			costs[1] = total;

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
