package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.util.buf.StringUtils;

import bean.BookBean;

public class BookDAO {

	private DataSource ds;

	public BookDAO() throws ClassNotFoundException {
		try {
			ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			System.out.println(e.getMessage());
		}

	}

	public ArrayList<BookBean> retrieveAnyBookOrBooks(String bid, String category, ArrayList<String> bids)
			throws SQLException {
		String query = "Select * FROM BOOK ";
		Connection con = this.ds.getConnection();
		String queryValueString = "";
		if (!bid.equals("")) {
			query = query + "WHERE BID = ?";
			queryValueString = bid;
		} else if (!category.equals("")) {
			query = query + "WHERE CATEGORY = ?";
			queryValueString = category;
		} else if (bids != null) {
			query = query + "WHERE BID IN (";
			for (String temp : bids) {
				query = query + "?,";
			}
			query = query.substring(0, query.length() - 1);
			query = query + ")";

		} else {
			System.out.println("Requesting all Books");
		}

		ArrayList<BookBean> arraylist = new ArrayList<BookBean>();

		PreparedStatement sanatizedQuery = con.prepareStatement(query);
		try {
			if (bids != null) {
				for (int i = 0; i < bids.size(); i++) {
					sanatizedQuery.setString(i + 1, bids.get(i));
				}
			} else if (bids != null || !bid.equals("") || !category.equals("")) {
				sanatizedQuery.setString(1, queryValueString);
			} else {
				// No preperation needed
			}

			ResultSet r = sanatizedQuery.executeQuery();
			while (r.next()) {
				int result_price = r.getInt("PRICE");
				String result_bid = r.getString("BID");
				String result_title = r.getString("TITLE");
				String result_category = r.getString("CATEGORY");
				BookBean book = new BookBean(result_bid, result_title, result_price, result_category);
				arraylist.add(book);
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve books");
		}

		sanatizedQuery.close();
		con.close();
		return arraylist;
	}

	public ArrayList<BookBean> findBooks(String searchStr) throws SQLException {
		String query = "Select * FROM BOOK WHERE TITLE like ?";
		Connection con = this.ds.getConnection();
		searchStr = "%" + searchStr + "%";
		ArrayList<BookBean> arraylist = new ArrayList<BookBean>();

		PreparedStatement sanatizedQuery = con.prepareStatement(query);
		try {
			sanatizedQuery.setString(1, searchStr);
			ResultSet r = sanatizedQuery.executeQuery();
			while (r.next()) {
				int result_price = r.getInt("PRICE");
				String result_bid = r.getString("BID");
				String result_title = r.getString("TITLE");
				String result_category = r.getString("CATEGORY");
				BookBean book = new BookBean(result_bid, result_title, result_price, result_category);
				arraylist.add(book);
			}
		} catch (SQLException e) {
			throw new SQLException("Query parsing and results in BOOKDAO failed");
		}

		sanatizedQuery.close();
		con.close();
		return arraylist;
	}

}
