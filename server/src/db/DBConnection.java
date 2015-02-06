package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
