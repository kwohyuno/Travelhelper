package database;
import dto.CommentDto;
import loader.Hotel;
import loader.Review;

import javax.xml.stream.events.Comment;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
* Connects database and process related queries
*/
public class DatabaseHandler {

    private static DatabaseHandler dbHandler = new DatabaseHandler("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();

    /**Constructs object*/
    private DatabaseHandler(String propertiesFile){
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database") + "?allowPublicKeyRetrieval=true&useSSL=false";
    }

    /**
     * Get Instance
     * */
    public static DatabaseHandler getInstance(){ return dbHandler; }

    /**load config files
    * @param propertyFile The property of database
    */
    public Properties loadConfigFile(String propertyFile){
        Properties config = new Properties();
        try(FileReader fr = new FileReader(propertyFile)){
            config.load(fr);
        }catch(IOException e){
            System.out.println(e);
        }
        return config;
    }

    /**
    * Creates table at the beginning
    */
    public void createTable(){
        Statement statement;
        try(Connection dbConenction = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            System.out.println("Connected to the database");
            statement = dbConenction.createStatement();

            statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
            statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
            statement.executeUpdate(PreparedStatements.CREATE_REVIEW_TABLE);
            statement.executeUpdate(PreparedStatements.CREATE_FAVORITEHOTEL_SQL);
            statement.executeUpdate(PreparedStatements.CREATE_EXPEDIALINK_TABLE);
            statement.executeUpdate(PreparedStatements.CREATE_LASTLOGIN_TABLE);
            statement.executeUpdate(PreparedStatements.CREATE_BOOKINGS_SQL);
            statement.executeUpdate(PreparedStatements.CREATE_COMMENT_SQL);

            System.out.println("Successfully created users, hotels, reviews tables");
        }catch(SQLException ex){
            System.out.println(ex);
        }
    }

    /**
     * Inserting existing reviews from dataset
     *
     * @param reviewsMap ReviewMap that will be used to add to the database
     */
    public void insertReviews(Map<String, TreeSet<Review>> reviewsMap){
        PreparedStatement statement;
        PreparedStatement checkStmt;

        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            checkStmt = dbConnection.prepareStatement(PreparedStatements.CHECK_BEFORE_INSERT_REVIEW_SQL);
            for(Map.Entry<String, TreeSet<Review>> entry : reviewsMap.entrySet()){
                for(Review review : entry.getValue()){
                    checkStmt.setString(1, review.getReviewId());
                    try(ResultSet rs = checkStmt.executeQuery()){
                        if(!rs.next()){
                            statement = dbConnection.prepareStatement(PreparedStatements.INSERT_DEFAULT_REVIEW_SQL);
                            statement.setString(1, review.getReviewId());
                            statement.setString(2, review.getHotelId());
                            statement.setDouble(3, review.getOverallRating());
                            statement.setString(4, review.getReviewTitle());
                            statement.setString(5, review.getReviewText());
                            statement.setString(6, review.getUserNickname());
                            statement.setDate(7, java.sql.Date.valueOf(review.getReviewDate()));
                            statement.executeUpdate();
                            statement.close();
                        }
                    }
                }
            }
        }catch(SQLException e){
            System.out.println("Failed to insert default reviews: "+e.getMessage());
        }
    }

    /**
     * Inserting existing id from dataset
     *
     * @param usernames Inserting existing users in the data set
     */
    public void insertUsers(Set<String> usernames){

        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32);
        String passhash = getHash("1234", usersalt);

        PreparedStatement statement;
        PreparedStatement checkStmt;

