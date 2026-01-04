import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;




public class TypeDefense extends Application {

    // 画面サイズ
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;

    private static final String[] WORDS = {
        "JAVA","CLASS","OBJECT","METHOD","PUBLIC","STATIC",
        "VOID","RETURN","IMPORT","JAVAFX","CANVAS","NODE",
        "STRING","INTEGER","DOUBLE","BOOLEAN","SYSTEM","OUT"
    };

    private Canvas canvas;       // 描画領域
    private GraphicsContext gc;  // 描画用の筆
    private Timer timer;         // タイマー
    private List<WordEnemy> enemies = new ArrayList<>(); // 敵の管理用のリスト
    private int score = 0;// スコアを記録するための変数
    private int spawnCounter = 0; // 時間をカウントする
    private int spawnRate = 60;   // 何カウントごとに敵を出すか
    private Image enemyImage;  // 敵の画像データを入れるための変数

    // UI部品
    private TextField nameField; // 名前入力欄
    private Button startButton;  // スタートボタン
    private RadioButton easyBtn, hardBtn;  // 難易度選択
    private boolean isRunning false; // ゲーム中であるかどうか
    
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

        // キーボードが押されたらprocessInputメソッドを呼ぶ
        scene.setOnKeyPressed(e -> processInput(e.getCode()));


        stage.setScene(scene);
        stage.setTitle("TypeDefense");
        stage.show(); // ウィンドウを表示する

        // 画像ファイルの読み込み処理
        try {
            // 画像ファイルがあるかtryする
            enemyImage = new Image("file:img/UFO.png");
        } catch (Exception e) {
            // もし画像がなくてもエラーで止まらないようにする
            System.out.println("画像読み込みエラー: img/UFO.pngが見つかりません");
        }
        
        enemies.clear();  // ゲーム開始時は敵は0体からスタートする
        startGameLoop();// ゲームのループを開始する
    }

    private void processInput(KeyCode code) {
        String key = code.toString();   // 押されたキー (例: "A")

        // 「一番下にいる(yが大きい)」かつ「入力された文字で始まる」敵を探す
        WordEnemy target = null;
        double maxY = -1000;

        for (WordEnemy e : enemies) {
            if (e.word.startsWith(key) && e.y > maxY) {
                maxY = e.y;
                target = e;
            }
        }

        if (target != null) {
            boolean isAlive = target.damage(); // 文字を1つずつ消す
            if (!isAlive) {
                // 全部消えたらリストから削除する
                enemies.remove(target);
                score += 100; // スコア加算
            }
        }
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
         // 敵を出すかどうかチェックする
         spawnCounter++; //カウンタを1増やす

         if (spawnCounter >= spawnRate) {
            // カウンタが設定値を超えたら敵を出す
            spawnEnemy();
            spawnCounter = 0; // カウンタをリセットする
         }

         // リストにいるすべての敵に対して「動け」と命令する
         for (WordEnemy e : enemies) {
            e.move(2.0);  // 2.0ピクセルずつ下に移動
         }
    }

    // ランダムな敵を生成するメソッド
    private void spawnEnemy() {
       Random rand = new Random();

       // 単語リストからランダムに1つ選ぶ
       int index = rand.nextInt(WORDS.length); // 0 ～ (単語数-1)の乱数
       String word = WORDS[index];

       // 出現位置(X座標)もランダムにする
       double x = rand.nextInt(WIDTH - 100) + 50; 

       // リストに追加する (Y座標は画面一番上の0)
       enemies.add(new WordEnemy(word, x, 0));
    }

    // ゲームの描画を行うメソッド
    private void draw() {
        // 画面を黒で塗りつぶしてリセットする (描画の前に必ず行う)
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // 敵を描画する
        gc.setFill(Color.WHITE);                // 文字の色は白
        gc.setFont(new Font("Consolas", 20));   // フォント設定

        // 左上にスコアを表示する
        gc.fillText("Score: " + score, 20, 30);

        // リストにいるすべての敵を描画する
        for (WordEnemy e : enemies) {
            // 画像があれば描画する
            if (enemyImage != null && !enemyImage.isError()) {
                // drawImage(画像, x, y, width, height)
                // 敵の座標(e.x, e.y)を中心にするためにずらして表示する
                gc.drawImage(enemyImage, e.x - 20, e.y - 40, 40, 40);
            } else {
                // 画像がないときは赤い四角を描く
                gc.setFill(Color.RED);
                gc.fillRect(e.x - 20, e.y - 40, 40, 40);
                gc.setFill(Color.WHITE); // 文字色を白に戻す
            }

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