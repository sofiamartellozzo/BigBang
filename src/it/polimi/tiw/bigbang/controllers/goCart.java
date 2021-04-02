package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.utils.ConnectionHandler;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;


public class goCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine = null;

	public goCart() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		this.templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
		
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// If the user is not logged in (not present in session) redirect to the login
				String loginpath = getServletContext().getContextPath() + "/login.html";
				HttpSession session = request.getSession();
				if (session.isNew() || session.getAttribute("user") == null) {
					response.sendRedirect(loginpath);
					return;
				}
				
		// Get params 
					
			ArrayList<Integer> items = null;
	
				try {
					items = (ArrayList<Integer>) session.getAttribute("items"); 
				} catch (NumberFormatException | NullPointerException e) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
					return;
				}
				
				ItemDAO IDAO = new ItemDAO(connection);
				ArrayList<Item> cartItem = null ;
				try {
					cartItem = IDAO.findItemsById(items);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String path = "cart.html";
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				dispatcher.forward(request, response);
				final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
				ctx.setVariable("cartItems", cartItem);
				templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
