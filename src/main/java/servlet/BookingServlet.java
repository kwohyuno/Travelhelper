package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.DatabaseHandler;
import dto.BookingDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;


/**
 * Servlet that is used to book hotel
 *
 * */
public class BookingServlet extends HttpServlet {

    /**
     * Use post method to add the booking of hotel
     *
     * */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        try(BufferedReader reader = request.getReader()){

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            BookingDto dto = gson.fromJson(reader, BookingDto.class);

            String username = dto.getUsername();
            String hotelId = dto.getHotelId();
            Date startDate = dto.getStartDate();
            Date endDate = dto.getEndDate();

            DatabaseHandler dbHandler = DatabaseHandler.getInstance();

            if(dbHandler.checkBookingStatus(hotelId, startDate, endDate)){
                try{
                    dbHandler.addBooking(username, hotelId, startDate, endDate);
                }catch(Exception e){
                    e.printStackTrace();
                }
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                response.getWriter().write("{\"message\": \"The hotel is already booked on the  date.\"}");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
