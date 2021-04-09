package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.bigbang.beans.ShippingRange;
import it.polimi.tiw.bigbang.beans.Vendor;

public class VendorDAO {
	private Connection con;

	public VendorDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Vendor> findById(List<Integer> vendorIDs) throws SQLException {
		String vendorsQuery = "SELECT id, name, score, free_limit FROM vendor WHERE id = ?";
		List<Vendor> vendors = new ArrayList<Vendor>();
		for (Integer id : vendorIDs) {
			PreparedStatement pStatementVendors = con.prepareStatement(vendorsQuery);
			pStatementVendors.setInt(1, id);
			ResultSet resultVendors = pStatementVendors.executeQuery(); 
			if (resultVendors.isBeforeFirst()) {
				resultVendors.next();
				Vendor v = new Vendor();
				v.setId(resultVendors.getInt("id"));
				v.setName(resultVendors.getString("name"));
				v.setScore(resultVendors.getInt("score"));
				v.setFree_limit(resultVendors.getInt("free_limit"));
				
				vendors.add(v);
			}
		}
		return vendors;
	}

	public List<Vendor> findFullById(List<Integer> vendorIDs) throws SQLException {
		String vendorsQuery = "SELECT id, name, score, free_limit FROM vendor WHERE id = ?";
		String shippingRangeQuery = "SELECT R.* FROM `range` R, shipping_policy SP WHERE R.id = SP.id_range AND SP.id_vendor = ?";
		List<Vendor> vendors = new ArrayList<Vendor>();
		for (Integer id : vendorIDs) {
			try (PreparedStatement pStatementVendors = con.prepareStatement(vendorsQuery);) {
				pStatementVendors.setInt(1, id);
				try (ResultSet resultVendors = pStatementVendors.executeQuery();) {
					if (resultVendors.isBeforeFirst()) {
						resultVendors.next();
						Vendor v = new Vendor();
						v.setId(resultVendors.getInt("id"));
						v.setName(resultVendors.getString("name"));
						v.setScore(resultVendors.getInt("score"));
						v.setFree_limit(resultVendors.getInt("free_limit"));
						v.setRanges(new ArrayList<ShippingRange>());
						try (PreparedStatement pStatementRanges = con.prepareStatement(shippingRangeQuery)) {
							pStatementRanges.setInt(1, id);
							try (ResultSet resultRanges = pStatementRanges.executeQuery()) {
								if (resultRanges.isBeforeFirst()) {
									while (resultRanges.next()) {
										ShippingRange shippingRange = new ShippingRange();
										shippingRange.setId(resultRanges.getInt("id"));
										shippingRange.setMin(resultRanges.getInt("min"));
										shippingRange.setMax(resultRanges.getInt("max"));
										shippingRange.setCost(resultRanges.getFloat("shipping_cost"));
										v.getRanges().add(shippingRange);
									}
								}
							}
						}

						vendors.add(v);
					}
				}
			}
		}
		return vendors;

	}

	public Vendor findBySingleId(Integer vendor) throws SQLException {
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
