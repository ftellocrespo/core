package ec.com.golem.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import ec.com.golem.domain.OrganizationUser;
import ec.com.golem.repository.OrganizationUserRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link OrganizationUser} entity.
 */
public interface OrganizationUserSearchRepository
    extends ElasticsearchRepository<OrganizationUser, Long>, OrganizationUserSearchRepositoryInternal {}

interface OrganizationUserSearchRepositoryInternal {
    Stream<OrganizationUser> search(String query);

    Stream<OrganizationUser> search(Query query);

    @Async
    void index(OrganizationUser entity);

    @Async
    void deleteFromIndexById(Long id);
}

class OrganizationUserSearchRepositoryInternalImpl implements OrganizationUserSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OrganizationUserRepository repository;

    OrganizationUserSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, OrganizationUserRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<OrganizationUser> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<OrganizationUser> search(Query query) {
        return elasticsearchTemplate.search(query, OrganizationUser.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(OrganizationUser entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), OrganizationUser.class);
    }
}
