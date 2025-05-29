package FLB;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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
	
	// Origins (0, 0) is at top left corner.
	// Birds initial position: (45, 320)
	int birdX = 45;
	int birdY = 320;
	// Birds size: 34 x 24
	int birdWidth = 34;
	int birdHeight = 24;
	private Bird bird;

	// Vòng lặp game
	private Timer gameLoop;
	private int vFall = 0;// Falling velocity
	private int g = 1; // Gravity
	
	/* Pipes */
	// Pipes initial position: (360, 0)
	int pipeX = 360; 
	int pipeY; 
	// Pipes dimension: 64 x 512
	int pipeWidth = 64; 
	int pipeHeight = 512;
	// Loop of pipes 
	private Timer placePipesTimer;

	// Array to hold pipes
	private ArrayList<Pipe> pipes;
	
	private Boolean gameOver = false;
	private Boolean isGameStarted = false;

	// Score
	private double score = 0; 
	
	private int pipesPassed = 0; 

	// Pipes initial speed
	private int pipeSpeed = 7; 

	// Pipes max speed
	private int maxPipeSpeed = 20; 

	// Interval of pipes (ms)
	private int pipeInterval = 1500; 
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
		Boolean passed = false; // Marks if bird have passed this pipes
		
		Pipe (Image img) 
		{
			this.img = img;
		}
	}

	public FlappyBird(String[] args) throws IOException 
	{
		this.username = args[0];
		this.password = args[1];


		setPreferredSize(new Dimension(360, 640));
		setFocusable(true); 
		KeyListener();

		// Bird's animation (when slow)
		slowDown = ImageIO.read(new File("res/img/bird1_yellow.png"));
		slowMid = ImageIO.read(new File("res/img/bird2_yellow.png"));
		slowUp = ImageIO.read(new File("res/img/bird3_yellow.png"));

		// Bird's animation (when fast)
		fastDown = ImageIO.read(new File("res/img/bird1_red.png"));
		fastMid = ImageIO.read(new File("res/img/bird2_red.png"));
		fastUp = ImageIO.read(new File("res/img/bird3_red.png"));

		// Load images to frame
		backgroundImg = ImageIO.read(new File("res/img/flappybirdbg.png"));
		birdImg = ImageIO.read(new File("res/img/bird1_yellow.png"));
		topPipImg = ImageIO.read(new File("res/img/toppipe.png"));
		bottomImg = ImageIO.read(new File("res/img/bottompipe.png"));
		bird = new Bird(birdImg);

		// Time when pipes start appearing
		pipes = new ArrayList<>(); // Empty array to hold pipes
		placePipesTimer = new Timer(1500, (ActionEvent e) -> {
					placePipes();
				}); // Pipes appear each 1.5s (1500 ms)

		// Game timer
		gameLoop = new Timer(20, (ActionEvent e) -> {
					vFall += g; // Increase falling velocity
					bird.y += vFall; // Falling in y coordinates
					// Point thresshol (switch from small to fast animation)
					if ((int) score < 20)
					{
						// Slow animation: yellow bird
						if (vFall < -5)
							bird.img = slowUp;
						else if (vFall < 5)
							bird.img = slowMid;
						else
							bird.img = slowDown;
					}
					else
					{
						// Fast animation: red bird
						if (vFall < -5)
							bird.img = fastUp;
						else if (vFall < 5)
							bird.img = fastMid;
						else
							bird.img = fastDown;
					}
					// Move pipes to the left
					for (int i = 0; i < pipes.size(); i++) 
					{
						Pipe pipe = pipes.get(i);
						pipe.x -= pipeSpeed; // Pipe speed
						
						// If bird fly pass an unmark pipe
						if (!pipe.passed && pipe.x + pipe.width < bird.x) 
						{
							pipe.passed = true;
							score += 0.5;
							pipesPassed++;
							
							// Only increase the speed once after passing 4 pipes
							if (pipesPassed != 0 && pipesPassed % 8 == 0 &&  pipesPassed != lastSpeedUpdate) // %8 is 2 pair of pipes (4 pipes) since each pipe is 0.5
							{
								if (pipeSpeed < maxPipeSpeed) 
								{
									pipeSpeed++;
									pipeInterval = Math.min(2000, pipeInterval + 150);
									placePipesTimer.setDelay(pipeInterval);
								}
								lastSpeedUpdate = pipesPassed;
							}
						}
						
						// Handle bird - pipes collision
						if (bird.x < pipe.x + pipe.width && bird.x + bird.width > pipe.x && bird.y < pipe.y + pipe.height && bird.y + bird.height > pipe.y)
							gameOver = true;
					}

					// Remove any pipe that went pass the game window
					pipes.removeIf(pipe -> pipe.x + pipe.width < 0);
					repaint();
					// Handle event when bird is too high or fall out of the window
					if (bird.y > 640 || bird.y < 0 )
						gameOver = true;
					if (gameOver) 
					{
						placePipesTimer.stop();
						gameLoop.stop();
						startButton.setVisible(false);
						restartButton.setVisible(true);
						menuButton.setVisible(true);
						okButton.setVisible(false);
						isGameStarted = false;
						// Cập nhật maxScore
						if ((int) score > best) 
						{
							New = true;
							best = (int) score;
							leaderboardScrollPane = null;
							manager.updateScore(username, password, best);
						}
					}
				});

		// Start button
		startButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/start.png")), 104, 58)));
		startButton.setBounds(51, 500, 104, 58);
		startButton.addActionListener((ActionEvent e) -> {
					startGame();
				});

		// Restart button
		restartButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/restart.png")), 120, 40)));
		restartButton.setBounds(120, 500, 120, 40);
		restartButton.addActionListener((ActionEvent e) -> {
					startGame();
				});
		restartButton.setVisible(gameOver);
	
		// Rank Button
		rankButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/rank.png")), 104, 58)));
		rankButton.setBounds(206, 500, 104, 58);
		rankButton.addActionListener((ActionEvent e) -> {
				showLeaderboard = !showLeaderboard;
				if (showLeaderboard) {
					try 
					{
						rankButton.setIcon(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/ok.png")), 120, 40)));
					} 
					catch (IOException e1) 
					{
						e1.printStackTrace(System.err);
					}
					rankButton.setBounds(120, 500, 120, 40);
					
				}
				else if (!showLeaderboard) 
				{
					try 
					{
						rankButton.setIcon(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/rank.png")), 104, 58)));
					} 
					catch (IOException e1) 
					{
						e1.printStackTrace(System.err);
					}
					rankButton.setBounds(206, 500, 104, 58);
				}
				startButton.setVisible(!showLeaderboard);
				shareButton.setVisible(!showLeaderboard);
				showLeaderboardTable();
				repaint();
				});

		// Menu button
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
					vFall = 0;
					pipes.clear();
					score = 0;
					pipesPassed = 0;
					pipeSpeed = 7;
					lastSpeedUpdate = 0;
					bird = new Bird(birdImg);
					repaint();
				});
		menuButton.setVisible(gameOver || isPaused);

		// OK button
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

		// Share button
		shareButton = new JButton(new ImageIcon(Helper.resizeImage(ImageIO.read(new File("res/img/share.png")), 120, 40)));
		shareButton.setBounds(120, 595, 120, 40);
		shareButton.addActionListener((ActionEvent e) -> {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection strSe = new StringSelection("github.com/VagueBassoon586/FlappyBird");
					clipboard.setContents(strSe, strSe);
					JOptionPane.showMessageDialog(new JFrame(), "Coppied source to clipboard!", "Notification", JOptionPane.INFORMATION_MESSAGE);
				});
		shareButton.setVisible(startButton.isVisible());

		// Mandatory for .setBounds() to work properly
		setLayout(null);
		add(startButton);
		add(restartButton);
		add(rankButton);
		add(menuButton);
		add(okButton);
		add(shareButton);
	}

	private void KeyListener() 
	{
		addKeyListener(this);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D G2D = (Graphics2D) g;

		G2D.drawImage(backgroundImg, 0, 0, 360, 640, null);

		if (!isGameStarted && !gameOver) 
		{
		try 
		{
			BufferedImage logo = Helper.resizeImage(ImageIO.read(new File("res/img/logo.png")), 153, 24);
			G2D.drawImage(logo, 5, 5, null);
		} 
		catch (IOException e) 
		{
			e.printStackTrace(System.err);
		}
		}
		/* Rotate bird base on speed */

		// Retrive bird's angle of rotation, 3 * vFall to make the rotation more noticable
		// Góc cao nhất:-45° (hướng lên trên 45 độ)
		// Góc thấp nhất: +90° (hướng thẳng xuống dưới)
		double angle = Math.toRadians(Math.max(-45, Math.min(90, 3 * vFall)));

		// Save graphics status
		AffineTransform old = G2D.getTransform();

		// Move bird's origin from its left corner to the middle
		// Since .rotate() use centre of origin to rotate --> origin must be in the middle.
		G2D.translate(bird.x + bird.width / 2, bird.y + bird.height / 2);
		G2D.rotate(angle);

		// Since the origin was changed, .drawImage()'s drawing position changed --> redraw bird at top left corner
		G2D.drawImage(bird.img, -bird.width / 2, -bird.height / 2, bird.width, bird.height, null);

		// Reset
		G2D.setTransform(old);
		
		// Draw pipes
		for(Pipe pipe: pipes)
			G2D.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
	
		if (isGameStarted && !showLeaderboard)
		{
			// Initialize font
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 40f));

			// Score's Shadow
			G2D.setColor(Color.DARK_GRAY);
			G2D.drawString("" + (int) score, 182, 102);

			// Draw score
			G2D.setColor(Color.WHITE);
			G2D.drawString("" + (int)score, 180, 100);
			
			// Username's shadow
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 11, 21);
			
			// Draw username
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 10, 20);
		}

		// Show current game status: Paused or playing
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

		// Show leaderboard
		if (showLeaderboard) {
			BufferedImage leaderBoard = null;
			try {
				leaderBoard = Helper.resizeImage(ImageIO.read(new File("res/img/leaderboard.png")), 350, 308);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			G2D.drawImage(leaderBoard, 5, 150, null);
		}

		// Show menu's title: Get Ready
		if (!isGameStarted && !gameOver && !showLeaderboard) {
			try {
				BufferedImage getReady = Helper.resizeImage(ImageIO.read(new File("res/img/getready.png")), 260, 60);
				G2D.drawImage(getReady, 50, 100, null);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		
		// Show score on banner and gameover title
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

			// Shadow
			G2D.setColor(Color.DARK_GRAY);
			G2D.drawString("MEDAL", 69, 321);
			G2D.drawString("SCORE: " + (int) score, 201, 321);
			G2D.drawString("BEST:  " + (int) best, 201, 391);

			// Score banner
			G2D.setColor(new Color(249, 121, 93));
			G2D.drawString("MEDAL", 68, 320);
			G2D.drawString("SCORE: " + (int) score, 200, 320);
			G2D.drawString("BEST:  " + (int) best, 200, 390);

			// Shadow
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 11, 21);
			
			// Draw username
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 10, 20);
			
			// If the score is the user's highest score
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

			// Show medals
			Image medal = null;
			try {
				if ((int) score < 10)
					medal = Helper.resizeImage(ImageIO.read(new File("res/img/Iron.png")), 60, 60);
				else if ((int) score < 30)
					medal = Helper.resizeImage(ImageIO.read(new File("res/img/Bronze.png")), 60, 60);
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
		vFall = 0;
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

	// 95% credit of this showLeaderboardTable() goes to ChatGPT 4o:
	public void showLeaderboardTable() {
		if (leaderboardScrollPane != null) {
			leaderboardScrollPane.setVisible(showLeaderboard && !isGameStarted);
			return;
		}

		String[] columns = {"Rank", "Username", "Score"};
		Object[][] data = null;
		try {
			// Extract list of players with highest score (max 100 players)
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

					JLabel label = new JLabel() {
						@Override
						protected void paintComponent(Graphics g) {
							Graphics2D g2 = (Graphics2D) g.create();
							g2.setFont(getFont());
							g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

							String text = getText();
							Icon icon = getIcon();

							if (!(column == 0 && row >= 0 && row <= 3 && icon != null) && text != null && !text.isEmpty()) {
								FontMetrics fm = g2.getFontMetrics();
								int textWidth = fm.stringWidth(text);
								int textHeight = fm.getAscent();

								// Calculate X based on alignment
								int x = switch (getHorizontalAlignment()) {
									case SwingConstants.LEFT -> 0;
									case SwingConstants.CENTER -> (getWidth() - textWidth) / 2;
									case SwingConstants.RIGHT -> getWidth() - textWidth;
									default -> 0;
								};

								int y = (getHeight() + textHeight) / 2 - 2; // Vertically center

								g2.setColor(Color.GRAY); // Shadow color
								g2.drawString(text, x + 1, y + 1); // Shadow (1px offset)
							}

							super.paintComponent(g); // Draw the actual label text
							g2.dispose();
						}
					};

				label.setOpaque(false);
				label.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 16));
				label.setHorizontalAlignment(column == 2 ? SwingConstants.RIGHT :
											column == 1 ? SwingConstants.LEFT :
											SwingConstants.CENTER);

				String rowUsername = table.getValueAt(row, 1).toString();
				boolean isCurrentPlayer = rowUsername.equalsIgnoreCase(username);

				if (value instanceof Icon icon) {
					label.setIcon(icon);
					label.setText("");
				} else {
					label.setText(String.valueOf(value));
				}

				label.setForeground(isCurrentPlayer ? new Color(90, 170, 255) : Color.WHITE);

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
		table.setFont(Helper.loadCustomFont("res/font/flappy-font.ttf", 12));
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
		leaderboardScrollPane.setBounds(70, 245, 200, 175);
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
			vFall = -9;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}