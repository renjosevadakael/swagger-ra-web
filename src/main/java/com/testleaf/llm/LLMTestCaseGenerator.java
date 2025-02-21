package com.testleaf.llm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class LLMTestCaseGenerator {

    @Value("${llm.api.url}")
    private String llmApiUrl;

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.model}")
    private String modelName;

    /**
     * Generates test cases given API details and a list of test types.
     */
    public String generateTestCases(String userStoryTxt) {

        if (userStoryTxt == null || userStoryTxt.isEmpty()) {
            return "No valid User Story provided.";
        }
        
        // Updated prompt that instructs the LLM to output only the final Playwright TypeScript code.
        String prompt = "Instructions:\n"
        		+ "\n"
        		+ "- Convert Behavior-Driven Development (BDD) user story to TestCases while preserving the logic and functionality.\n"
        		+ "- Ensure that the converted TestCases follow best practices, including:\n"
        		+ "    -- Proper test case structure and organization.\n"
        		+ "    -- Clear and concise test case descriptions.\n"
        		+ "    -- Relevant test data and parameters.\n"
        		+ "    -- Expected results and assertions.\n"
        		+ "- Maintain proper test case naming conventions and follow a consistent format.\n"
        		+ "- Optimize test case structure, removing unnecessary steps or redundant calls.\n"
        		+ "- Ensure that test cases cover all scenarios, including positive, negative, and edge cases.\n"
        		+ "- DO NOT add any additional test cases other than those derived from the input BDD user story.\n"
        		+ "\n"
        		+ "Context:\n"
        		+ "\n"
        		+ "I am building an AI-based prompt to convert BDD user stories to TestCases automatically.\n"
        		+ "The converted TestCases must be accurate and comprehensive, as quality is crucial for my project's success.\n"
        		+ "\n"
        		+ "Example Input (BDD User Story):\n"
        		+ "\n"
        		+ "Feature: Login Feature\n"
        		+ "  As a registered user\n"
        		+ "  I want to log in to the application\n"
        		+ "  So that I can access my account\n"
        		+ "\n"
        		+ "Scenario: Successful Login\n"
        		+ "  Given I am on the login page\n"
        		+ "  When I enter valid username and password\n"
        		+ "  Then I am redirected to my dashboard\n"
        		+ "  And I see my username displayed\n"
        		+ "\n"
        		+ "Example Output (Test Cases):\n"
        		+ "\n"
        		+ "TestCase 1: Successful Login\n"
        		+ "\n"
        		+ "Description: Test login with valid username and password\n"
        		+ "\n"
        		+ "Preconditions:\n"
        		+ "  - User has a valid username and password\n"
        		+ "  - User is on the login page\n"
        		+ "\n"
        		+ "Steps:\n"
        		+ "  1. Enter valid username\n"
        		+ "  2. Enter valid password\n"
        		+ "  3. Click the \"Log In\" button\n"
        		+ "\n"
        		+ "Expected Result: User is redirected to their dashboard and sees their username displayed\n"
        		+ "\n"
        		+ "Persona:\n"
        		+ "\n"
        		+ "You are a Senior Test Automation Architect specializing in BDD and TestCases development. \n"
        		+ "Your responsibility is to ensure that the converted TestCases are accurate, comprehensive, and follow industry best practices.\n"
        		+ "\n"
        		+ "Output Format:\n"
        		+ "\n"
        		+ "- The output should be fully working TestCases.\n"
        		+ "- It should be structured as a test case document or within a test management tool if required.\n"
        		+ "- The test cases should be formatted properly and follow a consistent structure.\n"
        		+ "- DO NOT provide anything other than TestCases, such as explanations or key points.\n"
        		+ "\n"
        		+ "Use the above framework to generate the TestCases for the following BDD user story:\n"
        		+ userStoryTxt;

        try {
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", prompt);
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", "Convert above BDD to TestCases include positive, negative and edge cases.");
            messages.add(userMessage);

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", modelName);
            payload.put("messages", messages);
            payload.put("temperature", 0.1);
            payload.put("top_p", 0.1);
            payload.put("max_tokens", 10000);

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(payload);
            return callLLMApi(requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error building JSON payload: " + e.getMessage();
        }
    
    	
    }

    
    private String callLLMApi(String requestBody) {
        try (var httpClient = HttpClients.createDefault()) {
            var request = new HttpPost(llmApiUrl);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);
            request.setEntity(new StringEntity(requestBody));
            System.out.println(requestBody);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling LLM API: " + e.getMessage();
        }
    }


}
