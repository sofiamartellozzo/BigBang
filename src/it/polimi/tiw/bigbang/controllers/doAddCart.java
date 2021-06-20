package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.bigbang.beans.ErrorMessage;
import it.polimi.tiw.bigbang.beans.Price;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.dao.PriceDAO;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;
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

	/**
	 * ERRORI GESTITI: 
	 * -cartSession non presente nella sessione 
	 * -vendor null or vendor id <0 
	 * -item null or item id<0 
	 * -non esiste una corrispondenza tra vendor e item 
	 * -decrementare l'item di un venditore non presente
	 * -decremenetare l'item non presente 
	 * -ho veramente passato un intero 
	 * -quantity <=0 
	 * -non passo a sub il valore corretto
	 * 
	 * ERRORI DA GESTIRE: -se metto sub dove dovrei invece incrementare??
	 * 
	 */

	@SuppressWarnings({ "unchecked", "unused", "unlikely-arg-type" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get active user
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		// space for errors
		ErrorMessage errorMessage = null;

		// Get items added to cart from session
		HashMap<Integer, HashMap<Integer, Integer>> cartSession = new HashMap<Integer, HashMap<Integer, Integer>>();
		try {
			cartSession = (HashMap<Integer, HashMap<Integer, Integer>>) session.getAttribute("cartSession");
		} catch (NumberFormatException | NullPointerException e) {

			errorMessage = new ErrorMessage("Session Error", "resources not found");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		//Initialization of some variables useful for build cart
		
		//current vendor id
		Integer vendorAdd = 0;
		
		//increment or decrement 
		String sub = null;
		boolean decrement = false;
		
		//current item id
		Integer itemAdd = 0;
		
		Integer quantity = 1;

		// Read variables from request

		String string_vendor = request.getParameter("vendorId");
		if (string_vendor != null && !string_vendor.equals("")) {
			try {
				vendorAdd = Integer.parseInt(string_vendor);
			} catch (NumberFormatException e) {
				errorMessage = new ErrorMessage("Vendor Parameter Error", "not corret format of credential value");
				request.getSession().setAttribute("error", errorMessage);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}
		} else {
			errorMessage = new ErrorMessage("Vendor Parameter Error", "not corret format of credential value");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}
		if (vendorAdd == null || vendorAdd < 0) {

			errorMessage = new ErrorMessage("Vendor Parameter Error", "missing or empty credential value");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		String string_item = request.getParameter("itemId");
		if (string_item != null && !string_item.equals("")) {
			try {
				itemAdd = Integer.parseInt(string_item);

			} catch (NumberFormatException e) {
				errorMessage = new ErrorMessage("Item Parameter Error", "not corret format of credential value");
				request.getSession().setAttribute("error", errorMessage);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}
		} else {
			errorMessage = new ErrorMessage("Item Parameter Error", "not corret format of credential value");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}
		if (itemAdd == null || itemAdd < 0) {

			errorMessage = new ErrorMessage("Item Parameter Error", "missing or empty credential value");
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		//Present only if item is added from search or home page
		String string_quantity = request.getParameter("quantity");
		if (string_quantity != null && !string_quantity.equals("")) {
			try {
				quantity = Integer.parseInt(string_quantity);
			} catch (NumberFormatException e) {
				errorMessage = new ErrorMessage("Quantity Parameter Error", "not corret format of credential value");
				request.getSession().setAttribute("error", errorMessage);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}
		
		}
		//Acceptable only positive values
		if (quantity != null && !quantity.equals("")) {
			if (quantity < 1) {
				errorMessage = new ErrorMessage("Quantity Parameter Error", "negative quantity");
				request.getSession().setAttribute("error", errorMessage);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}
		}

		// Set true only if user wish decrement the number of items added to cart
		if (request.getParameter("sub") != null && request.getParameter("sub").equals("true")) {
			decrement = true;
		}

		//Error manage: check if really exist a correspondence between vendor to item
		PriceDAO priceDAO = new PriceDAO(connection);
		Price price = new Price();
		try {
			price = priceDAO.findOneByItemIdAndVendorId(itemAdd, vendorAdd);
		} catch (DatabaseException e) {
			errorMessage = new ErrorMessage("Database Error", e.getBody());
			request.getSession().setAttribute("cartSession", cartSession);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}

		// Search if vendor is already present in cartSession
		boolean vendorIsPresent = false;
		if (cartSession.containsKey(vendorAdd)) {
			vendorIsPresent = true;
		}

		// If vendor is not present and decrement --> error : is not a possible situation 
		if (!vendorIsPresent && decrement) {
			errorMessage = new ErrorMessage("Request Error", "vendor not present in cart");
			request.getSession().setAttribute("cartSession", cartSession);
			response.sendRedirect(getServletContext().getContextPath() + "/cart");
			return;
		}
	
		if (!vendorIsPresent && !decrement) // vendor is not present
		{
			HashMap<Integer, Integer> itemQuantity = new HashMap<Integer, Integer>();
			itemQuantity.put(itemAdd, quantity);
			cartSession.put(vendorAdd, itemQuantity);

		} else { // vendor is present

			// Search if item is already present
			boolean isPresent = false;
			if (cartSession.get(vendorAdd).containsKey(itemAdd)) {
				isPresent = true;
			}

			// item is not present and decrement --> error : is not a possible situation 
			if (!isPresent && decrement) {
				errorMessage = new ErrorMessage("Request Error", "item not present in cart");
				request.getSession().setAttribute("cartSession", cartSession);
				response.sendRedirect(getServletContext().getContextPath() + "/cart");
				return;
			}

			if (!isPresent && !decrement) { // if not present create it

				cartSession.get(vendorAdd).put(itemAdd, quantity);
				
			} else { // item is already present
				int actualQuantity = cartSession.get(vendorAdd).get(itemAdd);

				if (decrement) {
					if (actualQuantity < 2) { // quantity == 1
						cartSession.get(vendorAdd).remove(itemAdd);
						if (cartSession.get(vendorAdd).isEmpty()) {
							cartSession.remove(vendorAdd);
						}
					} else { // simply decrement quantity
						actualQuantity = actualQuantity - quantity;
						cartSession.get(vendorAdd).put(itemAdd, actualQuantity);
					}
				} else { // simply decrement quantity
					actualQuantity = actualQuantity + quantity;
					cartSession.get(vendorAdd).put(itemAdd, actualQuantity);
				}
			}
		}
		
		request.getSession().setAttribute("cartSession", cartSession);
		response.sendRedirect(getServletContext().getContextPath() + "/cart");

	}
}
