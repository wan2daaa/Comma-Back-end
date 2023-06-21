package com.team.comma.spotify.archive.repository;

import com.team.comma.spotify.archive.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRepository extends JpaRepository<Archive , Long> {

}
