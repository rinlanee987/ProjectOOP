import java.awt.event.*;
import javax.swing.*;

//main
public class TinyBite extends JFrame implements ActionListener, GameListener {
    HomePanel home = new HomePanel();
    GamePanel game = new GamePanel();
    GameOverPanel gameOver = new GameOverPanel();

    public TinyBite() {
        home.bStart.addActionListener(this);
        home.bQuit.addActionListener(this);
        gameOver.bReplay.addActionListener(this);
        gameOver.bQuit.addActionListener(this);
        game.setGameListener(this);
        showPanel(home);
    }

    /** เปลี่ยนหน้าจอที่แสดงอยู่ในเฟรม */
    private void showPanel(JPanel panel) {
        getContentPane().removeAll();
        add(panel);
        pack();
        revalidate();
        repaint();
        panel.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == home.bStart || e.getSource() == gameOver.bReplay) {
            showPanel(game);
            game.startNewGame();
        } else if (e.getSource() == home.bQuit || e.getSource() == gameOver.bQuit) {
            System.exit(0);
        }
    }

    @Override
    public void onGameEnd(boolean win, int totalScore, int levelReached, int seconds) {
        gameOver.setResult(win, totalScore, levelReached, seconds);
        showPanel(gameOver);
    }

    public static void main(String[] args) {
        JFrame jf = new TinyBite();
        jf.setTitle("Tiny Bite - Fish Eat Fish");
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
}
