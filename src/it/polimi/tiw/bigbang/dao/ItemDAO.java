package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.User;

public class ItemDAO {
	private Connection con;

	public ItemDAO(Connection connection) {
		this.con = connection;
	}

	public ArrayList<Item> findItemsById(ArrayList<Integer> items) throws SQLException {
		
		String query = "SELECT  id, name, description, category, picture FROM item  WHERE id = ?";
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
						item.setDescription(result.getString("desctiption"));
						item.setCategory(result.getString("category"));
						item.setPicture(result.getString("picture"));
						cartItems.add(item);
					}
				}
			}
		}
		return (ArrayList<Item>) cartItems;
	}
	
	public ArrayList<Item> findItemsByWord(String research) throws SQLException{
		String query = "SELECT  id, name, description, category, picture FROM item WHERE name LIKE  '%'||?||'%'  OR description LIKE  '%'||?||'%' ";
		List<Item> searchItems = new ArrayList<Item>();
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setString(1, research);
			pstatement.setString(2, research);
			try(ResultSet result = pstatement.executeQuery();){
				if (!result.isBeforeFirst())
					return null;
				else {
					while(result.next()) {
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
