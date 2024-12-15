package servlet;

import database.DatabaseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Checking last login by get method
 *
 */
public class CheckLastLoginServlet extends HttpServlet {

    /**
     * Checking last login by get method
     *
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String username = request.getPathInfo().substring(1);
        try{
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();

            String lastLoginDate = dbHandler.returnLastLogin(username);
            System.out.println("Last Login Date : "+lastLoginDate);
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();
            if(lastLoginDate != null){
                out.write("{\"lastLoginDate\": \"" + lastLoginDate + "\"}");
            }
            out.flush();

        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
