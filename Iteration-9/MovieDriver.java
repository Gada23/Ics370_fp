import java.sql.*;

public class MovieDriver {
    private final Connection connection;

    public MovieDriver() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/omdb_final";
        String user = "root";
        String password = "";

        connection = DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) throws SQLException {
        MovieDriver movieDriver = new MovieDriver();
        movieDriver.findMoviesByAnagram("మోసము");
    }

    public void getYearWiseMovieCountSummary() throws SQLException {
        String query = "SELECT `year_made`, COUNT(year_made) FROM `movies` GROUP BY `year_made` ORDER BY `year_made`";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            int yearMade = resultSet.getInt("year_made");
            int movieCount = resultSet.getInt("COUNT(year_made)");
            System.out.println("Year Made: " + yearMade + ", Movie Count: " + movieCount);
        }

        resultSet.close();
        statement.close();
    }

    public void findMoviesByRoleAndStageName(String role, String stageName) throws SQLException {
        String query = "SELECT * FROM `movies` m, `movie_people` mp, people p  WHERE m.movie_id = mp.movie_id AND \n" +
                "p.people_id = mp.people_id AND mp.role = ? AND p.stage_name = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, role);
        statement.setString(2, stageName);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("movie_id");
            String nativeName = resultSet.getString("native_name");
            int yearMade = resultSet.getInt("year_made");

            System.out.println("Id: " + id + ", Native Name: " + nativeName + ", Year Made: " + yearMade);
        }

        resultSet.close();
        statement.close();
    }

    public void findMoviesWithNoSongs() throws SQLException {
        String query = "SELECT m.movie_id, m.native_name, m.english_name, m.year_made FROM movies as m LEFT JOIN movie_song " +
                "ON m.movie_id = movie_song.movie_id WHERE movie_song.movie_id is null";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        extractAndPrintMovieDetails(resultSet);

        resultSet.close();
        statement.close();
    }

    public void findMoviesWithNoPeople() throws SQLException {
        String query = "SELECT m.movie_id, m.native_name, m.english_name, m.year_made FROM movies as m LEFT JOIN movie_people ON m.movie_id = movie_people.movie_id WHERE movie_people.movie_id is null";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        extractAndPrintMovieDetails(resultSet);

        resultSet.close();
        statement.close();
    }

    public void findMoviesByAnagram(String anagram) throws SQLException {
        String query = "SELECT m.movie_id, m.native_name, m.english_name, m.year_made, ma.anagram FROM movies m, movie_anagrams ma WHERE m.movie_id = ma.movie_id AND ma.anagram = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, anagram);
        ResultSet resultSet = statement.executeQuery();

        extractAndPrintMovieDetails(resultSet);

        resultSet.close();
        statement.close();
    }

    private void extractAndPrintMovieDetails(ResultSet resultSet) throws SQLException {
        while (resultSet.next()){
            int id = resultSet.getInt("movie_id");
            String nativeName = resultSet.getString("native_name");
            String englishName = resultSet.getString("english_name");
            int yearMade = resultSet.getInt("year_made");

            String output = String.format("Id: %d, Native Name: %s, English Name: %s, Year Made: %d",
                    id, nativeName, englishName, yearMade);

            System.out.println(output);
        }
    }
}
