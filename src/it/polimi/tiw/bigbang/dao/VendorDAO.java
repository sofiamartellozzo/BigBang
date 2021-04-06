package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.bigbang.beans.Item;
import it.polimi.tiw.bigbang.beans.Vendor;

public class VendorDAO {
	private Connection con;

	public VendorDAO(Connection connection) {
		this.con = connection;
	}

	public ArrayList<Vendor> findById(ArrayList<Integer> vendor) throws SQLException{
		String query = "SELECT id, name, score, free_limit FROM vendor WHERE id = ?";
		ArrayList<Vendor> vendors = new ArrayList<Vendor>();
		for (Integer i : vendor) {
			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, i);
				try (ResultSet result = pstatement.executeQuery();) {
					if (!result.isBeforeFirst()) 
						return null;
					else {
						result.next();
						Vendor v = new Vendor();
						v.setId(result.getInt("id"));
						v.setName(result.getString("name"));
						v.setScore(result.getInt("score"));
						v.setFree_limit(result.getInt("free_limit"));
						vendors.add(v);
					}
				}
			}
		}
		return vendors;
	}
		
	public Vendor findBySingleId(Integer vendor) throws SQLException{
		String query = "SELECT id, name, score, free_limit FROM vendor WHERE id = ?";
			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, vendor);
				try (ResultSet result = pstatement.executeQuery();) {
					if (!result.isBeforeFirst()) 
						return null;
					else {
						result.next();
						Vendor v = new Vendor();
						v.setId(result.getInt("id"));
						v.setName(result.getString("name"));
						v.setScore(result.getInt("score"));
						v.setFree_limit(result.getInt("free_limit"));
						return v;
					}
				}
			}
	}
}
