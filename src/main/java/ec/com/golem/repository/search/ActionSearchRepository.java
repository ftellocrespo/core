package ec.com.golem.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import ec.com.golem.domain.Action;
import ec.com.golem.repository.ActionRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Action} entity.
 */
public interface ActionSearchRepository extends ElasticsearchRepository<Action, Long>, ActionSearchRepositoryInternal {}

interface ActionSearchRepositoryInternal {
    Stream<Action> search(String query);

    Stream<Action> search(Query query);

    @Async
    void index(Action entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ActionSearchRepositoryInternalImpl implements ActionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ActionRepository repository;

    ActionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ActionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Action> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Action> search(Query query) {
        return elasticsearchTemplate.search(query, Action.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Action entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Action.class);
    }
}
