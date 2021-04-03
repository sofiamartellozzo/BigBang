package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.bigbang.beans.Item;

public class ItemDAO {
	private Connection con;

	public ItemDAO(Connection connection) {
		this.con = connection;
	}

	public List<Item> findItemsById(ArrayList<Integer> items) throws SQLException {

		String query = "SELECT * FROM item WHERE id = ?";
		List<Item> cartItems = new ArrayList<Item>();
		
		for(Integer i: items) {
			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, i);
				
				try (ResultSet result = pstatement.executeQuery();) {
					if (!result.isBeforeFirst()) // no results
						return null;
					else {
						result.next();
						Item item = new Item();
						item.setId(result.getInt("id"));
						item.setName(result.getString("name"));
						item.setDescription(result.getString("description"));
						item.setCategory(result.getString("category"));
						item.setPicture(result.getString("picture"));
						cartItems.add(item);
						}
					}
				}
		}
		return cartItems;
	}

	public ArrayList<Item> findLastViewedItemsByUser(int userID) throws SQLException {

		String query = "SELECT I.* FROM item AS I, view as V WHERE V.id_user = ? AND V.id_item = I.id ORDER BY data LIMIT 5";

		ArrayList<Item> viewedItems = new ArrayList<>();

		try (PreparedStatement pStatement = con.prepareStatement(query)) {
			pStatement.setInt(1, userID);

			try (ResultSet result = pStatement.executeQuery()) {
				if (result.isBeforeFirst())
					while (result.next()) {
						Item item = new Item() {
							{
								setId(result.getInt("id"));
								setName(result.getString("name"));
								setDescription(result.getString("description"));
								setCategory(result.getString("category"));
								setPicture(result.getString("picture"));
							}
						};
						viewedItems.add(item);
					}
			}
		}

		return viewedItems;
	}

	public ArrayList<Item> findNItemsByCategory(String category, int number) throws SQLException {

		String query = "SELECT * FROM item WHERE category = ? LIMIT ?";

		ArrayList<Item> viewedItems = new ArrayList<>();

		try (PreparedStatement pStatement = con.prepareStatement(query)) {
			pStatement.setString(1, category);
			pStatement.setInt(2, number);

			try (ResultSet result = pStatement.executeQuery()) {
				if (result.isBeforeFirst())
					while (result.next()) {
						Item item = new Item() {
							{
								setId(result.getInt("id"));
								setName(result.getString("name"));
								setDescription(result.getString("description"));
								setCategory(result.getString("category"));
								setPicture(result.getString("picture"));
							}
						};
						viewedItems.add(item);
					}
			}
		}

		return viewedItems;
	}

	public ArrayList<Item> findItemsByWord(String research) throws SQLException {
		String query = "SELECT  id, name, description, category, picture FROM item WHERE name LIKE  '%'||?||'%'  OR description LIKE  '%'||?||'%' ";
		List<Item> searchItems = new ArrayList<Item>();
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, research);
			pstatement.setString(2, research);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					while (result.next()) {
						Item item = new Item();
						item.setId(result.getInt("id"));
						item.setName(result.getString("name"));
						item.setDescription(result.getString("description"));
						item.setCategory(result.getString("category"));
						item.setPicture(result.getString("picture"));
						searchItems.add(item);
					}

				}
			}
		}

		return (ArrayList<Item>) searchItems;
	}

}
