package dto;

import java.sql.Date;

/**
 * Dto used to process booking task
 */
public class BookingDto {
    private String username;
    private String hotelId;
    private Date startDate;
    private Date endDate;

    /**
     * Gets the name of the user.
     *
     * @return The name of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the name of the user.
     *
     * @param username The username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Sets the hotel ID for the booking.
     *
     * @param hotelId The hotel ID to be set
     */
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    /**
     * Gets the start date of the booking.
     *
     * @return The start date of the booking
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date for the booking.
     *
     * @param startDate The start date to be set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the booking.
     *
     * @return The end date of the booking
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date for the booking.
     *
     * @param endDate The end date to be set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


}
