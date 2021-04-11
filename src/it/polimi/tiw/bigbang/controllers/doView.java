package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;
import it.polimi.tiw.bigbang.beans.ExtendedItem;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.View;
import it.polimi.tiw.bigbang.dao.ViewDAO;


public class doView extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;
    private Connection connection;
    private TemplateEngine templateEngine;
    
    public void init() throws ServletException {
		servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	String path = "search";
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, webContext, response.getWriter());
    }
    
    @SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("I arrived here");
    	HttpSession session = request.getSession();
		
		//get the id of the item of wich the user ask the visualization
		Integer idItemAsked = null;
		User user = (User) session.getAttribute("user");
		Integer idUser = user.getId();
		View view = null;
		List<ExtendedItem> searchItems = (ArrayList<ExtendedItem>) session.getAttribute("itemSearch");
		try {
			idItemAsked = Integer.parseInt(request.getParameter("viewId"));
			view = new View();
			view.setUser_id(idUser);
			view.setItem_id(idItemAsked);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Problem in finding idItem to visualized!");
			return;
		}
		
		//creating the new object in the DB
		ViewDAO viewDAO = new ViewDAO(connection);
		try {
			viewDAO.createView(idUser, idItemAsked);
		}catch (SQLException e1) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Problem on adding a new view!");
			return;
		}
		
		//saving the viewId in the session
		List<Integer> idItemViewed = new ArrayList<>();
		idItemViewed.add(idItemAsked);
		session.setAttribute("itemViewed", idItemViewed);
		List<ExtendedItem> itemsSearch = new ArrayList<>();
		itemsSearch = (ArrayList<ExtendedItem>) session.getAttribute("itemSearch");
		
		//redirect to search
		String path = getServletContext().getContextPath() + "/search";
		response.sendRedirect(path);
	}

}