        try(Connection dbConenction = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){

            for(String username : usernames){
                statement = dbConenction.prepareStatement(PreparedStatements.INSERT_DEFAULT_USERS_SQL);
                checkStmt = dbConenction.prepareStatement(PreparedStatements.CHECK_DEFAULT_USERS_SQL);

                checkStmt.setString(1, username);
                try(ResultSet rs = checkStmt.executeQuery()){
                    rs.next();
                    if(rs.getInt(1)>0){
                        continue;
                    }
                }

                statement.setString(1, username);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }

        }catch(SQLException ex){
            System.out.println("Error while inserting Users" + ex);
        }

    }


    /**
     * Adding Review
     *
     * @param review The Review that is being added in database
     */
    public void writeReview(Review review){
        PreparedStatement statement;

        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){

            statement = dbConnection.prepareStatement(PreparedStatements.INSERT_DEFAULT_REVIEW_SQL);
            statement.setString(1, review.getReviewId());
            statement.setString(2, review.getHotelId());
            statement.setDouble(3, review.getOverallRating());
            statement.setString(4, review.getReviewTitle());
            statement.setString(5, review.getReviewText());
            statement.setString(6, review.getUserNickname());
            statement.setDate(7, java.sql.Date.valueOf(review.getReviewDate()));
            statement.executeUpdate();
            statement.close();

        }catch(SQLException e){
            System.out.println("Failed to insert new reviews: "+e.getMessage());
        }
    }



    /**
     * Adding Hotels that were given as data set
     *
     * @param hotelMap The map of hotels that will be used to add data in database
     */
    public void insertHotels(Map<String, Hotel> hotelMap){
        PreparedStatement statement;
        PreparedStatement checkStmt;
        try(Connection dbConnenction = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){

            for(Map.Entry<String, Hotel> entry : hotelMap.entrySet()){
                checkStmt = dbConnenction.prepareStatement(PreparedStatements.CHECK_HOTELS_TABLE);
                checkStmt.setString(1, entry.getValue().getHotelId());

                try(ResultSet rs = checkStmt.executeQuery()){
                    if(!rs.next()){
                        statement = dbConnenction.prepareStatement(PreparedStatements.INSERT_DEFAULT_HOTEL_SQL);
                        statement.setString(1, entry.getValue().getHotelId());
                        statement.setString(2, entry.getValue().getHotelName());
                        statement.setString(3, entry.getValue().getLatitude());
                        statement.setString(4, entry.getValue().getLongitude());
                        statement.setString(5, entry.getValue().getStreetAddress());
                        statement.setString(6, entry.getValue().getCity());
                        statement.setString(7, entry.getValue().getState());
                        statement.setString(8, entry.getValue().getCountry());
                        statement.executeUpdate();
                        statement.close();
                    }
                }
            }
            System.out.println("Successfully created hotels, hotels tables");
        }catch(SQLException ex){
            System.out.println("Failed to insert default hotels data : " + ex);
        }
    }

    /**
     * Updating review if user fixes it
     *
     * @param updateReview The review of hotels that will be updated
     */
    public void updateReviewInDatabase(Review updateReview) {
        PreparedStatement statement;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){

                statement = connection.prepareStatement(PreparedStatements.UPDATE_REVIEW_SQL);
                statement.setString(1, updateReview.getHotelId());
                statement.setString(2, updateReview.getUserNickname());
                statement.setDouble(3, updateReview.getOverallRating());
                statement.setString(4, updateReview.getReviewTitle());
                statement.setString(5, updateReview.getReviewText());
                statement.setString(6, updateReview.getUserNickname());
                statement.setDate(7, java.sql.Date.valueOf(updateReview.getReviewDate()));
                statement.setString(8, updateReview.getReviewId());
                statement.executeUpdate();
                statement.close();


        }catch(SQLException e){
            System.out.println("Failed to update review in database: " + e);
        }
    }

    /**
     * Deleting Review
     *
     * @param reviewId The reviewId of review that is being deleted
     */
    public void deleteReviewInDatabase(String reviewId) {
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){
            statement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW_SQL);
            statement.setString(1, reviewId);
            statement.execute();
            statement.close();
        }catch(SQLException e){
            System.out.println("Failed to delete review in database: " + e);
        }
    }


    /**
    * Generates Hex
    * @param bytes Generated saltBytes
    * @param length The length of Hex
    */
    public static String encodeHex(byte[] bytes, int length){
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
    * Get Hash with password and salt
    * @param password The password for database
    * @param salt The salt used for hashing
    */
    public static String getHash(String password, String salt){
        String salted = salt + password;
        String hashed = salted;

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }catch(Exception ex){
            System.out.println(ex);
        }
        return hashed;
    }

    /**
    * Register users with the id and password submitted
    * @param newuser The user ID
    * @param newpass The password
    */
    public void registerUser(String newuser, String newpass) {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32);
        String passhash = getHash(newpass, usersalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {

            try {
                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }


    /**
    * Check if user name exists
    * @param username The name of user
    */
    public boolean checkExistingUsername(String username){

        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){

            statement = connection.prepareStatement(PreparedStatements.USERNAMECHECK_SQL);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            return results.next();


        } catch (SQLException ex) {
            System.out.println(ex);
        }

        return false;
    }

    /**
    * Check if password follows the required condition
    * @param password The password submitted when user registers
    */
    public boolean checkPasswordRequirements(String password){

        final String REQUIRED_PASSWORD_REGEX = "^(?=.*[!@#$%^&*(),.?\":{}|<>])[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]{10,}$";

        Pattern p = Pattern.compile(REQUIRED_PASSWORD_REGEX);
        Matcher m = p.matcher(password);

        return m.find();
    }

    /**
    * Authenticates if the information submitted when login works
    * @param username The name of user
    * @param password The password submitted
    */
    public boolean authenticateUser(String username, String password){
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1,username);
            statement.setString(2,passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        }catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }

    /**
    * Getting salt from database
    * @param connection The connection used to link to database
    * @param username The user name submitted
    */
    public String getSalt(Connection connection, String username){
        String salt = null;
        try(PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)){
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            if(results.next()){
                salt = results.getString("usersalt");
                return salt;
            }
        }catch(SQLException e){
            System.out.println(e);
        }
        return salt;
    }

    /**
     * Adding favorite hotel
     *
     * @param username The username that is adding favorite hotel
     * @param hotelId The hotelId of hotel that is being added
     */
    public void addFavoriteHotel(String username, String hotelId){
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){
            statement = connection.prepareStatement(PreparedStatements.INSERT_FAVHOTEL_SQL);
            statement.setString(1, username);
            statement.setString(2, hotelId);
            statement.executeUpdate();
            statement.close();
        }catch(SQLException e){
            System.out.println("Failed adding favorite_hotel : " + e);
        }
    }

    /**
     * Fetching favorite hotel lists
     *
     * @param username The username that is fetching data
     */
    public List<Hotel> getFavoriteHotels(String username) {
        List<Hotel> favoriteHotels = new ArrayList<>();
        PreparedStatement statement;
        PreparedStatement getHotelStmt;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){

            statement = connection.prepareStatement(PreparedStatements.GET_FAVHOTEL_SQL);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            while(results.next()){
                String hotelID = results.getString("hotelId");

                getHotelStmt = connection.prepareStatement(PreparedStatements.GET_GETFAVHOTELCLASS_SQL);
                getHotelStmt.setString(1, hotelID);
                ResultSet hotelClassesResults = getHotelStmt.executeQuery();
                while(hotelClassesResults.next()){
                    Hotel hotel = new Hotel(
                            hotelClassesResults.getString("hotelName"),
                            hotelClassesResults.getString("hotelId"),
                            hotelClassesResults.getString("latitude"),
                            hotelClassesResults.getString("longitude"),
                            hotelClassesResults.getString("streetAddress"),
                            hotelClassesResults.getString("city"),
                            hotelClassesResults.getString("state"),
                            hotelClassesResults.getString("country")
                    );
                    favoriteHotels.add(hotel);
                }

            }
        }catch(SQLException e){
            System.out.println("Failed fetching favorite hotel data : " + e);
        }
        return favoriteHotels;
    }

    /**
     * Delete favorite hotel lists
     * @param username The nickName of user that is deleting lists
     */
    public void deleteFavoriteHotelLists(String username) {
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){
            statement = connection.prepareStatement(PreparedStatements.DELETE_FAVORITEHOTELLIST_SQL);
            statement.setString(1, username);
            statement.execute();
            statement.close();
        }catch(SQLException e){
            System.out.println("Failed to delete review in database: " + e);
        }
    }

    /**
     * Adding clicked Expedia Link in database
     *
     * @param link The link of clicked Expedia page
     * @param username The nickName of user clicking Expedia link
     */
    public void addExpediaLink(String link,String username) {
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_EXPEDIALINK_SQL);
            System.out.println(link);
            statement.setString(1, link);
            statement.setString(2, username);
            statement.executeUpdate();
            statement.close();
        }catch(SQLException e){
                System.out.println("Failed to add Expedia Link: " + e);
        }
    }

    /**
     * Fetching clicked Expedia links
     * @param username The nickName of user clicking Expedia link
     */
    public List<Map<String, String>> getExpediaLinks(String username) {
        List<Map<String,String>> expedialinks = new ArrayList<>();
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){

            statement = connection.prepareStatement(PreparedStatements.GET_EXPEDIALINK_SQL);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (results.next()) {
                Map<String, String> row = new HashMap<>();
                for(int i=1; i<= columnsNumber; i++){
                    row.put(rsmd.getColumnName(i), results.getString(i));
                }
                expedialinks.add(row);
            }
        }catch(SQLException e){
            System.out.println("Failed fetching favorite hotel data : " + e);
        }
        return expedialinks;
    }

    /**
     * Deleting clicked Expedia links
     * @param username The nickName of user deleting Expedia link
     */
    public void deleteExpediaLists(String username) {
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))){
            statement = connection.prepareStatement(PreparedStatements.DELETE_EXPEDIALINKS_SQL);
            statement.setString(1, username);
            statement.execute();
            statement.close();
        }catch(SQLException e){
            System.out.println("Failed to delete review in database: " + e);
        }
    }

    /**
     * Adding last login time in database
     * @param username The nickName of user adding last login time
     * @param loginTime The recent login time
     */
    public void addLastLogin(String username, String loginTime) {
        PreparedStatement insertStatement;
        PreparedStatement checkStatement;
        PreparedStatement updateStatement;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            checkStatement = connection.prepareStatement(PreparedStatements.CHECK_LASTLOGIN_SQL);
            checkStatement.setString(1, username);
            ResultSet rs = checkStatement.executeQuery();
            if(rs.next() && rs.getInt(1) > 0){
                updateStatement = connection.prepareStatement(PreparedStatements.UPDATE_LASTLOGIN_SQL);
                updateStatement.setString(1, username);
                updateStatement.executeUpdate();
                updateStatement.close();
                System.out.println("Last login date updated for username: "+ username);
            }else{
                insertStatement = connection.prepareStatement(PreparedStatements.INSERT_LASTLOGIN_SQL);
                insertStatement.setString(1, username);
                insertStatement.setString(2, loginTime);
                insertStatement.executeUpdate();
                insertStatement.close();
                System.out.println("New last login record created for username: " + username);
            }

            checkStatement.close();
            rs.close();
        }catch(SQLException e){
            System.out.println("Failed to add Expedia Link: " + e);
        }

    }

    /**
     * Fetching last login time
     * @param username The nickName of user fetching last login time
     */
    public String returnLastLogin(String username) {
        PreparedStatement statement;
        String lastLoginDate = null;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.FIND_LASTLOGIN_SQL);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                lastLoginDate = rs.getString("logindate");
            }else{
                System.out.println("No last login record found for username: " + username);
            }

            rs.close();
            statement.close();

        }catch(SQLException e){
            System.out.println("Failed to find last login record: " + e);
        }

        return lastLoginDate;

    }

    /**
     * Checking booking status of hotel
     * @param hotelId The hotel id that is being checked
     * @param startDate The start date of booking
     * @param endDate The  end date of booking
     */
    public boolean checkBookingStatus(String hotelId, Date startDate, Date endDate) {
        PreparedStatement statement;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.CHECK_BOOKINGSTATUS_SQL);
            statement.setString(1, hotelId);
            statement.setDate(2, endDate);
            statement.setDate(3, startDate);

            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    int totalBookings = resultSet.getInt("totalBooking");
                    System.out.println("Total Booking for this hotel: " + totalBookings);
                    return totalBookings<2;
                }
            }catch (SQLException e){
                System.out.println("Failed to check booking status: " + e.getMessage());
            }
        } catch (SQLException e){
            System.out.println("Failed to check booking status for hotel: " + e.getMessage());
        }
        return false;
    }

    /**
     * Adding booking information in database
     * @param username The nickName of user that is adding booking
     * @param hotelId The hotel id that is being added
     * @param startDate The start date of added
     * @param endDate The  end date of added
     */
    public void addBooking(String username, String hotelId, Date startDate, Date endDate) {
        PreparedStatement chkRoomNumStatement;
        PreparedStatement insertBookingStatement;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            int roomNumber = 1;
            chkRoomNumStatement = connection.prepareStatement(PreparedStatements.CHECK_ROOMNUM_SQL);
            chkRoomNumStatement.setString(1, hotelId);
            chkRoomNumStatement.setDate(2, endDate);
            chkRoomNumStatement.setDate(3, startDate);

            try(ResultSet resultSet = chkRoomNumStatement.executeQuery()){
                if(resultSet.next()){
                    roomNumber = resultSet.getInt("nextRoomNumber");
                }
            }

            insertBookingStatement = connection.prepareStatement(PreparedStatements.INSERT_BOOKIING_SQL);
            insertBookingStatement.setString(1, username);
            insertBookingStatement.setString(2,hotelId);
            insertBookingStatement.setDate(3, startDate);
            insertBookingStatement.setDate(4, endDate);
            insertBookingStatement.setInt(5, roomNumber);
            insertBookingStatement.executeUpdate();
            System.out.println("Successfully added booking for " + username + " at hotel " + hotelId);

        }catch(SQLException e){
            System.out.println("Failed to add booking: " + e.getMessage());
        }

    }

    /**
     * Adding comment in review
     * @param username The nickName of user that is adding comment
     * @param reviewId The review id that comment is being added
     * @param commentText The text of comment
     */
    public void addReviewComment(String username, String reviewId, String commentText) {
        PreparedStatement statement;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_COMMENT_SQL);
            statement.setString(1, reviewId);
            statement.setString(2, username);
            statement.setString(3, commentText);
            statement.executeUpdate();
            System.out.println("Successfully added comment");

        }catch(SQLException e){
            System.out.println("Failed to add booking: " + e.getMessage());
        }
    }

    /**
     * Get comments for all review
     */
    public List<CommentDto> getCommentsForReview() {
        List<CommentDto> comments = new ArrayList<>();
        PreparedStatement statement;

        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"),config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_COMMENT_SQL);

            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    CommentDto comment = new CommentDto(resultSet.getString("reviewId"),
                            resultSet.getString("username"),
                            resultSet.getString("commentText"),
                            resultSet.getTimestamp("commentDate").toString()
                    );

                    comments.add(comment);
                }
            }
        }catch(SQLException e){
            System.out.println("Failed to find comment for review: " + e.getMessage());
        }
        return comments;
    }
}


