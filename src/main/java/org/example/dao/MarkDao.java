package org.example.dao;

import org.example.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkDao extends JpaRepository<Mark, Long> {
}
