import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import java.util.List;
import java.util.Random;

public class GameDrawer {

    private Canvas canvas;
    private GraphicsContext gc;
    private Image enemyImage;

    // コンストラクタ
    public GameDrawer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        // 画像読み込み
        try {
            enemyImage = new Image("file:img/UFO.png"); 
        } catch (Exception e) {
            System.out.println("画像読み込みエラー");
        }
    }

    // ゲーム画面の描画
    public void drawGame(int score, List<WordEnemy> enemies) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // 背景リセット
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, w, h);

        // スコア表示
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Consolas", 20));
        gc.fillText("Score: " + score, 20, 30);

        // 敵の描画
        for (WordEnemy e : enemies) {
            if (enemyImage != null && !enemyImage.isError()) {
                gc.drawImage(enemyImage, e.x - 20, e.y - 40, 40, 40);
            } else {
                gc.setFill(Color.RED);
                gc.fillRect(e.x - 20, e.y - 40, 40, 40);
                gc.setFill(Color.WHITE);
            }
            gc.fillText(e.word, e.x, e.y);
        }
    }

    // タイトル画面の描画
    public void drawTitle() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        if (w <= 0 || h <= 0) return;

        // 背景グラデーション
        RadialGradient bg = new RadialGradient(
            0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#1a2a6c")),
            new Stop(0.8, Color.BLACK)
        );
        gc.setFill(bg);
        gc.fillRect(0, 0, w, h);

        // 星
        gc.setFill(Color.WHITE);
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            gc.fillOval(rand.nextInt((int)w), rand.nextInt((int)h), rand.nextDouble() * 2 + 1, rand.nextDouble() * 2 + 1);
        }

        // タイトル文字（発光エフェクト）
        gc.save();
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);
        gc.setEffect(glow);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        // 中央寄せ計算
        String title = "TYPE DEFENSE";
        double textWidth = 400; // およその幅
        gc.fillText(title, (w - textWidth) / 2 + 20, h / 2 - 50);
        gc.restore();

        // サブタイトルなど
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Consolas", 16));
        gc.fillText("Target the dropping words!", w / 2 - 120, h / 2);

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Enter Name & Press Start Button", w / 2 - 140, h / 2 + 150);

        // UFO
        if (enemyImage != null && !enemyImage.isError()) {
            gc.drawImage(enemyImage, w / 2 - 40, h / 2 + 20, 80, 80);
        }
    }
}