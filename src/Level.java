import java.awt.Color;
import java.awt.Graphics;

/**
 * ข้อมูลของแต่ละด่าน: ชื่อ เป้าคะแนน โควต้าการโดนกิน โอกาสเกิดศัตรู และวาดฉากหลังด้วย Graphics g
 */
public class Level {
    private int number;
    private String name;
    private int targetScore;
    private int lifeQuota;
    private int spawnDelay;         // เวลาเฉลี่ยระหว่างการเกิดศัตรู (ms)
    private int smallChance;        // โอกาสเกิด (เปอร์เซ็นต์)
    private int mediumChance;
    private Color waterColor;       // สีน้ำของด่าน

    public Level(int number, String name, int targetScore, int lifeQuota, int spawnDelay,
                 int smallChance, int mediumChance, Color waterColor) {
        this.number = number;
        this.name = name;
        this.targetScore = targetScore;
        this.lifeQuota = lifeQuota;
        this.spawnDelay = spawnDelay;
        this.smallChance = smallChance;
        this.mediumChance = mediumChance;
        this.waterColor = waterColor;
    }

    /** สร้างทั้ง 3 ด่านตาม storyboard */
    public static Level[] createAll() {
        Level[] levels = new Level[3];
        levels[0] = new Level(1, "Level 1 : Coral Reef", 300, 3, 900, 70, 25,
                              new Color(40, 140, 200));
        levels[1] = new Level(2, "Level 2 : Deep Sea", 500, 3, 800, 55, 30,
                              new Color(20, 70, 140));
        levels[2] = new Level(3, "Level 3 : Shipwreck", 800, 2, 700, 45, 35,
                              new Color(15, 50, 95));
        return levels;
    }

    /** สุ่มสร้างศัตรู 1 ตัวตามโอกาสของด่าน (คืนค่าเป็นคลาสแม่ EnemyFish) */
    public EnemyFish createEnemy(int w, int h, PlayerFish player) {
        int r = (int) (Math.random() * 100);
        if (r < smallChance) {
            return new SmallFish(w, h);
        } else if (r < smallChance + mediumChance) {
            return new MediumFish(w, h);
        } else {
            return new LargeFish(w, h, player);
        }
    }

    /** คะแนนในด่านที่ต้องได้ก่อนโตเป็นขนาดกลาง / ใหญ่ */
    public int getMediumScore() { return targetScore * 30 / 100; }
    public int getLargeScore()  { return targetScore * 65 / 100; }

    // ---------------- วาดฉากหลัง ----------------

    /** วาดฉากหลังของด่านขนาด 1000 x 700 */
    public void draw(Graphics g) {
        int w = 1000;
        int h = 700;

        // สีน้ำ (สีเดียวทั้งจอ)
        g.setColor(waterColor);
        g.fillRect(0, 0, w, h);

        // แสงส่องลงมาจากผิวน้ำ
        if (number == 1) {
            g.setColor(new Color(255, 255, 255, 28));
        } else {
            g.setColor(new Color(255, 255, 255, 14));
        }
        for (int i = 0; i < 5; i++) {
            int x = 80 + i * 210;
            int[] rayX = {x, x + 60, x + 160, x + 40};
            int[] rayY = {0, 0, h, h};
            g.fillPolygon(rayX, rayY, 4);
        }

        // ด่าน 2: ซากเสาหิน
        if (number == 2) {
            g.setColor(new Color(20, 60, 110, 170));
            for (int i = 0; i < 4; i++) {
                int x = 120 + i * 230;
                int colH = 180 + (i % 2) * 90;
                g.fillRect(x, h - 60 - colH, 50, colH);
                g.fillRect(x - 12, h - 70 - colH, 74, 18);
            }
        }

        // ด่าน 3: ซากเรือ
        if (number == 3) {
            g.setColor(new Color(30, 40, 60, 200));
            int[] hullX = {w / 2 - 330, w / 2 + 290, w / 2 + 220, w / 2 - 260};
            int[] hullY = {h - 210, h - 260, h - 60, h - 50};
            g.fillPolygon(hullX, hullY, 4);
            int[] mastX = {w / 2 - 45, w / 2 + 5, w / 2 + 15, w / 2 - 35};
            int[] mastY = {h - 230, h - 470, h - 470, h - 230};
            g.fillPolygon(mastX, mastY, 4);
            int[] yardX = {w / 2 - 60, w / 2 + 90, w / 2 + 90, w / 2 - 60};
            int[] yardY = {h - 385, h - 405, h - 395, h - 375};
            g.fillPolygon(yardX, yardY, 4);
            g.setColor(new Color(10, 20, 35, 200));
            for (int i = 0; i < 4; i++) {
                g.fillOval(w / 2 - 200 + i * 110, h - 170 - i * 8, 26, 26);   // ช่องหน้าต่างเรือ
            }
        }

        // พื้นทราย
        if (number == 1) {
            g.setColor(new Color(230, 205, 140));
        } else {
            g.setColor(new Color(120, 110, 90));
        }
        g.fillOval(-100, h - 70, w + 200, 160);

        // ปะการัง: วงรีหลายอันซ้อนกัน
        Color[] colors = { new Color(250, 90, 120), new Color(255, 170, 60),
                           new Color(180, 90, 220), new Color(255, 120, 170) };
        for (int i = 0; i < 7; i++) {
            int cx = 70 + i * 150;
            Color c = colors[i % colors.length];
            if (number > 1) c = c.darker();
            g.setColor(c);
            for (int b = 0; b < 4; b++) {
                g.fillOval(cx + b * 14 - 20, h - 50 - (b % 2) * 22, 26, 40);
            }
        }
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public int getTargetScore() { return targetScore; }
    public int getLifeQuota() { return lifeQuota; }
    public int getSpawnDelay() { return spawnDelay; }
}
