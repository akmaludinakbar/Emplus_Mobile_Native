package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class modelKomen {
    private String pegawai,isiKomen, tanggalKomen, actionKomen, nppPeg, rolePeg, rolestatPeg, jamKomen;
    private int gambarKomen;

    public modelKomen(){

    }

    public modelKomen(String pegawai, String isiKomen, String tanggalKomen, int gambarKomen, String actionKomen, String nppPeg, String rolePeg, String rolestatPeg, String jamKomen){
        this.pegawai = pegawai;
        this.isiKomen = isiKomen;
        this.tanggalKomen = tanggalKomen;
        this.gambarKomen = gambarKomen;
        this.actionKomen = actionKomen;
        this.nppPeg = nppPeg;
        this.rolePeg = rolePeg;
        this.rolestatPeg = rolestatPeg;
        this.jamKomen = jamKomen;
    }

    public void setPegawai(String pegawai){
        this.pegawai = pegawai;
    }

    public String getPegawai(){
        return pegawai;
    }

    public void setIsiKomen(String isiKomen){
        this.isiKomen = isiKomen;
    }

    public String getIsiKomen(){
        return isiKomen;
    }

    public void setTanggalKomen(String tanggalKomen){
        this.tanggalKomen = tanggalKomen;
    }

    public String getTanggalKomen(){
        return tanggalKomen;
    }

    public void setGambarKomen(int gambarKomen){
        this.gambarKomen = gambarKomen;
    }

    public int getGambarKomen(){
        return gambarKomen;
    }


    public String getActionKomen() {
        return actionKomen;
    }

    public void setActionKomen(String actionKomen) {
        this.actionKomen = actionKomen;
    }

    public String getNppPeg() {
        return nppPeg;
    }

    public void setNppPeg(String nppPeg) {
        this.nppPeg = nppPeg;
    }

    public String getRolePeg() {
        return rolePeg;
    }

    public void setRolePeg(String rolePeg) {
        this.rolePeg = rolePeg;
    }

    public String getRolestatPeg() {
        return rolestatPeg;
    }

    public void setRolestatPeg(String rolestatPeg) {
        this.rolestatPeg = rolestatPeg;
    }

    public String getJamKomen() {
        return jamKomen;
    }

    public void setJamKomen(String jamKomen) {
        this.jamKomen = jamKomen;
    }
}

