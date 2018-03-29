package org.esdemo.save;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.Person;
import org.esdemo.repository.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;

public class PersonSaveTest extends AbstractTestRunner {
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void update() throws Exception {
        personRepository.deleteAll();
        // tag :: update
        Person p = new Person("id", "name1", 10);
        personRepository.save(p);

        final UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.doc(XContentFactory.jsonBuilder().startObject()
                .field("name", "modifiedName").endObject());

        final UpdateQuery updateQuery = new UpdateQueryBuilder().withId(p.getId())
                .withClass(Person.class).withUpdateRequest(updateRequest).build();

        UpdateResponse res = elasticsearchTemplate.update(updateQuery);
        System.out.println(res.toString());

        personRepository.findAll().forEach(person -> System.out.println(person));
    }
}
