package servlet;

import com.google.gson.Gson;
import database.DatabaseHandler;
import dto.ExpediaLinkDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Process Expedia link (adding to mypage)
 *
 */
public class ExpediaServlet extends HttpServlet {



    /**
     * Write the information in the database by post request
     *
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        try(BufferedReader reader = request.getReader()){

            Gson gson = new Gson();
            ExpediaLinkDto dto = gson.fromJson(reader, ExpediaLinkDto.class);


            String username = dto.getUsername();
            System.out.println("username from expedia link post at ExpediaServlet : " + username);
            String link = dto.getLink();
            System.out.println("link from expedia link at ExpediaServlet : " + link);

            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.addExpediaLink(link, username);
            System.out.println("ExpediaLink add success");

            response.setStatus(HttpServletResponse.SC_OK);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Getting the expedia links from database
     *
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String username = request.getPathInfo().substring(1);
        try{
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            List<Map<String, String>> expedialinks = dbHandler.getExpediaLinks(username);

            Gson gson = new Gson();
            String json = gson.toJson(expedialinks);

            response.getWriter().write(json);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deleting every expedia links
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getPathInfo().substring(1);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.deleteExpediaLists(username);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}