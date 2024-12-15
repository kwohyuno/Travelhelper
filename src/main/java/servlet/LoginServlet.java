package servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.DatabaseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.commons.text.StringEscapeUtils;

/**
 * A servlet for handling HTTP GET requests related to login.
 */
public class LoginServlet extends HttpServlet {

    /**
    *
    * Handles login feqture with HTTP POST request.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object used to send the response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output error occurs.
    * */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        String line;
        try(
            BufferedReader reader = request.getReader()){
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
        }
        String requestBody = sb.toString();

        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
        String username = StringEscapeUtils.escapeHtml4(jsonObject.get("username").getAsString());
        String password = StringEscapeUtils.escapeHtml4(jsonObject.get("password").getAsString());

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        boolean pwValidation = dbHandler.authenticateUser(username,password);

        if(pwValidation){
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
        }else{
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
