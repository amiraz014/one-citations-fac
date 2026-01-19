package org.gso.profiles.dto;


import java.net.URI;
import java.util.List;

public record PageDto<T> (
    int pageSize,
    long totalElements,
    URI next,
    URI first,
    URI last,
    List<T> data) {
}
