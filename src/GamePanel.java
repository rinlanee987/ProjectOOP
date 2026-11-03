import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GamePanel extends JPanel implements ActionListener {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;

    private Level[] levels = Level.createAll();
    private PlayerFish player = new PlayerFish(WIDTH / 2, HEIGHT / 2);
    private ArrayList<EnemyFish> enemies = new ArrayList<EnemyFish>();
    private ArrayList<PopupText> popups = new ArrayList<PopupText>();
    private Object lock = new Object();   // กันการแก้ ArrayList พร้อมกันจากหลาย Thread

    private PlayerFish lifeIcon = new PlayerFish(WIDTH - 108, 34);   // รูปปลาเล็กในช่องโควต้า
    public JButton bPause = new JButton("II");
    private GameListener listener;

    private Level level;
    private int levelIndex;
    private int levelScore;       // คะแนนในด่านปัจจุบัน (ใช้เทียบเป้าหมาย)
    private int totalScore;       // คะแนนรวมทุกด่าน
    private int eatenCount;       // จำนวนครั้งที่โดนกินในด่านนี้
    private int seconds;
    private long levelClearUntil = 0;

    private boolean running = false;
    private boolean paused = false;

    // ---------------- Threads ----------------
    Thread gameLoop = new Thread(new Runnable() {
        public void run() {
            while (true) {
                if (running && !paused) {
                    update();
                }
                repaint();
                try {
                    Thread.sleep(16);   // ประมาณ 60 เฟรมต่อวินาที
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    Thread spawner = new Thread(new Runnable() {
        public void run() {
            while (true) {
                try {
                    int delay = 1000;
                    if (level != null) delay = level.getSpawnDelay();
                    Thread.sleep((long) (delay * (0.5 + Math.random())));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (running && !paused && levelClearUntil == 0) {
                    synchronized (lock) {
                        if (enemies.size() < 14) {
                            enemies.add(level.createEnemy(WIDTH, HEIGHT, player));
                        }
                    }
                }
            }
        }
    });

    Thread clock = new Thread(new Runnable() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (running && !paused) seconds++;
            }
        }
    });

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setFocusable(true);

        bPause.setBounds(WIDTH - 70, 62, 52, 46);
        bPause.setFont(new Font("SansSerif", Font.BOLD, 16));
        bPause.setBackground(new Color(30, 120, 200));
        bPause.setForeground(Color.WHITE);
        bPause.setFocusPainted(false);
        bPause.setFocusable(false);
        bPause.addActionListener(this);
        add(bPause);

        // ควบคุมปลาด้วยเมาส์ (anonymous class)
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                player.setTarget(e.getX(), e.getY());
            }
            public void mouseDragged(MouseEvent e) {
                player.setTarget(e.getX(), e.getY());
            }
        });

        // ปุ่ม P หรือ ESC เพื่อหยุดเกมชั่วคราว (inner class)
        addKeyListener(new PauseKeyHandler());

        gameLoop.start();
        spawner.start();
        clock.start();
    }

    /** inner class สำหรับรับการกดคีย์บอร์ด */
    private class PauseKeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                togglePause();
            }
        }
    }

    public void setGameListener(GameListener listener) {
        this.listener = listener;
    }

    /** เริ่มเกมใหม่ตั้งแต่ด่าน 1 */
    public void startNewGame() {
        totalScore = 0;
        seconds = 0;
        paused = false;
        bPause.setText("II");
        loadLevel(0);
        running = true;
        requestFocusInWindow();
    }

    private void loadLevel(int index) {
        synchronized (lock) {
            levelIndex = index;
            level = levels[index];
            levelScore = 0;
            eatenCount = 0;
            levelClearUntil = 0;
            enemies.clear();
            popups.clear();
            player.setTier(1);
            player.respawn(WIDTH / 2, HEIGHT / 2);
            player.makeInvincible(1500);
        }
    }

    public void togglePause() {
        if (running) {
            paused = !paused;
            if (paused) {
                bPause.setText(">");
            } else {
                bPause.setText("II");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bPause) {
            togglePause();
            requestFocusInWindow();
        }
    }

    // ---------------- Game logic ----------------
    private void update() {

        // ช่วงแสดงข้อความผ่านด่าน
        if (levelClearUntil > 0) {
            if (System.currentTimeMillis() > levelClearUntil) {
                if (levelIndex + 1 < levels.length) {
                    loadLevel(levelIndex + 1);
                } else {
                    endGame(true);
                }
            }
            return;
        }

        player.move();
        keepPlayerInside();

        synchronized (lock) {
            Iterator<EnemyFish> it = enemies.iterator();
            while (it.hasNext()) {
                EnemyFish enemy = it.next();
                enemy.move();
                if (enemy.isOffScreen(WIDTH)) {
                    it.remove();
                    continue;
                }
                if (Intersect(player, enemy)) {
                    if (enemy.getTier() <= player.getTier()) {
                        // ศัตรูเล็กกว่าหรือเท่ากัน -> กินได้
                        it.remove();
                        eat(enemy);
                    } else if (!player.isInvincible()) {
                        // ศัตรูใหญ่กว่า -> โดนกิน
                        playerEaten();
                        if (!running) return;
                    }
                }
            }

            Iterator<PopupText> pit = popups.iterator();
            while (pit.hasNext()) {
                PopupText p = pit.next();
                p.update();
                if (p.isDead()) pit.remove();
            }
        }

        if (levelScore >= level.getTargetScore()) {
            synchronized (lock) {
                enemies.clear();
            }
            levelClearUntil = System.currentTimeMillis() + 2500;
        }
    }

    /** ตรวจการชนจากกรอบสี่เหลี่ยมของปลา 2 ตัว */
    public boolean Intersect(Fish a, Fish b) {
        return a.getBound().intersects(b.getBound());
    }

    private void eat(EnemyFish enemy) {
        levelScore += enemy.getPoints();
        totalScore += enemy.getPoints();
        popups.add(new PopupText("+" + enemy.getPoints(), enemy.getX(), enemy.getY(), Color.YELLOW));
        updatePlayerSize();
    }

    private void playerEaten() {
        eatenCount++;
        int penalty = Math.min(20, levelScore);
        levelScore -= penalty;
        totalScore -= penalty;
        popups.add(new PopupText("-" + penalty, player.getX(), player.getY(), Color.RED));
        updatePlayerSize();

        if (eatenCount >= level.getLifeQuota()) {
            endGame(false);
            return;
        }
        player.respawn(WIDTH / 2, 140);
        player.makeInvincible(2000);
    }

    /** ปรับขนาดผู้เล่นตามคะแนนในด่าน */
    private void updatePlayerSize() {
        int tier = 1;
        if (levelScore >= level.getLargeScore()) {
            tier = 3;
        } else if (levelScore >= level.getMediumScore()) {
            tier = 2;
        }
        if (tier > player.getTier()) {
            popups.add(new PopupText("GROW UP!", player.getX() - 50, player.getY() - 40, Color.GREEN));
        }
        player.setTier(tier);
    }

    private void keepPlayerInside() {
        if (player.x < player.width / 2) player.x = player.width / 2;
        if (player.x > WIDTH - player.width / 2) player.x = WIDTH - player.width / 2;
        if (player.y < player.height / 2 + 90) player.y = player.height / 2 + 90;
        if (player.y > HEIGHT - player.height / 2) player.y = HEIGHT - player.height / 2;
    }

    private void endGame(final boolean win) {
        running = false;
        final int score = totalScore;
        final int reached = levelIndex + 1;
        final int time = seconds;          // เวลาที่ใช้ทั้งหมด (วินาที)
        if (listener != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    listener.onGameEnd(win, score, reached, time);
                }
            });
        }
    }

    // ---------------- Drawing ----------------
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (level == null) return;

        // ฉากหลังของด่าน
        level.draw(g);

        // ปลาและข้อความลอย
        synchronized (lock) {
            for (EnemyFish enemy : enemies) {
                enemy.draw(g);       // Polymorphism: แต่ละคลาสลูกวาดตัวเองต่างกัน
            }
            player.draw(g);
            for (PopupText p : popups) {
                p.draw(g);
            }
        }

        // ---------- HUD ----------
        Color box = new Color(10, 40, 90, 170);

        // กล่องคะแนน
        g.setColor(box);
        g.fillRoundRect(16, 14, 290, 40, 40, 40);
        g.setColor(Color.ORANGE);
        g.fillOval(26, 22, 24, 24);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("Score : " + levelScore + " / " + level.getTargetScore(), 60, 41);

        // ชื่อด่านและเวลา
        String time = (seconds / 60) + ":" + (seconds % 60 < 10 ? "0" : "") + (seconds % 60);
        g.setColor(box);
        g.fillRoundRect(340, 14, 320, 40, 40, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString(level.getName() + "   " + time, 358, 40);

        // โควต้าการโดนกินที่เหลือ
        int lives = level.getLifeQuota() - eatenCount;
        g.setColor(box);
        g.fillRoundRect(WIDTH - 140, 14, 124, 40, 40, 40);
        lifeIcon.draw(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("x " + lives, WIDTH - 72, 41);

        // แถบการเติบโต
        int barW = 290;
        int fill = barW * Math.min(levelScore, level.getTargetScore()) / level.getTargetScore();
        int mx = 16 + barW * level.getMediumScore() / level.getTargetScore();
        int lx = 16 + barW * level.getLargeScore() / level.getTargetScore();
        g.setColor(box);
        g.fillRoundRect(16, 62, barW, 18, 18, 18);
        g.setColor(new Color(90, 220, 140));
        g.fillRoundRect(16, 62, fill, 18, 18, 18);
        g.setColor(Color.WHITE);
        g.drawLine(mx, 60, mx, 82);
        g.drawLine(lx, 60, lx, 82);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("SIZE " + player.getTier(), 22, 76);

        // ---------- ข้อความกลางจอ ----------
        String title = null;
        int titleX = 0;
        if (levelClearUntil > 0) {
            if (levelIndex + 1 < levels.length) {
                title = "LEVEL " + level.getNumber() + " CLEAR!";
                titleX = 240;
            } else {
                title = "YOU WIN!";
                titleX = 330;
            }
        } else if (paused) {
            title = "PAUSED";
            titleX = 350;
        }
        if (title != null) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setFont(new Font("SansSerif", Font.BOLD, 72));
            g.setColor(new Color(10, 50, 110));
            g.drawString(title, titleX + 4, HEIGHT / 2 + 4);
            g.setColor(Color.WHITE);
            g.drawString(title, titleX, HEIGHT / 2);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            g.drawString("Total score : " + totalScore, 405, HEIGHT / 2 + 50);
        }
    }
}
