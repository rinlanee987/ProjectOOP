import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * ข้อความลอยขึ้นชั่วคราว เช่น "+10" เมื่อกินปลา หรือ "-20" เมื่อโดนกิน
 */
public class PopupText {
    private String text;
    private Color color;
    private double x;
    private double y;
    private int life = 45;   // จำนวนเฟรมที่จะแสดง

    public PopupText(String text, double x, double y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void update() {
        y -= 1.2;
        life--;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void draw(Graphics g) {
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(Color.BLACK);
        g.drawString(text, (int) x + 2, (int) y + 2);   // เงา
        g.setColor(color);
        g.drawString(text, (int) x, (int) y);
    }
}
