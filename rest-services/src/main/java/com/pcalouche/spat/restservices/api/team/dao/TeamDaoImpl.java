package com.pcalouche.spat.restservices.api.team.dao;

import com.pcalouche.spat.restservices.api.AbstractSpatDaoImpl;
import com.pcalouche.spat.restservices.api.entity.Team;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TeamDaoImpl extends AbstractSpatDaoImpl implements TeamDao {

    public TeamDaoImpl(@Qualifier("dataSource") DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public List<Team> getTeams() {
        return getJdbcTemplate().query(TeamQueries.GET_TEAMS, new BeanPropertyRowMapper<>(Team.class));
    }

    @Override
    public Team saveTeam(Team team) {
        String sql;
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("name", team.getName());

        if (team.getId() == null) {
            logger.debug("in create case");
            sql = TeamQueries.INSERT_TEAM;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getNamedParameterJdbcTemplate().update(sql, mapSqlParameterSource, keyHolder, new String[]{"id"});
            mapSqlParameterSource.addValue("id", keyHolder.getKey().longValue());
        } else {
            logger.debug("in update case");
            sql = TeamQueries.UPDATE_TEAM;
            // Query too see if it exists in the database first
            mapSqlParameterSource.addValue("id", team.getId());
            getNamedParameterJdbcTemplate().queryForObject(TeamQueries.GET_BY_ID, mapSqlParameterSource, new BeanPropertyRowMapper<>(Team.class));
            getNamedParameterJdbcTemplate().update(sql, mapSqlParameterSource);
        }
        return getNamedParameterJdbcTemplate().queryForObject(TeamQueries.GET_BY_ID, mapSqlParameterSource, new BeanPropertyRowMapper<>(Team.class));
    }

    @Override
    public Boolean deleteTeam(Long id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        int numRowsAffected = getNamedParameterJdbcTemplate().update(TeamQueries.DELETE_TEAM, mapSqlParameterSource);
        return numRowsAffected > 0;
    }
}
