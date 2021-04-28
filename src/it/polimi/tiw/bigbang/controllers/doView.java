package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;
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
		
    	HttpSession session = request.getSession();
		
		//get the id of the item of which the user ask the visualization
		Integer idItemAsked = null;
		String wordSearchedString = null;
		User user = (User) session.getAttribute("user");
		Integer idUser = user.getId();
		View view = null;
		try {
			idItemAsked = Integer.parseInt(request.getParameter("viewId"));
			wordSearchedString = request.getParameter("keyword");
			if (idItemAsked == null || idItemAsked < 0 || wordSearchedString == null || wordSearchedString.isEmpty()) {
				throw new Exception("Id asked to be viewed not valid or problem in word searched error");
			}
			
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
			e1.printStackTrace();
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Problem on adding a new view!");
			return;
		}
		
		//saving the viewId in the session
		List<Integer> idItemViewed = new ArrayList<Integer>();
		if(session.getAttribute("itemViewed")!=null) {
			idItemViewed = (List<Integer>) session.getAttribute("itemViewed");
			System.out.println("create new session attribute");
		}
		idItemViewed.add(idItemAsked);
		session.setAttribute("itemViewed", idItemViewed);
		
		//reloading the search page set this boolean attribute to false to not lost this and all old item viewed yet
		session.setAttribute("clearViewItemList", false);
		
		//redirect to search
		String path = getServletContext().getContextPath()+ "/search?keyword=" +wordSearchedString;
	    response.sendRedirect(path);
		
    }
	

}
