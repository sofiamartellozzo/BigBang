package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
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

import it.polimi.tiw.bigbang.beans.ErrorMessage;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.View;
import it.polimi.tiw.bigbang.dao.ViewDAO;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;


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

    	//get the user by the session
    	User user = (User) session.getAttribute("user");
    	Integer idUser = user.getId();

    	//create the variable to store a possible error
    	ErrorMessage errorMessage;
    	errorMessage = (ErrorMessage) session.getAttribute("error");

		//get the id of the item of which the user ask the visualization
		Integer idItemAsked = null;
		String wordSearchedString = null;

		//create a variable for the new View created
		View view = null;

		try {
			idItemAsked = Integer.parseInt(request.getParameter("viewId"));
			wordSearchedString = request.getParameter("keyword");
			if (idItemAsked == null || idItemAsked < 0 || wordSearchedString == null || wordSearchedString.isEmpty()) {
				//errorMessage = new ErrorMessage("Request Error", "not valid Item Id found to be viewed ");
				throw new Exception("Id asked to be viewed not valid or problem in word searched error");
			}

			view = new View();
			view.setUser_id(idUser);
			view.setItem_id(idItemAsked);
		} catch (Exception e) {
			errorMessage = new ErrorMessage("Request Error","Problem in finding idItem to visualized!");
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Problem in finding idItem to visualized!");
			session.setAttribute("clearViewItemList", false);
			String path = getServletContext().getContextPath()+ "/search?keyword=&&error=" +wordSearchedString +errorMessage;
		    response.sendRedirect(path);
			return;
		}

		//creating the new object in the DB
		ViewDAO viewDAO = new ViewDAO(connection);
		try {
			viewDAO.createOneViewByUserIdAndItemId(idUser, idItemAsked);
		}catch (DatabaseException e1) {
			errorMessage = new ErrorMessage("Database Error", e1.getBody());
			session.setAttribute("clearViewItemList", false);
			String path = getServletContext().getContextPath()+ "/search?keyword=&&error=" +wordSearchedString +errorMessage;
		    response.sendRedirect(path);
			return;
		}

		//saving the viewId in the session
		List<Integer> idItemViewed = new ArrayList<Integer>();
		if(session.getAttribute("itemViewed")!=null) {
			idItemViewed = (List<Integer>) session.getAttribute("itemViewed");
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
