package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.bigbang.beans.OrderInfo;
import it.polimi.tiw.bigbang.beans.OrderedItem;

public class OrderDAO {
	private Connection connection;
	
	public OrderDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Map<OrderInfo, List<OrderedItem>> findOrdersByUserID(int userID) throws SQLException {
		
		String orderInfoQuery = "SELECT * FROM order_info WHERE id_user = ? ORDER BY date DESC";
		String orderedItemsQuery = "SELECT * FROM ordered_item WHERE id_order = ? ORDER BY id_item";
		
		Map<OrderInfo, List<OrderedItem>> userOrders = new LinkedHashMap<>();
		PreparedStatement psInfo = connection.prepareStatement(orderInfoQuery);
		psInfo.setInt(1, userID);
		ResultSet resultInfo = psInfo.executeQuery();
		while (resultInfo.next()) {
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(resultInfo.getString("id"));
			orderInfo.setId_user(resultInfo.getInt("id_user"));
			orderInfo.setId_vendor(resultInfo.getInt("id_vendor"));
			orderInfo.setDate(resultInfo.getTimestamp("date"));
			orderInfo.setShipping_cost(resultInfo.getFloat("shipping_cost"));
			
			List<OrderedItem> orderedItems = new ArrayList<>();
			PreparedStatement psItem = connection.prepareStatement(orderedItemsQuery);
			psItem.setString(1, orderInfo.getId());
			ResultSet resultItem = psItem.executeQuery();
			while (resultItem.next()) {
				OrderedItem orderedItem = new OrderedItem();
				orderedItem.setId_item(resultItem.getInt("id_item"));
				orderedItem.setId_order(resultItem.getString("id_order"));
				orderedItem.setQuantity(resultItem.getInt("quantity"));
				orderedItem.setCost(resultItem.getFloat("cost"));
				orderedItems.add(orderedItem);
				orderInfo.setTotal_items_cost(orderInfo.getTotal_items_cost() + (orderedItem.getCost() * orderedItem.getQuantity()));
			}
			userOrders.put(orderInfo, orderedItems);
		}
		
		return userOrders;
	}
	
//	public List<OrderedItem> findOrdersByUserID(int userID) throws SQLException {
//		String query = "SELECT * FROM `order` WHERE id_user = ? ORDER BY date AND id";
//		List<OrderedItem> userOrders = new ArrayList<>();
//		
//		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
//			pstatement.setInt(1, userID);
//			try (ResultSet result = pstatement.executeQuery()) {
//				if (result.isBeforeFirst()) {
//					while (result.next()) {
//						OrderedItem order = new OrderedItem();
//						order.setId(result.getString("id"));
//						order.setId_user(result.getInt("id_user"));
//						order.setId_item(result.getInt("id_item"));
//						order.setDate(result.getTimestamp("date"));
//						order.setQuantity(result.getInt("quantity"));
//						userOrders.add(order);
//					}
//				}
//			}
//		}
//		
//		return userOrders;
//	}
}
