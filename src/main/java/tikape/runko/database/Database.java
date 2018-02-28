package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }

    public void init() {
        List<String> lauseet = sqliteLauseet();

        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            System.out.println("Error >> " + t.getMessage());
        }
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        lista.add("CREATE TABLE Annos (\n" +
                "    id   INTEGER PRIMARY KEY,\n" +
                "    nimi VARCHAR (90) \n" +
                ");");
        lista.add("CREATE TABLE RaakaAine (\n" +
                "    id   INTEGER PRIMARY KEY,\n" +
                "    nimi VARCHAR (90) \n" +
                ");");
        lista.add("CREATE TABLE AnnosRaakaAine (\n" +
                "    id           INTEGER      PRIMARY KEY,\n" +
                "    annos_id     INTEGER      REFERENCES Annos (id),\n" +
                "    raakaaine_id INTEGER      REFERENCES RaakaAine (id),\n" +
                "    jarjestys    INTEGER,\n" +
                "    maara        INTEGER,\n" +
                "    ohje         VARCHAR (90) \n" +
                ");");
        lista.add("INSERT INTO Annos (nimi) VALUES ('Persikkasmoothie');");
        lista.add("INSERT INTO Annos (nimi) VALUES ('Mansikkasmoothie');");
        lista.add("INSERT INTO RaakaAine (nimi) VALUES ('Persikka');");
        lista.add("INSERT INTO RaakaAine (nimi) VALUES ('Mansikka');");
        lista.add("INSERT INTO RaakaAine (nimi) VALUES ('Banaani');");
        lista.add("INSERT INTO RaakaAine (nimi) VALUES ('Mustikka');");
        lista.add("INSERT INTO AnnosRaakaAine (annos_id, raakaaine_id, jarjestys, maara, ohje) "
                + "VALUES (1, 1, 1, 2, 'Lisää joukkoon');");
        lista.add("INSERT INTO AnnosRaakaAine (annos_id, raakaaine_id, jarjestys, maara, ohje) "
                + "VALUES (1, 3, 2, 1, 'Sekoita huolellisesti');");
        lista.add("INSERT INTO AnnosRaakaAine (annos_id, raakaaine_id, jarjestys, maara, ohje) "
                + "VALUES (2, 2, 1, 1, 'Lisää joukkoon');");
        lista.add("INSERT INTO AnnosRaakaAine (annos_id, raakaaine_id, jarjestys, maara, ohje) "
                + "VALUES (2, 3, 2, 2, 'Lisää joukkoon ja sekoita');");

        return lista;
    }
}
