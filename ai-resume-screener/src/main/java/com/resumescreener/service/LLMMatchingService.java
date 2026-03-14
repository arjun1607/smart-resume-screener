package com.resumescreener.service;

import com.resumescreener.model.MatchResult;
import com.resumescreener.model.Resume;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMMatchingService {
    private final ChatClient chatClient;
    public LLMMatchingService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public MatchResult matchResumeWithJob(Resume resume, String jobDescription){
        String prompt = buildPrompt(resume, jobDescription);

        System.out.println("Sending prompt to OpenAI...");

        String llmResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        System.out.println("Received Response: " + llmResponse);

        Double score = extractScore(llmResponse);
        String justification = extractJustification(llmResponse);

        return new MatchResult(resume, score, justification);
    }

    private String buildPrompt(Resume resume, String jobDescription) {
        return """
                You are an expert resume screening AI assistant. Analyze the following resume against the job description.
                
                RESUME DETAILS:
                Candidate Name: %s
                Skills: %s
                Experience: %s
                Education: %s
                
                JOB DESCRIPTION:
                %s
                
                INSTRUCTIONS:
                1. Compare the candidate's skills, experience, and education with the job requirements.
                2. Rate the fit on a scale of 1-10 where :
                    9-10: Exceptional fit - Highly recommended
                    7-8:  Strong fit - Recommended for interview
                    5-6:  Moderate fit - Consider with reservations
                    3-4:  Weak fit - Missing key requirements
                    1-2:  Poor fit - Not recommended
                3. Provide a detailed justification for the score.
                
                RESPONSE FORMAT:
                Score: [number between 1-10]
                Justification: [brief explanation covering skills match, experience relevance, education fit, strengths, and gaps in each point. Make sure to give proper spacing and line breaks]
                
                Now analyze and respond.
                """.formatted(
                resume.getCandidateName(),
                resume.getSkills(),
                resume.getExperience(),
                resume.getEducation(),
                jobDescription);
    }

    private Double extractScore(String response) {
        Pattern pattern = Pattern.compile("Score:\\s*(\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return 5.0;
    }

    private String extractJustification(String response) {
        Pattern pattern = Pattern.compile("Justification:\\s*(.+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return response;
    }
}































