package tikape.runko.domain;

public class AnnosRaakaAine {
    private Integer id;
    private Integer annosid;
    private Integer raakaaineid; 
    private Integer jarjestys;
    private Integer maara; 
    private String ohje;

    public AnnosRaakaAine(Integer id, Integer annosid, Integer raakaaineid, Integer jarjestys, Integer maara, String ohje) {
        this.id = id;
        this.annosid = annosid;
        this.raakaaineid = raakaaineid;
        this.jarjestys = jarjestys;
        this.maara = maara;
        this.ohje = ohje;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnnosid() {
        return annosid;
    }

    public void setAnnosid(Integer annosid) {
        this.annosid = annosid;
    }

    public Integer getRaakaaineid() {
        return raakaaineid;
    }

    public void setRaakaaineid(Integer raakaaineid) {
        this.raakaaineid = raakaaineid;
    }

    public Integer getJarjestys() {
        return jarjestys;
    }

    public Integer getMaara() {
        return maara;
    }

    public String getOhje() {
        return ohje;
    }
    


}
