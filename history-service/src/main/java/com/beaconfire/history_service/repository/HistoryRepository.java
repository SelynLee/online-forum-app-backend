package com.beaconfire.history_service.repository;

import com.beaconfire.history_service.entity.History;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findByUserIdOrderByViewDateDesc(Integer userId);

    @Query("SELECT h FROM History h WHERE h.userId = :userId AND h.postId = :postId")
    Optional<History> findByUserIdAndPostId(
            @Param("userId") Integer userId,
            @Param("postId") String postId);
}