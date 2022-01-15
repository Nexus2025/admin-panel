package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;

public interface PlayerService {
    Integer getPlayersCount(String name, String title, Integer minExperience, Integer maxExperience, Integer minLevel,
                         Integer maxLevel, Race race, Profession profession, Boolean banned, Long after, Long before);

    List<Player> findAll(int pageNumber, int pageSize, String orderBy,
                         String name, String title, Integer minExperience, Integer maxExperience, Integer minLevel,
                         Integer maxLevel, Race race, Profession profession, Boolean banned, Long after, Long before);

    void savePlayer(Player player);

    Player getPlayerById(Long id);

    Player updatePlayer(Long id, Player player);

    void deletePlayer(Long id);
}
