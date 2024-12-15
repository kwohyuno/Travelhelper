package dto;

/**
 * Dto used to process saving Expedia link task
 */
public class ExpediaLinkDto {

    private String username;
    private String link;

    /**
     * Gets the username of the user who saved the Expedia link.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user who saved the Expedia link.
     *
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the URL link of the saved Expedia page.
     *
     * @return The URL of the Expedia link.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the URL link of the Expedia page to be saved.
     *
     * @param link The URL of the Expedia link.
     */
    public void setLink(String link) {
        this.link = link;
    }
}
