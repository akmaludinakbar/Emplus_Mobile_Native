package bni.emplus.Model;

/**
 * Created by FubianLathanio on 25/07/2018.
 */

public class modelJenisDokumen {
    private String idJenisDokumen;
    private String namaJenisDokumen;

    public modelJenisDokumen(){

    }

    public modelJenisDokumen(String idJenisDokumen, String namaJenisDokumen)
    {
        this.idJenisDokumen = idJenisDokumen;
        this.namaJenisDokumen = namaJenisDokumen;
    }

    public String getNamaJenisDokumen() {
        return namaJenisDokumen;
    }

    public void setNamaJenisDokumen(String namaJenisDokumen) {
        this.namaJenisDokumen = namaJenisDokumen;
    }

    public String getIdJenisDokumen() {
        return idJenisDokumen;
    }

    public void setIdJenisDokumen(String idJenisDokumen) {
        this.idJenisDokumen = idJenisDokumen;
    }
}
