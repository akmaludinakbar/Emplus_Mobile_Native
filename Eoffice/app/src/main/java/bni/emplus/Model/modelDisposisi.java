package bni.emplus.Model;

import java.util.ArrayList;

/**
 * Created by ZulfahPermataIlliyin on 1/8/2017.
 */
public class modelDisposisi {
    private String idpendisposisi,dispositiontime, isMonitor, metode, isidisposisi, empid, roleid, unitid, statid, boxid, docid, docregid,
            listpenerima, listfile="", listnamapenerima;

    private ArrayList<attachment> attachmentList;

    public modelDisposisi(){

    }
    public modelDisposisi(String idpendisposisi, String dispositiontime, String isMonitor, String metode, String isidisposisi, String empid,
                          String roleid, String unitid, String statid, String boxid, String docid, String docregid, String listpenerima,
                          String listfile, String listnamapenerima, ArrayList<attachment> attachmentList){
        this.idpendisposisi = idpendisposisi;
        this.dispositiontime = dispositiontime;
        this.isMonitor = isMonitor;
        this.metode = metode;
        this.isidisposisi = isidisposisi;
        this.empid = empid;
        this.roleid = roleid;
        this.unitid = unitid;
        this.statid = statid;
        this.boxid = boxid;
        this.docid = docid;
        this.docregid = docregid;
        this.listpenerima = listpenerima;
        this.listfile = listfile;
        this.listnamapenerima = listnamapenerima;
        this.attachmentList = attachmentList;
    }

    public String getIdpendisposisi() {
        return idpendisposisi;
    }

    public void setIdpendisposisi(String idpendisposisi) {
        this.idpendisposisi = idpendisposisi;
    }

    public String getDispositiontime() {
        return dispositiontime;
    }

    public void setDispositiontime(String dispositiontime) {
        this.dispositiontime = dispositiontime;
    }

    public String getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(String isMonitor) {
        this.isMonitor = isMonitor;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public String getIsidisposisi() {
        return isidisposisi;
    }

    public void setIsidisposisi(String isidisposisi) {
        this.isidisposisi = isidisposisi;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getStatid() {
        return statid;
    }

    public void setStatid(String statid) {
        this.statid = statid;
    }

    public String getBoxid() {
        return boxid;
    }

    public void setBoxid(String boxid) {
        this.boxid = boxid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getDocregid() {
        return docregid;
    }

    public void setDocregid(String docregid) {
        this.docregid = docregid;
    }

    public String getListpenerima() {
        return listpenerima;
    }

    public void setListpenerima(String listpenerima) {
        this.listpenerima = listpenerima;
    }

    public String getListfile() {
        return listfile;
    }

    public void setListfile(String listfile) {

        this.listfile += listfile;
    }

    public String getListnamapenerima() {
        return listnamapenerima;
    }

    public void setListnamapenerima(String listnamapenerima) {
        this.listnamapenerima = listnamapenerima;
    }

    public ArrayList<attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(ArrayList<attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }
}
