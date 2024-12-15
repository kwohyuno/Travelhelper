package loader;

/**
 * Represents a Hotel with detailed information such as name, location, and address.
 * This class is immutable, meaning once a Hotel object is created, its fields cannot be modified.
 */
public class Hotel {

    private final String hotelName;
    private final String hotelId;
    private final String latitude;
    private final String longitude;
    private final String streetAddress;
    private final String city;
    private final String state;
    private final String country;

    /**
     * Constructs a new Hotel object with the specified details.
     *
     * @param hotelName The name of the hotel
     * @param hotelId The unique ID of the hotel
     * @param latitude The latitude coordinate of the hotel
     * @param longitude The longitude coordinate of the hotel
     * @param streetAddress The street address of the hotel
     * @param city The city where the hotel is located
     * @param state The state where the hotel is located
     * @param country The country where the hotel is located
     */
    public Hotel(String hotelName, String hotelId, String latitude, String longitude, String streetAddress, String city, String state, String country) {
        this.hotelName = hotelName;
        this.hotelId = hotelId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    /**
     * Gets the name of the hotel.
     *
     * @return The name of the hotel
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Gets the unique ID of the hotel.
     *
     * @return The unique ID of the hotel
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Gets the latitude coordinate of the hotel.
     *
     * @return The latitude coordinate of the hotel
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude coordinate of the hotel.
     *
     * @return The longitude coordinate of the hotel
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Gets the street address of the hotel.
     *
     * @return The street address of the hotel
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Gets the city where the hotel is located.
     *
     * @return The city where the hotel is located
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the state where the hotel is located.
     *
     * @return The state where the hotel is located
     */
    public String getState() {
        return state;
    }

    /**
     * Gets the country where the hotel is located.
     *
     * @return The country where the hotel is located
     */
    public String getCountry() {
        return country;
    }
}

