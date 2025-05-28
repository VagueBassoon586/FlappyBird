package FLB;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class FlappyBird extends JPanel implements ActionListener,KeyListener{

	JButton startButton, restartButton, rankButton, menuButton, okButton, shareButton;
	
	private Image fastUp, fastMid, fastDown;
	private Image slowUp, slowMid, slowDown;
	Image backgroundImg, birdImg, topPipImg, bottomImg;

	private Database manager = new Database();

	private String username;
	private String password;

	private int best;

	private boolean New = false;
	private boolean isPaused = false;
	private JScrollPane leaderboardScrollPane;
	private boolean showLeaderboard = false;
	
	// Birds
	int birdX = 45; // Vị trí của con chim cánh mép trái cửa sổ 45px
	int birdY = 320; // Vị trí của con chim cánh mép trên cửa sổ 320px
	int birdWidth = 34;
	int birdHeight = 24;
	private Bird bird;

	// Vòng lặp game
	private Timer gameLoop;
	private int v_roi = 0;// Vận tốc rơi của chim
	private int p = 1; // Trọng lực
	
	/* Các pipes(ống) */
	// Vị trí ống xuất hiện
	int pipeX = 360; 
	// Tọa độ Y của ống trên (âm để đẩy ống lên trên)
	int pipeY; 
	//Chiều rộng của ống
	int pipeWidth = 64; 
	// Chiều cao của ống
	int pipeHeight = 512;
	// Vòng lặp ống 
	private Timer placePipesTimer;

	// Mảng chứa ống
	private ArrayList<Pipe> pipes;
	
	// Dừng lại game
	private Boolean gameOver = false;
	private Boolean isGameStarted = false;

	// Tính điểm
	private double score = 0; 
	
	/* Tăng thời gian ống chạy nhanh hơn */
	// Số ống đã vượt qua
	private int pipesPassed = 0; 

	// Tốc độ ban đầu của ống
	private int pipeSpeed = 7; 

	// Giới hạn tốc độ ống
	private int maxPipeSpeed = 20; 

	// Thời gian xuất hiện ống (ms)
	private int pipeInterval = 1500; 

	// Để nhớ lần cuối đã tăng tốc độ
	private int lastSpeedUpdate = 0; 

	class Bird{
		int x = birdX;
		int y = birdY;
		int width = birdWidth;
		int height = birdHeight;
		Image img;
		
		Bird (Image img) {
			this.img = img;  
		}
	}

	class Pipe{
		int x = pipeX;
		int y = pipeY;
		int width = pipeWidth;
		int height = pipeHeight;
		Image img;
		Boolean passed = false;// Đánh dấu chim đã qua 1 ống 
		
		Pipe (Image img) {
			this.img = img;
		}
	}

	public FlappyBird(String[] args) throws IOException {
		this.username = args[0];
		this.password = args[1];


		setPreferredSize(new Dimension(360, 640));
		// Tiếp nhận các sự kiện của phím
		setFocusable(true); 
		// Kiểm tra 3 hàm của keyList khi nhấn phím
		KeyListener();

		// Animation của Bird lúc chậm (vàng)
		slowDown = ImageIO.read(new File("res/img/bird1_yellow.png"));
		slowMid = ImageIO.read(new File("res/img/bird2_yellow.png"));
		slowUp = ImageIO.read(new File("res/img/bird3_yellow.png"));

		// Animation của Bird lúc nhanh (đỏ)
		fastDown = ImageIO.read(new File("res/img/bird1_red.png"));
		fastMid = ImageIO.read(new File("res/img/bird1_red.png"));
		fastUp = ImageIO.read(new File("res/img/bird1_red.png"));

		//Tải hình ảnh lên trên Frame
		backgroundImg = ImageIO.read(new File("res/img/flappybirdbg.png"));
		birdImg = ImageIO.read(new File("res/img/bird1_yellow.png"));
		topPipImg = ImageIO.read(new File("res/img/toppipe.png"));
		bottomImg = ImageIO.read(new File("res/img/bottompipe.png"));
		bird = new Bird(birdImg);

		// Thời gian ống xuất hiện
		pipes = new ArrayList<>(); // Tạo 1 mảng trống để chứa các ống
		placePipesTimer = new Timer(1500, (ActionEvent e) -> {
					placePipes();
				}); // phải gọi sự kiện để ống được thực hiện - 1,5s sẽ gọi ống 1 lần

		// Game timer
		gameLoop = new Timer(20, (ActionEvent e) -> {
					v_roi += p; // tăng vận tốc rơi
					bird.y += v_roi; //vị trí của chim sẽ rơi th
					// Thresshold điểm (quyết định số điểm tối thiểu để chuyển sang màu đỏ)
					if ((int)score < 20)
					{
						// Animation màu vàng
						if (v_roi < -5)
							bird.img = slowUp;
						else if (v_roi < 5)
							bird.img = slowMid;
						else
							bird.img = slowDown;
					}
					else
					{
						// Animation màu đỏ
						if (v_roi < -5)
							bird.img = fastUp;
						else if (v_roi < 5)
							bird.img = fastMid;
						else
							bird.img = fastDown;
					}
					// Di chuyển các ống sang trái
					for (int i = 0; i < pipes.size(); i++) {
						Pipe pipe = pipes.get(i);
						pipe.x -= pipeSpeed; // thời gian trôi của ống
						
						// Nếu chim đã bay qua ống và chưa được đánh dấu
						if (!pipe.passed && pipe.x + pipe.width < bird.x) {
							pipe.passed = true;
							score += 0.5;
							pipesPassed++;
							
							// Chỉ tăng tốc 1 lần mỗi khi đạt mốc mới
							if (pipesPassed != 0 && pipesPassed % 8 == 0 &&  pipesPassed != lastSpeedUpdate) { //% 8 là 2 cặp ống (4 ống) vì mỗi ống là 0,5 nên
								if (pipeSpeed < maxPipeSpeed) {
									pipeSpeed++; // Tăng tốc độ ống
									pipeInterval = Math.min(2000, pipeInterval + 150); // Giãn khoảng cách tối đa 2s
									placePipesTimer.setDelay(pipeInterval); // Cập nhật thời gian gọi ống
									System.out.println("Speed: " + pipeSpeed + " | Pipe distance: " + pipeInterval);
								}
								lastSpeedUpdate = pipesPassed;
							}
						}
						
						//Xử lí khi va chạm ống thì game dừng
						if (bird.x < pipe.x + pipe.width && bird.x + bird.width > pipe.x && bird.y < pipe.y + pipe.height && bird.y + bird.height > pipe.y) {
							gameOver = true;
						}
					}
					// Xoá ống đã đi qua khỏi màn hình
					pipes.removeIf(pipe -> pipe.x + pipe.width < 0);
					// Vẽ lại màn hình (gọi paintComponent)
					repaint();
					// Dừng game
					if (bird.y > 640 || bird.y < 0 ) {
						gameOver = true;
						System.out.println("Game over");
					}
					if (gameOver) {
						placePipesTimer.stop();
						gameLoop.stop();
						startButton.setVisible(false);
						restartButton.setVisible(true);
						menuButton.setVisible(true);
						okButton.setVisible(false);
						isGameStarted = false;
						// Cập nhật maxScore
						if ((int) score > best) {
							New = true;
							best = (int) score;
							manager.updateScore(username, password, best);
						}
					}
				});

		// Nút Start
		startButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/start.png")), 104, 58)));
		startButton.setBounds(51, 500, 104, 58);
		startButton.addActionListener((ActionEvent e) -> {
					startGame();
				});

		restartButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/restart.png")), 120, 40)));
		restartButton.setBounds(120, 500, 120, 40);
		restartButton.addActionListener((ActionEvent e) -> {
					startGame();
				});
		restartButton.setVisible(gameOver);
	
		// Nút Rank
		rankButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/rank.png")), 104, 58)));
		rankButton.setBounds(206, 500, 104, 58);
		rankButton.addActionListener((ActionEvent e) -> {
				showLeaderboard = !showLeaderboard;
				if (showLeaderboard) {
					try {
						rankButton.setIcon(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/ok.png")), 120, 40)));
					} catch (IOException e1) {
						e1.printStackTrace(System.err);
					}
					rankButton.setBounds(120, 500, 120, 40);
					
				}
				else if (!showLeaderboard) {
					try {
						rankButton.setIcon(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/rank.png")), 104, 58)));
					} catch (IOException e1) {
						e1.printStackTrace(System.err);
					}
					rankButton.setBounds(206, 500, 104, 58);
				}
				startButton.setVisible(!showLeaderboard);
				showLeaderboardTable();
				repaint();
				});

		// Nút Menu
		menuButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/menu.png")), 120, 40)));
		menuButton.setBounds(120, 560, 120, 40);
		menuButton.addActionListener((ActionEvent e) -> {
					isGameStarted = false;
					gameOver = false;
					startButton.setVisible(true);
					restartButton.setVisible(false);
					rankButton.setVisible(true);
					menuButton.setVisible(false);
					okButton.setVisible(false);
					shareButton.setVisible(true);
					birdY = 320;
					v_roi = 0;
					pipes.clear();
					score = 0;
					pipesPassed = 0;
					pipeSpeed = 7;
					lastSpeedUpdate = 0;
					bird = new Bird(birdImg);
					repaint();
				});
		menuButton.setVisible(gameOver || isPaused);

		// Nút Okay
		okButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/ok.png")), 120, 40)));
		okButton.setBounds(120, 440, 120, 40);
		okButton.addActionListener((ActionEvent e) -> {
					placePipesTimer.start();
					gameLoop.start();
					isPaused = false;
					restartButton.setVisible(false);
					menuButton.setVisible(false);
					okButton.setVisible(false);
					repaint();
				});
		okButton.setVisible(gameOver && isPaused);

		// Nút Share
		shareButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/share.png")), 120, 40)));
		shareButton.setBounds(120, 595, 120, 40);
		shareButton.addActionListener((ActionEvent e) -> {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection strSe = new StringSelection("github.com/VagueBassoon586/FlappyBird");
					clipboard.setContents(strSe, strSe);
					JOptionPane.showMessageDialog(new JFrame(), "Coppied source to clipboard!", "Notification", JOptionPane.INFORMATION_MESSAGE);
				});
		shareButton.setVisible(startButton.isVisible());

		// Bắt buộc để setBounds hoạt động
		setLayout(null);
		add(startButton);
		add(restartButton);
		add(rankButton);
		add(menuButton);
		add(okButton);
		add(shareButton);
	}

	private void KeyListener() {
		addKeyListener(this);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D G2D = (Graphics2D) g;

		G2D.drawImage(backgroundImg, 0, 0, 360, 640, null);

		if (!isGameStarted && !gameOver) {
		try {
			BufferedImage logo = Helper.resizeImage(ImageIO.read(new File("res/img/logo.png")), 153, 24);
			G2D.drawImage(logo, 5, 5, null);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		}
		// Xoay chim theo vận tốc

		/* Lấy góc của chim quay, v_roi * 3 để khiến cho góc quay dễ nhìn hơn */
		/* Góc cao nhất:-45° (hướng lên trên 45 độ) */
		/* Góc thấp nhất: +90° (hướng thẳng xuống dưới) */
		double angle = Math.toRadians(Math.max(-45, Math.min(90, v_roi * 3)));

		/* Lưu trạng thái đồ họa */
		AffineTransform old = G2D.getTransform();

		/* Di chuyển tâm của chim từ góc trái sang chính giữa con chim */
		/* Do rotate() xoay ảnh quanh tâm --> tâm phải ở chính giữa */
		G2D.translate(bird.x + bird.width / 2, bird.y + bird.height / 2);
		G2D.rotate(angle);

		/* Do thay đổi tâm, vị trí drawImage() cũng thay đổi, do đó phải vẽ lại chim theo tâm */
		G2D.drawImage(bird.img, -bird.width / 2, -bird.height / 2, bird.width, bird.height, null);

		/* Reset */
		G2D.setTransform(old);
		
		//vẽ các ống 
		for(Pipe pipe: pipes)
			G2D.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
		
		// Vẽ score (điểm)
		if (isGameStarted && !showLeaderboard)
		{
			// Khởi tạo font chữ
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 40f));

			// Đổ bóng
			G2D.setColor(Color.DARK_GRAY);
			G2D.drawString("" + (int) score, 182, 102);

			// Hiển thị điểm
			G2D.setColor(Color.WHITE);
			G2D.drawString("" + (int)score, 180, 100);
			
			//Thêm chữ user: "Name" trên góc trái. ĐỔ BÓNG
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 12, 22);
			
			//Thêm chữ usee: "Name: trên góc trái
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 10, 20);
		}

		try {
			if (!isPaused && !gameOver && isGameStarted) {
				Image stat = Helper.resizeImage(ImageIO.read(new File("res/img/ClickToStop.png")), 30, 30);
				G2D.drawImage(stat, 320, 10, null);
			}
			else if (isPaused && !gameOver && isGameStarted) {
				Image stat = Helper.resizeImage(ImageIO.read(new File("res/img/ClickToContinue.png")), 30, 30);
				G2D.drawImage(stat, 320, 10, null);
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}

		if (showLeaderboard) {
			BufferedImage leaderBoard = null;
			try {
				leaderBoard = Helper.resizeImage(ImageIO.read(new File("res/img/leaderboard.png")), 260, 260);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			G2D.drawImage(leaderBoard, 50, 150, null);
		}

		if (!isGameStarted && !gameOver && !showLeaderboard) {
			try {
				BufferedImage getReady = Helper.resizeImage(ImageIO.read(new File("res/img/getready.png")), 260, 60);
				G2D.drawImage(getReady, 50, 100, null);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	
		else if (!isGameStarted && gameOver && !showLeaderboard)
		{
			try {
				BufferedImage banner = Helper.resizeImage(ImageIO.read(new File("res/img/banner.png")), 300, 150);
				BufferedImage title = Helper.resizeImage(ImageIO.read(new File("res/img/gameover.png")), 260, 60);
				G2D.drawImage(banner, 30, 275, null);
				G2D.drawImage(title, 50, 100, null);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 18f));

			// Đổ bóng
			G2D.setColor(Color.DARK_GRAY);
			G2D.drawString("MEDAL", 69, 321);
			G2D.drawString("SCORE: " + (int) score, 201, 321);
			G2D.drawString("BEST:  " + (int) best, 201, 391);

			// Hiển thị banner điểm
			G2D.setColor(new Color(249, 121, 93));
			G2D.drawString("MEDAL", 68, 320);
			G2D.drawString("SCORE: " + (int) score, 200, 320);
			G2D.drawString("BEST:  " + (int) best, 200, 390);

			//Thêm chữ user: "Name" trên góc trái. ĐỔ BÓNG
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 12, 22);
			
			//Thêm chữ usee: "Name: trên góc trái
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 10, 20);
			
			// Nếu là kỉ lục
			if (New)
			{
				try {
					Image newRecord = Helper.resizeImage(ImageIO.read(new File("res/img/newScore.png")), 32, 16);
					G2D.drawImage(newRecord, 160, 375, null);
					New = false;
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}

			// Hiển thị Medal
			Image medal = null;
			try {
				if ((int) score < 10)
					medal = Helper.resizeImage(ImageIO.read(new File("res/img/Iron.png")), 60, 60);
				else if ((int) score < 30)
					medal = Helper.resizeImage(ImageIO.read(new File("res/img/Copper.png")), 60, 60);
				else if ((int) score < 60)
					medal = Helper.resizeImage(ImageIO.read(new File("res/img/Silver.png")), 60, 60);
				else
					medal = Helper.resizeImage(ImageIO.read(new File("res/img/Gold.png")), 60, 60);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			G2D.drawImage(medal, 64, 331, null);
		}
	}

	/** 
	 * 
	 * Hàm tạo ống
	 * 
	 */
	public void placePipes() {
		
		//???
		int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight/2));
		int SpaceOfPipes = 640 / 4;
		//Tạo ống trên
		Pipe topPipe = new Pipe(topPipImg);
		topPipe.y = randomPipeY;
		//Thêm ống vào mảng pipes
		pipes.add(topPipe);
		
		//Tạo ống dưới
		Pipe botPipe = new Pipe(bottomImg);
		botPipe.y = topPipe.y + SpaceOfPipes + pipeHeight;
		pipes.add(botPipe);
		
	}

	/** 
	 * 
	 * Hàm khởi động game
	 * 
	 */
	public void startGame() {
		if (isGameStarted = true);
		// Reset trạng thái game
		bird.y = birdY;
		v_roi = 0;
		pipes.clear();
		gameOver = false;
		score = 0;
		pipesPassed = 0;
		pipeSpeed = 4;
		pipeInterval = 1500;
		lastSpeedUpdate = 0;
		best = manager.getScore(username, password);
		showLeaderboard = false;

		// Reset Timer
		placePipesTimer.setDelay(pipeInterval);
		placePipesTimer.start();
		gameLoop.start();

		// Ẩn nút Start sau khi nhấn
		startButton.setVisible(false);
		restartButton.setVisible(false);
		rankButton.setVisible(false);
		menuButton.setVisible(false);
		okButton.setVisible(false);
		shareButton.setVisible(false);

		// Gọi repaint để cập nhật lại màn hình
		repaint();
	}

	public void showLeaderboardTable() {
		if (leaderboardScrollPane != null) {
			leaderboardScrollPane.setVisible(showLeaderboard && !isGameStarted);
			return;
		}

		// Dummy data
		String[] columns = {"Rank", "Username", "Score"};
		Object[][] data = null;
		try {
			data = manager.getLeaderBoard(username);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

		DefaultTableModel model = new DefaultTableModel(data, columns);
		JTable table = new JTable(model) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

				JLabel label = new JLabel();
				label.setOpaque(false);
				label.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14));
				label.setHorizontalAlignment(column == 2 ? SwingConstants.RIGHT :
											column == 1 ? SwingConstants.LEFT :
											SwingConstants.CENTER);

				// Custom shadow text rendering
				boolean isCurrentPlayer = false;
				String playerUsername = username;
				if (table.getValueAt(row, 1) != null) {
					String rowUsername = table.getValueAt(row, 1).toString();
					isCurrentPlayer = rowUsername.equalsIgnoreCase(playerUsername);
				}

				if (value instanceof Icon icon) {
					label.setIcon(icon);  // render the icon!
					label.setText("");
				} else {
					label.setText(String.valueOf(value));
				}

				if (isCurrentPlayer) {
					label.setForeground(new Color(0, 255, 100)); // green
				} else {
					label.setForeground(Color.WHITE);
				}

				return label;
			}
		};

		// Hide header
		table.setTableHeader(null);

		// Transparent background
		table.setOpaque(false);
		table.setBackground(new Color(0, 0, 0, 0));
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setForeground(Color.WHITE);
		table.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14));
		table.setRowHeight(30);
		table.setFocusable(false);
		table.setBorder(BorderFactory.createEmptyBorder());

		// === Column alignments ===
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Rank
		table.getColumnModel().getColumn(0).setPreferredWidth(60);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer); // Username

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Score

		// Create scroll pane and strip all visuals
		leaderboardScrollPane = new JScrollPane(table);
		leaderboardScrollPane.setBounds(101, 210, 160, 190);
		leaderboardScrollPane.setOpaque(false);
		leaderboardScrollPane.getViewport().setOpaque(false);
		leaderboardScrollPane.setBorder(BorderFactory.createEmptyBorder());
		leaderboardScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

		// Hide scrollbars but keep scrolling
		JScrollBar vScroll = leaderboardScrollPane.getVerticalScrollBar();
		vScroll.setPreferredSize(new Dimension(0, 0));
		vScroll.setOpaque(false);
		vScroll.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override protected void configureScrollBarColors() {
				this.thumbColor = new Color(0, 0, 0, 0);
				this.trackColor = new Color(0, 0, 0, 0);
			}

			@Override protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setBorder(BorderFactory.createEmptyBorder());
				button.setOpaque(false);
				return button;
			}
		});

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
		}

		leaderboardScrollPane.setVisible(showLeaderboard && !isGameStarted);
		// Final container styling
		setLayout(null);
		add(leaderboardScrollPane);
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (!isGameStarted && gameOver && e.getKeyCode() == KeyEvent.VK_R)
			startGame();
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			isPaused = !isPaused;
			if (isPaused && !gameOver && isGameStarted) {
				restartButton.setVisible(true);
				menuButton.setVisible(true);
				okButton.setVisible(true);
				placePipesTimer.stop();
				gameLoop.stop();
			}
			else if (!isPaused && !gameOver && isGameStarted) {
				restartButton.setVisible(false);
				menuButton.setVisible(false);
				okButton.setVisible(false);
				placePipesTimer.start();
				gameLoop.start();
			}
			repaint();
		}
		else if(isGameStarted && e.getKeyCode() == KeyEvent.VK_SPACE ) 
			v_roi = -9;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
}