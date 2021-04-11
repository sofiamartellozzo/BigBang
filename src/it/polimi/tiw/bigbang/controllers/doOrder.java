package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.OrderDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.OrderUtils;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class doOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Map<Vendor, List<SelectedItem>> cart = (Map<Vendor, List<SelectedItem>>) session.getAttribute("cart");
		
		int vendorID = Integer.parseInt(request.getParameter("vendorId"));
		Vendor vendor = null;
		for (Vendor v : cart.keySet()) {
			if (v.getId() == vendorID) {
				vendor = v;
				break;
			}
		}
		
		List<SelectedItem> items = cart.get(vendor);
		float shipping_cost = OrderUtils.calculateShipping(vendor, items);
		
		OrderDAO orderDAO = new OrderDAO(connection);
		orderDAO.createNewOrder(user.getId(), vendorID, shipping_cost, items);
		
		String path = getServletContext().getContextPath() + "/orders";
		response.sendRedirect(path);
	}
}
