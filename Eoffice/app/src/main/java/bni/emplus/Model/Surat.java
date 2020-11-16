package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 6/27/2016.
 */
public class Surat {
    private String Baris,BoxId,empdocid,doctype,doccode,prior,total,JudulSurat, DivisiSurat, NomorSurat, TanggalSurat, FlowSurat, StatusSurat, JamSurat, DispId, DocMode, isDisp, modeFirst, docReg, noReg, mode, crtime, favourite, tanggalAsliSurat, durasi, durasiDisp;
    private int IconSurat,IconFav,IconStatus;


    public Surat(){

    }

    public Surat(String Baris, String BoxId, String empdocid, String doctype, String doccode, String prior, String total, String JudulSurat, String DivisiSurat, String NomorSurat, String TanggalSurat, int IconSurat, String FlowSurat, String StatusSurat, String JamSurat, String isDisp, String modeFirst, String docReg, String noReg, String mode, String crtime, String favourite, int IconFav, String tanggalAsliSurat, String durasi, String durasiDisp, int IconStatus){
        this.Baris = Baris;
        this.BoxId=BoxId;
        this.empdocid=empdocid;
        this.noReg = noReg;
        this.doctype=doctype;
        this.doccode=doccode;
        this.prior=prior;
        this.total=total;
        this.JudulSurat = JudulSurat;
        this.DivisiSurat = DivisiSurat;
        this.NomorSurat = NomorSurat;
        this.TanggalSurat = TanggalSurat;
        this.JamSurat = JamSurat;
        this.IconSurat = IconSurat;
        this.FlowSurat = FlowSurat;
        this.StatusSurat = StatusSurat;
        this.isDisp = isDisp;
        this.modeFirst = modeFirst;
        this.docReg = docReg;
        this.mode = mode;
        this.crtime = crtime;
        this.favourite = favourite;
        this.IconFav = IconFav;
        this.tanggalAsliSurat = tanggalAsliSurat;
        this.durasi = durasi;
        this.durasiDisp = durasiDisp;
        this.IconStatus = IconStatus;
    }

    public void setFlowSurat(String FlowSurat){
        this.FlowSurat=FlowSurat;
    }
    public String getFlowSurat(){
        return FlowSurat;
    }

    public void setStatusSurat(String StatusSurat){
        this.StatusSurat=StatusSurat;
    }
    public String getStatusSurat(){
        return StatusSurat;
    }

    public void setJudulSurat(String JudulSurat){
        this.JudulSurat=JudulSurat;
    }
    public String getJudulSurat(){
        return JudulSurat;
    }
    public void setDivisiSurat(String DivisiSurat){
        this.DivisiSurat = DivisiSurat;
    }
    public String getDivisiSurat(){
        return DivisiSurat;
    }
    public void setNomorSurat(String NomorSurat){
        this.NomorSurat = NomorSurat;
    }
    public String getNomorSurat(){
        return NomorSurat;
    }
    public void setTanggalSurat(String TanggalSurat){
        this.TanggalSurat = TanggalSurat;
    }
    public String getTanggalSurat(){
        return TanggalSurat;
    }
    public void setJamSurat(String JamSurat){this.JamSurat=JamSurat;}
    public String getJamSurat(){return JamSurat;}
    public void setIconSurat(int IconSurat){
        this.IconSurat = IconSurat;
    }
    public int getIconSurat(){
        return IconSurat;
    }

    public String getBaris() {
        return Baris;
    }

    public void setBaris(String baris) {
        Baris = baris;
    }

    public String getBoxId() {
        return BoxId;
    }

    public void setBoxId(String boxId) {
        BoxId = boxId;
    }

    public String getEmpdocid() {
        return empdocid;
    }

    public void setEmpdocid(String empdocid) {
        this.empdocid = empdocid;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getDoccode() {
        return doccode;
    }

    public void setDoccode(String doccode) {
        this.doccode = doccode;
    }

    public String getPrior() {
        return prior;
    }

    public void setPrior(String prior) {
        this.prior = prior;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDispId() {
        return DispId;
    }

    public void setDispId(String dispId) {
        DispId = dispId;
    }

    public String getDocMode() {
        return DocMode;
    }

    public void setDocMode(String docMode) {
        DocMode = docMode;
    }

    public String getIsDisp() {
        return isDisp;
    }

    public void setIsDisp(String isDisp) {
        this.isDisp = isDisp;
    }

    public String getModeFirst() {
        return modeFirst;
    }

    public void setModeFirst(String modeFirst) {
        this.modeFirst = modeFirst;
    }

    public String getDocReg() {
        return docReg;
    }

    public void setDocReg(String docReg) {
        this.docReg = docReg;
    }

    public String getNoReg() {
        return noReg;
    }

    public void setNoReg(String noReg) {
        this.noReg = noReg;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCrtime() {
        return crtime;
    }

    public void setCrtime(String crtime) {
        this.crtime = crtime;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public int getIconFav() {
        return IconFav;
    }

    public void setIconFav(int IconFav) {
        this.IconFav = IconFav;
    }

    public String getTanggalAsliSurat() {
        return tanggalAsliSurat;
    }

    public void setTanggalAsliSurat(String tanggalAsliSurat) {
        this.tanggalAsliSurat = tanggalAsliSurat;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getDurasiDisp() {
        return durasiDisp;
    }

    public void setDurasiDisp(String durasiDisp) {
        this.durasiDisp = durasiDisp;
    }

    public int getIconStatus() {
        return IconStatus;
    }

    public void setIconStatus(int iconStatus) {
        IconStatus = iconStatus;
    }
}
