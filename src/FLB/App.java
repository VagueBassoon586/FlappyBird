package FLB;

import java.io.IOException;
import javax.swing.JFrame;

public class App {
	public static void main(String[] args) throws IOException {
		// SignUp.isClosed = false;
		// SignUp.signup();

		// while (!SignUp.isClosed) {
		// 	try { Pause(); 
		// 	} catch (InterruptedException e) {
		// 		e.printStackTrace(System.err);
		// 	}
		// }
		// LogIn.isloggedIn = false;
		// LogIn.login();

		// while (!LogIn.isloggedIn) {
		// 	try {
		// 		Pause(); // small pause
		// 	} catch (InterruptedException e) {
		// 		e.printStackTrace(System.err);
		// 	}
		// }

		String[] user = {LogIn.getUsername(), LogIn.getPassword()};
		if (user[0] == null || user[1] == null) {
			user[0] = "@ADMIN";
			user[1] = "@PASSWORD";
		}

		System.out.println("Logged in as " + user[0]);

		JFrame frame = new JFrame("Flappy Bird");
		frame.setSize(360, 640);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FlappyBird flb = new FlappyBird(user);
		frame.add(flb);
		frame.pack();
		frame.setVisible(true);

		flb.requestFocus();
		}

	private static void Pause() throws InterruptedException {
		Thread.sleep(100);
	}
}