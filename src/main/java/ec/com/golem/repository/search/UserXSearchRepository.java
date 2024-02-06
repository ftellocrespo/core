package ec.com.golem.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import ec.com.golem.domain.UserX;
import ec.com.golem.repository.UserXRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link UserX} entity.
 */
public interface UserXSearchRepository extends ElasticsearchRepository<UserX, Long>, UserXSearchRepositoryInternal {}

interface UserXSearchRepositoryInternal {
    Stream<UserX> search(String query);

    Stream<UserX> search(Query query);

    @Async
    void index(UserX entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UserXSearchRepositoryInternalImpl implements UserXSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserXRepository repository;

    UserXSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserXRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<UserX> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<UserX> search(Query query) {
        return elasticsearchTemplate.search(query, UserX.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(UserX entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserX.class);
    }
}
