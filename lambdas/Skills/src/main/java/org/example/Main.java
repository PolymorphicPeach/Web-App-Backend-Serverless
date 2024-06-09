package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Project;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

// Don't use RequestHandler<Map<String, String>, String>:

// "For some reason Amazon can't deserialize json to a String.
// You would think String would be as general as input parameter
// as you can get but rightly or wrongly it's not compatible.
// To handle JSON you can either use a Map or a custom POJO."
// https://stackoverflow.com/questions/35545642/error-executing-hello-world-for-aws-lambda-in-java

// Alternatively, include the aws-lambda-java-events dependency... (this is a lot of configuration omg)
// https://stackoverflow.com/questions/59535245/execution-failed-due-to-configuration-error-malformed-lambda-proxy-response-in
public class Main implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {
    private final DynamoDbEnhancedClient dynamoDbClient = DynamoDbEnhancedClient.create();
    private final DynamoDbTable<Project> projectTable = dynamoDbClient
            .table(System.getenv("TABLE_NAME"), TableSchema.fromBean(Project.class));
    private final ObjectMapper objectMapper = new ObjectMapper();

    // "QueryConditionals work with key values—either the partition key alone or in combination with
    // the sort key—and correspond to the key conditional expressions of the DynamoDB service API."
    //https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/ddb-en-client-use-multirecord.html

    // Using partition key alone
    private final QueryConditional partitionEqual = QueryConditional.keyEqualTo(item -> item.partitionValue("Project"));

    //"... the QueryEnhancedRequest instance that the code passes to the DynamoDbTable.query() method.
    // This object combines the key condition and filter that the SDK uses to generate the request to
    // the DynamoDB service."
    //https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/ddb-en-client-use-multirecord.html
    private final QueryEnhancedRequest getAllProjects =
            QueryEnhancedRequest
                    .builder()
                    .queryConditional(partitionEqual)
                    .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> event, Context context) {
        // https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/enhanced/dynamodb/model/PageIterable.html
        PageIterable<Project> pagedResults = projectTable.query(getAllProjects);

        Map<String, Project> projectMap = new HashMap<>();
        pagedResults.items()
                .stream()
                .forEach(item -> projectMap.put(item.getName(), item));

        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(projectMap))
                    .withIsBase64Encoded(false);
        }
        catch (JsonProcessingException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(502)
                    .withBody("")
                    .withIsBase64Encoded(false);
        }
    }
}