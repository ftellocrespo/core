package ec.com.golem.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import ec.com.golem.domain.Process;
import ec.com.golem.repository.ProcessRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Process} entity.
 */
public interface ProcessSearchRepository extends ElasticsearchRepository<Process, Long>, ProcessSearchRepositoryInternal {}

interface ProcessSearchRepositoryInternal {
    Stream<Process> search(String query);

    Stream<Process> search(Query query);

    @Async
    void index(Process entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProcessSearchRepositoryInternalImpl implements ProcessSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProcessRepository repository;

    ProcessSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProcessRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Process> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Process> search(Query query) {
        return elasticsearchTemplate.search(query, Process.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Process entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Process.class);
    }
}
