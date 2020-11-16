package bni.emplus.Model;

/**
 * Created by FubianLathanio on 12/12/2017.
 */
public class modelGlosarium {
    private String istilah, keterangan;

    public modelGlosarium(){

    }
    public modelGlosarium(String istilah, String keterangan){
        this.istilah = istilah;
        this.keterangan = keterangan;
    }

    public String getIstilah() {
        return istilah;
    }

    public void setIstilah(String istilah) {
        this.istilah = istilah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
