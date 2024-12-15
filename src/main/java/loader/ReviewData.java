package loader;

import database.DatabaseHandler;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles loading, storing, and searching review data, including building an inverted index
 * to search for specific words within reviews.
 */
public class ReviewData {

    private HashMap<String, TreeSet<Review>> reviewsMap = new HashMap<>();
    private Set<String> usernames = new HashSet<>();

    /**
     * Loads review files from a specified directory and adds them to the review map.
     * If the directory contains subdirectories, it recursively loads reviews from them.
     *
     * @param pathToReviewFolder the path to the directory containing review files
     * @throws IllegalArgumentException if the directory cannot be opened
     */
    public void loadReviews(String pathToReviewFolder){
        Path p = Paths.get(pathToReviewFolder);
        try(DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(p)){
            for(Path path : pathsInDir){
                if(Files.isDirectory(path)){
                    loadReviews(path.toString());
                }else if(path.toString().endsWith(".json")){
                    Review[] reviews = ReviewParser.parseReviews(path.toString());
                    addReviewsToMap(reviews);
                }
            }
        }catch(IOException e){
            throw new IllegalArgumentException("Cannot open directory: " + e.getMessage());
        }
    }

    /**
     * Adds an array of reviews to the reviews map and builds the inverted index.
     *
     * @param reviews an array of Review objects
     * @throws IllegalArgumentException if reviews belong to different hotels
     */
    public void addReviewsToMap(Review[] reviews){
        if(reviews == null || reviews.length == 0) return;

        String hotelId = reviews[0].getHotelId();
        TreeSet<Review> treeSet = reviewsMap.getOrDefault(hotelId, new TreeSet<>());

        for(Review review: reviews){
            treeSet.add(review);
            usernames.add(review.getUserNickname());
        }
        reviewsMap.put(hotelId, treeSet);
    }

    /**
     * Adds default user from data set into database
     *
     */
    public void addDefaultUsers(){
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.insertUsers(usernames);
    }

    /**
     * Adds default reviews from data set into database
     *
     */
    public void addDefaultReviews(){
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.insertReviews(reviewsMap);
    }


    /**
     * Adds new reviews
     * @param review the review object that is being written
     */
    public void writeReview(Review review){
        if(review == null) return;

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.writeReview(review);

        String hotelId = review.getHotelId();
        TreeSet<Review> treeSet = reviewsMap.getOrDefault(hotelId, new TreeSet<>());
        treeSet.add(review);
        reviewsMap.put(hotelId, treeSet);
    }

    /**
     * Finds reviews by the specified hotel ID.
     *
     * @param hotelId the ID of the hotel
     * @return a TreeSet of reviews for the hotel, or null if not found
     */
    public TreeSet<Review> findReviewsByHotelId(String hotelId){
        return reviewsMap.get(hotelId);
    }

    /**
    *
    * Update reviews with edited information
    *
    * @param updatedReview the Object of updated review
    * */
    public void updateReview(Review updatedReview){
        if(updatedReview == null) return;

        String hotelId = updatedReview.getHotelId();
        TreeSet<Review> treeSet = reviewsMap.get(hotelId);

        if(treeSet!=null){
            Iterator<Review> iterator = treeSet.iterator();
            while(iterator.hasNext()){
                Review existingReview = iterator.next();
                if(existingReview.getReviewId().equals(updatedReview.getReviewId())){
                    iterator.remove();
                    break;
                }
            }
            treeSet.add(updatedReview);
            reviewsMap.put(hotelId, treeSet);
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.updateReviewInDatabase(updatedReview);
        }
    }

    /**
     * Delete review if it is requested
     * @param reviewId the ID of review that is being deleted
     * */
    public void deleteReview(String reviewId){
        for(Map.Entry<String, TreeSet<Review>> entry: reviewsMap.entrySet()){
            TreeSet<Review> reviews = entry.getValue();
            reviews.removeIf(review ->  review.getReviewId().equals(reviewId));
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.deleteReviewInDatabase(reviewId);
        }
    }
}
