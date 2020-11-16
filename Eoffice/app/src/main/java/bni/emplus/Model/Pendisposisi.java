package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 7/15/2016.
 */
public class Pendisposisi {

    private String erid, empid, unitid, npp, empname, unitname, statid, roleid, ddlid, ddldisp;

    public Pendisposisi(){

    }
    public Pendisposisi(String erid, String empid, String unitid, String npp, String empname, String unitname, String statid, String roleid, String ddldisp, String ddlid){
        this.erid = erid;
        this.empid = empid;
        this.unitid = unitid;
        this.npp = npp;
        this.empname = empname;
        this.unitname = unitname;
        this.statid = statid;
        this.roleid = roleid;
        this.ddlid = ddlid;
        this.ddldisp = ddldisp;
    }

    public String getErid() {
        return erid;
    }

    public void setErid(String erid) {
        this.erid = erid;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getNpp() {
        return npp;
    }

    public void setNpp(String npp) {
        this.npp = npp;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getStatid() {
        return statid;
    }

    public void setStatid(String statid) {
        this.statid = statid;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getDdlid() {
        return ddlid;
    }

    public void setDdlid(String ddlid) {
        this.ddlid = ddlid;
    }

    public String getDdldisp() {
        return ddldisp;
    }

    public void setDdldisp(String ddldisp) {
        this.ddldisp = ddldisp;
    }
}
