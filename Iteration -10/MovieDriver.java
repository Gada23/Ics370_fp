import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//good work
public class MovieDriver {
    private final Connection connection;

    public MovieDriver() throws SQLException {
        String url = "jdbc:mysql://192.168.64.3:3306/omdb_final";
        String user = "root";
        String password = "";

        connection = DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) throws SQLException {
        MovieDriver movieDriver = new MovieDriver();
        FullyDressedMovie fullyDressedMovie = movieDriver.getInfo(291);
        System.out.println(fullyDressedMovie);
    }

    public FullyDressedMovie getInfo(int id){
        try {
            FullyDressedMovie fullyDressedMovie = getPartialMovie(id).orElseThrow(() -> {
               return new RuntimeException("No movie found with the id: " + id);
            });

            fullyDressedMovie.setAnagrams(findAnagramsByMovieId(id));
            fullyDressedMovie.setSongPeople(findSongPeople(id));
            fullyDressedMovie.setMovieSongs(findMovieSongs(id));
            fullyDressedMovie.setMoviePeople(findMoviePeople(id));

            return fullyDressedMovie;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<SongPerson> findSongPeople(int movieId) throws SQLException {
        String query = "SELECT * FROM `people` p, `song_people` sp, `movie_song` ms WHERE p.people_id = sp.people_id " +
                "AND ms.song_id = sp.song_id AND ms.movie_id = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        ResultSet resultSet = statement.executeQuery();
        List<SongPerson> songPeople = new ArrayList<>();

        while (resultSet.next()){
            int peopleId = resultSet.getInt("people_id");
            String stageName = resultSet.getString("stage_name");
            String firstName = resultSet.getString("first_name");
            String middleName = resultSet.getString("middle_name");
            String lastName = resultSet.getString("last_name");
            String gender = resultSet.getString("gender");
            String imageName = resultSet.getString("image_name");
            String role = resultSet.getString("role");
            int songId = resultSet.getInt("song_id");

            SongPerson songPerson = new SongPerson();
            songPerson.setRole(role);
            songPerson.setSongId(songId);
            songPerson.setPeopleId(peopleId);
            songPerson.setStageName(stageName);
            songPerson.setFirstName(firstName);
            songPerson.setMiddleName(middleName);
            songPerson.setLastName(lastName);
            songPerson.setGender(gender);
            songPerson.setImageName(imageName);

            songPeople.add(songPerson);
        }

        resultSet.close();
        statement.close();

        return songPeople;
    }

    private Optional<FullyDressedMovie> getPartialMovie(int movieId) throws SQLException {
        String query = "SELECT * FROM `movies` WHERE `movie_id` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        ResultSet resultSet = statement.executeQuery();
        Optional<FullyDressedMovie> fullyDressedMovieOptional;

        if(!resultSet.next()){
            fullyDressedMovieOptional = Optional.empty();
        } else {
           int id = resultSet.getInt("movie_id");
           String nativeName = resultSet.getString("native_name");
           String englishName = resultSet.getString("english_name");
           int yearMade = resultSet.getInt("year_made");

           FullyDressedMovie fullyDressedMovie = new FullyDressedMovie();
           fullyDressedMovie.setId(id);
           fullyDressedMovie.setEnglishName(englishName);
           fullyDressedMovie.setNativeName(nativeName);
           fullyDressedMovie.setYearMade(yearMade);

           fullyDressedMovieOptional = Optional.of(fullyDressedMovie);
        }

        resultSet.close();
        statement.close();

        return fullyDressedMovieOptional;
    }


    private List<MoviePerson> findMoviePeople(int movieId) throws SQLException {
        String query = "SELECT * FROM `movies` m, `movie_people` mp, `people` p " +
                "WHERE m.movie_id = mp.movie_id AND m.movie_id = ? AND p.people_id = mp.people_id";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<MoviePerson> moviePeople  = new ArrayList<>();

        while (resultSet.next()){
            int peopleId = resultSet.getInt("people_id");
            String stageName = resultSet.getString("stage_name");
            String firstName = resultSet.getString("first_name");
            String middleName = resultSet.getString("middle_name");
            String lastName = resultSet.getString("last_name");
            String gender = resultSet.getString("gender");
            String imageName = resultSet.getString("image_name");
            String role = resultSet.getString("role");
            String screenName = resultSet.getString("screen_name");

            MoviePerson moviePerson = new MoviePerson();
            moviePerson.setPeopleId(peopleId);
            moviePerson.setStageName(stageName);
            moviePerson.setFirstName(firstName);
            moviePerson.setMiddleName(middleName);
            moviePerson.setLastName(lastName);
            moviePerson.setGender(gender);
            moviePerson.setImageName(imageName);
            moviePerson.setRole(role);
            moviePerson.setScreenName(screenName);

            moviePeople.add(moviePerson);
        }

        resultSet.close();
        preparedStatement.close();
        return moviePeople;
    }

    private List<Song> findMovieSongs(int movieId) throws SQLException {
        String query = "SELECT * FROM `songs` s, `movie_song` ms WHERE s.song_id = ms.song_id AND ms.movie_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        ResultSet resultSet = statement.executeQuery();
        List<Song> movieSongs = new ArrayList<>();

        while (resultSet.next()){
            int songId  = resultSet.getInt("song_id");
            String title = resultSet.getString("title");
            String lyrics = resultSet.getString("lyrics");
            String theme = resultSet.getString("theme");

            Song song = new Song();
            song.setSongId(songId);
            song.setTitle(title);
            song.setLyrics(lyrics);
            song.setTheme(theme);

            movieSongs.add(song);
        }

        resultSet.close();
        statement.close();

        return movieSongs;
    }

    private List<String> findAnagramsByMovieId(int movieId) throws SQLException {
        String query = "SELECT * FROM `movie_anagrams` WHERE `movie_id` = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> anagrams = new ArrayList<>();

        while (resultSet.next()){
            String anagram = resultSet.getString("anagram");
            anagrams.add(anagram);
        }

        resultSet.close();
        preparedStatement.close();

        return anagrams;
    }
}
