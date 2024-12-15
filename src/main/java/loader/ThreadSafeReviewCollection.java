package loader;

import database.DatabaseHandler;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A thread-safe implementation of the ReviewData class that allows loading and managing reviews concurrently.
 * Uses a ReentrantReadWriteLock for synchronization and an ExecutorService for parallel processing.
 */
public class ThreadSafeReviewCollection extends ReviewData{


    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ExecutorService executor;
    private Phaser phaser;

    /**
     * Constructs an instance of ThreadSafeReviewCollection.
     */
    public ThreadSafeReviewCollection() {
        super();
    }

    /**
     * Initializes the ExecutorService with a fixed thread pool and a Phaser for synchronization.
     *
     * @param numThreads the number of threads to use in the thread pool
     */
    public void initializeExecutor(int numThreads){
        this.executor = Executors.newFixedThreadPool(numThreads);
        this.phaser = new Phaser();
    }

    /**
     * Loads reviews from the specified folder path. It uses the ExecutorService to
     * load reviews concurrently from multiple JSON files.
     *
     * @param pathToReviewsFolder the path to the folder containing review files
     */
    @Override
    public void loadReviews(String pathToReviewsFolder){
        Path p = Paths.get(pathToReviewsFolder);

        try(DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(p)){
            for(Path path : pathsInDir){
                if(Files.isDirectory(path)){
                    loadReviews(path.toString());
                }else if(path.toString().endsWith(".json")){
                    executor.submit(new ReviewLoader(path));
                }
            }

        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Inner class that handles loading reviews from a specified file.
     * It implements Runnable to allow concurrent execution.
     */
    public class ReviewLoader implements Runnable{
        private final Path path;
        private final Phaser phaser;

        /**
         * Constructs a ReviewLoader instance for a specific file path.
         * Registers itself with the provided Phaser.
         *
         * @param path the path to the JSON file containing review data
         */
        public ReviewLoader(Path path){
            this.path = path;
            this.phaser = ThreadSafeReviewCollection.this.phaser;
            phaser.register();
        }

        /**
         * Executes the review loading and parsing task. This method reads
         * reviews from the specified file path, parses them, and adds
         * them to the ThreadSafeReviewCollection instance.
         */
        @Override
        public void run(){
            try{
                Review[] reviews = ReviewParser.parseReviews(path.toString());
                addReviewsToMap(reviews);
            }catch(Exception e){
                System.out.print(e.getMessage());
            }finally{
                phaser.arriveAndDeregister();
            }
        }
    }

    /**
     * Adds reviews to the map in a thread-safe manner by acquiring a write lock.
     *
     * @param reviews the array of Review objects to add
     */
    @Override
    public void addReviewsToMap(Review[] reviews){
        lock.writeLock().lock();
        try{
            super.addReviewsToMap(reviews);
        }finally{
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds new reviews
     * @param review the review object that is being written
     */
    @Override
    public void writeReview(Review review){
        lock.writeLock().lock();
        try{
            super.writeReview(review);
        }finally{
            lock.writeLock().unlock();
        }
    }



    /**
     * Shuts down the ExecutorService and waits for all tasks to complete.
     */
    public void shutdownExecutor(){
        phaser.awaitAdvance(0);
        executor.shutdown();
    }

    /**
     * Finds reviews by hotel ID in a thread-safe manner by acquiring a read lock.
     *
     * @param hotelId the ID of the hotel to search for reviews
     * @return a TreeSet of Review objects associated with the hotel ID
     */
    @Override
    public TreeSet<Review> findReviewsByHotelId(String hotelId){
        lock.readLock().lock();
        try{
            return super.findReviewsByHotelId(hotelId);
        }finally{
            lock.readLock().unlock();
        }
    }

    /**
     *
     * Update reviews with edited information
     *
     * @param updatedReview the Object of updated review
     * */
    @Override
    public void updateReview(Review updatedReview){
       lock.writeLock().lock();
       try{
           super.updateReview(updatedReview);
       }finally{
           lock.writeLock().unlock();
       }
    }

    /**
     * Delete review if it is requested
     * */
    @Override
    public void deleteReview(String reviewId){
        lock.writeLock().lock();
        try{
            super.deleteReview(reviewId);
        }finally{
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds default users
     */
    @Override
    public void addDefaultUsers(){
        super.addDefaultUsers();
    }

    /**
     * Adds default reviews
     */
    @Override
    public void addDefaultReviews(){
        super.addDefaultReviews();
    }
}

