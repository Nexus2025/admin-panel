package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class MainController {

    @Autowired
    private PlayerService service;

    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(defaultValue = "0") Integer pageNumber,
                                       @RequestParam(defaultValue = "3") Integer pageSize,
                                       @RequestParam(defaultValue = "ID") String order,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) Race race,
                                       @RequestParam(required = false) Profession profession,
                                       @RequestParam(required = false) Long after,
                                       @RequestParam(required = false) Long before,
                                       @RequestParam(required = false) Boolean banned,
                                       @RequestParam(required = false) Integer minExperience,
                                       @RequestParam(required = false) Integer maxExperience,
                                       @RequestParam(required = false) Integer minLevel,
                                       @RequestParam(required = false) Integer maxLevel) {

        String orderBy = PlayerOrder.valueOf(order).getFieldName();

        List<Player> allPlayers = service.findAll(pageNumber, pageSize, orderBy,
                name, title, minExperience, maxExperience, minLevel,
                maxLevel, race, profession, banned, after, before);

        return allPlayers;
    }

    @GetMapping("/players/count")
    public Integer getPlayersCount(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) Race race,
                                @RequestParam(required = false) Profession profession,
                                @RequestParam(required = false) Long after,
                                @RequestParam(required = false) Long before,
                                @RequestParam(required = false) Boolean banned,
                                @RequestParam(required = false) Integer minExperience,
                                @RequestParam(required = false) Integer maxExperience,
                                @RequestParam(required = false) Integer minLevel,
                                @RequestParam(required = false) Integer maxLevel){

        return service.getPlayersCount(name, title, minExperience, maxExperience, minLevel,
                maxLevel, race, profession, banned, after, before);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player) {
        service.savePlayer(player);
        return player;
    }

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable Long id) {
        Player player = service.getPlayerById(id);
        return player;
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        player = service.updatePlayer(id, player);
        return player;
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        service.deletePlayer(id);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
