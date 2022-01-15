package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.PlayerIncorrectDataException;
import com.game.exception.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository repository;

    @Override
    public void deletePlayer(Long id) {
        if (id.longValue() <= 0) {
            throw new PlayerIncorrectDataException("ID is incorrect");
        }

        Player player = repository.getById(id);

        if (player == null) {
            throw new PlayerNotFoundException("Player is not found");
        }

        repository.delete(player);
    }

    @Override
    public Player updatePlayer(Long id, Player playerWithUpdateFields) {

        if (id.longValue() <= 0) {
            throw new PlayerIncorrectDataException("ID is incorrect");
        }

        Player playerFromDatabase = repository.getById(id);

        if (playerFromDatabase == null) {
            throw new PlayerNotFoundException("Player is not found");
        }

        if (playerWithUpdateFields.getName() != null) {
            playerFromDatabase.setName(playerWithUpdateFields.getName());
        }

        if (playerWithUpdateFields.getTitle() != null) {
            playerFromDatabase.setTitle(playerWithUpdateFields.getTitle());
        }

        if (playerWithUpdateFields.getRace() != null) {
            playerFromDatabase.setRace(playerWithUpdateFields.getRace());
        }

        if (playerWithUpdateFields.getBirthday() != null) {
            playerFromDatabase.setBirthday(playerWithUpdateFields.getBirthday());
        }

        if (playerWithUpdateFields.getProfession() != null) {
            playerFromDatabase.setProfession(playerWithUpdateFields.getProfession());
        }

        if (playerWithUpdateFields.getBanned() != null) {
            playerFromDatabase.setBanned(playerWithUpdateFields.getBanned());
        }

        if (playerWithUpdateFields.getExperience() != null) {
            playerFromDatabase.setExperience(playerWithUpdateFields.getExperience());
        }

        savePlayer(playerFromDatabase);
        return playerFromDatabase;
    }

    @Override
    public Player getPlayerById(Long id) {

        if (id.longValue() <= 0) {
            throw new PlayerIncorrectDataException("ID is incorrect");
        }

        Player player = repository.getById(id);

        if (player == null) {
            throw new PlayerNotFoundException("Player with this ID is not found");
        }

        return player;
    }

    @Override
    public void savePlayer(Player player) {
        validatePlayer(player);
        updateLvlAndUntilNextLvl(player);
        repository.save(player);
    }

    @Override
    @Transactional
    public Integer getPlayersCount(String name, String title, Integer minExperience, Integer maxExperience,
                                Integer minLevel, Integer maxLevel, Race race, Profession profession, Boolean banned,
                                Long after, Long before) {

        Specification<Player> specification = getSpecification(name, title, minExperience,
                maxExperience, minLevel, maxLevel, race, profession, banned, after, before);

        return repository.findAll(specification).size();
    }

    @Override
    @Transactional
    public List<Player> findAll(int pageNumber, int pageSize, String orderBy,
                                String name, String title, Integer minExperience, Integer maxExperience,
                                Integer minLevel, Integer maxLevel, Race race, Profession profession,
                                Boolean banned, Long after, Long before) {

        Specification<Player> specification = getSpecification(name, title, minExperience,
                maxExperience, minLevel, maxLevel, race, profession, banned, after, before);

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, orderBy));
        Page<Player> pagedResult = repository.findAll(specification, paging);

        return pagedResult.toList();
    }

    private Specification<Player> getSpecification(String name, String title, Integer minExperience,
                                                   Integer maxExperience, Integer minLevel, Integer maxLevel,
                                                   Race race, Profession profession, Boolean banned, Long after,
                                                   Long before) {

        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + name + "%")));
                }
                if (title != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("title"), "%" + title + "%")));
                }
                if (minExperience != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("experience"), minExperience - 1)));
                }
                if (maxExperience != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("experience"), maxExperience + 1)));
                }
                if (minLevel != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("level"), minLevel - 1)));
                }
                if (maxLevel != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("level"), maxLevel + 1)));
                }
                if (race != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("race"), race)));
                }
                if (profession != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("profession"), profession)));
                }
                if (banned != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("banned"), banned)));
                }
                if (after != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder
                            .greaterThanOrEqualTo(root.<Date>get("birthday"), new Date(after))));
                }
                if (before != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder
                            .lessThanOrEqualTo(root.<Date>get("birthday"), new Date(before))));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    private void validatePlayer(Player player) {
        boolean isIncorrectData = false;

        if (player.getName() == null) {
            isIncorrectData = true;
        } else if (player.getName().equals("")) {
            isIncorrectData = true;
        } else if (player.getName().length() > 12) {
            isIncorrectData = true;
        }

        if (player.getTitle() == null) {
            isIncorrectData = true;
        } else if (player.getTitle().equals("")) {
            isIncorrectData = true;
        } else if (player.getTitle().length() > 30) {
            isIncorrectData = true;
        }

        if (player.getProfession() == null) {
            isIncorrectData = true;
        }

        if (player.getRace() == null) {
            isIncorrectData = true;
        }

        if (player.getExperience() == null) {
            isIncorrectData = true;
        } else if (player.getExperience() < 0 || player.getExperience() > 10_000_000) {
            isIncorrectData = true;
        }

        if (player.getBirthday() == null) {
            isIncorrectData = true;
        } else if (player.getBirthday().before(new Date(946486800000L)) || player.getBirthday().after(new Date(32535104400000L))) {
            isIncorrectData = true;
        }

        if (isIncorrectData) {
            throw new PlayerIncorrectDataException("Incorrect Data");
        }

        if (player.getBanned() == null) {
            player.setBanned(false);
        }
    }

    private void updateLvlAndUntilNextLvl(Player player) {
        int level = (int) (Math.sqrt((player.getExperience() * 200) + 2500) - 50) / 100;
        player.setLevel(level);

        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setUntilNextLevel(untilNextLevel);
    }
}
