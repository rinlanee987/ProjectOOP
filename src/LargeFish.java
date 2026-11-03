import java.awt.Color;

/**
 * ศัตรูขนาดใหญ่ (ฉลาม) ถ้าผู้เล่นอยู่ข้างหน้าจะค่อย ๆ ไล่ตามระดับความลึกของผู้เล่น
 */
public class LargeFish extends EnemyFish {
    private PlayerFish target;   // อ้างถึงปลาผู้เล่น (Composition)

    public LargeFish(int panelWidth, int panelHeight, PlayerFish target) {
        super(panelWidth, panelHeight, 140, 80, 1.2 + Math.random() * 0.9, 50, new Color(85, 110, 140));
        this.target = target;
    }

    @Override
    public void move() {
        super.move();
        // ไล่ตามเฉพาะเมื่อผู้เล่นอยู่ข้างหน้า และผู้เล่นตัวเล็กกว่า
        boolean ahead = (target.getX() - x) * direction > 0;
        if (ahead && target.getTier() < getTier()) {
            if (target.getY() > y + 2) {
                y += 0.7;
            } else if (target.getY() < y - 2) {
                y -= 0.7;
            }
        }
    }

    @Override
    public int getTier() {
        return 3;
    }
}
