package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 7/15/2016.
 */
public class Pegawai {
    private String namaPegawai,idPegawai, tipeRec, firstChar;
    private Boolean selected;

    public Pegawai(){

    }
    public Pegawai(String namaPegawai, String idPegawai, String tipeRec,String firstChar){
        this.namaPegawai=namaPegawai;
        this.idPegawai=idPegawai;
        this.tipeRec = tipeRec;
        this.firstChar = firstChar;
    }

    public void setNamaPegawai(String namaPegawai){
        this.namaPegawai = namaPegawai;
    }
    public String getNamaPegawai(){
        return namaPegawai;
    }

    public void setTipeRec(String tipeRec){
        this.tipeRec = tipeRec;
    }
    public String getTipeRec(){
        return tipeRec;
    }

    public void setFirstChar(String firstChar){
        this.firstChar = firstChar;
    }
    public String getFirstChar(){
        return firstChar;
    }

    public String getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(String idPegawai) {
        this.idPegawai = idPegawai;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
