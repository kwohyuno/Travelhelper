package database;

/**
* Statements used to query data from database
*/
public class PreparedStatements {

    /**
    * Create user table
    */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    /**
     * Create hotels table
     */
    public static final String CREATE_HOTELS_TABLE =
            "CREATE TABLE IF NOT EXISTS hotels(" +
                    "hotelId VARCHAR(255) PRIMARY KEY," +
                    "hotelName VARCHAR(255) NOT NULL," +
                    "latitude VARCHAR(50)," +
                    "longitude VARCHAR(50)," +
                    "streetAddress VARCHAR(255)," +
                    "city VARCHAR(100)," +
                    "state VARCHAR(100)," +
                    "country VARCHAR(100));";

    /**
     * Create review table
     */
    public static final String CREATE_REVIEW_TABLE =
            "CREATE TABLE IF NOT EXISTS reviews(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, "+
                    "reviewId VARCHAR(255) NOT NULL UNIQUE , " +
                    "hotelId VARCHAR(255) NOT NULL," +
                    "overallRating DOUBLE NOT NULL, " +
                    "reviewTitle VARCHAR(255)," +
                    "reviewText TEXT, " +
                    "userNickname VARCHAR(32)," +
                    "reviewDate DATE, " +
                    "FOREIGN KEY (hotelId) REFERENCES hotels(hotelId) ON DELETE CASCADE," +
                    "FOREIGN KEY (userNickname) REFERENCES users(username) ON DELETE CASCADE);";

    /**
     * Checking hotel
     */
    public static final String CHECK_HOTELS_TABLE =
            "SELECT 1 FROM hotels WHERE hotelId = ?";

    /**
     * Checking user
     */
    public static final String CHECK_DEFAULT_USERS_SQL =
            "SELECT COUNT(*) FROM users WHERE username = ?";

    /**
     * Inserting default review
     */
    public static final String INSERT_DEFAULT_REVIEW_SQL =
            "INSERT INTO reviews (reviewId, hotelId, overallRating, reviewTitle, reviewText, userNickname, reviewDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * Inserting default hotel
     */
    public static final String INSERT_DEFAULT_HOTEL_SQL =
            "INSERT INTO hotels (hotelId, hotelName, latitude, longitude, streetAddress, city, state, country) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Update Review SQL
     */
    public static final String UPDATE_REVIEW_SQL =
            "UPDATE reviews SET hotelID = ?, userNickname = ?, overallRating = ?, reviewTitle = ?, reviewText = ?, userNickname = ?, reviewDate = ?" +
            "WHERE reviewId = ?";

    /**
     * Delete Review
     */
    public static final String DELETE_REVIEW_SQL=
            "DELETE FROM reviews WHERE reviewId = ?";

    /**
     * Checking before inserting review
     */
    public static final String CHECK_BEFORE_INSERT_REVIEW_SQL =
            "SELECT 1 FROM reviews WHERE reviewID = ?";

    /**
     * Inserting default users
     */
    public static final String INSERT_DEFAULT_USERS_SQL =
            "INSERT INTO users (username, password, usersalt) VALUES (?, ?, ?) ";



