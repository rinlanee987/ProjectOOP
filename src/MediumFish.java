import java.awt.Color;

/**
 * ศัตรูขนาดกลาง (ปลาสีม่วง) ว่ายตรงและค่อย ๆ เปลี่ยนความลึก
 */
public class MediumFish extends EnemyFish {
    private double drift = (Math.random() - 0.5) * 0.6;
    private int panelHeight;

    public MediumFish(int panelWidth, int panelHeight) {
        super(panelWidth, panelHeight, 76, 54, 1.4 + Math.random() * 1.1, 25, new Color(165, 60, 200));
        this.panelHeight = panelHeight;
    }

    @Override
    public void move() {
        super.move();
        y += drift;
        if (y < 100 || y > panelHeight - 80) {
            drift = -drift;                   // ชนขอบบน/ล่างให้กลับทิศ
        }
    }

    @Override
    public int getTier() {
        return 2;
    }
}
