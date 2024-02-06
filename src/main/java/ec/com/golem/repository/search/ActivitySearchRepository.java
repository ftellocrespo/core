package ec.com.golem.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import ec.com.golem.domain.Activity;
import ec.com.golem.repository.ActivityRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Activity} entity.
 */
public interface ActivitySearchRepository extends ElasticsearchRepository<Activity, Long>, ActivitySearchRepositoryInternal {}

interface ActivitySearchRepositoryInternal {
    Stream<Activity> search(String query);

    Stream<Activity> search(Query query);

    @Async
    void index(Activity entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ActivitySearchRepositoryInternalImpl implements ActivitySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ActivityRepository repository;

    ActivitySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ActivityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Activity> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Activity> search(Query query) {
        return elasticsearchTemplate.search(query, Activity.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Activity entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Activity.class);
    }
}
