package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.Order;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.OrderDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

class OrderModel {
	private String id;
	private Timestamp date;
	private Map<Item, Integer> items;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public Map<Item, Integer> getItems() {
		return items;
	}
	public void setItems(Map<Item, Integer> items) {
		this.items = items;
	}
}

public class goOrders extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
	private ServletContext servletContext;

	public void init() throws ServletException {
		servletContext = getServletContext();
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
		connection = DBConnectionProvider.getConnection(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("user");
		OrderDAO orderDAO = new OrderDAO(connection);
		List<Order> orders = new ArrayList<>();
		
		try {
			orders = orderDAO.findOrdersByUserID(user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ItemDAO itemDAO = new ItemDAO(connection);
		List<OrderModel> orderModels = new ArrayList<>();		
		for (int i = 0; i < orders.size(); i++) {
			Order currentOrder = orders.get(i);
			Item currentItem = null;
			try {
				currentItem = itemDAO.findItemsById(new ArrayList<Integer>() {{
					add(currentOrder.getId_item());
				}}).get(0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if (i != 0 && orderModels.get(orderModels.size()-1).getId().equals(currentOrder.getId())) {
				
				orderModels.get(orderModels.size()-1).getItems().put(currentItem, currentOrder.getQuantity());
				
			} else {
				OrderModel newOrderModel = new OrderModel();
				newOrderModel.setId(currentOrder.getId());
				newOrderModel.setDate(currentOrder.getDate());
				
				Map<Item, Integer> quantityMap = new HashMap<>();
				quantityMap.put(currentItem, currentOrder.getQuantity());
				newOrderModel.setItems(quantityMap);
				
				orderModels.add(newOrderModel);
			}
		}
		
		String path = "orders";
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("user", user);
		webContext.setVariable("orders", orderModels);
		templateEngine.process(path, webContext, response.getWriter());
	}
}
