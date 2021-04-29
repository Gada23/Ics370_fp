public class MoviePerson extends Person {
    private String role;
    private int movieId;
    private String screenName;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public String toString() {
        String template = "Id: %d, Screen Name: %s, Stage Name: %s, First Name: %s, " +
                "Middle Name: %s, Last Name: %s, Gender: %s, Image Name: %s";

        return String.format(template, peopleId, screenName, stageName,
                firstName, middleName, lastName, gender, imageName);
    }
}
