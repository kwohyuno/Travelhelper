package dto;

/**
 * Dto used to process saving favorite hotel
 */
public class FavoriteRequestDto {
    private String username;
    private String hotelId;

    /**
     * Gets the username of the user who is saving the favorite hotel.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user who is saving the favorite hotel.
     *
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the hotel ID of the hotel being saved as a favorite.
     *
     * @return The unique identifier of the hotel.
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Sets the hotel ID of the hotel being saved as a favorite.
     *
     * @param hotelId The unique identifier of the hotel.
     */
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}
