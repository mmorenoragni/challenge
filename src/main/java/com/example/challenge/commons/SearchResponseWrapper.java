package com.example.challenge.commons;

import java.util.List;

public class SearchResponseWrapper<T> {

    private final long totalSize;
    private final int currentPage;
    private final long totalPages;
    private final List<T> content;

    public SearchResponseWrapper(long totalSize, int currentPage,long totalPages, List<T> content) {
        this.totalSize = totalSize;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.content = content;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public List<T> getContent() {
        return content;
    }
}
