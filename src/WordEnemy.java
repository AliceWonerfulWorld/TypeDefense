import javafx.scene.paint.Color;

public class WordEnemy {
    public String word;
    public double x;
    public double y;
    
    // 0:通常(Cyan), 1:赤(Red)
    public int type; 

    // コンストラクタを修正：typeを受け取れるようにする
    public WordEnemy(String word, double x, double y, int type) {
        this.word = word;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    // 移動メソッド
    public void move(double baseSpeed) {
        // 赤い敵(type 1)はスピード2倍
        double multiplier = (type == 1) ? 2.0 : 1.0;
        this.y += baseSpeed * multiplier;
    }

    // ダメージ処理
    public boolean damage() {
        if (word.length() > 0) {
            word = word.substring(1);
        }
        return word.length() > 0;
    }

    // --- 以下、エラーの原因になっていたメソッドを追加 ---

    // 倒した時のスコアを返すメソッド
    public int getScore() {
        return (type == 1) ? 300 : 100;
    }

    // 表示する色を返すメソッド
    public Color getColor() {
        return (type == 1) ? Color.RED : Color.CYAN;
    }
}