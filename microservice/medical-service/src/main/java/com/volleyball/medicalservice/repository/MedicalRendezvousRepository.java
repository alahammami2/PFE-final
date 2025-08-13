package com.volleyball.medicalservice.repository;

import com.volleyball.medicalservice.model.MedicalRendezvous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRendezvousRepository extends JpaRepository<MedicalRendezvous, Long> {

    List<MedicalRendezvous> findByPlayerIdOrderByRendezvousDatetimeDesc(Long playerId);

    List<MedicalRendezvous> findByStatusOrderByRendezvousDatetimeAsc(String status);

    @Query("SELECT mr FROM MedicalRendezvous mr WHERE mr.rendezvousDatetime BETWEEN :start AND :end ORDER BY mr.rendezvousDatetime ASC")
    List<MedicalRendezvous> findBetween(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT mr FROM MedicalRendezvous mr WHERE mr.rendezvousDatetime > CURRENT_TIMESTAMP ORDER BY mr.rendezvousDatetime ASC")
    List<MedicalRendezvous> findUpcoming();

    @Query("SELECT mr FROM MedicalRendezvous mr WHERE LOWER(mr.playerName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY mr.rendezvousDatetime DESC")
    List<MedicalRendezvous> searchByPlayerName(@Param("name") String name);

    List<MedicalRendezvous> findByKineNameOrderByRendezvousDatetimeAsc(String kineName);

    long countByStatus(String status);
}
