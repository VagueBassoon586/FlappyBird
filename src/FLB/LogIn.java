package FLB;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LogIn {
	public static boolean isloggedIn = false;
	public static String username;
	public static String password;

	private static Font customFont;
	private static Database manager;

	public static String[] login() {
		String[] user = {username, password};
		manager = new Database();
		
		JFrame frame = new JFrame("Log in Flappy Bird");
		frame.setSize(360, 640);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null); // Sử dụng null layout cho frame

		Color customBG = new Color(77, 199, 208); // Nền ngoài
		frame.getContentPane().setBackground(customBG);

		customFont = Helper.loadCustomFont("res/font/PressStart2P-Regular.ttf", 25f);
		
		// Title FlappyBird & copyright
		BufferedImage title = null;
		BufferedImage copyright = null;
			try {
				title = Helper.resizeImage(ImageIO.read(new File("res/img/LoginLogo.png")), 262, 70);
				copyright = Helper.resizeImage(ImageIO.read(new File("res/img/copyright.png")), 170, 20);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}

		JLabel titleLabel = new JLabel(new ImageIcon(title)); 
		titleLabel.setBounds(40, 40, 262, 100); 
		frame.add(titleLabel);

		JLabel copyrightLabel = new JLabel(new ImageIcon(copyright));
		copyrightLabel.setBounds(95, 130, 244, 20);
		frame.add(copyrightLabel);

		// Panel chứa form
		JPanel panel = new JPanel();
		panel.setLayout(null); // Quan trọng để setBounds có hiệu lực trong panel
		panel.setBounds(40, 180, 260, 370);
		Color BG = new Color(63, 163, 169); // Màu nền của panel
		panel.setBackground(BG);

		// Màu cho TextField khi focus và không focus
		Color textFieldBgFocus = new Color(52, 107, 108); // Màu xanh đậm khi focus
		Color textFieldBgNoFocus = new Color(210, 230, 228); // Màu nền khi không focus (màu xám nhạt như hình mẫu)

		// Tiêu đề "Sign Up"
		JLabel label = new JLabel("LOGIN");
		label.setBounds(70, 30, 200, 50);
		label.setForeground(Color.WHITE);
		label.setBackground(BG);
		label.setOpaque(true);
		label.setFont(customFont);
		
		// Ô nhập tên / email
		JTextField JName = new JTextField("Username"); // Khởi tạo với "Users"
		JName.setBounds(20, 100, 220, 50);
		JName.setFont(customFont.deriveFont(12f));
		JName.setForeground(Color.GRAY); // Màu chữ placeholder
		JName.setBackground(textFieldBgNoFocus); // Đặt màu nền ban đầu
		JName.setCaretColor(Color.WHITE); // Màu con trỏ nháy

		// Ô nhập mật khẩu
		JPasswordField JPassword = new JPasswordField("Password"); // Nên dùng JPasswordField cho mật khẩu
		JPassword.setBounds(20, 170, 220, 50);
		JPassword.setFont(customFont.deriveFont(12f));
		JPassword.setForeground(Color.GRAY); // Màu chữ placeholder
		JPassword.setBackground(textFieldBgNoFocus); // Đặt màu nền ban đầu
		JPassword.setCaretColor(Color.WHITE);

		// JCheckBox để hiện / ẩn mật khẩu
		JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
		showPasswordCheckBox.setBounds(20, 230, 150, 20); // Đặt vị trí dưới ô mật khẩu
		showPasswordCheckBox.setFont(customFont.deriveFont(Font.PLAIN, 10f)); // Font nhỏ hơn
		showPasswordCheckBox.setForeground(Color.WHITE); // Màu chữ
		showPasswordCheckBox.setOpaque(false); // Đảm bảo nền trong suốt nếu muốn
		
		// Nút Sign up
		JButton BTsignUp = new JButton("Log in Now");
		BTsignUp.setBounds(32, 270, 200, 40);
		BTsignUp.setFont(customFont.deriveFont(Font.PLAIN, 15f));
		BTsignUp.setForeground(Color.WHITE);
		BTsignUp.setBackground(new Color(230, 120, 50));
		BTsignUp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

		// === FocusListener cho JName ===
		JName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (JName.getText().equals("Username")) {
					JName.setText("");
					JName.setForeground(Color.WHITE);
					JName.setBackground(textFieldBgFocus);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (JName.getText().isEmpty()) {
					JName.setForeground(Color.GRAY);
					JName.setText("Username");
					JName.setBackground(textFieldBgNoFocus);
				}
			}
		});
		
		// === FocusListener cho JPassword ===
		JPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// Lấy mật khẩu dạng char[] và chuyển về String để so sánh
				if (String.valueOf(JPassword.getPassword()).equals("Password")) {
					JPassword.setText(""); // Xóa placeholder
					JPassword.setForeground(Color.WHITE);
					JPassword.setBackground(textFieldBgFocus);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (String.valueOf(JPassword.getPassword()).isEmpty()) {
					JPassword.setForeground(Color.GRAY);
					JPassword.setText("Password"); // Đặt lại đúng "Password"
					JPassword.setBackground(textFieldBgNoFocus);
				}
			}
		});

		// === ActionListener cho showPasswordCheckBox (cách dễ hiểu hơn) ===
		showPasswordCheckBox.addActionListener((ActionEvent e) -> {
					if (showPasswordCheckBox.isSelected()) {
						// Nếu checkbox được CHỌN (tức là muốn hiện mật khẩu)
						JPassword.setEchoChar((char) 0); // Đặt ký tự ẩn thành null (hiện chữ)
					} else {
						// Nếu checkbox KHÔNG được chọn (tức là muốn ẩn mật khẩu)
						JPassword.setEchoChar('\u2022'); // Đặt ký tự ẩn về mặc định (dấu chấm tròn)
					}
				});
		  
		BTsignUp.addActionListener((ActionEvent e) -> {
			username = JName.getText();
			password = new String(JPassword.getPassword());
			
			
			// Kiểm tra xem trường Username có phải là placeholder không
			if (username.equals("Username") || username.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Please enter your username!", "Error", JOptionPane.ERROR_MESSAGE);
				isloggedIn = false;
				return;
			}
			// Kiểm tra xem trường Password có phải là placeholder không
			if (password.equals("Password") || password.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Please enter your password!", "Error", JOptionPane.ERROR_MESSAGE);
				isloggedIn = false;
				return;
			}
			
			if (manager.validateUser(username, password)) {
				isloggedIn = true;
				user[0] = username;
				user[1] = password;
				frame.dispose();
			} else
				JOptionPane.showMessageDialog(frame, "Login failed");
		});

		panel.add(JName);
		panel.add(JPassword);
		panel.add(showPasswordCheckBox); // Thêm checkbox vào panel
		panel.add(BTsignUp);
		panel.add(label);

		frame.add(panel);
		frame.setVisible(true);

		if (isloggedIn) {
			System.out.println(username + " " + password);
			return user;
		}
		else 
			return null;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public void draw(Graphics g) {
		BufferedImage resizedLogo = null;
		try {
			resizedLogo = Helper.resizeImage(ImageIO.read(new File("res/img/LoginLogo.png")), 262, 60);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		g.drawImage(resizedLogo, 50, 100, null);
	}
}