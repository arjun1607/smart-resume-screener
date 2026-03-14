package com.resumescreener.repository;

import com.resumescreener.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByCandidateNameContainingIgnoreCase(String name);
}
