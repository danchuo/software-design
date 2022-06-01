package jigsawserver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

public final class JigsawDB implements AutoCloseable {

  public static final String DB_NAME = "jigsawDB";
  private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
  private static final String CREATE_STRING =
      "CREATE TABLE GAME_RESULTS  "
          + "(WINNER_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
          + "LOGIN VARCHAR(32) NOT NULL, "
          + "ENTRY_DATE TIMESTAMP, "
          + "FIGURES INT NOT NULL, "
          + "SECONDS INT NOT NULL)";

  private static final String CONNECTION_URL =
      "jdbc:derby:.\\..\\ServerProject\\src\\main\\resources\\" + DB_NAME + ";create=true";
  private final Connection connection;
  private final Statement statement;

  private final PreparedStatement insertStatement;

  public JigsawDB() {
    try {
      connection = DriverManager.getConnection(CONNECTION_URL);
      statement = connection.createStatement();

      if (!isTableExist("GAME_RESULTS")) {
        statement.execute(CREATE_STRING);
      }
      insertStatement =
          connection.prepareStatement("insert into GAME_RESULTS values (DEFAULT, ?, ?, ?, ?)");

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isTableExist(String sTablename) throws SQLException {
    DatabaseMetaData dbmd = connection.getMetaData();
    ResultSet rs = dbmd.getTables(null, null, sTablename.toUpperCase(Locale.ROOT), null);
    return rs.next();
  }

  public ArrayList<JigsawGameResult> getTop10Games() throws SQLException {
    var array = new ArrayList<JigsawGameResult>();
    var game =
        statement.executeQuery(
            "select * from GAME_RESULTS order by FIGURES DESC, SECONDS ASC, ENTRY_DATE OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");

    while (game.next()) {
      array.add(
          new JigsawGameResult(
              game.getString(2),
              game.getTimestamp(3).toLocalDateTime(),
              game.getInt(4),
              game.getInt(5)));
    }

    game.close();
    return array;
  }

  public void addGameResult(JigsawGameResult gameResult) throws SQLException {
    insertStatement.setString(1, gameResult.login());
    insertStatement.setTimestamp(2, Timestamp.valueOf(gameResult.endGameTime()));
    insertStatement.setInt(3, gameResult.amountOfTurns());
    insertStatement.setInt(4, gameResult.amountOfSeconds());
    insertStatement.executeUpdate();
  }

  @Override
  public void close() throws Exception {
    insertStatement.close();
    statement.close();
    connection.close();
    boolean gotSQLExc = false;
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    } catch (SQLException se) {
      if ("XJ015".equals(se.getSQLState())) {
        gotSQLExc = true;
      }
    }
    if (gotSQLExc) {
      System.out.println("Database shut down normally");
    } else {
      System.out.println("Database did not shut down normally");
    }
  }
}
