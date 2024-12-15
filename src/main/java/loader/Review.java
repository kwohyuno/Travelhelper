package loader;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

/**
 * Represents a review for a hotel, including information such as hotel ID, review ID, rating, title, text, user nickname, and review date.
 * Implements Comparable to allow sorting reviews by date and review ID.
 */
public class Review implements Comparable<Review>{

    private String hotelId;
    private String reviewId;
    private double overallRating;
    private String reviewTitle;
    private String reviewText;
    private String userNickname;
    private String reviewDate;

    /**
     * Constructs a Review instance with the specified details.
     *
     * @param hotelId       the ID of the hotel being reviewed
     * @param reviewId      the ID of the review
     * @param overallRating the overall rating given in the review
     * @param reviewTitle   the title of the review
     * @param reviewText    the content of the review
     * @param userNickname  the nickname of the user who wrote the review
     * @param reviewDate    the submission date of the review in the format "yyyy-MM-dd"
     */
    public Review(String hotelId, String reviewId, double overallRating, String reviewTitle, String reviewText, String userNickname, String reviewDate) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.overallRating = overallRating;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.userNickname = userNickname;
        this.reviewDate = reviewDate;
    }


    /**
     * Returns the hotel ID.
     *
     * @return the hotel ID
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Returns the review ID.
     *
     * @return the review ID
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Returns the overall rating of the review.
     *
     * @return the overall rating
     */
    public double getOverallRating() {
        return overallRating;
    }

    /**
     * Returns the title of the review.
     *
     * @return the review title
     */
    public String getReviewTitle() {
        return reviewTitle;
    }

    /**
     * Returns the text of the review.
     *
     * @return the review text
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Returns the nickname of the user who wrote the review.
     *
     * @return the user nickname
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * Returns the submission date of the review.
     *
     * @return the review date in the format "yyyy-MM-dd"
     */
    public String getReviewDate() {
        return reviewDate;
    }

    /**
     * Returns a string representation of the review.
     *
     * @return a string representing the review details
     */
    @Override
    public int compareTo(Review other){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date1 = LocalDate.parse(this.reviewDate, formatter);
        LocalDate date2 = LocalDate.parse(other.reviewDate, formatter);

        int dateComparison = date2.compareTo(date1);
        if(dateComparison!=0){
            return dateComparison;
        }
        return this.reviewId.compareTo(other.getReviewId());
    }
}
