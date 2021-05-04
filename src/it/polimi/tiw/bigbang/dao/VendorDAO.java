package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.bigbang.beans.ShippingRange;
import it.polimi.tiw.bigbang.beans.Vendor;
import it.polimi.tiw.bigbang.exceptions.DatabaseException;

public class VendorDAO {
	private Connection con;

	public VendorDAO(Connection connection) {
		this.con = connection;
	}

	/*
	 * USELESS ?? 
	 * 
	 * public List<Vendor> findManyByVendorsId(List<Integer> vendorIDs) throws DatabaseException {
	 
		String vendorsQuery = "SELECT id, name, score, free_limit FROM vendor WHERE id = ?";
		List<Vendor> vendors = new ArrayList<Vendor>();

		for (Integer id : vendorIDs) {
			try (PreparedStatement pStatementVendors = con.prepareStatement(vendorsQuery)) {
				pStatementVendors.setInt(1, id);
				ResultSet resultVendors = pStatementVendors.executeQuery();
				if (resultVendors.isBeforeFirst()) {
					resultVendors.next();
					Vendor v = new Vendor();
					v.setId(resultVendors.getInt("id"));
					v.setName(resultVendors.getString("name"));
					v.setScore(resultVendors.getInt("score"));
					v.setFree_limit(resultVendors.getFloat("free_limit"));
					vendors.add(v);
				}
			} catch (SQLException e) {
				throw new DatabaseException("resources not found");
			}
		}
		return vendors;
	}
*/

	public List<Vendor> findManyByVendorsId(List<Integer> vendorIDs) throws DatabaseException {
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
						v.setFree_limit(resultVendors.getFloat("free_limit"));
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
			} catch (SQLException e) {
				throw new DatabaseException("The server wasn't able to find the requested vendors in database");
			}
		}
		return vendors;
	}

	public Vendor fineOneByVendorId (Integer vendorId) throws DatabaseException {
		String vendorsQuery = "SELECT id, name, score, free_limit FROM vendor WHERE id = ?";
		String shippingRangeQuery = "SELECT R.* FROM `range` R, shipping_policy SP WHERE R.id = SP.id_range AND SP.id_vendor = ?";

		try (PreparedStatement pStatementVendors = con.prepareStatement(vendorsQuery);) {
			pStatementVendors.setInt(1, vendorId);
			try (ResultSet resultVendors = pStatementVendors.executeQuery();) {
				if (resultVendors.isBeforeFirst()) {
					resultVendors.next();
					Vendor v = new Vendor();
					v.setId(resultVendors.getInt("id"));
					v.setName(resultVendors.getString("name"));
					v.setScore(resultVendors.getInt("score"));
					v.setFree_limit(resultVendors.getFloat("free_limit"));
					v.setRanges(new ArrayList<ShippingRange>());
					try (PreparedStatement pStatementRanges = con.prepareStatement(shippingRangeQuery)) {
						pStatementRanges.setInt(1, vendorId);
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

					return v;
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("The server wasn't able to find the requested vendor in database");
		}
		return null;
	}
}
