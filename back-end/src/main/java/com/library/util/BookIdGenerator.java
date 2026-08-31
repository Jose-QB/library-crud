package com.library.util;

import com.library.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BookIdGenerator {

    private static final char FIRST_LETTER_MIN = 'A';
    private static final char FIRST_LETTER_MAX = 'Z';

    private static final int NUMBER_MIN = 0;
    private static final int NUMBER_MAX = 99;

    private final BookRepository bookRepository;

    private final SecureRandom random = new SecureRandom();

    /**
     * IDs currently available for new books.
     *
     * Maximum size: 2600.
     */
    private final List<String> availableIds =
            new ArrayList<>();

    /**
     * Initializes the available ID pool when
     * the application starts.
     */
    @PostConstruct
    public synchronized void initialize() {
        rebuild();
    }

    /**
     * Generates and reserves a random available ID.
     *
     * The selected ID is immediately removed from
     * the available pool.
     *
     * @return a random available book ID
     * @throws IllegalStateException if no IDs are available
     */
    public synchronized String generate() {

        if (availableIds.isEmpty()) {
            throw new IllegalStateException(
                    "No book IDs are available. " +
                            "The maximum number of books has been reached."
            );
        }

        int index =
                random.nextInt(availableIds.size());

        return availableIds.remove(index);
    }

    /**
     * Returns an ID to the available pool.
     *
     * This should be called after a book is successfully deleted.
     *
     * @param id the ID to release
     */
    public synchronized void release(String id) {

        if (id == null || id.isBlank()) {
            return;
        }

        if (!availableIds.contains(id)) {
            availableIds.add(id);
        }
    }

    /**
     * Rebuilds the available ID pool using the database
     * as the source of truth.
     *
     * All 2600 possible IDs are generated and IDs that
     * already exist in the database are removed.
     */
    public synchronized void rebuild() {

        Set<String> existingIds =
                new HashSet<>(bookRepository.findAllIds());

        availableIds.clear();

        for (
                char letter = FIRST_LETTER_MIN;
                letter <= FIRST_LETTER_MAX;
                letter++
        ) {
            for (
                    int number = NUMBER_MIN;
                    number <= NUMBER_MAX;
                    number++
            ) {
                String id =
                        String.format("%c%02d", letter, number);

                if (!existingIds.contains(id)) {
                    availableIds.add(id);
                }
            }
        }
    }

    /**
     * Returns the number of IDs currently available.
     *
     * @return number of available IDs
     */
    public synchronized int availableCount() {
        return availableIds.size();
    }
}