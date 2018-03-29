package org.esdemo;

import org.esdemo.repository.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbstractTestRunner {
    @Autowired
    protected ElasticsearchTemplate elasticsearchTemplate;

    @Before
    public void setUp() {

    }

}
