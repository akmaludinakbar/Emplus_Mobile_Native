package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 9/20/2016.
 */
public class attachment {
    private String namaFile,pathFile;
    private Boolean selected,inLandingPage;

    public attachment(){

    }
    public attachment(String namaFile, Boolean selected, Boolean inLandingPage, String pathFile){
        this.namaFile = namaFile;
        this.pathFile = pathFile;
        this.selected = selected;
        this.inLandingPage = inLandingPage;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public void setNamaFile(String namaFile) {
        this.namaFile = namaFile;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getInLandingPage() {
        return inLandingPage;
    }

    public void setInLandingPage(Boolean inLandingPage) {
        this.inLandingPage = inLandingPage;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }
}
