package arieluniversity.vicationmusic;

public class Song {
    private String name;
    private String urlSong,urlVoice;
    private String fromWho,toWho;

    public Song(){
        name = "ללא שם";
        urlSong = "";
        urlVoice = "";
        fromWho = "";
        toWho = "";
    }

    public Song(Song song){
        this.name = song.name;
        this.urlSong = song.urlSong;
        this.urlVoice= song.urlVoice;
        this.fromWho = song.fromWho;
        this.toWho = song.toWho;
    }

    public Song(String name,String urlSong,String urlVoice,String fromWho,String toWho){
        this.name = name;
        this.urlSong = urlSong;
        this.urlVoice= urlVoice;
        this.fromWho = fromWho;
        this.toWho = toWho;
    }

    public String getName(){
        return name;
    }
    public String getUrlSong(){
        return urlSong;
    }
    public String getUrlVoice(){
        return urlVoice;
    }
    public String getFromWho(){
        return fromWho;
    }
    public String getToWho(){
        return toWho;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setUrlSong(String urlSong){
        this.urlSong = urlSong;
    }
    public void setUrlVoice(String urlVoice){
        this.urlVoice = urlVoice;
    }
    public void setFromWho(String fromWho){
        this.fromWho = fromWho;
    }
    public void setToWho(String toWho){
        this.toWho = toWho;
    }

}
