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
import javafx.scene.effect.BlendMode;
import java.util.List;
import java.util.Random;

public class GameDrawer {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private Image enemyImage;

    public GameDrawer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        
        try {
            enemyImage = new Image("file:img/UFO.png"); 
        } catch (Exception e) {
            System.out.println("画像読み込みエラー");
        }
    }

    public void drawGame(int score, int life, int maxLife, List<WordEnemy> enemies) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        // 背景
        RadialGradient bg = new RadialGradient(
            0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#0a0a2a")),
            new Stop(1.0, Color.BLACK)
        );
        gc.setFill(bg);
        gc.fillRect(0, 0, w, h);

        drawCyberGrid(w, h);

        // 敵の描画
        for (WordEnemy e : enemies) {
            if (enemyImage != null && !enemyImage.isError()) {
                gc.drawImage(enemyImage, e.x - 20, e.y - 40, 40, 40);
            } else {
                gc.setFill(Color.RED);
                gc.fillRect(e.x - 20, e.y - 40, 40, 40);
            }
            
            gc.save();
            gc.setEffect(new DropShadow(10, Color.CYAN));
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
            gc.fillText(e.word, e.x, e.y);
            gc.restore();
        }

        // HUD描画
        drawHUD(w, h, score, life, maxLife);
    }

    private void drawCyberGrid(double w, double h) {
        gc.setStroke(Color.web("#00ffff", 0.1));
        gc.setLineWidth(1);
        for (int x = 0; x < w; x += 50) gc.strokeLine(x, 0, x, h);
        for (int y = 0; y < h; y += 50) gc.strokeLine(0, y, w, y);
    }

    // ★変更: HPバーをやめてハート表示にする
    private void drawHUD(double w, double h, int score, int life, int maxLife) {
        // --- フレーム描画 ---
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(3);
        double len = 30;
        // 四隅の枠
        gc.strokeLine(10, 10, 10 + len, 10);
        gc.strokeLine(10, 10, 10, 10 + len);
        gc.strokeLine(w - 10, 10, w - 10 - len, 10);
        gc.strokeLine(w - 10, 10, w - 10, 10 + len);
        gc.strokeLine(10, h - 10, 10 + len, h - 10);
        gc.strokeLine(10, h - 10, 10, h - 10 - len);
        gc.strokeLine(w - 10, h - 10, w - 10 - len, h - 10);
        gc.strokeLine(w - 10, h - 10, w - 10, h - 10 - len);

        // --- 左上: スコアパネル ---
        drawPanel(15, 15, 200, 40);
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        gc.fillText("SCORE: " + String.format("%05d", score), 25, 42);

        // --- 右上: HPパネル (ハート表示) ---
        double panelWidth = 220; // ハートが入るように少し広げる
        drawPanel(w - panelWidth - 15, 15, panelWidth, 40);
        
        // "HP:" ラベル
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText("HP:", w - panelWidth - 5, 42);

        // ★ハートの描画ループ
        double heartStartX = w - panelWidth + 40; // 描き始めのX座標
        double heartY = 42;                       // Y座標
        int heartSize = 24;                       // ハートの文字サイズ

        gc.setFont(Font.font("Arial", FontWeight.BOLD, heartSize));

        for (int i = 0; i < maxLife; i++) {
            gc.save(); // 設定を保存
            
            if (i < life) {
                // 生存している分（赤いハート）
                gc.setFill(Color.RED); // 赤色
                gc.setEffect(new DropShadow(15, Color.RED)); // 赤く光らせる！
            } else {
                // ダメージを受けた分（灰色のハート）
                gc.setFill(Color.GRAY); // 灰色
                gc.setEffect(null);     // 光らせない
            }

            // ハートを描画 (間隔を30pxずつ空ける)
            gc.fillText("♥", heartStartX + (i * 30), heartY);
            
            gc.restore(); // 設定を戻す
        }
    }

    // パネルの下地を描く補助メソッド
    private void drawPanel(double x, double y, double w, double h) {
        gc.setFill(Color.web("#000000", 0.7));
        gc.fillRoundRect(x, y, w, h, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, w, h, 10, 10);
    }

    public void drawTitle() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        RadialGradient bg = new RadialGradient(
            0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#1a2a6c")),
            new Stop(0.8, Color.BLACK)
        );
        gc.setFill(bg);
        gc.fillRect(0, 0, w, h);

        gc.setFill(Color.WHITE);
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            gc.fillOval(rand.nextInt((int)w), rand.nextInt((int)h), rand.nextDouble() * 2 + 1, rand.nextDouble() * 2 + 1);
        }

        gc.save();
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);
        gc.setEffect(glow);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        String title = "TYPE DEFENSE";
        double textWidth = 400; 
        gc.fillText(title, (w - textWidth) / 2 + 20, h / 2 - 50);
        gc.restore();

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Consolas", 16));
        gc.fillText("Target the dropping words!", w / 2 - 120, h / 2);

        if (enemyImage != null && !enemyImage.isError()) {
            gc.drawImage(enemyImage, w / 2 - 40, h / 2 + 20, 80, 80);
        }
    }
}