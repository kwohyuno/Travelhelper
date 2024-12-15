package server;


import database.DatabaseHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import loader.HotelCollection;
import loader.ThreadSafeReviewCollection;
import org.eclipse.jetty.servlet.ServletHolder;
import servlet.*;

/**
 * This class sets up a Jetty server to handle HTTP GET requests using servlets.
 * It maps various servlets to specific URL paths to handle different endpoints.
 */
public class JettyServer {

    private static final int PORT = 9999;
    private Server jettyServer;
    private ServletHandler handler;
    private HotelCollection hotelCollection;
    private ThreadSafeReviewCollection threadSafeReviewCollection;

    /**
     * Constructs a JettyServer instance with the given HotelCollection and ThreadSafeReviewCollection.
     * Initializes the Jetty server, sets up servlet handlers, and maps servlets to URL paths.
     *
     * @param hotelCollection           the collection of hotels
     * @param threadSafeReviewCollection the collection of reviews in a thread-safe manner
     */
    public JettyServer(HotelCollection hotelCollection, ThreadSafeReviewCollection threadSafeReviewCollection) {
        this.hotelCollection = hotelCollection;
        this.threadSafeReviewCollection = threadSafeReviewCollection;
        this.jettyServer = new Server(PORT);
        this.handler = new ServletHandler();

        handler.addServletWithMapping(new ServletHolder(new HotelServlet(hotelCollection)), "/hotels/*");
        handler.addServletWithMapping(new ServletHolder(new RegisterServlet()), "/register");
        handler.addServletWithMapping(new ServletHolder(new LoginServlet()), "/");
        handler.addServletWithMapping(new ServletHolder(new ReviewServlet(threadSafeReviewCollection)),"/reviews/*");
        handler.addServletWithMapping(new ServletHolder(new FavoriteHotelServlet()),"/favorite/*");
        handler.addServletWithMapping(new ServletHolder(new ExpediaServlet()),"/expedia/*");
        handler.addServletWithMapping(new ServletHolder(new LastLoginServlet()),"/lastlogin/*");
        handler.addServletWithMapping(new ServletHolder(new CheckLastLoginServlet()),"/loginrecord/*");
        handler.addServletWithMapping(new ServletHolder(new BookingServlet()),"/bookings/*");
        handler.addServletWithMapping(new ServletHolder(new CommentServlet()),"/comments/*");


        jettyServer.setHandler(handler);
    }

    /**
     * Starts the Jetty server and waits for it to join.
     *
     * @throws Exception if the server fails to start or join
     */
    public void start() throws Exception{
        jettyServer.start();
        jettyServer.join();
    }
}
