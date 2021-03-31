package it.polimi.tiw.bigbang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.bigbang.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, name, surname, email, address FROM user  WHERE name = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setEmail(result.getString("email"));
					user.setAddress(result.getString("address"));
					return user;
				}
			}
		}
	}

}
