package loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for parsing hotel data from a JSON file.
 */
public class HotelParser {

    /**
     * Parses the given JSON file and converts it into an array of Hotel objects.
     *
     * @param filepath The path to the JSON file containing hotel data.
     * @return An array of Hotel objects parsed from the JSON file.
     * @throws IllegalArgumentException If there is an error reading the file, parsing the JSON data,
     *                                  or if the data format is incorrect.
     */
    public static Hotel[] parseHotelJson(String filepath) throws IOException {

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filepath)));
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            JsonArray hotelsArray = jsonObject.getAsJsonArray("sr");
            Hotel[] hotels = new Hotel[hotelsArray.size()];

            for (int i = 0; i < hotelsArray.size(); i++) {
                JsonObject ob = hotelsArray.get(i).getAsJsonObject();
                String hotelName = ob.get("f").getAsString();
                String hotelId = ob.get("id").getAsString();
                JsonObject ll = ob.getAsJsonObject("ll");
                String latitude = ll.get("lat").getAsString();
                String longitude = ll.get("lng").getAsString();
                String streetAddress = ob.get("ad").getAsString();
                String city = ob.get("ci").getAsString();
                String state = ob.get("pr").getAsString();
                String country = ob.get("c").getAsString();

                hotels[i] = new Hotel(hotelName, hotelId, latitude, longitude, streetAddress, city, state, country);
            }
            return hotels;
        }catch(IOException e){
            throw new IllegalArgumentException("Could not read the file: " + e.getMessage());
        }
    }
}
