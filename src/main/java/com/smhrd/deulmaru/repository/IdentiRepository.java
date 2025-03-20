package com.smhrd.deulmaru.repository;

import com.smhrd.deulmaru.entity.IdentiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentiRepository extends JpaRepository<IdentiEntity, Long> {
}
