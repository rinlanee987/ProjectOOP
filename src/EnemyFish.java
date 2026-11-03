import java.awt.Color;

/**
 * คลาสแม่ของปลาศัตรู สืบทอดจาก Fish
 * ปลาศัตรูจะว่ายเข้ามาจากขอบซ้ายหรือขวาของจอ
 */
public abstract class EnemyFish extends Fish {
    protected int direction;   // 1 = ว่ายไปขวา, -1 = ว่ายไปซ้าย
    protected int points;      // คะแนนที่ได้เมื่อกินปลาตัวนี้

    public EnemyFish(int panelWidth, int panelHeight, int width, int height,
                     double speed, int points, Color color) {
        super(0, 0, width, height, speed, color);
        this.points = points;
        if (Math.random() < 0.5) {
            direction = 1;
            x = -width;                    // เกิดนอกจอฝั่งซ้าย
        } else {
            direction = -1;
            x = panelWidth + width;        // เกิดนอกจอฝั่งขวา
        }
        facingRight = (direction == 1);
        y = 110 + Math.random() * (panelHeight - 190);
    }

    /** การว่ายพื้นฐาน: ไปทางเดียวตามความเร็ว */
    @Override
    public void move() {
        x += speed * direction;
    }

    /** ปลาว่ายพ้นจอไปแล้วหรือยัง */
    public boolean isOffScreen(int panelWidth) {
        if (direction == 1) {
            return x - width > panelWidth;
        } else {
            return x + width < 0;
        }
    }

    public int getPoints() {
        return points;
    }
}
