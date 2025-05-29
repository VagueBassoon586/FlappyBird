package FLB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Database {

	private static final String DB_URL = "jdbc:sqlite:flappybird.db";

	public Database() {
		try (Connection conn = DriverManager.getConnection(DB_URL)) {
			if (conn != null) {
				String createTable = """
					CREATE TABLE IF NOT EXISTS users (
						username TEXT PRIMARY KEY,
						password TEXT NOT NULL,
						score INTEGER DEFAULT 0
					);
				""";
				try (Statement stmt = conn.createStatement()) {
					stmt.execute(createTable);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
	}

	// Validate user credentials
	public boolean validateUser(String username, String password) {
		String query = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL);
			PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return false;
	}

	// Get the current score of a user
    public int getScore(String username, String password) {
        String query = "SELECT score FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return 0;
    }

	// Update the user's score if the new score is higher
	public void updateScore(String username, String password, int newScore) {
		if (validateUser(username, password)) {
			int currentScore = getScore(username, password);
			if (newScore > currentScore) {
				String update = "UPDATE users SET score = ? WHERE username = ?";
				try (Connection conn = DriverManager.getConnection(DB_URL);
					 PreparedStatement stmt = conn.prepareStatement(update)) {
					stmt.setInt(1, newScore);
					stmt.setString(2, username);
					stmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	// Insert a new user
    public boolean registerUser(String username, String password) {
        String insert = "INSERT OR IGNORE INTO users (username, password, score) VALUES (?, ?, 0)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

public Object[][] getLeaderBoard(String username) throws IOException {
		ImageIcon platinum = new ImageIcon(ImageIO.read(new File("res/img/Platinum.png")));
		ImageIcon gold = new ImageIcon(ImageIO.read(new File("res/img/Gold.png")));
		ImageIcon silver = new ImageIcon(ImageIO.read(new File("res/img/Silver.png")));
		ImageIcon bronze = new ImageIcon(ImageIO.read(new File("res/img/Bronze.png")));

		List<Object[]> leaderboard = new ArrayList<>();


		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement stmt = conn.prepareStatement("SELECT username, score FROM users ORDER BY score DESC");
				ResultSet rs = stmt.executeQuery()) {
			int rank = 1;
			int userRank = -1;
			String name;
			int score = 0;
			while (rs.next()) {
				name = rs.getString("username");
				score = rs.getInt("score");

				Object rankDisplay = switch (rank) {
					case 1 -> platinum;
					case 2 -> gold;
					case 3 -> silver;
					case 4 -> bronze;
					default -> rank;
				};

				Object[] row = { rankDisplay, name, score };

				if (rank <= 100) {
					leaderboard.add(row);
				}

				if (name.equals(username)) {
					userRank = rank;
				}

				rank++;
			}

			if (leaderboard.size() < 100 && leaderboard.size() < rank - 1) {
				return leaderboard.toArray(Object[][]::new);
			} else if (userRank > 100) {
				leaderboard.remove(leaderboard.size() - 1);
				Object[] customUserRow = { userRank, username, score };
				leaderboard.add(customUserRow);
			}

		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return leaderboard.toArray(Object[][]::new);
	}
	public static void main(String[] args) {
		// Use this for debugging
	}

}