package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, created_at) VALUES (?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, urlCheck.getUrlId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            var createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(3, Timestamp.valueOf(createdAt));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> getEntities(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

                var urlCheck = new UrlCheck();
                urlCheck.setId(id);
                urlCheck.setStatusCode(statusCode);
                urlCheck.setCreatedAt(createdAt);
                result.add(urlCheck);
            }
            resultSet.close();
            return result;
        }
    }
}
