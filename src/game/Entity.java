package game;

public interface Entity {

    Field.Cell fieldLocation();

    void setFieldLocation(Field field, int x, int y);

    Team team();

    void setTeam(Team team);
}
