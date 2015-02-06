package db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBImport {

	public static String parseString(String str) {
		return str.replace("\"", "\\\"");
	}

	public static String JSONArrayToString(JSONArray array) {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < array.length(); i++) {
				String obj = (String) array.get(i);
				sb.append(obj);
				if (i != array.length() - 1) {
					sb.append(",");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static JSONArray StringToJSONArray(String str) {
		try {
			return new JSONArray(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;
			String line = null;

			try {
				conn = DriverManager
						.getConnection("jdbc:mysql://localhost:8889/mysql?"
								+ "user=root&password=mypass");
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS RESTAURANTS";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE RESTAURANTS "
					+ "(business_id VARCHAR(255) not NULL, "
					+ " name VARCHAR(255), " + "categories VARCHAR(255), "
					+ "city VARCHAR(255), " + "state VARCHAR(255), "
					+ "stars FLOAT," + "full_address VARCHAR(255), "
					+ "latitude FLOAT, " + " longitude FLOAT, "
					+ " PRIMARY KEY ( business_id ))";
			stmt.executeUpdate(sql);

			BufferedReader reader = new BufferedReader(new FileReader(
					"../dataset/business.json"));
			while ((line = reader.readLine()) != null) {
				JSONObject restaurant = new JSONObject(line);
				String business_id = restaurant.getString("business_id");
				String name = parseString(restaurant.getString("name"));
				String categories = parseString(JSONArrayToString(restaurant
						.getJSONArray("categories")));
				String city = parseString(restaurant.getString("city"));
				String state = restaurant.getString("state");
				String full_address = parseString(restaurant
						.getString("full_address"));
				double stars = restaurant.getDouble("stars");
				double latitude = restaurant.getDouble("latitude");
				double longitude = restaurant.getDouble("longitude");
				sql = "INSERT INTO RESTAURANTS " + "VALUES ('" + business_id
						+ "', \"" + name + "\", \"" + categories + "\", '"
						+ city + "', '" + state + "', " + stars + ", \""
						+ full_address + "\", " + latitude + "," + longitude
						+ ")";
				System.out.println(sql);
				stmt.executeUpdate(sql);
			}

			reader.close();
			System.out.println("Done Importing");

		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
	}
}
