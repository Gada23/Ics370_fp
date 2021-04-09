import java.sql.*;

public class MovieDriver {
    private final Connection connection;

    private static final String MOVIE_CREATED = "M created ";
    private static final String MOVIE_IGNORED = "M ignored ";
    private static final String SONG_CREATED = "S created ";
    private static final String SONG_IGNORED = "S ignored ";
    private static final String PEOPLE_CREATED = "P created ";
    private static final String PEOPLE_IGNORED = "P ignored ";
    private static final String MOVIE_SONG_CREATED = "MS created ";
    private static final String MOVIE_SONG_IGNORED = "MS ignored ";
    private static final String SONG_PEOPLE_CREATED = "SP created ";
    private static final String SONG_PEOPLE_IGNORED = "SP ignored ";

    public MovieDriver() throws SQLException{
        String url = "jdbc:mysql://localhost:3306/omdb_final";
        String user = "root";
        String password = "Aeroplane1994";

        connection = DriverManager.getConnection(url, user, password);
    }


    public boolean processMovieSongPeople(){
        try {
            String query = "SELECT * FROM `mspr_test_data`";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nativeName = resultSet.getString("native_name");
                int yearMade = resultSet.getInt("year_made");
                String title = resultSet.getString("title");
                String stageName = resultSet.getString("stage_name");
                String role = resultSet.getString("role");

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

                Integer peopleId = peopleExistsByStageName(stageName);
                if(peopleId != null){
                    executionStatus += PEOPLE_IGNORED;
                } else {
                    int currentPeopleCount = getCurrentPeopleCount();
                    peopleId = currentPeopleCount + 1;
                    createPeople(peopleId, stageName, "", "", "", "", "");
                    executionStatus += PEOPLE_CREATED;
                }


                if(movieSongRelationExists(movieId, songId)){
                    executionStatus += MOVIE_SONG_IGNORED;
                } else {
                    createMovieSongRelation(movieId, songId);
                    executionStatus += MOVIE_SONG_CREATED;
                }


                if(songPeopleRelationExists(songId, peopleId, role)){
                    executionStatus += SONG_PEOPLE_IGNORED;
                } else {
                    createSongPeopleRelation(songId, peopleId, role);
                    executionStatus += SONG_PEOPLE_CREATED;
                }

                String updateQuery = "UPDATE `mspr_test_data` SET `execution_status` = ? WHERE `id` = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, executionStatus);
                updateStatement.setInt(2, id);
                updateStatement.execute();
                updateStatement.close();

                System.out.println("Done processing mspr_test_data row with id: " + id);
            }
            return true;

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        new MovieDriver().processMovieSongPeople();
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

    private int getCurrentPeopleCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM `people`";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        int peopleCount = resultSet.getInt(1);
        resultSet.close();
        return peopleCount;
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

    private void createSongPeopleRelation(int songId, int peopleId, String role) throws SQLException {
        String query = "INSERT INTO `song_people` VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, songId);
        statement.setInt(2, peopleId);
        statement.setString(3, role);

        statement.execute();
        statement.close();
    }

    private boolean songPeopleRelationExists(int songId, int peopleId, String role) throws SQLException {
        String query = "SELECT * FROM `song_people` WHERE `song_id` = ? AND `people_id` = ? AND `role` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, songId);
        statement.setInt(2, peopleId);
        statement.setString(3, role);

        ResultSet resultSet = statement.executeQuery();
        boolean returnVal = resultSet.next();
        resultSet.close();
        return returnVal;
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


    public void createMovieSongRelation(int movieId, int songId) throws SQLException {
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

    public Integer peopleExistsByStageName(String stageName) throws SQLException {
        String query = "SELECT * FROM `people` WHERE `stage_name` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, stageName);

        ResultSet resultSet = statement.executeQuery();
        Integer peopleId = null;
        if(resultSet.next()){
            peopleId = resultSet.getInt("people_id");
        }
        resultSet.close();
        statement.close();
        return peopleId;
    }

    public void createPeople(int peopleId,
                             String stageName,
                             String firstName,
                             String middleName,
                             String lastName,
                             String gender,
                             String imageFileName
                             ) throws SQLException {

        String query = "INSERT INTO `people` VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, peopleId);
        statement.setString(2, stageName);
        statement.setString(3, firstName);
        statement.setString(4, middleName);
        statement.setString(5, lastName);
        statement.setString(6, gender);
        statement.setString(7, imageFileName);


        statement.execute();
        statement.close();
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
