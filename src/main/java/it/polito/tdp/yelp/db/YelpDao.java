package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao
{

	public List<Business> getAllBusiness()
	{
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				Business business = new Business(res.getString("business_id"), res.getString("full_address"),
						res.getString("active"), res.getString("categories"), res.getString("city"),
						res.getInt("review_count"), res.getString("business_name"), res.getString("neighborhoods"),
						res.getDouble("latitude"), res.getDouble("longitude"), res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<Review> getAllReviews()
	{
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				Review review = new Review(res.getString("review_id"), res.getString("business_id"),
						res.getString("user_id"), res.getDouble("stars"), res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"), res.getInt("votes_useful"), res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<User> getAllUsers()
	{
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				User user = new User(res.getString("user_id"), res.getInt("votes_funny"), res.getInt("votes_useful"),
						res.getInt("votes_cool"), res.getString("name"), res.getDouble("average_stars"),
						res.getInt("review_count"));

				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getAllCitta()
	{
		String sql = "SELECT DISTINCT(business.city) FROM Business";
		
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while (res.next())
			{

				String s = res.getString("city");

				result.add(s);
			}
			
			res.close();
			st.close();
			conn.close();
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Map<String, Business> getBusinessCityYear(Map<String, Business> mappaB, String c, int y)
	{
		String sql = "SELECT * "
				+ "FROM business AS b, reviews AS r "
				+ "WHERE b.business_id = r.business_id "
				+ "		AND city = ? "
				+ "		AND YEAR( r.review_date ) = ? ";
		
		Connection conn = DBConnect.getConnection();
		
		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, c);
			st.setInt(2, y);
			ResultSet res = st.executeQuery();
			
			while (res.next())
			{
				Business business = new Business(res.getString("business_id"), res.getString("full_address"),
						res.getString("active"), res.getString("categories"), res.getString("city"),
						res.getInt("review_count"), res.getString("business_name"), res.getString("neighborhoods"),
						res.getDouble("latitude"), res.getDouble("longitude"), res.getString("state"),
						res.getDouble("stars"));
				
				if(!mappaB.containsKey(business.getBusinessId()))
					mappaB.put(business.getBusinessId(), business);
			}
			
			res.close();
			st.close();
			conn.close();
			return mappaB;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Double> getRecensioni(Map<String, Double> recensioni, int anno)
	{
		String sql = "SELECT r.business_id, AVG(r.stars) AS avg "
						+ "FROM reviews AS r "
						+ "WHERE YEAR(r.review_date) = ? "
						+ "GROUP BY r.business_id ";
		 
		Connection conn = DBConnect.getConnection();
		
		try
		{
			PreparedStatement st = conn.prepareStatement(sql); 
			st.setInt(1, anno);
			ResultSet res = st.executeQuery();
			
			while (res.next())
			{
				 
				String id = res.getString("business_id");
				Double r = res.getDouble("avg");
				 
				if(!recensioni.containsKey(id))
				{
					recensioni.put(id, r);
				}
			}
			
			res.close();
			st.close();
			conn.close();
			return recensioni;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	} 
}
