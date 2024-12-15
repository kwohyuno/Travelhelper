package dto;

/**
 * Dto used to process commenting task
 */
public class CommentDto {
    private String reviewId;
    private String username;
    private String commentText;
    private String commentDate;

    /**
     * Constructor to create a CommentDto object with specified values.
     *
     * @param reviewId The ID of the review that the comment is linked to.
     * @param username The username of the user who made the comment.
     * @param commentText The actual text content of the comment.
     * @param commentDate The date when the comment was posted.
     */
    public CommentDto(String reviewId, String username, String commentText, String commentDate) {
        this.reviewId = reviewId;
        this.username = username;
        this.commentText = commentText;
        this.commentDate = commentDate;
    }

    /**
     * Gets the ID of the review to which this comment belongs.
     *
     * @return The review ID associated with this comment.
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Sets the ID of the review to which this comment belongs.
     *
     * @param reviewId The review ID to be associated with this comment.
     */
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * Gets the username of the user who made this comment.
     *
     * @return The username of the commenter.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user who made this comment.
     *
     * @param username The username of the commenter.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the text of the comment.
     *
     * @return The content of the comment.
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the text of the comment.
     *
     * @param commentText The content of the comment.
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    /**
     * Gets the date the comment was made.
     *
     * @return The date when the comment was posted.
     */
    public String getCommentDate() {
        return commentDate;
    }

    /**
     * Sets the date the comment was made.
     *
     * @param commentDate The date when the comment was posted.
     */
    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

}
