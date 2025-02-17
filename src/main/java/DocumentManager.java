import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
 public class DocumentManager {
    private final Map<String, Document> storage = new HashMap<>();  // Використання мапи для зберігання даних в локальній пам'яті

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString()); // ідентифікатор у вигляді рядка відповідно до специфікації
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(document -> matches(document, request)) // створив окремий метод для перевірки відповідності умовам
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));

    }
    // Також простим варіантом є використання предикатів, але загальний обсяг коду буде приблизно такий же
    private boolean matches(Document document, SearchRequest request) {
        if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
            boolean titleMatches = request.getTitlePrefixes().stream()
                    .anyMatch(prefix -> document.getTitle() != null && document.getTitle().startsWith(prefix)); // повертає всі збіги за префіксом, тобто якщо є тайтли Book & Booking то поверне обидва
            if (!titleMatches) return false;
        }

        if (request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
            boolean contentMatches = request.getContainsContents().stream()
                    .anyMatch(keyword -> document.getContent() != null && document.getContent().contains(keyword));
            if (!contentMatches) return false;
        }

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            boolean authorMatches = request.getAuthorIds().contains(document.getAuthor().getId());
            if (!authorMatches) return false;
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }

        return request.getCreatedTo() == null || !document.getCreated().isAfter(request.getCreatedTo());
    }
    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}