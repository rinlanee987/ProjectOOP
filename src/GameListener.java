public interface GameListener {
    void onGameEnd(boolean win, int totalScore, int levelReached, int seconds);
}
