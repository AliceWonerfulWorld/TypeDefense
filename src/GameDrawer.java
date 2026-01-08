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
    private Image normalImage; 
    private Image redImage;    

    public GameDrawer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        
        try {
            normalImage = new Image("file:img/UFO.png");      
            redImage = new Image("file:img/EnemyUFO.png");     
        } catch (Exception e) {
            System.out.println("画像読み込みエラー: imgフォルダを確認してください");
        }
    }

    public void drawGame(int score, int life, int maxLife, List<WordEnemy> enemies, double currentTime, TypeDefense.GameMode mode) {
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

        // 敵描画
        for (WordEnemy e : enemies) {
            Image targetImage = (e.type == 1) ? redImage : normalImage;
            
            // UFOを大きく表示 (60x60)
            double ufoSize = 60;
            double drawX = e.x - (ufoSize / 2); 
            double drawY = e.y - ufoSize;       

            if (targetImage != null && !targetImage.isError()) {
                gc.drawImage(targetImage, drawX, drawY, ufoSize, ufoSize);
            } else {
                gc.setFill(e.getColor());
                gc.fillRect(drawX, drawY, ufoSize, ufoSize);
            }
            
            // 文字を大きく (26px)
            Color enemyColor = e.getColor();
            gc.save();
            gc.setEffect(new DropShadow(15, enemyColor)); 
            gc.setFill(Color.WHITE); 
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 26));
            
            double textOffset = e.word.length() * 7; 
            gc.fillText(e.word, e.x - textOffset, e.y);
            gc.restore();
        }

        // HUD描画
        drawHUD(w, h, score, life, maxLife, currentTime, mode);
    }

    private void drawCyberGrid(double w, double h) {
        gc.setStroke(Color.web("#00ffff", 0.1));
        gc.setLineWidth(1);
        for (int x = 0; x < w; x += 50) gc.strokeLine(x, 0, x, h);
        for (int y = 0; y < h; y += 50) gc.strokeLine(0, y, w, y);
    }

    // ★修正: パネルサイズと位置を調整して重なりを解消
    private void drawHUD(double w, double h, int score, int life, int maxLife, double time, TypeDefense.GameMode mode) {
        // パネルの基本設定
        double panelH = 55;
        double panelY = 15;
        double fontSize = 24;

        // --- フレーム ---
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(3);
        double len = 40;
        gc.strokeLine(10, 10, 10 + len, 10);
        gc.strokeLine(10, 10, 10, 10 + len);
        gc.strokeLine(w - 10, 10, w - 10 - len, 10);
        gc.strokeLine(w - 10, 10, w - 10, 10 + len);
        gc.strokeLine(10, h - 10, 10 + len, h - 10);
        gc.strokeLine(10, h - 10, 10, h - 10 - len);
        gc.strokeLine(w - 10, h - 10, w - 10 - len, h - 10);
        gc.strokeLine(w - 10, h - 10, w - 10, h - 10 - len);

        // --- 左上: スコアパネル ---
        // 幅を 180 -> 170 に短縮
        double scorePanelW = 170;
        drawPanel(15, panelY, scorePanelW, panelH);
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize));
        gc.fillText("SCORE: " + String.format("%05d", score), 25, panelY + 35);

        // --- 中央上: 時間パネル ---
        // 幅を 160 -> 140 に短縮
        double timePanelW = 140;
        drawPanel(w / 2 - timePanelW / 2, panelY, timePanelW, panelH);
        
        gc.setFill(Color.LIME);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize));
        
        if (mode == TypeDefense.GameMode.ENDLESS) {
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20)); // Endlessは文字長いので少し小さく
            gc.fillText("∞ ENDLESS", w / 2 - 45, panelY + 35);
        } else {
            gc.fillText("TIME: " + String.format("%.0f", time), w / 2 - 50, panelY + 35);
        }

        // --- 右上: HPパネル ---
        // 幅を 200 -> 190 に短縮
        double hpPanelW = 190;
        drawPanel(w - hpPanelW - 15, panelY, hpPanelW, panelH);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("HP:", w - hpPanelW - 5, panelY + 35); 

        // ハートの描画
        // 開始位置を調整
        double heartStartX = w - hpPanelW + 35; 
        double heartY = panelY + 35;
        int heartSize = 30;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, heartSize));

        for (int i = 0; i < maxLife; i++) {
            gc.save(); 
            if (i < life) {
                gc.setFill(Color.RED); 
                gc.setEffect(new DropShadow(15, Color.RED)); 
            } else {
                gc.setFill(Color.GRAY); 
                gc.setEffect(null);     
            }
            // ハートの間隔を少し詰める (32 -> 28)
            gc.fillText("♥", heartStartX + (i * 28), heartY);
            gc.restore(); 
        }
    }

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

        if (normalImage != null && !normalImage.isError()) {
            gc.drawImage(normalImage, w / 2 - 50, h / 2 + 20, 100, 100);
        }
    }
}