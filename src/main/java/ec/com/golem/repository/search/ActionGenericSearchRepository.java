package ec.com.golem.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import ec.com.golem.domain.ActionGeneric;
import ec.com.golem.repository.ActionGenericRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ActionGeneric} entity.
 */
public interface ActionGenericSearchRepository
    extends ElasticsearchRepository<ActionGeneric, Long>, ActionGenericSearchRepositoryInternal {}

interface ActionGenericSearchRepositoryInternal {
    Stream<ActionGeneric> search(String query);

    Stream<ActionGeneric> search(Query query);

    @Async
    void index(ActionGeneric entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ActionGenericSearchRepositoryInternalImpl implements ActionGenericSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ActionGenericRepository repository;

    ActionGenericSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ActionGenericRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ActionGeneric> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ActionGeneric> search(Query query) {
        return elasticsearchTemplate.search(query, ActionGeneric.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ActionGeneric entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ActionGeneric.class);
    }
}
