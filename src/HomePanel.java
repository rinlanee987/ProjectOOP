import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * หน้าแรกของเกม สืบทอดจาก JPanel มีปุ่ม START และ QUIT
 */
public class HomePanel extends JPanel {
    public JButton bStart = new JButton("START");
    public JButton bQuit = new JButton("QUIT");

    private Level scene = Level.createAll()[0];
    private PlayerFish logoFish = new PlayerFish(195, 205);

    public HomePanel() {
        setPreferredSize(new Dimension(1000, 700));
        setLayout(null);
        logoFish.setTier(3);

        bStart.setBounds(400, 400, 200, 60);
        bQuit.setBounds(400, 480, 200, 60);
        styleButton(bStart, new Color(30, 110, 210));
        styleButton(bQuit, new Color(220, 60, 70));
        add(bStart);
        add(bQuit);
    }

    /** ตั้งสี ตัวอักษร ให้ปุ่ม */
    private void styleButton(JButton b, Color color) {
        b.setFont(new Font("SansSerif", Font.BOLD, 26));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        scene.draw(g);

        // ชื่อเกม (เงา + ตัวอักษรสีขาว)
        g.setFont(new Font("SansSerif", Font.BOLD, 110));
        g.setColor(new Color(10, 60, 140));
        g.drawString("Tiny Bite", 296, 246);
        g.setColor(Color.WHITE);
        g.drawString("Tiny Bite", 290, 240);

        // ป้ายชื่อรอง
        g.setColor(new Color(150, 80, 30));
        g.fillRoundRect(390, 262, 340, 54, 20, 20);
        g.setColor(new Color(255, 200, 60));
        g.setFont(new Font("SansSerif", Font.BOLD, 34));
        g.drawString("Fish Eat Fish", 450, 302);

        logoFish.draw(g);

        // วิธีเล่นย่อ
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.drawString("Move the mouse to swim. Eat smaller fish, avoid bigger ones!", 245, 600);
    }
}
