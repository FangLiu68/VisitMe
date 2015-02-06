package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class GetRestaurantsNearby
 */
@WebServlet(description = "Get Restaurants near a location with latitude and longitude", urlPatterns = { "/GetRestaurantsNearby" })
public class GetRestaurantsNearby extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetRestaurantsNearby() {
		super();
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println("Init JDBC Exception " + ex.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		String username = "";
		if (request.getParameter("username") != null) {
			username = request.getParameter("username");
		}
		JSONObject obj = new JSONObject();
		try {
			obj.append("username", username);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
			reader.close();
		} catch (Exception e) { /* report an error */
		}

		try {
			JSONObject input = new JSONObject(jb.toString());
			JSONObject obj = null;
			if (input.has("lat") && input.has("lon")) {
				double lat = (Double) input.get("lat");
				double lon = (Double) input.get("lon");
				obj = new JSONObject();
				obj.append("name", "Ramen");
				obj.append("city", "Los Angeles");
				obj.append("lat", lat);
				obj.append("lon", lon);
			}

			Connection conn = null;
			try {
				conn = DriverManager
						.getConnection("jdbc:mysql://localhost:8889/mysql?"
								+ "user=root&password=mypass");
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}

			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(obj);
			out.flush();
			out.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
