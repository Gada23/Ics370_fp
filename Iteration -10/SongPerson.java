public class SongPerson extends Person {
    private int songId;
    private String role;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {

        String template = "Id: %d, Stage Name: %s, First Name: %s, " +
                "Middle Name: %s, Last Name: %s, Gender: %s, Image Name: %s, Role: %s, Song Id: %d";

        return String.format(template, peopleId, stageName,
                firstName, middleName, lastName, gender, imageName, role, songId);
    }
}
