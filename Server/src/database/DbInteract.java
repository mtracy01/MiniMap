package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/* IMPORTANT USAGE DETAILS:
* To interact with the database, instantiate a new DbInteract object. When you are done
* using the database, please close the connection using the closeConnection() method.
* 
* Users should be added upon account creation or initial login or whatever we call it.
* Groups are represented by String arrays where the first element is the name of the group.
* Please adhere to above convention so I don't have to spend 5 minutes making a proper class.
*/

public class DbInteract {
	private Connection conn = null;
	
	public DbInteract() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = "jdbc:mysql://localhost:3306/minimap";
		String user = "root";
		String pass = "raspberry";
		
		try {
			conn = DriverManager.getConnection(url, user, pass);
		} catch (SQLException ex) {
			System.out.printf(ex.getMessage());
		}
	}
	
	/** addUser
	* Adds a user to the database. This should be done upon registration/activation of account
	* @param userID		The unique identifier associated with a given user
	*/
	public void addUser(String userID) {
		String query = String.format("INSERT INTO usergroups (user, groups) VALUES ('" + userID +"', '')");
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	/** getGroupsByID
	* @param userID		The unique identifier associated with a given user
	* @return			An array of strings, each one containing the IDs of group members. null if not found
	*/
	public String[] getGroupsByID(String userID) {
		String query = String.format("SELECT groups FROM usergroups WHERE user = '" + userID + "'");
		String rawGroup = "";
		String[] groups = null;
		Statement stmt = null;
		ResultSet rslt = null;
		try {
			stmt = conn.createStatement();
			rslt = stmt.executeQuery(query);
			if (rslt.next()) {
				rawGroup = rslt.getString(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		if(rawGroup != null) {
			groups = rawGroup.split(":");
		}
		closeStatementsAndResults(stmt, rslt);
		return groups;
	}
	
	/** addGroup
	* @param userID		The unique identifier associated with a given user
	* @param group		A group in the form - "groupname:id:id:id:...etc" where ID is a unique identifier
	*/
	public void addGroup(String userID, String group) {
		String[] groups = getGroupsByID(userID);
		String augmentedGroups = "";
		String query = null;
		Statement stmt = null;
				
		if(groups != null && !groups[0].equals("")) {
			for(int i = 0; i < groups.length; i++) {
				augmentedGroups += groups[i] + ":";
			}
		}
		augmentedGroups += group;
		query = String.format("UPDATE usergroups SET groups='" + augmentedGroups + "' WHERE user='" + userID + "'");
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeStatementsAndResults(stmt, null);
	}
	
	/** removeGroup
	* @param userID		The unique identifier associated with a given user
	* @param groupName	The name of the group(s) to remove, given by first entry of a group. * removes all groups
	* 
	*/
	public void removeGroup(String userID, String groupName) {
		String[] groups = getGroupsByID(userID);
		String reducedGroups = "";
		String query = null;
		Statement stmt = null;
				
		if(groups != null && !groups[0].equals("") && !groupName.equals("*")) {
			for(int i = 0; i < groups.length; i++) {
				if(!(groups[i].substring(0, groups[i].indexOf(","))).equals(groupName)) {
					reducedGroups += groups[i] + ":";
				}
			}
		}
		// To remove hanging colons (ew)
		if(reducedGroups.length() > 0) {
			reducedGroups = reducedGroups.substring(0, reducedGroups.length() -1);
		}
		if(groupName.equals("*")) {
			query = "UPDATE usergroups SET groups = ''";
		} else {
			query = String.format("UPDATE usergroups SET groups='" + reducedGroups + "' WHERE user='" + userID + "'");
		}
		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeStatementsAndResults(stmt, null);
	}
	
	/** closeConnection
	* please call this when you're done using the database
	*/
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.printf("Unable to disconnect\n");
		}
	}
	
	/** convertToGroupString
	* This is for converting group arrays into string form for transmission
	* between client and server.
	*/
	public static String convertToGroupString(String[] group) {
		String str = "";
		for(int i = 0; i < group.length-1; i++) {
			str.concat(group[i]);
			str.concat(":");
		}
		str.concat(group[group.length - 1]);
		return str;
	}
	
	/** convertToGroupArray
	* Splits a group string on colons for convenience
	*/
	public static String[] convertToGroupArray(String group) {
		return group.split(":");
	}
	
	/** closeStatementsAndResults
	* To be called at the end of any function that uses a query
	* @param stmt		A sql statement
	* @param rslt		Results generated by a query
	*/
	private void closeStatementsAndResults(Statement stmt, ResultSet rslt) {
		try {
			if (rslt != null) {
				rslt.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.printf("Problem when closing statements\n");
		}
	} 
}
