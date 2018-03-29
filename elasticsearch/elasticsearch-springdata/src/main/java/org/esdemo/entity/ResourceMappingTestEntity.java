package org.esdemo.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "resource-mapping", type = "resource-mapping")
@Setting(settingPath = "/settings/settings.json")
@Mapping(mappingPath = "/settings/mappings.json")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResourceMappingTestEntity {
    @Id
    private String id;

    private String name;

    private String salaryInCompany;

    private String textField;
}
