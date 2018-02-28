package tikape.runko.dao;

import java.sql.*;
import tikape.runko.database.Database;
import tikape.runko.domain.Annos;


public class AnnosDao extends AbstractNamedObjectDao<Annos> {

    public AnnosDao(Database database, String tableName) {
        super(database, tableName);
    }

    @Override
    public Annos createFromRow(ResultSet resultSet) throws SQLException {
        return new Annos(resultSet.getInt("id"), resultSet.getString("nimi"));
    }
}
