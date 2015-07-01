import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JFrame {

	private static final long serialVersionUID = 69L;

	public Main() {
		super("Insanity");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(new Custom(), BorderLayout.CENTER);
		pack();
		setSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(500, 500));
		setMinimumSize(new Dimension(500, 500));
		setMaximumSize(new Dimension(500, 500));
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}

class Custom extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	public Random random = new Random();

	int xpos = 220;
	int ypos = 220;
	int yOffset = 0;
	int xOffset = 0;
	int timer = 0;
	int initMobs = 5;
	int score = 0;
	int hp = 5;
	int hpBonus = 3;

	boolean hurtCooldown = false;
	boolean upgradeCooldown = false;
	boolean upgradeAvailable = false;

	double angle = 0;

	Color playerColor = Color.WHITE;

	List<Mob> mobList = new ArrayList<Mob>();
	Upgrade hpBlock = new Upgrade(0, 0, 5, hpBonus, 4, -1);

	Font font = new Font("Arial", Font.BOLD, 60);
	Font font2 = new Font("Calibri", Font.PLAIN, 20);
	boolean up = false, down = false, left = false, right = false;

	public Custom() {
		setBackground(Color.BLACK);
		addKeyListener(this);
		for (int i = 0; i < initMobs; i++)
			mobList.add(new Mob(0, random.nextInt(500), new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))));
		Timer t = new Timer(1000 / 60, this);
		t.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(xpos - 3, ypos - 3, 26, 26);
		g.setColor(playerColor);
		g.fillRect(xpos, ypos, 20, 20);
		if (upgradeCooldown)
			setBackground(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString("" + score, 250 - String.valueOf(score).length() * 20, 50);
		g.setFont(font2);
		g.drawString("HP: " + hp, 10, 460);
		for (int i = 0; i < initMobs; i++) {
			g.setColor(mobList.get(i).col);
			g.fillOval(mobList.get(i).x, mobList.get(i).y, mobList.get(i).diameter, mobList.get(i).diameter);
		}
		if (upgradeCooldown)
			g.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
		if (upgradeAvailable) {
			g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
			g.fillRect(hpBlock.x, hpBlock.y, 40, 40);
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			up = true;
		if (e.getKeyCode() == KeyEvent.VK_S)
			down = true;
		if (e.getKeyCode() == KeyEvent.VK_A)
			left = true;
		if (e.getKeyCode() == KeyEvent.VK_D)
			right = true;
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			up = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			down = false;
		if (e.getKeyCode() == KeyEvent.VK_A)
			left = false;
		if (e.getKeyCode() == KeyEvent.VK_D)
			right = false;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		tick();
	}

	public void tick() {
		if (hp <= 0)
			System.exit(0);
		if (up)
			yOffset -= 5;
		if (down)
			yOffset += 5;
		if (left)
			xOffset -= 5;
		if (right)
			xOffset += 5;
		angle += Math.toRadians(50 / 3);
		int xxpos = (int) Math.round(Math.cos(angle) * Math.PI * 3 + 220 + xOffset);
		int yypos = (int) Math.round(Math.sin(angle) * Math.PI * 3 + 220 + yOffset);
		if (xxpos < getWidth() && xxpos > 0)
			xpos = xxpos;
		if (yypos < getHeight() && yypos > 0)
			ypos = yypos;
		if (timer++ % 60 == 0)
			score++;
		if (timer % (60 * 15) == 0) {
			upgradeAvailable = true;
			if (upgradeAvailable && random.nextBoolean() == true)
				upgradeAvailable = false;
			hpBlock.time--;
		}
		if (timer % (60 * 2) == 0) {
			playerColor = Color.white;
			hurtCooldown = false;
			upgradeCooldown = false;
		}
		if (timer % (60 * 5) == 0) {
			initMobs++;
			mobList.add(new Mob(0, random.nextInt(500), new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))));
		}
		for (int i = 0; i < initMobs; i++)
			move(mobList.get(i).x, mobList.get(i).y, mobList.get(i).xm, mobList.get(i).ym, i);

		move(hpBlock.x, hpBlock.y, hpBlock.mx, hpBlock.my);

		repaint();
		requestFocus();
	}

	private void move(int x, int y, int xx, int yy, int i) {
		int posx = x + xx;
		int posy = y + yy;
		if (posx < 0 || posx > getWidth())
			mobList.get(i).xm *= -1;
		if (posy < 0 || posy > getHeight())
			mobList.get(i).ym *= -1;
		if (posx <= xpos + mobList.get(i).diameter + 5 && posx >= xpos - 5
				&& posy <= ypos + mobList.get(i).diameter + 5 && posy >= ypos - 5 && !hurtCooldown) {
			if (!upgradeCooldown) {
				hurtCooldown = true;
				mobList.get(i).xm *= -1;
				playerColor = Color.red;
				hp--;
			}
			setBackground(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		}
		mobList.get(i).x += mobList.get(i).xm;
		mobList.get(i).y += mobList.get(i).ym;
	}

	private void move(int x, int y, int xx, int yy) {
		int posx = x + xx;
		int posy = y + yy;
		if (posx < 0 || posx > getWidth())
			hpBlock.mx *= -1;
		if (posy < 0 || posy > getHeight())
			hpBlock.my *= -1;
		if (posx <= xpos + 40 && posx >= xpos - 10 && posy <= ypos + 40 && posy >= ypos - 10 && !upgradeCooldown
				&& upgradeAvailable) {
			hp += hpBlock.pointValue;
			upgradeAvailable = false;
			upgradeCooldown = true;
			playerColor = Color.GREEN;
		}
		hpBlock.x += hpBlock.mx;
		hpBlock.y += hpBlock.my;
	}

	class Mob {
		int x;
		int y;
		Color col;
		boolean dead;
		int ym;
		int xm;
		int diameter;

		public Mob(int x, int y, Color col) {
			this.x = x;
			this.y = y;
			this.col = col;
			dead = false;
			ym = 2 + random.nextInt(3);
			xm = 2 + random.nextInt(3);
			diameter = 20 + random.nextInt(30);

		}
	}

	class Upgrade {
		int pointValue;
		int time;
		int x;
		int y;
		int mx;
		int my;
		public Upgrade(int x, int y, int points, int t, int mx, int my) {
			this.x = x;
			this.y = y;
			this.pointValue = points;
			this.time = t;
			this.mx = new Random().nextInt(3);
			this.my = new Random().nextInt(3);
		}
	}
}
