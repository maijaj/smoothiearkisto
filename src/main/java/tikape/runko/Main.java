package tikape.runko;

import java.util.HashMap;
import spark.*;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.dao.AnnosDao;
import tikape.runko.dao.AnnosRaakaAineDao;
import tikape.runko.dao.RaakaAineDao;
import tikape.runko.domain.Annos;
import tikape.runko.domain.AnnosRaakaAine;
import tikape.runko.domain.RaakaAine;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        Database database = new Database("jdbc:sqlite:reseptit.db");
        database.init();
        AnnosDao annosDao = new AnnosDao(database, "Annos");
        RaakaAineDao raakaaineDao = new RaakaAineDao(database, "RaakaAine");
        AnnosRaakaAineDao annosRaakaAineDao = new AnnosRaakaAineDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        get("/poistaAnnos/:id", (req, res) -> {
            annosDao.delete(Integer.parseInt(req.params(":id")));
            res.redirect("/");
            return "";
        });
        get("/annos/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer annosId = Integer.parseInt(req.params(":id"));
            map.put("annos", annosDao.findOne(annosId));
            map.put("annosraakaaineettext", annosRaakaAineDao.tulostaRaakaaineetAnnoksessa(annosId));
            map.put("annosraakaaineetlista", annosRaakaAineDao.raakaaineetAnnoksesssa(annosId));
            map.put("eiannoksessa", annosRaakaAineDao.raakaaineetJotkaEiAnnoksesssa(annosId));
            return new ModelAndView(map, "annos");
        }, new ThymeleafTemplateEngine());
        post("/annos/:id", (req, res) -> {
            if (req.queryParams("raakaaine_id") == null) {
                res.redirect("/annos/" + (req.queryParams("annos_id")));
            }
            Integer annos_id = Integer.parseInt(req.queryParams("annos_id"));
            Integer montako;
            if (req.queryParams("maara").equals("")) {
                montako = 1;
            } else {
                montako = Integer.parseInt(req.queryParams("maara"));
            }
            AnnosRaakaAine ara = new AnnosRaakaAine(null, annos_id,
                    Integer.parseInt(req.queryParams("raakaaine_id")),
                    annosRaakaAineDao.moneskoRaakaAineLisataan(annos_id),
                    montako, req.queryParams("ohje"));
            annosRaakaAineDao.saveOrUpdate(ara);
            res.redirect("/annos/" + (req.queryParams("annos_id")));
            return "";
        });
        get("/poistaAnnosRaakaAine/:id", (req, res) -> {
            annosRaakaAineDao.delete(Integer.parseInt(req.params(":id")));
            res.redirect("/");
            return "";
        });
        get("/lisaaAnnos", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            return new ModelAndView(map, "lisaaAnnos");
        }, new ThymeleafTemplateEngine());
        post("/lisaaAnnos", (req, res) -> {
            String nimi = req.queryParams("nimi");
            Annos annos = new Annos(null, nimi);
            Annos tallennettu = annosDao.saveOrUpdate(annos);
            if (tallennettu == null) {
                res.redirect("/lisaaAnnos");
            }
            res.redirect("/annos/" + (annosDao.findByName(nimi).getId()));
            return "";
        });
        get("/raakaAineet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raakaAineet", raakaaineDao.findAll());
            map.put("raakaaineetJotkaAnnoksessa", raakaaineDao.raakaaineetJotkaAnnoksessa());
            map.put("raakaaineetJotkaEiAnnoksessa", raakaaineDao.raakaaineetJotkaEiAnnoksessa());
            return new ModelAndView(map, "raakaAineet");
        }, new ThymeleafTemplateEngine());
        post("/raakaAineet", (req, res) -> {
            String nimi = req.queryParams("nimi");
            raakaaineDao.saveOrUpdate(new RaakaAine(null, nimi));
            res.redirect("/raakaAineet");
            return "";
        });
        get("/poistaRaakaAine/:id", (req, res) -> {
            raakaaineDao.delete(Integer.parseInt(req.params(":id")));
            res.redirect("/raakaAineet");
            return "";
        });
    }
}
