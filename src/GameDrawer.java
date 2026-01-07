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
}