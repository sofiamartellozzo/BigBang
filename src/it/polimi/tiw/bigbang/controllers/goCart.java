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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.Price;
import it.polimi.tiw.bigbang.beans.User;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.dao.ItemDAO;
import it.polimi.tiw.bigbang.utils.DBConnectionProvider;
import it.polimi.tiw.bigbang.utils.TemplateEngineProvider;

public class goCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public goCart() {
		super();
	}

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
		
	
		//Get items added to cart 
		HashMap<Vendor,List<SelectedItem>> cart = new HashMap<Vendor,List<SelectedItem>>(); //items added to cart from session
		
		try {
			cart = (HashMap<Vendor, List<SelectedItem>>) session.getAttribute("cart");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}
		
		//ricostruire il carrello 
		//calcolare la spedizione e il totale
		
		/**
		//Search all different vendors in Cart
			List<Integer> vendorInCart = new ArrayList<Integer>();
			
			for(Price p: itemInCart) {
				if(!(vendorInCart.contains(p.getIdVendor()))) {vendorInCart.add(p.getIdVendor());}
			}
		
		//For each vendor create a list of items sold by him 
			List<VendorItemCart> vendorList = new ArrayList<VendorItemCart>();
			
			for(int v: vendorInCart) {
				
				//collect informations about vendor
				
				VendorItemCart vic= new VendorItemCart();
				VendorDAO vDAO = new VendorDAO(connection);
				Vendor vendor = new Vendor();
				try {
					
					vendor = vDAO.findBySingleId(v);
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				vic.setVendorName(vendor.getName());
				vic.setVendorScore(vendor.getScore());
				
				//collect information about items sold by vendor
				
				for(Price p: itemInCart) {
					if(p.getIdVendor()==v) {
						SelectedItem ic = new SelectedItem();
						
						ItemDAO iDAO = new ItemDAO(connection);
						Item item = new Item();
						try {
							
							item = iDAO.findItemsBySingleId(p.getIdItem());
							
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						ic.setCost(p.getPrice());
						ic.setIdItem(p.getIdItem());
						ic.setItemName(item.getName());
						ic.setQuantities(p.getQuantity());
						vic.setItemVendor(ic);
					}
				}
				
				//collect information about shipping policy
				
				System.out.println(vic.getItemVendor().size());
				
				ShippingDAO sDAO = new ShippingDAO(connection);
				Shipping s= new Shipping();
				try {
					
					s = sDAO.findShippingPriceById(v, vic.getItemVendor().size());
					
				}catch (SQLException e) {
					e.printStackTrace();
				}
				
				if(vic.getItemVendor().size()>=s.getFreeLimit()) {vic.setShippingCost(0);}
				else {vic.setShippingCost(s.getShippingCost());}
				
				vendorList.add(vic);
			}
			*/
		
		String path = "cart";
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		webContext.setVariable("cart", cart);
		webContext.setVariable("user", user);
		templateEngine.process(path, webContext, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
	}

	public void destroy() {
		try {
			DBConnectionProvider.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
