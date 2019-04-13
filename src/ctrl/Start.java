package ctrl;

import java.awt.print.Book;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.BookBean;
import bean.POBean;
import model.model;

/**
 * Servlet implementation class Start
 */
@WebServlet({ "/Start", "/Start/*" })
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private model theModel;
	ArrayList<String> dummyInfo = new ArrayList<String>();
	String creditNumber = "";

	private int bookCategoryError = 0;
	private int singleBookInfoError = 0;
	private int addingTheBookToShoppingCart = 0;

	// no sessionScope with AJAX so pseudo session scope is done with map of current
	// sessionId -> shopping cart
	// when username and password are received, can just store in login table the
	// shopping cart of current sessionId
	// with the inputted username/password and then dump the sessionId from the map
	// to be used again later
	HashMap<String, ArrayList<String>> userSessionToShoppingCart = new HashMap<String, ArrayList<String>>();
	
	//Reviews are handled
	HashMap<String, ArrayList<String>> bookReviews = new HashMap<String, ArrayList<String>>();
	ArrayList<String> shoppingCart = new ArrayList<String>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Start() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		try {
			theModel = new model();
			context.setAttribute("model", theModel);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Excpetion has occured");
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		/// For the purposes of testing Adrians Part

		try {
			theModel.retrieveAddress("1");
			theModel.retrieveSingleBook("b002");
			theModel.retrieveBookCat("Engineering");
			theModel.retrieveAllBooks();
			ArrayList<String> bids = new ArrayList<String>();
			bids.add("b002");
			bids.add("b003");
			ArrayList<BookBean> data = theModel.retrieveShoppingCart(bids);
			theModel.searchBooks(" ");
			/*
			 * System.out.println(data.get(0).toStringArray()[0]);
			 * System.out.println(data.get(0).toStringArray()[1]);
			 * System.out.println(data.get(0).toStringArray()[2]);
			 */
			int id = theModel.addAddress("ABC Street", "Test ON", "CANADA", "L4K3Y7", "905-434-5258");
			// System.out.println(id);
			int id2 = theModel.addPO("FakeNameHERE", "FakeFIRSTNameHERE", "PROCESSED", "2");
			// System.out.println(id2);
			POBean po = theModel.retrieveSinglePO("2");
			/*
			 * System.out.println(po.poStringArray()[0]);
			 * System.out.println(po.poStringArray()[1]);
			 * System.out.println(po.poStringArray()[2]);
			 * System.out.println(po.poStringArray()[3]);
			 * System.out.println(po.poStringArray()[4]);
			 */

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		/*
		 * Map containing bid -> quantity to be rerouted to response Check is the
		 * session ID if it exists in the map, and make sure it is there before
		 * executing the method
		 */
		ArrayList<ArrayList<String>> scInfo = new ArrayList<ArrayList<String>>();
		if (userSessionToShoppingCart.get(request.getSession().getId()) != null)
			scInfo = quantityOfBooksForShoppingCart(userSessionToShoppingCart.get(request.getSession().getId()),
					request);

		/*
		 * bookInfo map that allows HTML access to all the information in the format
		 * title price rating as elements in an arraylist.
		 */

		/*
		 * bookReviews keeps track of all the reviews that were submitted on a
		 * particular book (with Id) just keep appending reviews to the end and then
		 * saving them
		 * 
		 * I don't know what to do with the reviews but we're saving it for later
		 */

		System.out.println(request.getPathInfo() + " " + request.getQueryString());
		if (request.getPathInfo() != null && request.getPathInfo().contains("Ajax")) {
//			System.out.println(bookReviews);
			// System.out.println("global shopping cart:" + shoppingCart);
//			System.out.println(dummyInfo);
//			System.out.println(creditNumber);
			System.out.println("session map: " + userSessionToShoppingCart);
			System.out.println(scInfo);
			System.out.println(request.getQueryString());

			/*
			 * Information passed to front end of the following format: for one string (i.e.
			 * the information of one specific book) it is: Title | Rating | Price
			 * otherwise, for any arraylists it is: an array of strings with similar format
			 * as above. They should be comma-separated by default as arrays
			 */
			// Here we just show the books in this category
			if (request.getParameter("category") != null) {
				displayBooksByCategory(request, response);
				// here we select the book info based on its ID
			} else if (request.getParameter("bid") != null) {
				displaySelectedBookInformation(request, response);
			} else if (request.getParameter("quantity") != null) {
				// here we look into the list of book IDs added to the shopping cart and send
				// them to the shooping cart page
				String[] out = new String[scInfo.size()];
				int i = 0;
				while (i < scInfo.size()) {
					out[i] = scInfo.get(i).toString().replaceAll(",", "|");
					i++;
				}
				response.getWriter().write(Arrays.toString(out));
			}else if (request.getParameter("verify") != null) {
				System.out.println("HERE");
				
				
				response.getWriter().write("taken");
//				response.getWriter().write("");
			} else if (request.getParameter("username") != null) {
				/*
				 * get address info based on username and password and then convert it to String
				 * array
				 */
				String out = "";
				out += "Steve|Irwin|42 Wallaby Way|Sydney|Australia|L4J 4Z5|647-989-5484";

				response.getWriter().write(out);

			}

		} else if (request.getParameter("shoppingCart") != null) {
			// when shopping cart button is clicked, we move to shopping cart page
			request.getRequestDispatcher("/ShoppingCart.jspx").forward(request, response);
		} else if (request.getParameter("checkOut") != null) {
			// when check out button is clicked, we move to payment page
			request.getRequestDispatcher("/Payment.jspx").forward(request, response);
		} else {

			request.getRequestDispatcher("/MainPage.jspx").forward(request, response); // always redirect to the main
																						// bookstore page
			response.getWriter().append("Served at: ").append(request.getContextPath());

		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * bookReviews keeps track of all the reviews that were submitted on a
		 * particular book (with Id) just keep appending reviews to the end and then
		 * saving them
		 * 
		 * I don't know what to do with the reviews but we're saving it for later
		 * 
		 */
		if (request.getParameter("review") != null) {
			String bookId = request.getParameter("bid");
			ArrayList<String> temp = bookReviews.getOrDefault(bookId, new ArrayList<String>());
			temp.add(request.getParameter("review"));
			bookReviews.put(bookId, temp);

			/*
			 * adding a bookId to the running shoppingCart list for this session - for this
			 * UserID
			 */
		} else if (request.getParameter("addBid") != null) {
			shoppingCart = userSessionToShoppingCart.getOrDefault(request.getSession().getId(),
					new ArrayList<String>());
			shoppingCart.add(request.getParameter("addBid"));
			userSessionToShoppingCart.put(request.getSession().getId(), shoppingCart);
		} else if (request.getParameter("fname") != null) {
			String fname = request.getParameter("fname");
			String lname = request.getParameter("lname");
			String street = request.getParameter("street");
			String prov = request.getParameter("prov");
			String country = request.getParameter("country");
			String zip = request.getParameter("zip");
			String phone = request.getParameter("phone");

			dummyInfo.add("First name: " + fname + "\nLast Name: " + lname + "\nStreet: " + street + "\nProvince: "
					+ prov + "\nCountry: " + country + "\nZIP Code: " + zip + "\nPhone Number: " + phone);
		} else if (request.getParameter("creditNum") != null) {
			creditNumber = request.getParameter("creditNum");
		}
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * This method grabs the book IDs that are currently in the shopping cart for
	 * this session (per user log in) and retrieves the price and title of the books
	 * that were added to the shopping cart on Main Page.
	 * 
	 * If the "Add to Shopping cart" was clicked multiple times on the Main page,
	 * then the quantity of this book will be incremented accordingly.
	 * 
	 * Everything is translated into useful Strings that the front end uses to get
	 * the information.
	 * 
	 * @param listOfBookIDs - the ArrayList of book IDs that were added to the
	 *                      shopping cart in this session.
	 * @return the ArrayList of information about the books (title, quantity and
	 *         price) represented in an ArrayList as well
	 */
	private ArrayList<ArrayList<String>> quantityOfBooksForShoppingCart(ArrayList<String> listOfBookIDs,
			HttpServletRequest request) {
		// Array to return
		ArrayList<ArrayList<String>> bookIDsAndTheirQuantities = new ArrayList<ArrayList<String>>();
		HashMap<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();

		// grab the book IDs that were added to shopping cart in this session
		for (String bookID : listOfBookIDs) {

			// if the ID is already in the map that means that this book was already added,
			// so grab the middle entry that represents the count and increment it by one.
			if (tempMap.containsKey(bookID)) {
				int currentQuantity = Integer.parseInt(tempMap.get(bookID).get(1)) + 1;
				tempMap.get(bookID).set(1, String.valueOf(currentQuantity));
			} else {
				// Otherwise create a temporary array where we are going to store the title and
				// price we get from the database. Quantity goes as the middle entry to serve
				// the front end accordingly
				ArrayList<String> bookInfoForThisID;
				try {
					// get the info from the database
					bookInfoForThisID = theModel.retrieveSingleBook(bookID);

					// form the temporary array that we add the info and the quantity to.
					ArrayList<String> infoAndQuantityForThisID = new ArrayList<String>();
					// add the retrieved title
					infoAndQuantityForThisID.add(0, bookInfoForThisID.get(0));
					// quantity is 1
					infoAndQuantityForThisID.add(1, "1");
					// add the price
					infoAndQuantityForThisID.add(2, bookInfoForThisID.get(1));

					tempMap.put(bookID, infoAndQuantityForThisID);
					request.setAttribute("addingTheBookToShoppingCart", addingTheBookToShoppingCart);

				} catch (Exception e) {
					System.out.println(
							"There was something wrong with retrieveing the book information from the database when adding it ot the shopping cart. Please try again.");
					addingTheBookToShoppingCart = 1;
					request.setAttribute("addingTheBookToShoppingCart", addingTheBookToShoppingCart);
				}
			}
		}

		for (String bookID : tempMap.keySet()) {
			bookIDsAndTheirQuantities.add(tempMap.get(bookID));
		}

		return bookIDsAndTheirQuantities;
	}

	/**
	 * A private method that connects to Model and retrieves the books by the
	 * selected category(i.e Engineering, Science, etc.). The method uses the
	 * "category" parameter in the request and triggers the Model to run the
	 * respective method of retrieving the books by their category.
	 * 
	 * If there is an Exception thrown by the internal methods either in the Model
	 * or in BookDAO, the Exception is caught and the error flag is set 1, which
	 * will indicate the error on the front end as well.
	 * 
	 * 
	 * @param request  this HttpServlet request
	 * @param response this HttpServlet response
	 * 
	 * @throws an Exception, should the returned result be empty, or if there is an
	 *            internal database error.
	 */
	private void displayBooksByCategory(HttpServletRequest request, HttpServletResponse response) {
		String category = request.getParameter("category");
		System.out.println(category);
		try {
			HashMap<String, String> outMap = theModel.retrieveBookCat(category);

			String[] out = new String[outMap.size()];
			int i = 0;
			for (String k : outMap.keySet()) {
				out[i] = k + "|" + outMap.get(k);
				// System.out.println(out[i]);
				i++;
			}
			response.getWriter().write(Arrays.toString(out));
			request.setAttribute("bookCategoryError", bookCategoryError);
		} catch (Exception e) {
			// set the flag to 1 to indicate the error on the front end
			bookCategoryError = 1;
			request.setAttribute("bookCategoryError", bookCategoryError);

		}

	}

	/**
	 * This method retrieves all the information about this book from the database.
	 * Uses the respective method from the model to do so. It also adds a random
	 * rating to this book out of 10.
	 * 
	 * @param request  this HttpServlet request
	 * @param response this HttpServlet response
	 */
	private void displaySelectedBookInformation(HttpServletRequest request, HttpServletResponse response) {
		String bookID = request.getParameter("bid");
		try {
			ArrayList<String> bookInfo = theModel.retrieveSingleBook(bookID);
			System.out.println(bookID);

			Random rand = new Random();
			int rating = rand.nextInt(11);

			String out = bookInfo.get(0) + "|" + String.valueOf(rating) + "/10|" + bookInfo.get(1);

			System.out.println(out);

			response.getWriter().write(out);
			request.setAttribute("singleBookInfoError", singleBookInfoError);
		} catch (Exception e) {
			// set the flag to 1 to indicate the error on the front end
			bookCategoryError = 1;
			request.setAttribute("singleBookInfoError", singleBookInfoError);

		}

	}

}