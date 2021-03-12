package fr.univtln.bruno.samples.jaxrs.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Page<T> {
    final long pageSize;
    final long pageNumber;
    final long elementTotal;
    final List<T> content;
    final long pageTotal;

    private Page(long pageSize, long pageNumber, long elementTotal, List<T> content) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.elementTotal = elementTotal;
        this.content = content;
        this.pageTotal = elementTotal / pageSize;
    }

    public static <V> Page<V> newInstance(long pageSize, long pageNumber, long elementTotal, List<V> content) {
        return new Page<>(pageSize, pageNumber, elementTotal, content);
    }
}
