package bni.emplus.Model;

/**
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class FileUpload implements Comparable<FileUpload>{
    private String name;
    private String data;
    private String date;
    private String path;
    private String image;
    private String size;

    public FileUpload(String n, String d, String dt, String p, String img, String sz)
    {
        name = n;
        data = d;
        date = dt;
        path = p;
        image = img;
        size = sz;
    }
    public FileUpload(String n, String d, String dt, String p, String img)
    {
        name = n;
        data = d;
        date = dt;
        path = p;
        image = img;

    }
    public String getName()
    {
        return name;
    }
    public String getData()
    {
        return data;
    }
    public String getDate()
    {
        return date;
    }
    public String getPath()
    {
        return path;
    }
    public String getImage() {
        return image;
    }
    public String getSize(){return size;}

    public int compareTo(FileUpload o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}

