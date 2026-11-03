import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * หน้าจอจบเกม (แพ้หรือชนะ) สืบทอดจาก JPanel มีปุ่ม REPLAY และ QUIT
 */
public class GameOverPanel extends JPanel {
    public JButton bReplay = new JButton("REPLAY");
    public JButton bQuit = new JButton("QUIT");

    private Level scene = Level.createAll()[1];
    private LargeFish shark = new LargeFish(1000, 700, new PlayerFish(0, 0));
    private PlayerFish hero = new PlayerFish(500, 280);

    private boolean win;
    private int score;
    private int level;
    private int seconds;      // เวลาที่ใช้เล่น

    public GameOverPanel() {
        setPreferredSize(new Dimension(1000, 700));
        setLayout(null);

        // ปลาประดับกลางจอ
        shark.setPosition(500, 280);
        shark.setSize(190, 108);
        hero.setTier(3);

        bReplay.setBounds(400, 430, 200, 60);
        bQuit.setBounds(400, 510, 200, 60);
        styleButton(bReplay, new Color(220, 60, 70));
        styleButton(bQuit, new Color(30, 110, 210));
        add(bReplay);
        add(bQuit);
    }

    private void styleButton(JButton b, Color color) {
        b.setFont(new Font("SansSerif", Font.BOLD, 26));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    /** ตั้งค่าผลลัพธ์ก่อนแสดงหน้าจอ */
    public void setResult(boolean win, int score, int level, int seconds) {
        this.win = win;
        this.score = score;
        this.level = level;
        this.seconds = seconds;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        scene.draw(g);

        g.setFont(new Font("SansSerif", Font.BOLD, 90));
        if (win) {
            g.setColor(new Color(10, 50, 120));
            g.drawString("YOU WIN!", 296, 166);
            g.setColor(Color.WHITE);
            g.drawString("YOU WIN!", 290, 160);
            hero.draw(g);
        } else {
            g.setColor(new Color(10, 50, 120));
            g.drawString("GAME OVER", 236, 166);
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", 230, 160);
            shark.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        String time = (seconds / 60) + ":" + (seconds % 60 < 10 ? "0" : "") + (seconds % 60);
        g.drawString("SCORE  " + score + "     LEVEL  " + level + "     TIME  " + time, 235, 395);
    }
}
