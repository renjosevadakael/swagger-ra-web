/**
 * 
 */
package com.testleaf.llm;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class TestCaseExtractor {

    /**
     * Extracts test cases from a JSON response.
     *
     * @param jsonResponse The JSON response containing the test cases.
     * @return A string containing the extracted test cases, or an error message if no test cases are found.
     */
    public  String extractTestCases(String jsonResponse) {
        try {
            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Extract the "content" field from the first choice
            JsonNode contentNode = rootNode
                .path("choices")
                .path(0)
                .path("message")
                .path("content");

            // Get the content as a string
            String content = contentNode.asText();

            // Find the start of the test cases section
            int startIndex = content.indexOf("### Test Cases for Feature:");
            if (startIndex == -1) {
                // If the specific marker is not found, look for a generic test cases section
                startIndex = content.indexOf("### Test Cases");
                if (startIndex == -1) {
                    return "No test cases found in the response.";
                }
            }

            // Extract the test cases section
            String testCases = content.substring(startIndex);

            // Remove any trailing JSON or unnecessary content
            int endIndex = testCases.indexOf("\"},\"logprobs\"");
            if (endIndex != -1) {
                testCases = testCases.substring(0, endIndex);
            }

            return testCases;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting test cases: " + e.getMessage();
        }
    }

    
}
