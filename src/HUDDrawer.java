import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;

// HUDの描画を担当するクラス

public class HUDDrawer {
    
    private Canvas canvas;
    private GraphicsContext gc;
    
    public HUDDrawer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }
    
    // HUD全体を描画
    public void draw(int score, int life, int maxLife, double time, TypeDefense.GameMode mode) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        
        drawFrame(w, h);
        drawScorePanel(w, score);
        drawTimePanel(w, time, mode);
        drawHPPanel(w, life, maxLife);
    }
    
    // 画面四隅のフレームを描画
    private void drawFrame(double w, double h) {
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(3);
        double len = 40;
        
        // 左上
        gc.strokeLine(10, 10, 10 + len, 10);
        gc.strokeLine(10, 10, 10, 10 + len);
        
        // 右上
        gc.strokeLine(w - 10, 10, w - 10 - len, 10);
        gc.strokeLine(w - 10, 10, w - 10, 10 + len);
        
        // 左下
        gc.strokeLine(10, h - 10, 10 + len, h - 10);
        gc.strokeLine(10, h - 10, 10, h - 10 - len);
        
        // 右下
        gc.strokeLine(w - 10, h - 10, w - 10 - len, h - 10);
        gc.strokeLine(w - 10, h - 10, w - 10, h - 10 - len);
    }
    
    // スコアパネルを描画
    private void drawScorePanel(double w, int score) {
        double panelY = 15;
        double panelH = 55;
        double panelW = 170;
        
        drawPanel(15, panelY, panelW, panelH);
        
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        gc.fillText("SCORE: " + String.format("%05d", score), 25, panelY + 35);
    }
    
    // 時間パネルを描画
    private void drawTimePanel(double w, double time, TypeDefense.GameMode mode) {
        double panelY = 15;
        double panelH = 55;
        double panelW = 140;
        
        drawPanel(w / 2 - panelW / 2, panelY, panelW, panelH);
        
        gc.setFill(Color.LIME);
        
        if (mode == TypeDefense.GameMode.ENDLESS) {
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
            gc.fillText("∞ ENDLESS", w / 2 - 45, panelY + 35);
        } else {
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
            gc.fillText("TIME: " + String.format("%.0f", time), w / 2 - 50, panelY + 35);
        }
    }
    
    // HPパネルを描画
    private void drawHPPanel(double w, int life, int maxLife) {
        double panelY = 15;
        double panelH = 55;
        double panelW = 190;
        
        drawPanel(w - panelW - 15, panelY, panelW, panelH);
        
        // HPラベル
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("HP:", w - panelW - 5, panelY + 35);
        
        drawHearts(w - panelW + 35, panelY + 35, life, maxLife);
    }
    
    // ハートを描画
    private void drawHearts(double startX, double y, int life, int maxLife) {
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
            
            gc.fillText("♥", startX + (i * 28), y);
            gc.restore();
        }
    }
    
    // パネルの枠を描画
    private void drawPanel(double x, double y, double w, double h) {
        gc.setFill(Color.web("#000000", 0.7));
        gc.fillRoundRect(x, y, w, h, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, w, h, 10, 10);
    }
}
