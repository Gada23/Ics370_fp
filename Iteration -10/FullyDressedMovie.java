import java.util.List;

public class FullyDressedMovie {
    private int id;
    private String nativeName;
    private String englishName;
    private int yearMade;
    private List<Song> movieSongs;
    private List<MoviePerson> moviePeople;
    private List<SongPerson> songPeople;
    private List<String> anagrams;
    
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setNativeName(String nativeName){
        this.nativeName = nativeName;
    }

    public String getNativeName(){
        return this.nativeName;
    }

    public void setEnglishName(String englishName){
        this.englishName = englishName;
    }

    public String getEnglishName(){
        return this.englishName;
    }

    public void setYearMade(int yearMade){
        this.yearMade = yearMade;
    }

    public int getYearMade(){
        return this.yearMade;
    }

    public void setMovieSongs(List<Song> movieSongs){
        this.movieSongs = movieSongs;
    }

    public List<Song> getMovieSongs(){
        return this.movieSongs;
    }

    public List<MoviePerson> getMoviePeople(){
        return this.moviePeople;
    }

    public void setMoviePeople(List<MoviePerson> moviePeople){
        this.moviePeople = moviePeople;
    }

    public void setSongPeople(List<SongPerson> songPeople){
        this.songPeople = songPeople;
    }

    public List<SongPerson> getSongPeople(){
        return this.songPeople;
    }

    public List<String> getAnagrams(){
        return this.anagrams;
    }

    public void setAnagrams(List<String> anagrams){
        this.anagrams = anagrams;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String noDataFound = "There is no data for this area\n";

        builder.append("------- Fully Dressed Movie Object Details -----------------\n");
        builder.append("Movie ID: ").append(id).append("\n");
        builder.append("Native Name: ").append(nativeName).append("\n");
        builder.append("English Name: ").append(englishName).append("\n");
        builder.append("Year Made: ").append(yearMade).append("\n");

        builder.append("\n\n");
        builder.append("--------- Movie Songs ----------------------").append("\n");
        for (Song movieSong : this.movieSongs) {
            builder.append(movieSong).append("\n");
        }
        if(this.movieSongs.isEmpty()){
            builder.append(noDataFound);
        }

        builder.append("\n\n");
        builder.append("--------- Movie People ---------------------").append("\n");
        for (MoviePerson moviePerson : moviePeople) {
            builder.append(moviePerson).append("\n");
        }
        if(moviePeople.isEmpty()){
            builder.append(noDataFound);
        }

        builder.append("\n\n");
        builder.append("-------- People Associated With Movie Songs -------------").append("\n");
        for (SongPerson songPerson : songPeople) {
            builder.append(songPerson).append("\n");
        }
        if(songPeople.isEmpty()){
            builder.append(noDataFound);
        }

        builder.append("\n\n");
        builder.append("-------- Movie Anagrams ---------------").append("\n");
        for (String anagram : anagrams) {
            builder.append(anagram).append("\n");
        }
        if(anagrams.isEmpty()){
            builder.append(noDataFound);
        }

        return builder.toString();
    }
}