package com.library.util;

import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookIdGeneratorTest {

    @Mock
    private BookRepository bookRepository;

    private BookIdGenerator bookIdGenerator;

    @BeforeEach
    void setUp() {

        bookIdGenerator =
                new BookIdGenerator(bookRepository);
    }

    // =========================================================
    // INITIALIZE / REBUILD
    // =========================================================

    @Test
    void initialize_shouldCreateAllAvailableIdsWhenDatabaseIsEmpty() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        verify(bookRepository)
                .findAllIds();
    }

    @Test
    void rebuild_shouldCreateAllPossibleIds() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.rebuild();

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        verify(bookRepository)
                .findAllIds();
    }

    @Test
    void rebuild_shouldExcludeExistingIds() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of(
                        "A00",
                        "A01",
                        "M50",
                        "Z99"
                ));

        bookIdGenerator.rebuild();

        assertEquals(
                2596,
                bookIdGenerator.availableCount()
        );

        verify(bookRepository)
                .findAllIds();
    }

    @Test
    void rebuild_shouldExcludeOnlyExistingIds() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of(
                        "A00",
                        "A01",
                        "A01",
                        "Z99"
                ));

        bookIdGenerator.rebuild();

        /*
         * A01 aparece dos veces en la BD, pero solo
         * representa un ID ocupado.
         */
        assertEquals(
                2597,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void rebuild_shouldRebuildPoolFromDatabase() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.rebuild();

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        /*
         * Simulamos que posteriormente existen
         * varios libros en la base de datos.
         */
        when(bookRepository.findAllIds())
                .thenReturn(List.of(
                        "A00",
                        "B01",
                        "C02"
                ));

        bookIdGenerator.rebuild();

        assertEquals(
                2597,
                bookIdGenerator.availableCount()
        );
    }

    // =========================================================
    // GENERATE
    // =========================================================

    @Test
    void generate_shouldReturnValidId() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        String id =
                bookIdGenerator.generate();

        assertNotNull(id);

        assertTrue(
                id.matches("[A-Z][0-9]{2}")
        );

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void generate_shouldReserveId() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        String id =
                bookIdGenerator.generate();

        assertNotNull(id);

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );

        /*
         * El mismo ID ya no debe estar disponible.
         *
         * Generamos el resto de IDs y verificamos
         * que no vuelva a aparecer.
         */
        boolean foundAgain = false;

        for (int i = 0; i < 2599; i++) {

            String generated =
                    bookIdGenerator.generate();

            if (generated.equals(id)) {
                foundAgain = true;
                break;
            }
        }

        assertFalse(foundAgain);
        assertEquals(
                0,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void generate_shouldEventuallyGenerateAllUniqueIds() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        /*
         * Un HashSet permite comprobar que los 2600
         * IDs generados son únicos.
         */
        java.util.Set<String> generatedIds =
                new java.util.HashSet<>();

        for (int i = 0; i < 2600; i++) {

            String id =
                    bookIdGenerator.generate();

            assertTrue(
                    generatedIds.add(id),
                    "Duplicate ID generated: " + id
            );
        }

        assertEquals(
                2600,
                generatedIds.size()
        );

        assertEquals(
                0,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void generate_shouldThrowExceptionWhenNoIdsAreAvailable() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        /*
         * Consumimos los 2600 IDs disponibles.
         */
        for (int i = 0; i < 2600; i++) {
            bookIdGenerator.generate();
        }

        assertEquals(
                0,
                bookIdGenerator.availableCount()
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bookIdGenerator.generate()
                );

        assertEquals(
                "No book IDs are available. " +
                        "The maximum number of books has been reached.",
                exception.getMessage()
        );
    }

    // =========================================================
    // RELEASE
    // =========================================================

    @Test
    void release_shouldReturnIdToAvailablePool() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        String id =
                bookIdGenerator.generate();

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );

        bookIdGenerator.release(id);

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void release_shouldNotAddDuplicateId() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        String id =
                bookIdGenerator.generate();

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );

        bookIdGenerator.release(id);

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        /*
         * Liberarlo nuevamente no debe crear
         * un duplicado en availableIds.
         */
        bookIdGenerator.release(id);

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void release_shouldIgnoreNullId() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        bookIdGenerator.release(null);

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void release_shouldIgnoreBlankId() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        bookIdGenerator.release("");

        bookIdGenerator.release("   ");

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void release_shouldMakeGeneratedIdAvailableAgain() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        String releasedId =
                bookIdGenerator.generate();

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );

        bookIdGenerator.release(releasedId);

        assertEquals(
                2600,
                bookIdGenerator.availableCount()
        );

        /*
         * Consumimos todos los IDs hasta encontrar
         * nuevamente el ID liberado.
         */
        boolean foundReleasedId = false;

        for (int i = 0; i < 2600; i++) {

            String generated =
                    bookIdGenerator.generate();

            if (generated.equals(releasedId)) {
                foundReleasedId = true;
                break;
            }
        }

        assertTrue(foundReleasedId);
    }

    // =========================================================
    // DATABASE STATE
    // =========================================================

    @Test
    void rebuild_shouldRespectDatabaseStateAfterPreviouslyGeneratedIds() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        String generatedId =
                bookIdGenerator.generate();

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );

        /*
         * Simulamos que el ID generado ya fue persistido
         * en la base de datos.
         */
        when(bookRepository.findAllIds())
                .thenReturn(List.of(generatedId));

        bookIdGenerator.rebuild();

        assertEquals(
                2599,
                bookIdGenerator.availableCount()
        );

        /*
         * Si rebuild() realmente toma la BD como fuente
         * de verdad, el ID no debe volver al pool.
         */
        for (int i = 0; i < 2599; i++) {

            String id =
                    bookIdGenerator.generate();

            assertNotEquals(
                    generatedId,
                    id
            );
        }

        assertEquals(
                0,
                bookIdGenerator.availableCount()
        );
    }

    @Test
    void initialize_shouldCallRepository() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.initialize();

        verify(
                bookRepository,
                times(1)
        ).findAllIds();
    }

    @Test
    void rebuild_shouldCallRepositoryEveryTime() {

        when(bookRepository.findAllIds())
                .thenReturn(List.of());

        bookIdGenerator.rebuild();
        bookIdGenerator.rebuild();

        verify(
                bookRepository,
                times(2)
        ).findAllIds();
    }
}