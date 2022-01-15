package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Page<Player> findAll(Specification<Player> specification, Pageable paging);

    List<Player> findAll(Specification<Player> specification);

    Player getById(Long id);
}
