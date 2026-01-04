import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
}