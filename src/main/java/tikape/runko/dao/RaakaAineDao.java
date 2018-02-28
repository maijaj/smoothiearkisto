package tikape.runko.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.database.Database;
import tikape.runko.domain.Annos;
import tikape.runko.domain.RaakaAine;


public class RaakaAineDao extends AbstractNamedObjectDao<RaakaAine> {

    public RaakaAineDao(Database database, String tableName) {
        super(database, tableName);
    }

    @Override
    public RaakaAine createFromRow(ResultSet resultSet) throws SQLException {
        return new RaakaAine(resultSet.getInt("id"), resultSet.getString("nimi"));
    }
    
    public List<String> raakaaineetJotkaAnnoksessa() throws SQLException {
        String query = "SELECT nimi, id FROM RaakaAine\n" 
                + "        WHERE EXISTS (\n" 
                + "        SELECT raakaaine_id FROM AnnosRaakaAine\n"
                + "        WHERE RaakaAine.id = AnnosRaakaAine.raakaaine_id\n" 
                +           "    )";
        List<String> annoksissa = new ArrayList<>();
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                annoksissa.add(result.getString("nimi") + " - " + annoksetJossaRaakaAineOn(result.getInt("id")));
            }
        }

        return annoksissa;
    }
    private String annoksetJossaRaakaAineOn(Integer id) throws SQLException {
        String query = "SELECT *\n" +
                    "FROM Annos\n" +
                    "JOIN AnnosRaakaAine on AnnosRaakaAine.annos_id = Annos.id\n" +
                    "JOIN RaakaAine on RaakaAine.id = AnnosRaakaAine.raakaaine_id\n" +
                    "WHERE RaakaAine.id = ?";
        String annokset = "";

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                annokset += (result.getString("nimi") + " ");
            }
        }

        return annokset;
    }
    public List<RaakaAine> raakaaineetJotkaEiAnnoksessa() throws SQLException {
        String query = "SELECT id FROM RaakaAine\n" 
                + "        WHERE NOT EXISTS (\n" 
                + "        SELECT raakaaine_id FROM AnnosRaakaAine\n"
                + "        WHERE RaakaAine.id = AnnosRaakaAine.raakaaine_id\n" 
                +           "    )";
        List<RaakaAine> raakaaineet = new ArrayList<>();

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                raakaaineet.add(super.findOne(result.getInt("id")));
            }
        }

        return raakaaineet;
    }    
}
