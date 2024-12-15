package loader;

import database.DatabaseHandler;

import java.util.*;

/**
 * A collection of Hotel objects stored in a TreeMap for easy access.
 * Provides methods to load hotel data from a JSON file and retrieve hotel information by ID.
 */
public class HotelCollection {

    /**
     * Retrieves the map containing all hotels.
     *
     * @return A map of hotel IDs to Hotel objects.
     */
    private Map<String, Hotel> hotelMap = new TreeMap<>();


    /**
     * Loads hotels from a JSON file and stores them in the hotel map.
     *
     * @param pathToHotelFile The path to the JSON file containing hotel data.
     * @throws IllegalArgumentException If there is an error parsing the hotel data.
     */
    public void loadHotels(String pathToHotelFile){
        try{
            Hotel[] hotels = HotelParser.parseHotelJson(pathToHotelFile);

            for(Hotel hotel : hotels) {
                hotelMap.put(hotel.getHotelId(), hotel);
            }

            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.insertHotels(hotelMap);
            System.out.println("Hotel loaded successfully");

        } catch (Exception e){
            throw new IllegalArgumentException("Error processing hotel data: " + e.getMessage());
        }
    }

    /**
     * Fetch all hotel data from hotel map.
     */
    public List<Hotel> getAllHotels(){
        return new ArrayList<>(hotelMap.values());
    }
}

