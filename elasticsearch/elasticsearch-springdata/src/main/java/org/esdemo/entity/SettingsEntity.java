package org.esdemo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author zacconding
 * @Date 2018-04-05
 * @GitHub : https://github.com/zacscoding
 */
@Document(indexName = "settings-test", type = "test")
@Setting(settingPath = "/settings/settings.json")
public class SettingsEntity {
    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SettingsEntity{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
