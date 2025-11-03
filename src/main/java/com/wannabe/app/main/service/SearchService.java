package com.wannabe.app.main.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.JsonData;
import com.wannabe.app.main.data.dto.search.SearchDto;
import com.wannabe.app.main.data.state.SortType;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.utility.constant.ElasticSearch;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchService {

    Logger logger = LogManager.getLogger(this.getClass());

    private final CloudFrontService cloudFrontService;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 이벤트 검색
     *
     * @param query    검색어
     * @param type     이벤트 타입
     * @param city     도시
     * @param district 지역
     * @param sort     정렬 조건
     * @param page     현재 페이지
     * @param size     가져올 사이즈
     * @return ListResponseDto<SearchDto.Event> 이벤트 목록 정보
     * @throws IOException ElasticsearchClient 검색 에러가 발생한 경우
     */
    public ListResponseDto<SearchDto.Event> searchEvents(String query, String type, String city, Set<String> district, String sort, int page, int size)
        throws IOException {
        List<Query> queries = new ArrayList<>();
        queries.add(multiMatchQuery(query, ElasticSearch.FIELDS_NAME, ElasticSearch.FIELDS_HOSPITAL_NAME, ElasticSearch.FIELDS_CITY,
            ElasticSearch.FIELDS_DISTRICT));
        if (isExist(city)) {
            queries.add(matchQuery(ElasticSearch.FIELDS_CITY, city));
        }
        if (isExist(district)) {
            queries.add(matchQuery(ElasticSearch.FIELDS_DISTRICT, district.toString()));
        }
        queries.add(term(ElasticSearch.FIELDS_TYPE, type));
        queries.add(term(ElasticSearch.FIELDS_STATE, "ACTIVE"));

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(ElasticSearch.INDEX_EVENT)
            .from(page)
            .size(size)
            .query(boolQuery(queries))
            .sort(st -> st.field(f -> f.field("_score").order(SortOrder.Desc)))
            .sort(st -> st.field(f -> f.field(SortType.getValue(sort)).order(SortType.getOrder(sort))))
        );

        SearchResponse<SearchDto.Event> searchResponse = elasticsearchClient.search(searchRequest, SearchDto.Event.class);
        List<SearchDto.Event> list = searchResponse.hits().hits().stream().map(Hit::source).filter(Objects::nonNull)
            .peek(i -> i.setThumbnail((cloudFrontService.generateSignedUrl(i.getThumbnail())))).toList();

        return ListResponseDto.of(list, total(searchResponse));
    }

    /**
     * 병원 검색
     *
     * @param query    검색어
     * @param city     도시
     * @param district 지역
     * @param sort     정렬 조건
     * @param page     현재 페이지
     * @param size     가져올 사이즈
     * @return ListResponseDto<SearchDto.Hospital> 병원 목록 정보
     * @throws IOException ElasticsearchClient 검색 에러가 발생한 경우
     */
    public ListResponseDto<SearchDto.Hospital> searchHospitals(String query, String city, Set<String> district, String sort, int page, int size)
        throws IOException {

        List<Query> queries = new ArrayList<>();
        queries.add(multiMatchQuery(query, ElasticSearch.FIELDS_NAME, ElasticSearch.FIELDS_CITY, ElasticSearch.FIELDS_DISTRICT, ElasticSearch.FIELDS_MEDICAL_CATEGORY));
        if (isExist(city)) {
            queries.add(matchQuery(ElasticSearch.FIELDS_CITY, city));
        }
        if (isExist(district)) {
            queries.add(matchQuery(ElasticSearch.FIELDS_DISTRICT, district.toString()));
        }
        queries.add(term(ElasticSearch.FIELDS_STATE, "ACTIVE"));

        List<SortOptions> sortOptions = new ArrayList<>();
        if(SortType.getValue(sort).equals(SortType.LATEST.fieldValue())) {
            sortOptions.add(getSortOptions("_score", SortOrder.Desc));
            sortOptions.add(getSortOptions(ElasticSearch.FIELDS_EXPOSED_RANK, SortOrder.Asc));
            sortOptions.add(getSortOptions(SortType.LATEST.fieldValue(), SortType.LATEST.orderValue()));
        } else {
            sortOptions.add(getSortOptions(SortType.getValue(sort), SortType.getOrder(sort)));
            sortOptions.add(getSortOptions(ElasticSearch.FIELDS_EXPOSED_RANK, SortOrder.Asc));
            sortOptions.add(getSortOptions("_score", SortOrder.Desc));
        }

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(ElasticSearch.INDEX_HOSPITAL)
            .from(page)
            .size(size)
            .query(boolQuery(queries))
            .sort(sortOptions)
        );

        SearchResponse<SearchDto.Hospital> searchResponse = elasticsearchClient.search(searchRequest, SearchDto.Hospital.class);
        List<SearchDto.Hospital> list = searchResponse.hits().hits().stream().map(Hit::source).filter(Objects::nonNull)
            .peek(i -> i.setThumbnail((cloudFrontService.generateSignedUrl(i.getThumbnail())))).toList();

        return ListResponseDto.of(list, total(searchResponse));
    }

    private SortOptions getSortOptions(String sort, SortOrder order) {
        return new SortOptions.Builder().field(f -> f.field(sort).order(order)).build();
    }

    /**
     * 게시글 검색
     *
     * @param query       검색어
     * @param articleType 게시글 타입
     * @param reviewType 가상성형 후기 타입
     * @param sort        정렬 조건
     * @param page        현재 페이지
     * @param size        가져올 사이즈
     * @return ListResponseDto<SearchDto.Community> 게시글 목록 정보
     * @throws IOException ElasticsearchClient 검색 에러가 발생한 경우
     */
    public ListResponseDto<SearchDto.Community> searchCommunity(String query, String articleType, String reviewType, String sort, int page, int size)
        throws IOException {
        List<Query> queries = new ArrayList<>();
        queries.add(multiMatchQuery(query, ElasticSearch.FIELDS_CONTENT, ElasticSearch.FIELDS_NICKNAME));
        if (isExist(articleType)) {
            queries.add(term(ElasticSearch.FIELDS_ARTICLE_TYPE, articleType));
        }
        if (isExist(reviewType)) {
            queries.add(term(ElasticSearch.FIELDS_REVIEW_TYPE, reviewType));
        }

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(ElasticSearch.INDEX_ARTICLE)
            .from(page)
            .size(size)
            .query(boolQuery(queries))
            .sort(st -> st.field(f -> f.field("_score").order(SortOrder.Desc)))
            .sort(st -> st.field(f -> f.field(SortType.getValue(sort)).order(SortType.getOrder(sort))))
        );

        SearchResponse<SearchDto.Community> searchResponse = elasticsearchClient.search(searchRequest, SearchDto.Community.class);
        List<SearchDto.Community> list = searchResponse.hits().hits()
            .stream().map(Hit::source).filter(Objects::nonNull)
            .peek(i -> {
                i.setThumbnailList((cloudFrontService.generateSignedUrl(i.getThumbnail())));
                i.setProfilePath(cloudFrontService.generateSignedUrl(i.getProfilePath()));
                i.setBeforeImage(cloudFrontService.generateSignedUrl(i.getBeforeImage()));
                i.setAfterImage(cloudFrontService.generateSignedUrl(i.getAfterImage()));
            }).toList();

        return ListResponseDto.of(list, total(searchResponse));
    }

    /**
     * 검색어 저장
     *
     * @param searchTerm 검색어 정보
     * @throws IOException ElasticsearchClient 데이터 저장시 에러가 발생한 경우
     */
    @Async
    public void addSearchTerm(SearchDto.SearchTerm searchTerm) throws IOException {
        elasticsearchClient.index(i -> i
            .index(ElasticSearch.INDEX_QUERY)
            .id(searchTerm.getDateUpdated().toString())
            .document(searchTerm)
        );
    }

    /**
     * 검색 순위
     *
     * @param query 검색어
     * @return List<SearchDto.SearchTermRanking> 검색 순위 정보
     * @throws IOException ElasticsearchClient 검색 에러가 발생한 경우
     */
    public List<SearchDto.SearchTermRanking> getSearchTermRanking(String query) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(ElasticSearch.INDEX_QUERY)
            .size(0)
            .query(matchQuery(ElasticSearch.FIELDS_QUERY, query))
            .aggregations(ElasticSearch.AGGREGATIONS_KEY, terms(ElasticSearch.FIELDS_QUERY_KEYWORD, 10))
        );

        return elasticsearchClient.search(searchRequest, Void.class)
            .aggregations().get(ElasticSearch.AGGREGATIONS_KEY).sterms().buckets().array()
            .stream().map(bucket -> new SearchDto.SearchTermRanking(bucket.key().stringValue(), bucket.docCount()))
            .toList();
    }

    /**
     * 검색어 삭제
     *
     * @return DeleteByQueryResponse
     * @throws IOException ElasticsearchClient 데이터 삭제중 에러가 발생한 경우
     */
    public DeleteByQueryResponse deleteQuery() throws IOException {
        return elasticsearchClient.deleteByQuery(d -> d
            .index(ElasticSearch.INDEX_QUERY)
            .query(q -> q
                .range(r -> r
                    .field(ElasticSearch.FIELDS_DATE_UPDATED)
                    .lt(JsonData.of(Instant.now().minus(100, ChronoUnit.DAYS).getEpochSecond()))
                )
            )
        );
    }

    /**
     * 커뮤니티 삭제
     *
     * @param communityId 커뮤니티 아이디
     * @return DeleteResponse
     * @throws IOException ElasticsearchClient 인덱스 삭제중 에러가 발생한 경우
     */
    public DeleteResponse deleteCommunity(long communityId) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d.index(ElasticSearch.INDEX_ARTICLE).id(String.valueOf(communityId)));
        return elasticsearchClient.delete(request);
    }

    /**
     * 이벤트 삭제
     *
     * @param eventId 이벤트 아이디
     * @return DeleteResponse
     * @throws IOException ElasticsearchClient 인덱스 삭제중 에러가 발생한 경우
     */
    public DeleteResponse deleteEvent(long eventId) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d.index(ElasticSearch.INDEX_EVENT).id(String.valueOf(eventId)));
        return elasticsearchClient.delete(request);
    }

    /**
     * 병원 삭제
     *
     * @param hospitalId 병원 아이디
     * @return DeleteResponse
     * @throws IOException ElasticsearchClient 인덱스 삭제중 에러가 발생한 경우
     */
    public DeleteResponse deleteHospital(long hospitalId) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d.index(ElasticSearch.INDEX_HOSPITAL).id(String.valueOf(hospitalId)));
        return elasticsearchClient.delete(request);
    }

    /**
     * 인덱스 삭제
     *
     * @param indexName 인덱스 이름
     * @return DeleteIndexResponse
     * @throws IOException ElasticsearchClient 인덱스 삭제중 에러가 발생한 경우
     */
    public DeleteIndexResponse deleteIndex(String indexName) throws IOException {
        return elasticsearchClient.indices().delete(d -> d.index(indexName));
    }

    /**
     * 검색 결과 총 개수
     *
     * @param searchResponse 검색 결과
     * @return 검색 결과 총 개수
     */
    private long total(SearchResponse searchResponse) {
        if (ObjectUtils.isEmpty(searchResponse.hits().total())) {
            return 0;
        }
        return searchResponse.hits().total().value();
    }

    /**
     * @param field 검색 필드
     * @param value 검색어
     * @return Query
     */
    private Query term(String field, String value) {
        return QueryBuilders.term(m -> m
            .field(field)
            .value(value));
    }

    /**
     * @param field 검색 필드
     * @param size 집계 결과 추출 개수
     * @return Aggregation
     */
    private Aggregation terms(String field, int size) {
        return new Aggregation.Builder().terms(new TermsAggregation.Builder()
            .field(field)
            .size(size).build()).build();
    }

    /**
     * @param object 검사 대상 객체
     * @return boolean
     */
    private boolean isExist(Object object) {
        return ObjectUtils.isNotEmpty(object);
    }

    /**
     * @param field 검색 필드
     * @param query 검색어
     * @return Query
     */
    private Query matchQuery(String field, String query) {
        return QueryBuilders.match(m -> m
            .field(field)
            .query(query));
    }

    /**
     * @param query 검색어
     * @param fields 검색 필드 목록
     * @return Query
     */
    private Query multiMatchQuery(String query, String... fields) {
        return QueryBuilders.multiMatch(m -> m
            .type(TextQueryType.BoolPrefix)
            .fields(Arrays.stream(fields).toList())
            .query(query));
    }

    /**
     * @param queries 검색 조건 목록
     * @return Query
     */
    private Query boolQuery(List<Query> queries) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        for (Query searchQuery : queries) {
            boolQueryBuilder.must(searchQuery);
        }
        return boolQueryBuilder.build()._toQuery();
    }
}



