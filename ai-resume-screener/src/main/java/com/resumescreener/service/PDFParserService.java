package com.resumescreener.service;

import com.resumescreener.model.Resume;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PDFParserService {

    public Resume parseResume(MultipartFile file) throws IOException {
        Resume resume = new Resume();
        resume.setFileName(file.getOriginalFilename());

        String extractedText = extractTextFromFile(file);
        resume.setExtractedText(extractedText);
        resume.setCandidateName(extractName(extractedText));
        resume.setEmail(extractEmail(extractedText));
        resume.setPhone(extractPhone(extractedText));
        resume.setSkills(extractSkills(extractedText));
        resume.setExperience(extractExperience(extractedText));
        resume.setEducation(extractEducation(extractedText));

        return resume;
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        try {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (IOException e){
            throw new IOException("Error extracting text from PDF: " + e.getMessage(), e);
        }
    }

    private String extractName(String text) {
        // Extract first line as name (common in resumes)
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 2 && line.length() < 50) {
                // Check if it looks like a name (letters, spaces, some special chars)
                if (line.matches("^[A-Za-z\\s.]+$")) {
                    return line;
                }
            }
        }
        return "Unknown Candidate";
    }

    private String extractEmail(String text) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "Not provided";
    }

    private String extractPhone(String text) {
        // Match various phone formats
        Pattern pattern = Pattern.compile("\\+?\\d[\\d\\s\\-]{8,15}\\d");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "Not provided";
    }

    private String extractSkills(String text) {
        StringBuilder skills = new StringBuilder();
        String lowerText = text.toLowerCase();

        // Common skill keywords
        String[] skillKeywords = {
                "java", "python", "javascript", "react", "angular", "spring", "springboot",
                "nodejs", "sql", "mysql", "postgresql", "mongodb", "aws", "azure", "docker",
                "kubernetes", "git", "html", "css", "rest api", "microservices", "agile",
                "machine learning", "data structures", "algorithms", "c++", "c#", ".net",
                "redis", "rabbitmq", "kafka", "ai", "gcp", "system design"
        };

        for (String skill : skillKeywords) {
            if (lowerText.contains(skill.toLowerCase())) {
                if (!skills.isEmpty()) skills.append(", ");
                skills.append(skill);
            }
        }

        return !skills.isEmpty() ? skills.toString() : "Not clearly specified";
    }

    private String extractExperience(String text) {
        StringBuilder experience = new StringBuilder();
        String[] lines = text.split("\n");

        boolean inExperienceSection = false;
        int lineCount = 0;

        for (String line : lines) {
            String lowerLine = line.toLowerCase();

            if (lowerLine.contains("experience") || lowerLine.contains("work history")
                    || lowerLine.contains("employment")) {
                inExperienceSection = true;
                continue;
            }

            if (inExperienceSection) {
                if (lowerLine.contains("education") || lowerLine.contains("skills")
                        || lowerLine.contains("projects")) {
                    break;
                }

                // consider next 15 lines of experience section
                if (!line.trim().isEmpty() && lineCount < 15) {
                    experience.append(line.trim()).append(" ");
                    lineCount++;
                }
            }
        }

        return !experience.isEmpty() ? experience.toString().trim() : "Not clearly specified";
    }

    private String extractEducation(String text) {
        StringBuilder education = new StringBuilder();
        String[] lines = text.split("\n");

        boolean inEducationSection = false;
        int lineCount = 0;

        for (String line : lines) {
            String lowerLine = line.toLowerCase();

            if (lowerLine.contains("education") || lowerLine.contains("academic")
                    || lowerLine.contains("qualification")) {
                inEducationSection = true;
                continue;
            }

            if (inEducationSection) {
                if (lowerLine.contains("experience") || lowerLine.contains("skills")
                        || lowerLine.contains("projects")) {
                    break;
                }

                if (!line.trim().isEmpty() && lineCount < 8) {
                    education.append(line.trim()).append(" ");
                    lineCount++;
                }
            }
        }

        // Also check for degree keywords anywhere in text
        String[] degrees = {"bachelor", "master", "phd", "b.tech", "m.tech", "mba", "b.sc", "m.sc"};
        for (String degree : degrees) {
            if (text.toLowerCase().contains(degree) && education.isEmpty()) {
                education.append("Found: ").append(degree);
            }
        }

        return !education.isEmpty() ? education.toString().trim() : "Not clearly specified";
    }

}




















