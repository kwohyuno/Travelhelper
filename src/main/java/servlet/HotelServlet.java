package servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import loader.HotelCollection;
import com.google.gson.Gson;
import loader.Hotel;

/**
 * A servlet for handling HTTP GET requests related to hotel information.
 * The servlet responds with JSON data containing hotel details.
 */
public class HotelServlet extends HttpServlet{

    private final HotelCollection hotelCollection;
    private final Gson gson = new Gson();

    /**
     * Constructor to initialize the servlet with a given HotelCollection.
     *
     * @param hotelCollection The collection of hotels to be used by the servlet.
     */
    public HotelServlet(HotelCollection hotelCollection) {
        this.hotelCollection = hotelCollection;
    }

    /**
     * Handles HTTP GET requests to retrieve hotel information.
     * The request URL is expected to include a hotel ID, and the response will
     * contain the details of the specified hotel in JSON format.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object used to send the response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        List<Hotel> hotels = hotelCollection.getAllHotels();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(gson.toJson(hotels));

    }
}


