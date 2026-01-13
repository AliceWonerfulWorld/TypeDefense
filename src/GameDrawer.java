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

//ゲーム画面の描画を担当する
//クラス背景、敵、タイトル画面の描画を管理する

public class GameDrawer {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private Image normalImage; 
    private Image redImage;
    private HUDDrawer hudDrawer;

    public GameDrawer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.hudDrawer = new HUDDrawer(canvas);
        
        loadImages();
    }
    
    // 画像の読み込みを行う。
    private void loadImages() {
        try {
            normalImage = new Image("file:img/UFO.png");      
            redImage = new Image("file:img/EnemyUFO.png");     
        } catch (Exception e) {
            System.out.println("画像読み込みエラー: imgフォルダを確認してください");
        }
    }

    // スコアやライフなどの要素を描画する
    public void drawGame(int score, int life, int maxLife, List<WordEnemy> enemies, double currentTime, TypeDefense.GameMode mode) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        drawBackground(w, h);
        drawEnemies(enemies);
        hudDrawer.draw(score, life, maxLife, currentTime, mode);
    }
    
    // 背景を描画する
    private void drawBackground(double w, double h) {
        RadialGradient bg = new RadialGradient(
            0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#0a0a2a")),
            new Stop(1.0, Color.BLACK)
        );
        gc.setFill(bg);
        gc.fillRect(0, 0, w, h);

        drawCyberGrid(w, h);
    }
    
    // SFっぽい背景
    private void drawCyberGrid(double w, double h) {
        gc.setStroke(Color.web("#00ffff", 0.1));
        gc.setLineWidth(1);
        for (int x = 0; x < w; x += 50) gc.strokeLine(x, 0, x, h);
        for (int y = 0; y < h; y += 50) gc.strokeLine(0, y, w, y);
    }
    
    // 敵を描画する
    private void drawEnemies(List<WordEnemy> enemies) {
        for (WordEnemy e : enemies) {
            drawEnemy(e);
        }
    }

    private void drawEnemy(WordEnemy e) {
        Image targetImage = (e.type == 1) ? redImage : normalImage;
        
        double ufoSize = 60;
        double drawX = e.x - (ufoSize / 2); 
        double drawY = e.y - ufoSize;
        
        if (targetImage != null && !targetImage.isError()) {
            gc.drawImage(targetImage, drawX, drawY, ufoSize, ufoSize);
        } else {
            gc.setFill(e.getColor());
            gc.fillRect(drawX, drawY, ufoSize, ufoSize);
        }
        
        drawEnemyText(e);
    }
    
    // 敵の単語テキストを描画
    private void drawEnemyText(WordEnemy e) {
        Color enemyColor = e.getColor();
        gc.save();
        gc.setEffect(new DropShadow(15, enemyColor)); 
        gc.setFill(Color.WHITE); 
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 26));
        
        double textOffset = e.word.length() * 7; 
        gc.fillText(e.word, e.x - textOffset, e.y);
        gc.restore();
    }

    
    // タイトル画面を描画する
    public void drawTitle() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        drawTitleBackground(w, h);
        drawStars(w, h);
        drawTitleText(w, h);
        drawUFOImage(w, h);
    }
    
    // タイトル背景を描画
    private void drawTitleBackground(double w, double h) {
        RadialGradient bg = new RadialGradient(
            0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#1a2a6c")),
            new Stop(0.8, Color.BLACK)
        );
        gc.setFill(bg);
        gc.fillRect(0, 0, w, h);
    }
    
    // 星を描画
    private void drawStars(double w, double h) {
        gc.setFill(Color.WHITE);
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            gc.fillOval(rand.nextInt((int)w), rand.nextInt((int)h), 
                       rand.nextDouble() * 2 + 1, rand.nextDouble() * 2 + 1);
        }
    }
    
    // タイトルテキストを描画
    private void drawTitleText(double w, double h) {
        gc.save();
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);
        gc.setEffect(glow);

        gc.setFill(Color.WHITE);
        Font font = Font.font("Verdana", FontWeight.BOLD, 50);
        gc.setFont(font);
        String title = "TYPE DEFENSE";
        
        // テキストの実際の幅を計算
        javafx.scene.text.Text text = new javafx.scene.text.Text(title);
        text.setFont(font);
        double textWidth = text.getLayoutBounds().getWidth();
        
        // 画面中央に配置
        gc.fillText(title, (w - textWidth) / 2, h / 2 - 100);
        gc.restore();
    }
    
    // UFO画像を描画
    private void drawUFOImage(double w, double h) {
        if (normalImage != null && !normalImage.isError()) {
            // タイトル画面上にUFOを配置する
            gc.drawImage(normalImage, w / 2 - 50, h / 2 - 40, 100, 100);
        }
    }
}