package tikape.runko.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.database.Database;
import tikape.runko.domain.AnnosRaakaAine;
import tikape.runko.domain.RaakaAine;

public class AnnosRaakaAineDao implements Dao<AnnosRaakaAine, Integer> {

    private Database database;

    public AnnosRaakaAineDao(Database database) {
        this.database = database;
    }

    @Override
    public AnnosRaakaAine findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<AnnosRaakaAine> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AnnosRaakaAine saveOrUpdate(AnnosRaakaAine object) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO AnnosRaakaAine (annos_id, raakaaine_id, jarjestys, maara, ohje) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, object.getAnnosid());
            stmt.setInt(2, object.getRaakaaineid());
            stmt.setInt(3, object.getJarjestys());
            stmt.setInt(4, object.getMaara());
            stmt.setString(5, object.getOhje());
            stmt.executeUpdate();
        }

        return null;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosRaakaAine WHERE raakaaine_id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public List<String> tulostaRaakaaineetAnnoksessa(Integer annos_id) throws SQLException {
        String query = "SELECT RaakaAine.nimi, AnnosRaakaAine.maara, AnnosRaakaAine.ohje"
                + "         FROM RaakaAine, AnnosRaakaAine\n"
                + "              WHERE RaakaAine.id = AnnosRaakaAine.raakaaine_id "
                + "                  AND AnnosRaakaAine.annos_id = ?\n"
                + "                  ORDER BY AnnosRaakaAine.jarjestys ASC\n";

        List<String> raakaaineet = new ArrayList<>();

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, annos_id);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                if (!result.getString("ohje").equals("")) {
                    raakaaineet.add("" + result.getInt("maara") + " " + result.getString("nimi") + " - " + result.getString("ohje"));
                } else {
                    raakaaineet.add("" + result.getInt("maara") + " " + result.getString("nimi"));
                }
            }
        }
        if (raakaaineet.isEmpty()) {
            raakaaineet.add("- ei ainesosia -");
        }

        return raakaaineet;
    }
    public List<RaakaAine> raakaaineetAnnoksesssa(Integer annos_id) throws SQLException {
        String query = "SELECT nimi FROM RaakaAine\n" 
                + "        WHERE EXISTS (\n" 
                + "        SELECT raakaaine_id FROM AnnosRaakaAine\n"
                + "        WHERE RaakaAine.id = AnnosRaakaAine.raakaaine_id AND AnnosRaakaAine.annos_id = ?\n" 
                + "        )";
        List<RaakaAine> raakaaineet = new ArrayList<>();

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, annos_id);
            ResultSet result = stmt.executeQuery();
            RaakaAineDao raakaaineDao = new RaakaAineDao(database, "RaakaAine");
            while (result.next()) {
                raakaaineet.add(raakaaineDao.findByName(result.getString("nimi")));
            }
        }

        return raakaaineet;
    }
    public List<RaakaAine> raakaaineetJotkaEiAnnoksesssa(Integer annos_id) throws SQLException {
        String query = "SELECT nimi FROM RaakaAine\n" 
                + "        WHERE NOT EXISTS (\n" 
                + "        SELECT raakaaine_id FROM AnnosRaakaAine\n"
                + "        WHERE RaakaAine.id = AnnosRaakaAine.raakaaine_id AND AnnosRaakaAine.annos_id = ?\n" 
                +           "    )";
        List<RaakaAine> raakaaineet = new ArrayList<>();

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, annos_id);
            ResultSet result = stmt.executeQuery();
            RaakaAineDao raakaaineDao = new RaakaAineDao(database, "RaakaAine");
            while (result.next()) {
                raakaaineet.add(raakaaineDao.findByName(result.getString("nimi")));
            }
        }

        return raakaaineet;
    }
    public Integer moneskoRaakaAineLisataan(Integer annos_id) throws SQLException {
        String query = "SELECT COUNT(id) FROM RaakaAine\n" 
                + "        WHERE EXISTS (\n" 
                + "        SELECT raakaaine_id FROM AnnosRaakaAine\n"
                + "        WHERE RaakaAine.id = AnnosRaakaAine.raakaaine_id AND AnnosRaakaAine.annos_id = ?\n" 
                +           "    )";
        Integer montako = 0;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, annos_id);
            ResultSet result = stmt.executeQuery();
            RaakaAineDao raakaaineDao = new RaakaAineDao(database, "RaakaAine");
            while (result.next()) {
                montako = result.getInt("COUNT(id)");
            }
        }

        return montako;
    }
}
