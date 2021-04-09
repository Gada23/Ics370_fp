import java.sql.*;
//The code is working
public class MovieDriver {
    private final Connection connection;

    private static final String MOVIE_CREATED = "[1] M created";
    private static final String MOVIE_IGNORED = "[2] M ignored";
    private static final String SONG_CREATED = "[3] S created";
    private static final String SONG_IGNORED = "[4] S ignored";
    private static final String MS_CREATED = "[5] MS created";
    private static final String MS_IGNORED = "[6] MS ignored";


    public MovieDriver() throws SQLException{
        String url = "jdbc:mysql://localhost:3306/omdb_final";
        String user = "root";
        String password = "Aeroplane1994";

        connection = DriverManager.getConnection(url, user, password);
    }


    public static void main(String[] args) throws Exception {
        new MovieDriver().processMovieSongs();
    }

    public boolean processMovieSongs() {
        try {
            String query = "SELECT * FROM `ms_test_data`";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                int id = resultSet.getInt("ID");
                String nativeName = resultSet.getString("native_name");
                int yearMade = resultSet.getInt("year_made");
                String title = resultSet.getString("title");

                String executionStatus = "";

                Integer movieId = movieExistsByNativeNameAndYearMade(nativeName, yearMade);
                if(movieId != null){
                    executionStatus += MOVIE_IGNORED;
                } else {
                    int currentMovieCount = getCurrentMovieCount();
                    movieId = currentMovieCount + 1;
                    createMovie(movieId, nativeName, "", yearMade);
                    executionStatus += MOVIE_CREATED;
                }

                Integer songId = songExistsByTitle(title);
                if(songId != null){
                    executionStatus += SONG_IGNORED;
                } else {
                    int currentSongCount = getCurrentSongCount();
                    songId = currentSongCount + 1;
                    createSong(songId, title, "", "");
                    executionStatus += SONG_CREATED;
                }

                if(movieSongRelationExists(movieId, songId)){
                    executionStatus += MS_IGNORED;
                } else {
                    createMovieSongRelationShip(movieId, songId);
                    executionStatus += MS_CREATED;
                }

                String updateQuery = "UPDATE `ms_test_data` SET `execution_status` = ? WHERE `ID` = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, executionStatus);
                updateStatement.setInt(2, id);
                updateStatement.execute();
                updateStatement.close();

                System.out.println("Done processing ms_test_data row with id: " + id);
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Integer songExistsByTitle(String songTitle) throws SQLException {
        String query = "SELECT * FROM `songs` WHERE `title` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, songTitle);

        ResultSet resultSet = statement.executeQuery();
        Integer songId = null;
        if(resultSet.next()){
            songId = resultSet.getInt("song_id");
        }
        resultSet.close();
        return songId;
    }

    private int getCurrentSongCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM `songs`";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        int songsCount = resultSet.getInt(1);
        resultSet.close();
        return songsCount;
    }

    private Integer movieExistsByNativeNameAndYearMade(String nativeName, int yearMade) throws SQLException{
        String query = "SELECT * FROM `movies` WHERE `native_name` = ? AND `year_made` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nativeName);
        statement.setInt(2, yearMade);

        ResultSet resultSet = statement.executeQuery();
        Integer movieId = null;
        if(resultSet.next()){
            movieId = resultSet.getInt("movie_id");
        }
        resultSet.close();
        return movieId;
    }

    private void fetchMovieAndMovieData(int movieId) throws SQLException{

        String movieQuery = "SELECT * FROM `movies` WHERE `movie_id` = ?";
        PreparedStatement movieStatement = connection.prepareStatement(movieQuery);
        movieStatement.setInt(1, movieId);

        ResultSet movieResultSet =  movieStatement.executeQuery();
        if(!movieResultSet.next()){
            System.out.println("No movie found with id: " + movieId);
            return;
        }

        String nativeName = movieResultSet.getString("native_name");
        String englishName = movieResultSet.getString("english_name");
        int yearMade = movieResultSet.getInt("year_made");

        System.out.println("Movie Id: " + movieId);
        System.out.println("Native Name: " + nativeName);
        System.out.println("English Name: " + englishName);
        System.out.println("Year Made: " + yearMade);

        movieResultSet.close();

        String movieDataQuery = "SELECT * FROM `movie_data` WHERE `movie_id` = ?";
        PreparedStatement movieDataStatement = connection.prepareStatement(movieDataQuery);
        movieDataStatement.setInt(1, movieId);

        ResultSet movieDataResultSet = movieDataStatement.executeQuery();
        if(!movieDataResultSet.next()){
            System.out.println("No movie data found for movie id: " + movieId);
            return;
        }

        String tagLine = movieDataResultSet.getString("tag_line");
        String language = movieDataResultSet.getString("language");
        String country = movieDataResultSet.getString("country");
        String genre = movieDataResultSet.getString("genre");
        String plot = movieDataResultSet.getString("plot");

        System.out.println("Tag Line: " + tagLine);
        System.out.println("Language: " + language);
        System.out.println("Country: " + country);
        System.out.println("Genre: " + genre);
        System.out.println("Plot: " + plot);

        movieDataResultSet.close();
    }

    private  void updateMovie(int movieId,
                                    String nativeName,
                                    String englishName,
                                    int yearMade) throws SQLException{

        String updateQuery = "UPDATE `movies` SET `native_name` = ?, `english_name` = ?, " +
                "`year_made` = ? WHERE `movie_id` = ?";

        PreparedStatement statement =
                connection.prepareStatement(updateQuery);

        statement.setString(1, nativeName);
        statement.setString(2, englishName);
        statement.setInt(3, yearMade);
        statement.setInt(4, movieId);

        statement.execute();
        statement.close();
    }

    private boolean movieSongRelationExists(int movieId, int songId) throws SQLException {
        String query = "SELECT * FROM `movie_song` WHERE `movie_id` = ? AND `song_id` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        statement.setInt(2, songId);

        ResultSet resultSet = statement.executeQuery();
        boolean returnVal = resultSet.next();

        resultSet.close();
        statement.close();
        return returnVal;
    }

    private void deleteMovie(int movieId) throws SQLException{
        PreparedStatement statement =
                connection.prepareStatement("DELETE FROM `movies` WHERE `movie_id` = ?");

        statement.setInt(1, movieId);

        statement.execute();
        statement.close();
    }

    public void createMovieSongRelationShip(int movieId, int songId) throws SQLException {
        String query = "INSERT INTO `movie_song` VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        statement.setInt(2, songId);
        statement.execute();
        statement.close();
    }

    public void createSong(int songId, String title, String lyrics, String theme) throws SQLException {
        String query = "INSERT INTO `songs` VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, songId);
        statement.setString(2, title);
        statement.setString(3, lyrics);
        statement.setString(4, theme);

        statement.execute();
    }

    public int getCurrentMovieCount() throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM `movies`");
        resultSet.next();
        return resultSet.getInt(1);
    }

    private  void createMovie(int movieId,
                                    String nativeName,
                                    String englishName,
                                    int yearMade) throws SQLException {

        PreparedStatement statement =
                connection.prepareStatement("INSERT INTO `movies` VALUES (?, ?, ?, ?)");

        statement.setInt(1, movieId);
        statement.setString(2, nativeName);
        statement.setString(3, englishName);
        statement.setInt(4, yearMade);

        statement.execute();
        statement.close();
    }
}
