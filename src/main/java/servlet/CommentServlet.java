package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.DatabaseHandler;
import dto.BookingDto;
import dto.CommentDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Processing comment related requests
 *
 */
public class CommentServlet extends HttpServlet {

    /**
     * Process comment posting
     *
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try(BufferedReader reader = request.getReader()){
            Gson gson = new Gson();
            CommentDto dto = gson.fromJson(reader, CommentDto.class);

            String username = dto.getUsername();
            String reviewId = dto.getReviewId();
            String commentText = dto.getCommentText();

            DatabaseHandler dbHandler = DatabaseHandler.getInstance();

            try{
                dbHandler.addReviewComment(username, reviewId, commentText);
                response.setStatus(HttpServletResponse.SC_OK);
            }catch(Exception e){
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }


    /**
     * Process comment fetching
     *
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try{
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            List<CommentDto> comments = dbHandler.getCommentsForReview();

            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(comments));
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

}
