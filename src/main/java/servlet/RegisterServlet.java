package servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import java.io.BufferedReader;
import java.io.IOException;
import database.DatabaseHandler;

/**
 * A servlet for handling HTTP requests to register new user.
 */
public class RegisterServlet  extends HttpServlet{

    /**
     * Handles HTTP Post requests to register new user
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object used to send the response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        StringBuilder sb = new StringBuilder();
        String line;
        try(BufferedReader reader = request.getReader()){
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
        }
        String requestBody = sb.toString();
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();

        username = StringEscapeUtils.escapeHtml4(username);
        password = StringEscapeUtils.escapeHtml4(password);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        if(!dbHandler.checkExistingUsername(username)){

            if(dbHandler.checkPasswordRequirements(password)){
                System.out.println("Registered successfully");
                dbHandler.registerUser(username, password);
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                System.out.println("password doesn't meet requirements");
                response.getWriter().write("{\"message\": \"Please include one or more special characters and make it longer than ten characters.\"}");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }else{
            System.out.println("Username already exists");
            response.getWriter().write("{\"message\": \"Username already exists.\"}");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}

