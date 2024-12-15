package servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loader.ThreadSafeReviewCollection;
import java.io.BufferedReader;
import java.io.IOException;
import loader.Review;
import java.util.TreeSet;
import org.apache.commons.text.StringEscapeUtils;
import com.google.gson.Gson;


/**
 * A servlet for handling HTTP requests to read, write, update, and delete reviews.
 */
public class ReviewServlet extends HttpServlet {

    private ThreadSafeReviewCollection reviewCollection;
    private final Gson gson = new Gson();

    /**
     *
     * Constructor to initialize the servlet.
     * @param reviewCollection The collection of reviews to be used by the servlet.
     * */
    public ReviewServlet(ThreadSafeReviewCollection reviewCollection){
        this.reviewCollection = reviewCollection;
    }

    /**
     * Handles HTTP GET requests to retrieve reviews for a specified hotel.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when handling the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            response.setContentType("application/json");
            String pathInfo = request.getPathInfo();

            String hotelId = pathInfo.substring(1);
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            TreeSet<Review> reviews = reviewCollection.findReviewsByHotelId(hotelId);
            response.setStatus(HttpServletResponse.SC_OK);

            response.getWriter().print(gson.toJson(reviews));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Handles HTTP Post requests to register new review
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object used to send the response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try(BufferedReader reader = request.getReader()){
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine())!=null){
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();

            Gson gson = new Gson();

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            String username = jsonObject.get("userNickname").getAsString();
            System.out.println("username from reviewpost at ReviewServlet : "+ username);

            Review newReview = gson.fromJson(json, Review.class);
            Review[] reviews = new Review[1];
            reviews[0] = newReview;
            reviewCollection.writeReview(newReview);

            response.setStatus(HttpServletResponse.SC_OK);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Editing Review by this method
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try(BufferedReader reader = request.getReader()){
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while((line=reader.readLine())!=null){
                jsonBuilder.append(line);
            }

            String json = jsonBuilder.toString();
            Gson gson = new Gson();
            Review updatedReview = gson.fromJson(json, Review.class);

            if(updatedReview!=null){
                reviewCollection.updateReview(updatedReview);
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deleting Review with this method
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reviewId = request.getPathInfo().substring(1);
        reviewCollection.deleteReview(reviewId);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
