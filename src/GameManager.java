import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

// 敵のスポーン、更新、スコア・ライフ管理を担当

public class GameManager {
    
    private Canvas canvas;
    private GameDrawer drawer;
    private Timer timer;
    private List<WordEnemy> enemies = new ArrayList<>();
    
    // ゲームの状態
    private int score = 0;
    private int spawnCounter = 0;
    private int spawnRate = 60;
    private int maxLife = 5;
    private int currentLife;
    private boolean isRunning = false;
    private boolean isPaused = false;
    
    // 時間管理
    private double currentTime;
    private TypeDefense.GameMode currentMode;
    
    // コールバック
    private Runnable onGameOver;
    
    public GameManager(Canvas canvas, GameDrawer drawer) {
        this.canvas = canvas;
        this.drawer = drawer;
    }
    
  
    public void setOnGameOver(Runnable callback) {
        this.onGameOver = callback;
    }
    
    
    public void startGame(TypeDefense.GameMode mode) {
        if (isRunning) return;
        
        this.currentMode = mode;
        enemies.clear();
        score = 0;
        spawnCounter = 0;
        currentLife = maxLife;
        isRunning = true;
        isPaused = false;
        
        // モード別の初期設定
        if (mode == TypeDefense.GameMode.EASY) {
            spawnRate = 60;
            currentTime = 60.0;
        } else if (mode == TypeDefense.GameMode.HARD) {
            spawnRate = 45;
            currentTime = 60.0;
        } else {
            spawnRate = 45;
            currentTime = 0.0;
        }
        
        // タイマー開始
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> update());
            }
        }, 0, 33);
        
        // 即座に最初のゲーム画面を描画
        draw();
    }
    
    /**
     * ゲームを停止
     */
    public void stopGame() {
        isRunning = false;
        isPaused = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /**
     * ゲームの更新処理
     */
    private void update() {
        if (isPaused) return;
        
        updateTime();
        updateSpawn();
        updateEnemies();
        checkGameOver();
        draw();
    }
    
    /**
     * 時間の更新
     */
    private void updateTime() {
        if (currentMode == TypeDefense.GameMode.ENDLESS) {
            currentTime += 0.033;
            
            // 難易度上昇
            int baseRate = 60;
            int difficultyLevel = score / 500;
            spawnRate = Math.max(10, baseRate - (difficultyLevel * 5));
        } else {
            currentTime -= 0.033;
            if (currentTime <= 0) {
                currentTime = 0;
                gameOver(true);
            }
        }
    }
    
    /**
     * 敵のスポーン処理
     */
    private void updateSpawn() {
        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            spawnEnemy();
            spawnCounter = 0;
        }
    }
    
    /**
     * 敵の移動とライフ管理
     */
    private void updateEnemies() {
        double currentHeight = canvas.getHeight();
        List<WordEnemy> currentEnemies = new ArrayList<>(enemies);
        
        // スピード計算
        double endlessSpeedMultiplier = 1.0;
        if (currentMode == TypeDefense.GameMode.ENDLESS) {
            endlessSpeedMultiplier = 1.0 + (score / 1000.0) * 0.5;
        }
        
        double baseSpeed = 2.0;
        if (currentMode == TypeDefense.GameMode.HARD) {
            baseSpeed = 3.0;
        }
        
        for (WordEnemy e : currentEnemies) {
            e.move(baseSpeed * endlessSpeedMultiplier);
            
            if (e.y > currentHeight) {
                enemies.remove(e);
                currentLife--;
            }
        }
    }
    
    /**
     * ゲームオーバー判定
     */
    private void checkGameOver() {
        if (currentLife <= 0) {
            gameOver(false);
        }
    }
    
    /**
     * 描画処理
     */
    private void draw() {
        drawer.drawGame(score, currentLife, maxLife, enemies, currentTime, currentMode);
    }
    
    /**
     * 敵を生成
     */
    private void spawnEnemy() {
        Random rand = new Random();
        
        // モード別の単語リスト選択
        String[] targetList;
        if (currentMode == TypeDefense.GameMode.EASY) {
            targetList = GameConstants.WORDS_EASY;
        } else if (currentMode == TypeDefense.GameMode.HARD) {
            targetList = GameConstants.WORDS_HARD;
        } else {
            targetList = rand.nextBoolean() ? GameConstants.WORDS_EASY : GameConstants.WORDS_HARD;
        }
        
        String word = targetList[rand.nextInt(targetList.length)];
        double w = canvas.getWidth();
        double x = rand.nextInt(Math.max(1, (int)w - 150)) + 50;
        
        // 敵タイプ決定
        int chance = 2;
        if (currentMode == TypeDefense.GameMode.ENDLESS && score > 2000) {
            chance = 4;
        }
        
        int type = (rand.nextInt(10) < chance) ? 1 : 0;
        enemies.add(new WordEnemy(word, x, 0, type));
    }
    

    public void processInput(String key) {
        if (!isRunning) return;
        
        WordEnemy target = null;
        double maxY = -1000;
        
        for (WordEnemy e : enemies) {
            if (e.word.startsWith(key) && e.y > maxY) {
                maxY = e.y;
                target = e;
            }
        }
        
        if (target != null) {
            if (!target.damage()) {
                enemies.remove(target);
                score += target.getScore();
            }
        }
    }
    
    // ゲームオーバー処理
     
    private void gameOver(boolean isTimeUp) {
        stopGame();
        if (onGameOver != null) {
            onGameOver.run();
        }
    }
    
    // Getter
    public boolean isRunning() {
        return isRunning;
    }
    
    public int getScore() {
        return score;
    }
    
    public double getCurrentTime() {
        return currentTime;
    }
    
    public boolean isTimeUp() {
        return currentMode != TypeDefense.GameMode.ENDLESS && currentTime <= 0;
    }
    
    /**
     * ゲームを一時停止
     */
    public void pauseGame() {
        isPaused = true;
    }
    
    /**
     * ゲームを再開
     */
    public void resumeGame() {
        isPaused = false;
    }
    
    /**
     * 一時停止中かどうか
     */
    public boolean isPaused() {
        return isPaused;
    }
}
