public class WordEnemy {

    // 敵が持っているデータ
    public String word; // 表示される単語 
    public double x;    // 横の位置
    public double y;    // 縦の位置

    // コンストラクタ
    // new WordEnemy("test", 100, 0)のように書くと、ここの部分が動く
    public WordEnemy(String word, double x, double y) {
        this.word = word;
        this.x = x;
        this.y = y;
    }

    // 敵の動きを決めるメソッド
    public void move(double speed) {
        this.y = this.y + speed; // y座標にスピードの分を足す(画面下方向へ進む)
    }
}
