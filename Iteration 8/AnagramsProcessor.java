import java.io.File;
import java.sql.*;
import java.util.Scanner;
//looks good
public class AnagramsProcessor {
    private final Connection connection;

    public AnagramsProcessor() throws Exception {
        String url = "jdbc:mysql://192.168.64.3:3306/omdb_final";
        String user = "root";
        String password = "";

        connection = DriverManager.getConnection(url, user, password);
    }

    private boolean anagramExists(int movieId, String anagram) throws SQLException {
        String query = "SELECT * FROM `movie_anagrams` WHERE `movie_id` = ? AND `anagram` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        statement.setString(2, anagram);
        ResultSet resultSet = statement.executeQuery();
        boolean returnValue = resultSet.next();
        resultSet.close();
        statement.close();
        return returnValue;
    }

    public void createAnagram(int movieId, String anagram) throws SQLException {
        String query = "INSERT INTO `movie_anagrams` VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, movieId);
        statement.setString(2, anagram);
        statement.execute();
        statement.close();
    }

    private void createMovie(int movieId,
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

    public void processAnagrams() throws Exception {
        int count = 0;
        // pass the path to the file as a parameter
        File file = new File("movie_anagrams.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String csvLine = sc.nextLine();
            String[] csvLineSplit = csvLine.split(",");

            String nativeName = csvLineSplit[0].trim();
            int yearMade = Integer.parseInt(csvLineSplit[1].trim());
            String anagram = csvLineSplit[2].trim();

            processSingleCsvLine(nativeName, yearMade, anagram);

            System.out.println(csvLine);
            count = count + 1;
        }
        System.out.println(count);
    }

    public int getCurrentMovieCount() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM `movies`");
        resultSet.next();
        return resultSet.getInt(1);
    }

    private void processSingleCsvLine(String nativeName, int yearMade, String anagram) throws SQLException {
        Integer movieId = movieExistsByNativeNameAndYearMade(nativeName, yearMade);
        if (movieId == null) {
            // no such movie exists. Create it.
            movieId = getCurrentMovieCount() + 1;
            createMovie(movieId, nativeName, "", yearMade);
        }

        if(!anagramExists(movieId, anagram)){
            // no such anagram, create it for this movie
            createAnagram(movieId, anagram);
        }
    }

    private Integer movieExistsByNativeNameAndYearMade(String nativeName, int yearMade) throws SQLException {
        String query = "SELECT * FROM `movies` WHERE `native_name` = ? AND `year_made` = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nativeName);
        statement.setInt(2, yearMade);

        ResultSet resultSet = statement.executeQuery();
        Integer movieId = null;
        if (resultSet.next()) {
            movieId = resultSet.getInt("movie_id");
        }
        resultSet.close();
        return movieId;
    }

    public static void main(String[] args) throws Exception {
        new AnagramsProcessor().processAnagrams();
    }
}
