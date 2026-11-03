import java.awt.Color;
import java.awt.Graphics;

/**
 * ปลาของผู้เล่น สืบทอดจาก Fish
 * ว่ายตามตำแหน่ง cursor เมาส์ และโตขึ้นได้ 3 ระดับ
 */
public class PlayerFish extends Fish {
    // ขนาดของปลาผู้เล่นในแต่ละระดับ {กว้าง, สูง}
    private int[][] sizes = { {50, 34}, {82, 56}, {125, 84} };

    private double targetX;
    private double targetY;
    private int tier = 1;
    private long invincibleUntil = 0;

    public PlayerFish(double x, double y) {
        super(x, y, 50, 34, 6.0, new Color(52, 140, 230));
        this.targetX = x;
        this.targetY = y;
    }

    /** รับตำแหน่งเมาส์ล่าสุด */
    public void setTarget(double tx, double ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    @Override
    public void move() {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 1) {
            double step = Math.min(speed, dist);
            x += dx / dist * step;
            y += dy / dist * step;
        }
        if (dx > 3) {
            facingRight = true;
        } else if (dx < -3) {
            facingRight = false;
        }
    }

    /** ตั้งระดับขนาดใหม่ ระดับ 1-3 */
    public void setTier(int newTier) {
        if (newTier < 1) newTier = 1;
        if (newTier > 3) newTier = 3;
        tier = newTier;
        width = sizes[tier - 1][0];
        height = sizes[tier - 1][1];
    }

    @Override
    public int getTier() {
        return tier;
    }

    /** กลับไปตำแหน่งเริ่มต้น */
    public void respawn(double sx, double sy) {
        x = sx;
        y = sy;
        targetX = sx;
        targetY = sy;
    }

    public void makeInvincible(long millis) {
        invincibleUntil = System.currentTimeMillis() + millis;
    }

    public boolean isInvincible() {
        return System.currentTimeMillis() < invincibleUntil;
    }

    /** ตอนอมตะหลังโดนกิน ให้ปลากะพริบ (วาดบ้างไม่วาดบ้าง) */
    @Override
    public void draw(Graphics g) {
        if (isInvincible() && (System.currentTimeMillis() / 120) % 2 == 0) {
            return;
        }
        super.draw(g);
    }
}
