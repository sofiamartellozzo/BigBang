package it.polimi.tiw.bigbang.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.Order;
import it.polimi.tiw.bigbang.beans.Price;
import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.ShippingRange;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.dao.UserDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class doLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = DBConnectionProvider.getConnection(servletContext);
		templateEngine = TemplateEngineProvider.getTemplateEngine(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		String path = "login";
		templateEngine.process(path, ctx, response.getWriter());
	}

	@SuppressWarnings("serial")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
		String email = null;
		String pwd = null;
		try {
			// usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			// pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
			email = request.getParameter("email");
			pwd = request.getParameter("pwd");
			if (email == null || pwd == null || email.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		// query db to authenticate for user
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkCredentials(email, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check credentials");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (user == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect email or password");
			path = "login";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", user);
			
			//just for debugging 
			
			List<ShippingRange> range = new ArrayList<ShippingRange>();
			ShippingRange s1 = new ShippingRange();
			ShippingRange s2 = new ShippingRange();
			
			s1.setCost(6.1f);
			s1.setId(1);
			s1.setMax(2);
			s1.setMin(0);
			range.add(s1);
			
			s2.setCost(5.2f);
			s2.setId(2);
			s2.setMax(5);
			s2.setMin(3);
			range.add(s2);
			
			Vendor v1= new Vendor();
			v1.setId(1);
			v1.setName("SAMcommerce");
			v1.setFree_limit(7);
			v1.setScore(5);
			v1.setRanges(range);
			
			SelectedItem si1 = new SelectedItem();
			SelectedItem si2 = new SelectedItem();
			Item i1= new Item();
			Item i2 =new Item();
			
			i1.setId(5);
			i1.setCategory("Technology");
			i1.setDescription("Useful for seeing what''s going on on your computer");
			i1.setName("Monitor");
			i1.setPicture("link5");
			i2.setId(9);
			i2.setCategory("Food");
			i2.setDescription("Best food in the world, even better than gelato!");
			i2.setName("Pizza");
			i2.setPicture("link9");
			
			si1.setItem(i1);
			si2.setItem(i2);
			
			si1.setCost(62.21f);
			si2.setCost(4.5f);
			
			si1.setQuantity(2);
			si2.setQuantity(3);
			
			List<SelectedItem> ls = new ArrayList<SelectedItem>();
			ls.add(si2);
			ls.add(si1);
			
			List<ShippingRange> range2 = new ArrayList<ShippingRange>();
			ShippingRange s12 = new ShippingRange();
			ShippingRange s22 = new ShippingRange();
			
			s12.setCost(6.0f);
			s12.setId(3);
			s12.setMax(2);
			s12.setMin(0);
			range2.add(s12);
			
			s22.setCost(5.0f);
			s22.setId(3);
			s22.setMax(15);
			s22.setMin(3);
			range2.add(s22);
			
			Vendor v12= new Vendor();
			v12.setId(2);
			v12.setName("MercatinoMilano");
			v12.setFree_limit(70);
			v12.setScore(1);
			v12.setRanges(range2);
			
			SelectedItem si12 = new SelectedItem();
			SelectedItem si22 = new SelectedItem();
			Item i12= new Item();
			Item i22 =new Item();
			
			i12.setId(5);
			i12.setCategory("Tec");
			i12.setDescription("Useful for seeing what''s going on on your computer");
			i12.setName("Mon");
			i12.setPicture("link5");
			i22.setId(9);
			i22.setCategory("Food");
			i22.setDescription("Best food in the world, even better than gelato!");
			i22.setName("Pizza");
			i22.setPicture("link9");
			
			si12.setItem(i12);
			si22.setItem(i22);
			
			si12.setCost(62.0f);
			si22.setCost(4.0f);
			
			si12.setQuantity(1);
			si22.setQuantity(1);
			
			List<SelectedItem> ls2 = new ArrayList<SelectedItem>();
			ls2.add(si22);
			ls2.add(si12);
			
			HashMap<Vendor,List<SelectedItem>> hm = new HashMap<Vendor,List<SelectedItem>>();
			hm.put(v1,ls);
			hm.put(v12,ls2);
			
			request.getSession().setAttribute("cart", hm);
			path = getServletContext().getContextPath() + "/home";
			response.sendRedirect(path);
		}
	}

	public void destroy() {
		try {
			DBConnectionProvider.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
