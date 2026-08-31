package com.library.util;

import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookIdGeneratorTest {

    @Mock
    private BookRepository repository;

    private BookIdGenerator generator;

    @BeforeEach
    void setUp() {
        when(repository.findAllIds())
                .thenReturn(Collections.emptyList());

        generator = new BookIdGenerator(repository);
        generator.initialize();
    }

    @Test
    void shouldGenerateValidPattern() {

        String id = generator.generate();

        assertTrue(id.matches("^[A-Z][0-9]{2}$"));
    }

    @Test
    void shouldGenerateUniqueIds() {

        Set<String> ids = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            ids.add(generator.generate());
        }

        assertEquals(1000, ids.size());
    }

    @Test
    void shouldDecreaseAvailableCount() {

        int initial = generator.availableCount();

        generator.generate();

        assertEquals(initial - 1, generator.availableCount());
    }

    @Test
    void shouldReleaseId() {

        String id = generator.generate();
        int afterGenerate = generator.availableCount();

        generator.release(id);

        assertEquals(afterGenerate + 1, generator.availableCount());
    }

    @Test
    void shouldNotDuplicateReleasedId() {

        String id = generator.generate();

        generator.release(id);
        generator.release(id);

        assertEquals(2600, generator.availableCount());
    }

    @Test
    void shouldIgnoreNullOrBlankRelease() {

        int initial = generator.availableCount();

        generator.release(null);
        generator.release("");
        generator.release("   ");

        assertEquals(initial, generator.availableCount());
    }

    @Test
    void shouldThrowWhenPoolIsEmpty() {

        for (int i = 0; i < 2600; i++) {
            generator.generate();
        }

        assertThrows(
                IllegalStateException.class,
                () -> generator.generate()
        );
    }
}