package server;

import database.DatabaseHandler;
import loader.HotelCollection;
import loader.ProgramArgumentParser;
import loader.ThreadSafeReviewCollection;

/**
 * Driver class for running the Jetty server.
 * This class handles loading hotel and review data and starts the server.
 */
public class TravelServerDriver {
    public static final int PORT = 8080;

    private HotelCollection hotelCollection;
    private ProgramArgumentParser argParser;
    private ThreadSafeReviewCollection threadSafeReviewCollection;

    /**
     * Constructs a TravelServerDriver instance.
     * Initializes the HotelCollection, ProgramArgumentParser, and ThreadSafeReviewCollection.
     */
    public TravelServerDriver(){
        hotelCollection = new HotelCollection();
        argParser = new ProgramArgumentParser();
        threadSafeReviewCollection = new ThreadSafeReviewCollection();
    }


    /**
     * The main method to run the Jetty server.
     * It loads hotel and review data, sets up the server, and starts it.
     *
     * @param args command line arguments to specify paths for hotel and review data, and the number of threads
     */
    public static void main(String[] args)  {

        TravelServerDriver driver = new TravelServerDriver();

        driver.argParser.parseArgs(args);
        String resHotel = driver.argParser.getArgumentValue("-hotels");


        String resReview = driver.argParser.getArgumentValue("-reviews");
        String threadCountStr = driver.argParser.getArgumentValue("-threads");
        int numThreads = 1;
        if(threadCountStr!=null){
            numThreads = Integer.parseInt(threadCountStr);
        }

        try{
            try{
                try{
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance();
                    dbHandler.createTable();
                }finally{
                    driver.hotelCollection.loadHotels(resHotel);
                }
            }finally{
                driver.threadSafeReviewCollection.initializeExecutor(numThreads);
                driver.threadSafeReviewCollection.loadReviews(resReview);
                driver.threadSafeReviewCollection.shutdownExecutor();
            }
        }finally{
            try{
                driver.threadSafeReviewCollection.addDefaultUsers();
            }finally{
                driver.threadSafeReviewCollection.addDefaultReviews();
            }
        }

        try{
            JettyServer server = new JettyServer(driver.hotelCollection, driver.threadSafeReviewCollection);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


