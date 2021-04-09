import java.sql.*;
//Change
public class MovieDriver {
    private final Connection connection;

    private static final String MOVIE_CREATED = "M created ";
    private static final String MOVIE_IGNORED = "M ignored ";
    private static final String PEOPLE_IGNORED = "P ignored ";
    private static final String PEOPLE_CREATED = "P created ";
    private static final String MOVIE_PEOPLE_CREATED = "R created ";
    private static final String MOVIE_PEOPLE_IGNORED = "R ignored ";

    public MovieDriver() throws SQLException{
        String url = "jdbc:mysql://192.168.64.3:3306/omdb_final";
        String user = "root";
        String password = "";

        connection = DriverManager.getConnection(url, user, password);
    }


    public static void main(String[] args) throws Exception {
        new MovieDriver().processMoviePeople();
    }

    public boolean processMoviePeople() {
       try {
           String query = "SELECT * FROM `mpr_test_data`";
           Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery(query);
           while (resultSet.next()){
               int id = resultSet.getInt("id");
               String nativeName = resultSet.getString("native_name");
               int yearMade = resultSet.getInt("year_made");
               String stageName = resultSet.getString("stage_name");
               String role = resultSet.getString("role");
               String screenName = resultSet.getString("screen_name");

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

               Integer peopleId = peopleExistsByStageName(stageName);

               if(peopleId != null){
                   executionStatus += PEOPLE_IGNORED;
               } else {
                   int currentPeopleCount = getCurrentPeopleCount();
                   peopleId = currentPeopleCount + 1;
                   createPeople(peopleId, stageName, "", "", "", "", "");
                   executionStatus += PEOPLE_CREATED;
               }


               if(moviePeopleRelationExists(movieId, peopleId, role, screenName)){
                   executionStatus += MOVIE_PEOPLE_IGNORED;
               } else {
                    createMoviePeopleRelation(movieId, peopleId, role, screenName);
                    executionStatus += MOVIE_PEOPLE_CREATED;
               }


               String updateQuery = "UPDATE `mpr_test_data` SET `execution_status` = ? WHERE `id` = ?";
               PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
               updateStatement.setString(1, executionStatus);
               updateStatement.setInt(2, id);
               updateStatement.execute();
               updateStatement.close();

               System.out.println("Done processing mpr_test_data row with id: " + id);
           }

           return true;
       } catch (SQLException sqe){
           sqe.printStackTrace();
           return false;
       }
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

    public boolean moviePeopleRelationExists(int movieId, int peopleId, String role, String screenName) throws SQLException {
        String query = "SELECT * FROM `movie_people` WHERE " +
                "`movie_id` = ? AND `people_id` = ? AND `role` = ? AND `screen_name` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        statement.setInt(2, peopleId);
        statement.setString(3, role);
        statement.setString(4, screenName);

        ResultSet resultSet = statement.executeQuery();
        boolean returnVal = resultSet.next();
        resultSet.close();
        statement.close();
        return returnVal;
    }

    public void createMoviePeopleRelation(int movieId, int peopleId, String role, String screenName) throws SQLException {
        String query = "INSERT INTO `movie_people` VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        statement.setInt(2, peopleId);
        statement.setString(3, role);
        statement.setString(4, screenName);

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
