import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TypeDefense extends Application {

    // 画面サイズ
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;

    private Canvas canvas;       // 描画領域
    private GraphicsContext gc;  // 描画用の筆
    private Timer timer;         // タイマー
    private List<WordEnemy> enemies = new ArrayList<>(); // 敵の管理用のリスト

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 画面のベース
        BorderPane root = new BorderPane();   // レイアウトの部品
        canvas = new Canvas(WIDTH, HEIGHT);   // 描画領域の作成する
        gc = canvas.getGraphicsContext2D();   // 描画用の筆を取得する
        root.setCenter(canvas);               // 描画領域を画面中央に配置する

        // ウィンドウの表示設定
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("TypeDefense");
        stage.show(); // ウィンドウを表示する

        // テスト用の敵を作成する
        WordEnemy enemy = new WordEnemy("TEST", 300, 0);
        enemies.add(enemy);

        // ゲームのループを開始する
        startGameLoop();
    }

    // ゲームのアニメーションを管理するメソッド
    private void startGameLoop() {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               Platform.runLater(() -> {
                   update();   // ゲームの状態を更新する
                   draw();     // ゲームの描画を行う
               });
            }
        }, 0, 33); // 0秒後に開始して、33ミリ秒ごとにrun()を実行する。
    }

    // ゲームの状態を更新するメソッド
    private void update() {
         // リストにいるすべての敵に対して「動け」と命令する
         for (WordEnemy e : enemies) {
            e.move(2.0);  // 2.0ピクセルずつ下に移動
         }
    }

    // ゲームの描画を行うメソッド
    private void draw() {
        // 画面を黒で塗りつぶしてリセットする (描画の前に必ず行う)
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // 敵を描画する
        gc.setFill(Color.WHITE);                // 文字の色は白
        gc.setFont(new Font("Consolas", 20));   // フォント設定

        // リストにいるすべての敵を描画する
        for (WordEnemy e : enemies) {
            // 敵(e.word)を横(e.x)、縦(e.y)の場所に描画する
            gc.fillText(e.word, e.x, e.y);
        }
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) timer.cancel();
        super.stop();
    }
}