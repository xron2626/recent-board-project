package com.example.demo.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteBoardRepository extends JpaRepository<DeleteBoard,Long> {
}
