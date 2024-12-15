package servlet;

import com.google.gson.Gson;
import database.DatabaseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.FavoriteRequestDto;
import loader.Hotel;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * Adding favorite hotel
 */
public class FavoriteHotelServlet extends HttpServlet {

    /**
     * Adding favorite hotel into database by post method
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try(BufferedReader reader = request.getReader()){

            Gson gson = new Gson();
            FavoriteRequestDto dto = gson.fromJson(reader, FavoriteRequestDto.class);


            String username = dto.getUsername();
            System.out.println("username from reviewpost at ReviewServlet : " + username);
            String hotelId = dto.getHotelId();
            System.out.println("hotelId from reviewpost at ReviewServlet : " + hotelId);

            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.addFavoriteHotel(username,hotelId);
            System.out.println("FavoriteHotelervlet success : " + hotelId);

            response.setStatus(HttpServletResponse.SC_OK);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Getting the information of favorite hotel
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String username = request.getPathInfo().substring(1);
        try{
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            List<Hotel> favoriteHotels = dbHandler.getFavoriteHotels(username);

            Gson gson = new Gson();
            String json = gson.toJson(favoriteHotels);

            response.getWriter().write(json);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deleting favorite hotel
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getPathInfo().substring(1);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.deleteFavoriteHotelLists(username);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}


