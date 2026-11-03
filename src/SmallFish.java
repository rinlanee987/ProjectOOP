import java.awt.Color;

/**
 * ศัตรูขนาดเล็ก (ปลาการ์ตูนสีส้ม) ว่ายแบบส่ายขึ้นลง
 */
public class SmallFish extends EnemyFish {
    private double baseY;
    private double wave = Math.random() * Math.PI * 2;

    public SmallFish(int panelWidth, int panelHeight) {
        super(panelWidth, panelHeight, 38, 26, 1.6 + Math.random() * 1.4, 10, new Color(255, 128, 20));
        baseY = y;
    }

    @Override
    public void move() {
        super.move();                         // ว่ายไปข้างหน้าแบบคลาสแม่
        wave += 0.08;
        y = baseY + Math.sin(wave) * 18;      // แล้วส่ายขึ้นลง
    }

    @Override
    public int getTier() {
        return 1;
    }
}
