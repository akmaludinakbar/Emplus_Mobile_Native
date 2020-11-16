package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 7/14/2016.
 */
public class modelHistori {

    private String id,unitPendisposisi,empPendisposisi,namaPendisposisi, listPenerima, content, isMonitor, isHardcopy,statId, crtgl, crjam, disptgl, dispjam, komenDuration, dispKomenDuration,Disp_ByAdmin;
    private int gambarHistori;

    public modelHistori(){

    }
    public modelHistori(int gambarHistori, String id, String unitPendisposisi, String empPendisposisi, String namaPendisposisi, String listPenerima, String content, String isMonitor, String isHardcopy, String statId, String crtgl, String crjam, String disptgl, String dispjam, String komenDuration, String dispKomenDuration, String Disp_ByAdmin){
        this.id = id;
        this.unitPendisposisi = unitPendisposisi;
        this.empPendisposisi = empPendisposisi;
        this.namaPendisposisi = namaPendisposisi;
        this.listPenerima = listPenerima;
        this.content = content;
        this.isMonitor = isMonitor;
        this.isHardcopy = isHardcopy;
        this.statId = statId;
        this.gambarHistori = gambarHistori;
        this.crjam = crjam;
        this.crtgl = crtgl;
        this.dispjam = dispjam;
        this.disptgl = disptgl;
        this.komenDuration = komenDuration;
        this.dispKomenDuration  = dispKomenDuration;
        this.Disp_ByAdmin = Disp_ByAdmin;
    }

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }

    public String getUnitPendisposisi() {
        return unitPendisposisi;
    }

    public void setUnitPendisposisi(String unitPendisposisi) {
        this.unitPendisposisi = unitPendisposisi;
    }

    public String getEmpPendisposisi() {
        return empPendisposisi;
    }

    public void setEmpPendisposisi(String empPendisposisi) {
        this.empPendisposisi = empPendisposisi;
    }

    public String getNamaPendisposisi() {
        return namaPendisposisi;
    }

    public void setNamaPendisposisi(String namaPendisposisi) {
        this.namaPendisposisi = namaPendisposisi;
    }

    public String getListPenerima() {
        return listPenerima;
    }

    public void setListPenerima(String listPenerima) {
        this.listPenerima = listPenerima;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(String isMonitor) {
        this.isMonitor = isMonitor;
    }

    public String getIsHardcopy() {
        return isHardcopy;
    }

    public void setIsHardcopy(String isHardcopy) {
        this.isHardcopy = isHardcopy;
    }

    public String getStatId() {
        return statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
    }

    public int getGambarHistori() {
        return gambarHistori;
    }

    public void setGambarHistori(int gambarHistori) {
        this.gambarHistori = gambarHistori;
    }

    public String getDispjam() {
        return dispjam;
    }

    public void setDispjam(String dispjam) {
        this.dispjam = dispjam;
    }

    public String getDisptgl() {
        return disptgl;
    }

    public void setDisptgl(String disptgl) {
        this.disptgl = disptgl;
    }

    public String getCrjam() {
        return crjam;
    }

    public void setCrjam(String crjam) {
        this.crjam = crjam;
    }

    public String getCrtgl() {
        return crtgl;
    }

    public void setCrtgl(String crtgl) {
        this.crtgl = crtgl;
    }

    public String getKomenDuration() {
        return komenDuration;
    }

    public void setKomenDuration(String komenDuration) {
        this.komenDuration = komenDuration;
    }

    public String getDispKomenDuration() {
        return dispKomenDuration;
    }

    public void setDispKomenDuration(String dispKomenDuration) {
        this.dispKomenDuration = dispKomenDuration;
    }

    public String getDisp_ByAdmin() {
        return Disp_ByAdmin;
    }

    public void setDisp_ByAdmin(String disp_ByAdmin) {
        Disp_ByAdmin = disp_ByAdmin;
    }
}
