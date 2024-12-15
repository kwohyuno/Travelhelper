package servlet;

import database.DatabaseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Last login information is processed by this servlet
 */
public class LastLoginServlet extends HttpServlet {

    /**
     * Getting the information of last login
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String str = request.getPathInfo().substring(1);
        String[] tmp = str.split("/");

        String username = tmp[0];
        String loginTime = tmp[1];
        try{
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.addLastLogin(username, loginTime);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
