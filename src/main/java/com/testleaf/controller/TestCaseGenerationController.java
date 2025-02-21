package com.testleaf.controller;

import com.testleaf.llm.LLMTestCaseGenerator;
import com.testleaf.llm.TestCaseExtractor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generate")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TestCaseGenerationController {

    private final LLMTestCaseGenerator llmTestGenerator;
    private final TestCaseExtractor testCaseExtractor;

    /**
     * Generates manual test cases from the provided user story.
     *
     * Example usage:
     *  POST /api/generate/manualTestcase
     *  Body (raw JSON):
     *  {
     *    "userStory": "Feature: Login to Salesforce..."
     *  }
     */
    @PostMapping("/manualTestcase")
    public ResponseEntity<String> generateManualTestcase(@RequestBody ManualTestcaseRequest request) {
        try {
            if (request.getUserStory() == null || request.getUserStory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User story cannot be empty.");
            }

            String llmResponse = llmTestGenerator.generateTestCases(request.getUserStory());
            System.out.println(llmResponse);
            String testCases = testCaseExtractor.extractTestCases(llmResponse);
            System.out.println(testCases);
           
            return ResponseEntity.ok(testCases);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error generating manual test case: " + e.getMessage());
        }
    }

    /**
     * Inner class to represent the request body for manual test case generation.
     */
    public static class ManualTestcaseRequest {
        private String userStory;

        public String getUserStory() {
            return userStory;
        }

        public void setUserStory(String userStory) {
            this.userStory = userStory;
        }
    }
}