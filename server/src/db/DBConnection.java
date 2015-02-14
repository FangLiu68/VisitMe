package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBConnection {
	private Connection conn = null;

	public DBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost:8889/mysql?"
							+ "user=root&password=mypass");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SetVisitedRestaurants(String user_id, String visited_list) {
		try {
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			String sql = "UPDATE USERS SET visited_business_id=\"" + visited_list + "\" WHERE user_id=" + user_id;
			stmt.executeUpdate(sql);
			
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
	}
	
	private Set<String> getCategories(String business_id) {
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT categories from RESTAURANTS WHERE business_id='" + business_id +"'";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Set<String> set = new HashSet<>();
				String[] categories = rs.getString("categories").split(",");
				for(String categorie : categories) {
					set.add(categorie.trim());
				}
				return set;
			}
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return new HashSet<String>();
	}
	
	private Set<String> getBusinessId(String category) {
		Set<String> set = new HashSet<String>();
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT business_id from RESTAURANTS WHERE categories LIKE '%" + category + "%'" ;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String business_id = rs.getString("business_id");
				set.add(business_id);
			}
			return set;
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return set;
	}
	
	public JSONArray RecommendRestaurants(String user_id) {
		JSONArray array = null;
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT visited_business_id from USERS WHERE user_id=" + user_id;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String[] visited_restaurants = rs.getString("visited_business_id").split(",");
				Set<String> visitedRestaurants = new HashSet<String>();
				for (String restaurant : visited_restaurants) {
					visitedRestaurants.add(restaurant);
				}
				Set<String> allCategories = new HashSet<String>();
				for (String restaurant : visited_restaurants) {
					allCategories.addAll(getCategories(restaurant));
				}
				Set<String> allRestaurants = new HashSet<String>();
				for (String category : allCategories) {
					Set<String> set = getBusinessId(category);
					allRestaurants.addAll(set);
				}
				Set<String> diff = new HashSet<String>();
				for (String business_id : allRestaurants) {
					if (!visitedRestaurants.contains(business_id)) {
						diff.add(business_id);
					}
				}
				return new JSONArray(diff);
			}
			
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return array;
	}
	
	public JSONArray GetRestaurantsNearLoation(double lat, double lon) {
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT business_id, name, full_address, categories, stars, city, state from RESTAURANTS LIMIT 10";
			ResultSet rs = stmt.executeQuery(sql);
			List<JSONObject> list = new ArrayList<JSONObject>();
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.append("business_id", rs.getString("business_id"));
				obj.append("name", rs.getString("name"));
				obj.append("stars", rs.getFloat("stars"));
				obj.append("full_address", rs.getString("full_address"));
				obj.append("city", rs.getString("city"));
				obj.append("state", rs.getString("state"));
				obj.append("categories",
						DBImport.StringToJSONArray(rs.getString("categories")));
				list.add(obj);
			}
			return new JSONArray(list);
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return null;
	}

}
