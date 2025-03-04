package com.redbox.domain.attach.repository;

import com.redbox.domain.attach.entity.AttachFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachFileRepository extends JpaRepository<AttachFile, Long> {

}
