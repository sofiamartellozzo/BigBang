package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.bigbang.beans.Shipping;

public class ShippingDAO {
	
	private Connection connection;
	
	public ShippingDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Shipping findShippingPriceById(int vendor, int quantities) throws SQLException{
		
		String query = "SELECT * FROM progtiw.range AS R JOIN progtiw.shipping_policy AS S JOIN progtiw.vendor AS V 	WHERE R.id = S.id_range AND S.id_vendor = V.id AND R.min< ? AND R.max > ? AND V.id = ?";
			
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, quantities);
			pstatement.setInt(2, quantities);
			pstatement.setInt(3, vendor);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					Shipping s= new Shipping();
					s.setFreeLimit(result.getInt("free_limit"));
					s.setIdVendor(result.getInt("id"));
					s.setShippingCost(result.getInt("shipping_cost"));
					return s;
				}
			}
		}
	}
}
