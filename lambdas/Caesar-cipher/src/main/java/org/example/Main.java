package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.CaesarCiphertext;

import java.util.HashMap;
import java.util.Map;
import static java.util.Map.entry;

public class Main implements RequestHandler<Map<String,Object>, APIGatewayProxyResponseEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> CORSHeaders = Map.ofEntries(
            entry("Content-Type", "application/json"),
            entry("Access-Control-Allow-Origin", "*"),
            entry("Access-Control-Allow-Methods", "GET, POST, OPTIONS"),
            entry("Access-Control-Allow-Headers", "Content-Type, Authorization")
    );

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> event, Context context) {
        String plaintext = (String) event.get("plaintext");
        Integer key = (Integer) event.get("key");
        CaesarCiphertext ciphertext = encrypt(plaintext, key);

        try{
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(CORSHeaders)
                    .withBody(objectMapper.writeValueAsString(ciphertext))
                    .withIsBase64Encoded(false);
        }
        catch(JsonProcessingException e){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(502)
                    .withHeaders(CORSHeaders)
                    .withBody("")
                    .withIsBase64Encoded(false);
        }
    }

    private CaesarCiphertext encrypt(String plaintext, int key){
        StringBuilder ciphertext = new StringBuilder();
        Map<String, Integer> letterFrequency = new HashMap<>();

        for(int i = 0; i < plaintext.length(); ++i){
            char currentChar = plaintext.charAt(i);

            if(isLatin(currentChar)){
                char lowercaseChar = Character.toLowerCase(currentChar);
                char convertedChar = (char) (key + (int) lowercaseChar % 127);

                letterFrequency.merge(String.valueOf(currentChar), 1, Integer::sum);
                ciphertext.append(convertedChar);
            }
            else{
                ciphertext.append(currentChar);
            }
        }
        return new CaesarCiphertext(ciphertext.toString(), letterFrequency);
    }

    private boolean isLatin(char character){
        return(character >= 'A' && character <= 'Z' || (character >= 'a' && character <= 'z'));
    }
}