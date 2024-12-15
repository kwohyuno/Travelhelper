package loader;

import java.util.*;

/**
 * This class is responsible for parsing command-line arguments.
 * It stores arguments in a map for easy retrieval.
 */
public class ProgramArgumentParser {

    private Map<String, String> argsMap;

    /**
     * Constructs an instance of ProgramArgumentParser.
     * Initializes the internal map to store arguments.
     */
    public ProgramArgumentParser(){
        argsMap = new HashMap<>();
    }

    /**
     * Parses command-line arguments and stores them in a map.
     * Arguments are expected in the form of key-value pairs (e.g., "-key value").
     *
     * @param args an array of command-line arguments
     */
    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (i + 1 < args.length) {
                    argsMap.put(args[i], args[i + 1]);
                    i++;
                }
            }
        }
    }

    /**
     * Retrieves the value associated with a specified argument key.
     *
     * @param name the key of the argument
     * @return the value associated with the key, or null if not found
     */
    public String getArgumentValue(String name){
        return argsMap.get(name);
    }
}



