package loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * This class handles parsing reviews from a JSON file and converting them into Review objects.
 */
public class ReviewParser {

    /**
     * Parses a JSON file containing reviews and returns an array of Review objects.
     *
     * @param filepath the path to the JSON file containing reviews
     * @return an array of Review objects
     * @throws IllegalArgumentException if there is an error reading the file or processing the JSON data
     */
    public static Review[] parseReviews(String filepath){
        ArrayList<Review> reviewsList = new ArrayList<>();

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filepath)));
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            JsonObject reviewDetails = jsonObject.getAsJsonObject("reviewDetails");
            JsonObject reviewCollection = reviewDetails.getAsJsonObject("reviewCollection");
            JsonArray reviewArray = reviewCollection.getAsJsonArray("review");

            for (int i = 0; i < reviewArray.size(); i++) {
                JsonObject ob = reviewArray.get(i).getAsJsonObject();
                String hotelId = ob.get("hotelId").getAsString();
                String reviewId = ob.get("reviewId").getAsString();
                double overallRating = ob.get("ratingOverall").getAsDouble();
                String reviewTitle = ob.get("title").getAsString();
                String reviewText = ob.get("reviewText").getAsString();
                String userNickname = ob.get("userNickname").getAsString();
                String reviewDate = ob.get("reviewSubmissionDate").getAsString();

                Review review = new Review(hotelId, reviewId, overallRating, reviewTitle, reviewText, userNickname, reviewDate);
                reviewsList.add(review);
            }
        } catch(IOException e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return reviewsList.toArray(new Review[0]);
    }
}

