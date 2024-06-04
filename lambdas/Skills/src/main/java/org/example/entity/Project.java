package org.example.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Set;

// https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/ddb-en-client-use-multirecord.html
@DynamoDbBean
public class Project {
    private String partition;
    private String sortkey;
    private String name;
    private String link;
    private Set<String> languages;
    private Set<String> technologies;
    private Set<String> techniques;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partition")
    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("sortkey")
    public String getSortkey() {
        return sortkey;
    }

    public void setSortkey(String sortkey) {
        this.sortkey = sortkey;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDbAttribute("link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @DynamoDbAttribute("languages")
    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    @DynamoDbAttribute("technologies")
    public Set<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<String> technologies) {
        this.technologies = technologies;
    }

    @DynamoDbAttribute("techniques")
    public Set<String> getTechniques() {
        return techniques;
    }

    public void setTechniques(Set<String> techniques) {
        this.techniques = techniques;
    }

    @Override
    public String toString() {
        return "Project{" +
                "partition='" + partition + '\'' +
                ", sortkey='" + sortkey + '\'' +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", languages=" + languages +
                ", technologies=" + technologies +
                ", techniques=" + techniques +
                '}';
    }

    public Project(String partition, String sortkey, String name, String link, Set<String> languages, Set<String> technologies, Set<String> techniques) {
        this.partition = partition;
        this.sortkey = sortkey;
        this.name = name;
        this.link = link;
        this.languages = languages;
        this.technologies = technologies;
        this.techniques = techniques;
    }

    public Project() {

    }
}
