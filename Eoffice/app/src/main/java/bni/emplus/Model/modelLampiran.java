package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class modelLampiran {
    private String judulDokumen;
    private String tanggalDokumen;
    private String jamDokumen;
    private String byteDokumen;
    private String fileSize;
    private String fileType;
    private String id;
    private String isFirstDoc;
    private int gambarDokumen;

    public modelLampiran(){

    }
    public modelLampiran(String id, String judulDokumen, String tanggalDokumen, int gambarDokumen, String byteDokumen, String fileSize, String fileType, String nilaiBytes, String isFirstDoc){
        this.id = id;
        this.judulDokumen = judulDokumen;
        this.tanggalDokumen = tanggalDokumen;
        this.judulDokumen = judulDokumen;
        this.byteDokumen = byteDokumen;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.gambarDokumen = gambarDokumen;
        this.isFirstDoc = isFirstDoc;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }
    public void setJamDokumen(String judulDokumen){
        this.jamDokumen = jamDokumen;
    }
    public String getJamDokumen(){
        return jamDokumen;
    }
    public void setJudulDokumen(String judulDokumen){
        this.judulDokumen = judulDokumen;
    }
    public String getJudulDokumen(){
        return judulDokumen;
    }
    public void setTanggalDokumen(String tanggalDokumen){
        this.tanggalDokumen = tanggalDokumen;
    }
    public String getTanggalDokumen(){
        return tanggalDokumen;
    }
    public void setByteDokumen(String byteDokumen){ this.byteDokumen = byteDokumen;}
    public String getByteDokumen(){return byteDokumen;}
    public void setFileSize(String fileSize){this.fileSize = fileSize;}
    public String getFileSize(){return fileSize;}
    public void setFileType(String fileType){this.fileType = fileType;}
    public String getFileType(){return fileType;}
    public int getGambarDokumen(){
        return gambarDokumen;
    }
    public  void setGambarDokumen(int gambarDokumen){
        this.gambarDokumen = gambarDokumen;
    }

    public String getIsFirstDoc() {
        return isFirstDoc;
    }

    public void setIsFirstDoc(String isFirstDoc) {
        this.isFirstDoc = isFirstDoc;
    }
}
