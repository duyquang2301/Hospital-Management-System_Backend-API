package com.wannabe.app.main.response;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Builder
@Getter
@AllArgsConstructor
public class ListResponseDto<T> {

    private long total;
    private List<T> list;

    public static <T> ListResponseDto<T> of(List<T> list, long total) {
        return new ListResponseDto<>(total, list);
    }

    public static <T> ListResponseDto<T> from(List<T> list) {
        return new ListResponseDto<>(list.size(), list);
    }

    public <U> ListResponseDto<U> map(Function<? super T, ? extends U> converter) {
        return new ListResponseDto<>(this.total, getConvertedContent(converter));
    }

    private <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
        Assert.notNull(converter, "Function must not be null");

        return this.list.stream().map(converter).collect(Collectors.toList());
    }
}