    /**
     * Register new user by saving the info
     */
    public static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) VALUES (?, ?, ?);";

    /**
     * Checking if username exists
     */
    public static final String USERNAMECHECK_SQL =
            "SELECT * from users WHERE username = ? ;";

    /**
     * Authenticating username and password by checking if it exists
     */
    public static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username=? AND password =?";

    /**
     * Getting salt from the table
     */
    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username=?";
    /**
     * Creating favorite hotel table
     */
    public static final String CREATE_FAVORITEHOTEL_SQL =
            "CREATE TABLE IF NOT EXISTS favoritehotels(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, "+
                    "username VARCHAR(32), " +
                    "hotelId VARCHAR(255)," +
                    "FOREIGN KEY (hotelId) REFERENCES hotels(hotelId) ON DELETE CASCADE," +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);";

    /**
     * Inserting favorite hotel
     */
    public static final String INSERT_FAVHOTEL_SQL =
            "INSERT INTO favoritehotels (username, hotelId) " +
                    "VALUES (?, ?)";

    /**
     * Getting favorite hotel
     */
    public static final String GET_FAVHOTEL_SQL =
            "SELECT * FROM favoritehotels WHERE username = ?";

    /**
     * Getting favorite hotel class
     */
    public static final String GET_GETFAVHOTELCLASS_SQL =
            "SELECT * from hotels WHERE hotelId = ?";

    /**
     * Creating expedia link table
     */
    public static final String CREATE_EXPEDIALINK_TABLE =
            "CREATE TABLE IF NOT EXISTS expediahistory(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "expediaLink VARCHAR(255), " +
                    "username VARCHAR(32) NOT NULL, " +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);";

    /**
     * Getting Expedia link
     */
    public static final String GET_EXPEDIALINK_SQL =
            "SELECT * FROM expediahistory WHERE username = ?";

    /**
     * Inserting Expedia link
     */
    public static final String INSERT_EXPEDIALINK_SQL =
            "INSERT INTO expediahistory (expediaLink, username)" +
                    "VALUES (?, ?)";

    /**
     * Deleting Expedia link
     */
    public static final String DELETE_EXPEDIALINKS_SQL =
            "DELETE FROM expediahistory WHERE username = ?";

    /**
     * Deleting Favorite Hotel List
     */
    public static final String DELETE_FAVORITEHOTELLIST_SQL =
            "DELETE FROM favoritehotels WHERE username = ?";

    /**
     * Create last login table
     */
    public static final String CREATE_LASTLOGIN_TABLE =
            "CREATE TABLE IF NOT EXISTS lastlogin(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, "+
                    "username VARCHAR(32) NOT NULL, "+
                    "logindate VARCHAR(255), "+
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE)";

    /**
     * Insert last login
     */
    public static final String INSERT_LASTLOGIN_SQL =
            "INSERT INTO lastlogin (username,logindate) VALUES (?,?)";

    /**
     * Check last login
     */
    public static final String CHECK_LASTLOGIN_SQL =
            "SELECT * FROM lastlogin WHERE username = ?";

    /**
     * Update last login
     */
    public static final String UPDATE_LASTLOGIN_SQL=
            "UPDATE lastlogin SET logindate = CURRENT_TIMESTAMP WHERE username=? ";

    /**
     * Find last login
     */
    public static final String FIND_LASTLOGIN_SQL =
            "SELECT  logindate FROM lastlogin WHERE username = ?" +
                "ORDER BY logindate DESC " +
                "LIMIT 1";

    /**
     * Create booking table
     */
    public static final String CREATE_BOOKINGS_SQL =
            "CREATE TABLE IF NOT EXISTS bookings(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "hotelId VARCHAR(255) NOT NULL, " +
                    "startDate DATE, " +
                    "endDate DATE, " +
                    "roomNumber INT NOT NULL, " +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE, " +
                    "FOREIGN KEY (hotelId) REFERENCES hotels(hotelId) ON DELETE CASCADE)";

    /**
     * Checking booking status
     */
    public static final String CHECK_BOOKINGSTATUS_SQL =
            "SELECT COUNT(*) as totalBooking FROM bookings " +
                    "WHERE hotelId = ? " +
                    "AND (startDate < ? AND endDate > ?)";

    /**
     * Checking Room num
     */
    public static  final String CHECK_ROOMNUM_SQL =
            "SELECT COALESCE(MAX(roomNumber), 0)+1 AS nextRoomNumber FROM bookings " +
                    "WHERE hotelId = ? AND NOT (startDate > ? OR endDate < ?)";

    /**
     * Inserting booking
     */
    public static final String INSERT_BOOKIING_SQL =
            "INSERT INTO bookings (username, hotelId, startDate, endDate, roomNumber) VALUES (?, ?, ?, ?, ?)";

    /**
     * Creating comment table
     */
    public static final String CREATE_COMMENT_SQL =
            "CREATE TABLE IF NOT EXISTS comments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "reviewId VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "commentText TEXT NOT NULL, " +
                    "commentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (reviewId) REFERENCES reviews(reviewId) ON DELETE CASCADE," +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);";

    /**
     * Inserting into comment table
     */
    public static final String INSERT_COMMENT_SQL =
            "INSERT INTO comments (reviewId, username, commentText) VALUES (?, ?, ?)";

    /**
     * Getting all comments
     */
    public static final String GET_COMMENT_SQL =
            "SELECT * FROM comments ORDER BY commentDate DESC";
}


