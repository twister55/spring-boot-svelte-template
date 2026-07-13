package dev.template.app.repository;

import dev.template.app.model.Todo;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class TodoRepository {

    private static final String COLUMNS = "id, title, completed, created_at";
    private static final RowMapper<Todo> MAPPER = (ResultSet rs, int _rowNum) -> new Todo(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getBoolean("completed"),
            rs.getObject("created_at", OffsetDateTime.class)
    );

    private final JdbcClient jdbcClient;

    public TodoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Todo> findAll() {
        return jdbcClient
                .sql("SELECT " + COLUMNS + " FROM todo ORDER BY created_at")
                .query(MAPPER)
                .list();
    }

    public Optional<Todo> findById(long id) {
        return jdbcClient
                .sql("SELECT " + COLUMNS + " FROM todo WHERE id = :id")
                .param("id", id)
                .query(MAPPER)
                .optional();
    }

    public Todo insert(String title, boolean completed) {
        return jdbcClient
                .sql("INSERT INTO todo (title, completed) VALUES (:title, :completed) RETURNING " + COLUMNS)
                .param("title", title)
                .param("completed", completed)
                .query(MAPPER)
                .single();
    }

    public Optional<Todo> update(long id, String title, boolean completed) {
        return jdbcClient
                .sql("UPDATE todo SET title = :title, completed = :completed WHERE id = :id RETURNING " + COLUMNS)
                .param("title", title)
                .param("completed", completed)
                .param("id", id)
                .query(MAPPER)
                .optional();
    }

    /**
     * @return whether a row with this id existed and was deleted
     */
    public boolean delete(long id) {
        int rows = jdbcClient.sql("DELETE FROM todo WHERE id = :id").param("id", id).update();
        return rows > 0;
    }
}
