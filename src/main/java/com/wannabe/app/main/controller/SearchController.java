package com.wannabe.app.main.controller;

import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.search.SearchDto;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping(value = "/events")
    @Operation(summary = "이벤트 검색")
    public Callable<Response<ListResponseDto<SearchDto.Event>>> searchEvents(
        @Parameter(description = "검색어") @RequestParam(defaultValue = "") String query,
        @Parameter(description = "이벤트 타입", example = "EVENT, PROMOTION") @RequestParam(defaultValue = "EVENT") String type,
        @Parameter(description = "정렬", example = "HIGH_PRICE, LOW_PRICE, CONSULT_COUNT, LATEST") @RequestParam(defaultValue = "LATEST") String sort,
        @Parameter(description = "도시") @RequestParam(required = false) String city,
        @Parameter(description = "지역") @RequestParam(required = false) Set<String> district,
        @Parameter(description = "from (시작 위치)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "from 은 0 이하일 수 없습니다.") int from,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            searchService.addSearchTerm(SearchDto.SearchTerm.create(query));
            ListResponseDto<SearchDto.Event> events = searchService.searchEvents(query, type, city, district, sort, from, size);
            return Response.of(events);
        };
    }

    @GetMapping(value = "/hospitals")
    @Operation(summary = "병원 검색")
    public Callable<Response<ListResponseDto<SearchDto.Hospital>>> searchHospitals(
        @Parameter(description = "검색어") @RequestParam String query,
        @Parameter(description = "도시") @RequestParam(required = false) String city,
        @Parameter(description = "지역") @RequestParam(required = false) Set<String> district,
        @Parameter(description = "정렬", example = "CONSULT_COUNT, LATEST") @RequestParam(defaultValue = "LATEST") String sort,
        @Parameter(description = "from (시작 위치)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "from 은 0 이하일 수 없습니다.") int from,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            searchService.addSearchTerm(SearchDto.SearchTerm.create(query));
            ListResponseDto<SearchDto.Hospital> hospitals = searchService.searchHospitals(query, city, district, sort, from, size);
            return Response.of(hospitals);
        };
    }

    @GetMapping(value = "/community")
    @Operation(summary = "커뮤니티 검색")
    public Callable<Response<ListResponseDto<SearchDto.Community>>> searchCommunity(
        @Parameter(description = "검색어") @RequestParam String query,
        @Parameter(description = "작성글 유형", example = "REVIEW, ARTICLE") @RequestParam(required = false) String articleType,
        @Parameter(description = "성형후기 유형", example = "NORMAL, EVENT, VIRTUAL") @RequestParam(required = false) String reviewType,
        @Parameter(description = "정렬", example = "LATEST, VIEW_COUNT") @RequestParam(defaultValue = "LATEST") String sort,
        @Parameter(description = "from (시작 위치)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "from 은 0 이하일 수 없습니다.") int from,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            searchService.addSearchTerm(SearchDto.SearchTerm.create(query));
            ListResponseDto<SearchDto.Community> communities = searchService.searchCommunity(query, articleType, reviewType, sort, from, size);
            return Response.of(communities);
        };
    }

    @GetMapping(value = "/all")
    @Operation(summary = "전체 검색")
    public Callable<Response<SearchDto>> searchAll(
        @Parameter(description = "검색어") @RequestParam String query,
        @Parameter(description = "이벤트 타입", example = "EVENT, PROMOTION") @RequestParam(defaultValue = "EVENT") String type,
        @Parameter(description = "도시") @RequestParam(required = false) String city,
        @Parameter(description = "지역") @RequestParam(required = false) Set<String> district,
        @Parameter(description = "작성글 유형") @RequestParam(required = false) String articleType,
        @Parameter(description = "성형후기 유형", example = "NORMAL, EVENT, VIRTUAL") @RequestParam(required = false) String reviewType,
        @Parameter(description = "정렬", example = "HIGH_PRICE, LOW_PRICE, CONSULT_COUNT, LATEST, VIEW_COUNT") @RequestParam(defaultValue = "LATEST") String sort,
        @Parameter(description = "from (시작 위치)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "from 은 0 이하일 수 없습니다.") int from,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "20") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            searchService.addSearchTerm(SearchDto.SearchTerm.create(query));
            ListResponseDto<SearchDto.Event> events = searchService.searchEvents(query, type, city, district, sort, from, size);
            ListResponseDto<SearchDto.Community> articles = searchService.searchCommunity(query, articleType, reviewType, sort, from, size);
            ListResponseDto<SearchDto.Hospital> hospitals = searchService.searchHospitals(query, city, district, sort, from, size);
            return Response.of(SearchDto.builder().hospital(hospitals).article(articles).event(events).build());
        };
    }

    @GetMapping(value = "/search-term-ranking")
    @Operation(summary = "검색 순위")
    @AnonymousCallable
    public Callable<Response<List<SearchDto.SearchTermRanking>>> getSearchTermRanking(
        @Parameter(description = "검색어") @RequestParam String query
    ) {
        return () -> {
           List<SearchDto.SearchTermRanking> list = searchService.getSearchTermRanking(query);
            return Response.of(list);
        };
    }

    @DeleteMapping(value = "/community/{communityId}")
    @Operation(summary = "커뮤니티 삭제")
    public Callable<Response<DeleteResponse>> deleteCommunity(
        @PathVariable long communityId
    ) {
        return () -> {
            DeleteResponse response = searchService.deleteCommunity(communityId);
            return Response.of(response);
        };
    }

    @DeleteMapping(value = "/event/{eventId}")
    @Operation(summary = "이벤트 삭제")
    public Callable<Response<DeleteResponse>> deleteEvent(
        @PathVariable long eventId
    ) {
        return () -> {
            DeleteResponse response = searchService.deleteEvent(eventId);
            return Response.of(response);
        };
    }

    @DeleteMapping(value = "/hospital/{hospitalId}")
    @Operation(summary = "병원 삭제")
    public Callable<Response<DeleteResponse>> deleteHospital(
        @PathVariable long hospitalId
    ) {
        return () -> {
            DeleteResponse response = searchService.deleteHospital(hospitalId);
            return Response.of(response);
        };
    }

    @DeleteMapping(value = "/search-term")
    @Operation(summary = "검색어 삭제")
    public Callable<Response<DeleteByQueryResponse>> deleteSearchTerm() {
        return () -> {
            DeleteByQueryResponse response = searchService.deleteQuery();
            return Response.of(response);
        };
    }

    @DeleteMapping(value = "/index/{indexName}")
    @Operation(summary = "인덱스 삭제")
    public Callable<Response<DeleteIndexResponse>> deleteIndex (
        @PathVariable String indexName
    ) {
        return () -> {
            DeleteIndexResponse response = searchService.deleteIndex(indexName);
            return Response.of(response);
        };
    }
}
