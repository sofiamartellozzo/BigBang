package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.bigbang.beans.Order;

public class OrderDAO {
	private Connection connection;
	
	public OrderDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Order> findOrdersByUserID(int userID) throws SQLException {
		String query = "SELECT * FROM `order` WHERE id_user = ? ORDER BY date AND id";
		List<Order> userOrders = new ArrayList<>();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()) {
					while (result.next()) {
						Order order = new Order();
						order.setId(result.getString("id"));
						order.setId_user(result.getInt("id_user"));
						order.setId_item(result.getInt("id_item"));
						order.setDate(result.getTimestamp("date"));
						order.setQuantity(result.getInt("quantity"));
						userOrders.add(order);
					}
				}
			}
		}
		
		return userOrders;
	}
}
