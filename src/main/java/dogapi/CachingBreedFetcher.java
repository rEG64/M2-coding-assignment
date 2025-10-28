package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher; // The underlying fetcher (e.g., DogApiBreedFetcher)
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Normalize breed name (to handle case-insensitive duplicates)
        String normalizedBreed = breed.toLowerCase();

        // Check cache first
        if (cache.containsKey(normalizedBreed)) {
            return cache.get(normalizedBreed);
        }

        // If not cached, call the underlying fetcher
        try {
            List<String> subBreeds = fetcher.getSubBreeds(normalizedBreed);
            callsMade++; // Count only successful API calls
            // Cache the result
            cache.put(normalizedBreed, subBreeds);
            return subBreeds;
        } catch (BreedNotFoundException e) {
            // Do not cache failed results
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}
