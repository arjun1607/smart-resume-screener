package com.resumescreener.controller;

import com.resumescreener.model.MatchRequest;
import com.resumescreener.model.MatchResult;
import com.resumescreener.model.Resume;
import com.resumescreener.repository.ResumeRepository;
import com.resumescreener.service.LLMMatchingService;
import com.resumescreener.service.PDFParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final PDFParserService pdfParserService;
    private final LLMMatchingService lLMMatchingService;

    public ResumeController(ResumeRepository resumeRepository, PDFParserService pdfParserService, LLMMatchingService lLMMatchingService) {
        this.resumeRepository = resumeRepository;
        this.pdfParserService = pdfParserService;
        this.lLMMatchingService = lLMMatchingService;
    }

    @GetMapping("/test")
    public String test() {
        return "Backend is working";
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            String fileName = file.getOriginalFilename();
            if (Objects.isNull(fileName) || !fileName.toLowerCase().endsWith(".pdf")) {
                response.put("success", false);
                response.put("error", "Only PDF files are supported");
                return ResponseEntity.badRequest().body(response);
            }

            Resume resume = pdfParserService.parseResume(file);
            resume = resumeRepository.save(resume);

            response.put("success", true);
            response.put("message", "Resume uploaded successfully");
            response.put("resumeId", resume.getId());
            response.put("candidateName", resume.getCandidateName());
            response.put("data", resume);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("error", "Error processing resume: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/match")
    public ResponseEntity<?> matchResumes(@RequestBody MatchRequest request){
        Map<String, Object> response = new HashMap<>();

        // validate input
        if(Objects.isNull(request.getJobDescription()) || request.getJobDescription().trim().isEmpty()){
            response.put("success", false);
            response.put("error", "Job description is required");
            return ResponseEntity.badRequest().body(response);
        }

        List<Resume> resumes = resumeRepository.findAll();
        if (resumes.isEmpty()) {
            response.put("success", false);
            response.put("error", "No resumes found. Please upload resumes first.");
            return ResponseEntity.badRequest().body(response);
        }

        double threshold = Objects.nonNull(request.getThreshold()) ? request.getThreshold() : 6.0;
        List<MatchResult> shortlisted = resumes.stream()
                .map(resume -> lLMMatchingService.matchResumeWithJob(resume, request.getJobDescription()))
                .filter(matchResult -> matchResult.getMatchScore() >= threshold)
                .sorted(Comparator.comparing(MatchResult::getMatchScore).reversed())
                .toList();

        response.put("success", true);
        response.put("totalCandidates", resumes.size());
        response.put("shortlistedCount", shortlisted.size());
        response.put("results", shortlisted);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumes")
    public ResponseEntity<?> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return ResponseEntity.ok(resumes);
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        resumeRepository.deleteById(id);
        response.put("success", true);
        response.put("message", "Resume deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/resumes")
    public ResponseEntity<?> deleteAllResumes() {
        Map<String, Object> response = new HashMap<>();
        resumeRepository.deleteAll();
        response.put("success", true);
        response.put("message", "All resumes deleted successfully");
        return ResponseEntity.ok(response);
    }
}




























































