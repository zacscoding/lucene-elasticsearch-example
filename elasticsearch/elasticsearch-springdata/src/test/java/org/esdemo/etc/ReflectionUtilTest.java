package org.esdemo.etc;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.esdemo.entity.NullTestEntity;
import org.esdemo.util.ReflectionUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author zacconding
 * @Date 2018-04-07
 * @GitHub : https://github.com/zacscoding
 */
public class ReflectionUtilTest {

    public static void main(String[] args) {
        // exist @Id - String
        ReflectionUtilTestEntity1 e1 = ReflectionUtilTestEntity1.builder().id("exist").name("name").build();
        assertThat(ReflectionUtil.getSpringDataId(e1), is("exist"));
        e1.setId(null);
        assertNull(ReflectionUtil.getSpringDataId(e1));

        // exist @Id - Integer
        ReflectionUtilTestEntity2 e2 = ReflectionUtilTestEntity2.builder().id(10).name("name").build();
        assertNull(ReflectionUtil.getSpringDataId(e2));

        // not exist id
        ReflectionUtilTestEntity3 e3 = ReflectionUtilTestEntity3.builder().id("exist").name("name").build();
        assertNull(ReflectionUtil.getSpringDataId(e3));
    }
}

// exist @Id - String
@Getter
@Setter
@Builder
class ReflectionUtilTestEntity1 {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String name;
}

// exist @Id - Integer
@Getter
@Setter
@Builder
class ReflectionUtilTestEntity2 {

    @Id
    private Integer id;

    @Field(type = FieldType.keyword)
    private String name;
}

// not exist @id
@Getter
@Setter
@Builder
class ReflectionUtilTestEntity3 {

    private String id;
    private String name;
}






