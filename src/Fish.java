import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/**
 * คลาสแม่ (abstract class) ของปลาทุกตัวในเกม
 * เก็บตำแหน่ง ขนาด ความเร็ว สี และวาดรูปทรงปลาพื้นฐานด้วย Graphics g
 * คลาสลูกต้อง override move() และ getTier() เอง (Polymorphism)
 */
public abstract class Fish {
    protected double x;            // ตำแหน่งจุดกึ่งกลางของปลา แกน x
    protected double y;            // ตำแหน่งจุดกึ่งกลางของปลา แกน y
    protected int width;
    protected int height;
    protected double speed;
    protected boolean facingRight = true;
    protected Color bodyColor;     // สีลำตัว
    protected Color finColor;      // สีหางและครีบ

    public Fish(double x, double y, int width, int height, double speed, Color bodyColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.bodyColor = bodyColor;
        this.finColor = bodyColor.darker();
    }

    /** การเคลื่อนที่ของปลาแต่ละชนิดต่างกัน คลาสลูกต้องเขียนเอง */
    public abstract void move();

    /** ระดับขนาดของปลา 1 = เล็ก, 2 = กลาง, 3 = ใหญ่ */
    public abstract int getTier();

    /**
     * วาดปลาด้วย Graphics g
     * d = 1 เมื่อปลาหันขวา, d = -1 เมื่อปลาหันซ้าย
     * ตำแหน่งแกน x ของทุกส่วนคูณด้วย d ภาพปลาจึงกลับด้านตามทิศที่ว่าย
     */
    public void draw(Graphics g) {
        int cx = (int) x;
        int cy = (int) y;
        int w = width;
        int h = height;
        int d;
        if (facingRight) {
            d = 1;
        } else {
            d = -1;
        }

        // หาง
        g.setColor(finColor);
        int[] tailX = {cx - d * w * 25 / 100, cx - d * w / 2, cx - d * w * 44 / 100, cx - d * w / 2};
        int[] tailY = {cy, cy - h * 42 / 100, cy, cy + h * 42 / 100};
        g.fillPolygon(tailX, tailY, 4);

        // ครีบหลัง
        int[] finX = {cx - d * w * 12 / 100, cx + d * w * 2 / 100, cx + d * w * 18 / 100};
        int[] finY = {cy - h * 38 / 100, cy - h * 62 / 100, cy - h * 40 / 100};
        g.fillPolygon(finX, finY, 3);

        // ลำตัว
        g.setColor(bodyColor);
        int bodyW = w * 82 / 100;
        g.fillOval(cx + d * w * 9 / 100 - bodyW / 2, cy - h * 45 / 100, bodyW, h * 90 / 100);

        // ท้อง
        g.setColor(new Color(255, 255, 255, 90));
        int bellyW = w * 55 / 100;
        g.fillOval(cx + d * w * 12 / 100 - bellyW / 2, cy, bellyW, h * 38 / 100);

        // ครีบข้าง
        g.setColor(finColor);
        int sideW = w * 20 / 100;
        g.fillOval(cx + d * w * 8 / 100 - sideW / 2, cy + h * 2 / 100, sideW, h * 18 / 100);

        // ตา
        int eye = h * 26 / 100;
        int eyeX = cx + d * (w * 20 / 100 + eye / 2);      // จุดกึ่งกลางตา
        int eyeY = cy - h * 28 / 100;
        g.setColor(Color.WHITE);
        g.fillOval(eyeX - eye / 2, eyeY, eye, eye);
        g.setColor(Color.BLACK);
        int pupil = eye * 55 / 100;
        g.fillOval(eyeX + d * eye / 8 - pupil / 2, eyeY + eye / 4, pupil, pupil);

        // ปาก (เส้นโค้ง)
        int mouthW = w * 12 / 100;
        int mouthX = cx + d * w * 42 / 100 - mouthW / 2;
        if (facingRight) {
            g.drawArc(mouthX, cy + h * 2 / 100, mouthW, h * 14 / 100, 200, 120);
        } else {
            g.drawArc(mouthX, cy + h * 2 / 100, mouthW, h * 14 / 100, -20, -120);
        }
    }

    /** กรอบสี่เหลี่ยมสำหรับตรวจการชน (เล็กกว่าตัวปลาเล็กน้อยให้พอดีลำตัว) */
    public Rectangle2D getBound() {
        return new Rectangle2D.Double(x - width * 0.375, y - height * 0.35, width * 0.75, height * 0.7);
    }

    /** เปลี่ยนตำแหน่งและขนาด (ใช้ตอนวาดปลาประดับในหน้าแรก/หน้าจบเกม) */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
