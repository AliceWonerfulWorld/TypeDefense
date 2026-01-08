import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class TypeDefense extends Application {

    // モード定義
    public enum GameMode {
        EASY, HARD, ENDLESS
    }

    private Canvas canvas;
    private GameDrawer drawer;
    private Timer timer;
    private List<WordEnemy> enemies = new ArrayList<>();
    
    // ゲームの状態
    private int score = 0;
    private int spawnCounter = 0;
    private int spawnRate = 60;
    private int maxLife = 5;
    private int currentLife;
    private boolean isRunning = false;
    
    // 時間制限用 (秒)
    private double timeLimit = 60.0; 
    private double currentTime;

    // プレイヤー設定
    private String playerName = "Agent";
    private GameMode currentMode = GameMode.EASY;

    // UI部品
    private Button titleStartBtn;
    private VBox startOverlay;
    private TextField nameInput;
    private RadioButton easyBtn, hardBtn, endlessBtn; // 3つのボタン
    private Label messageLabel; 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        canvas = new Canvas(GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        drawer = new GameDrawer(canvas);
        
        canvas.widthProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });
        canvas.heightProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });

        createTitleButton();
        createStartOverlay();
        startOverlay.setVisible(false);

        root.getChildren().addAll(titleStartBtn, startOverlay);

        Scene scene = new Scene(root, GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        scene.setOnKeyPressed(e -> processInput(e.getCode()));

        stage.setScene(scene);
        stage.setTitle("TypeDefense - Mode Selection Update");
        stage.show();

        Platform.runLater(() -> drawer.drawTitle());
    }

    private void createTitleButton() {
        titleStartBtn = new Button("MISSION START");
        
        String btnStyle = 
            "-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas'; " +
            "-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: cyan; " +
            "-fx-border-color: cyan; -fx-border-width: 2px; -fx-cursor: hand;";
            
        titleStartBtn.setStyle(btnStyle);
        
        titleStartBtn.setOnMouseEntered(e -> {
            titleStartBtn.setStyle(
                btnStyle + "-fx-background-color: rgba(0, 255, 255, 0.3); -fx-effect: dropshadow(three-pass-box, cyan, 20, 0.5, 0, 0);"
            );
        });
        titleStartBtn.setOnMouseExited(e -> titleStartBtn.setStyle(btnStyle));

        titleStartBtn.setOnAction(e -> {
            titleStartBtn.setVisible(false);
            startOverlay.setVisible(true);
            messageLabel.setText("SYSTEM READY");
            messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        });
    }

    private void createStartOverlay() {
        startOverlay = new VBox(20);
        startOverlay.setAlignment(Pos.CENTER);
        startOverlay.setMaxSize(450, 400); // 少し大きくする
        startOverlay.setPadding(new Insets(30));

        startOverlay.setStyle(
            "-fx-background-color: rgba(0, 20, 40, 0.9); -fx-border-color: cyan; -fx-border-width: 2px; " +
            "-fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, cyan, 20, 0.5, 0, 0);"
        );

        messageLabel = new Label("SYSTEM READY");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");

        // 名前入力
        VBox nameBox = new VBox(5);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label("AGENT NAME:");
        nameLbl.setStyle("-fx-text-fill: cyan; -fx-font-family: 'Consolas';");
        nameInput = new TextField("Agent");
        nameInput.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: gray; -fx-font-family: 'Consolas';");
        nameBox.getChildren().addAll(nameLbl, nameInput);

        // ★モード選択 (3つに増やす)
        VBox diffBox = new VBox(5);
        diffBox.setAlignment(Pos.CENTER_LEFT);
        Label diffLbl = new Label("MISSION MODE:");
        diffLbl.setStyle("-fx-text-fill: cyan; -fx-font-family: 'Consolas';");
        
        HBox radios = new HBox(15);
        ToggleGroup group = new ToggleGroup();
        
        easyBtn = new RadioButton("EASY (60s)");
        easyBtn.setToggleGroup(group);
        easyBtn.setSelected(true);
        easyBtn.setStyle("-fx-text-fill: white;");

        hardBtn = new RadioButton("HARD (60s)");
        hardBtn.setToggleGroup(group);
        hardBtn.setStyle("-fx-text-fill: white;");
        
        // Endlessボタン追加
        endlessBtn = new RadioButton("ENDLESS");
        endlessBtn.setToggleGroup(group);
        endlessBtn.setStyle("-fx-text-fill: magenta; -fx-font-weight: bold;"); // 色を変えて目立たせる

        radios.getChildren().addAll(easyBtn, hardBtn, endlessBtn);
        diffBox.getChildren().addAll(diffLbl, radios);

        Button startBtn = new Button("INITIATE MISSION");
        startBtn.setPrefWidth(200);
        startBtn.setPrefHeight(40);
        String btnStyle = 
            "-fx-background-color: rgba(0, 255, 255, 0.2); -fx-text-fill: cyan; " +
            "-fx-border-color: cyan; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;";
        startBtn.setStyle(btnStyle);

        startBtn.setOnMouseEntered(e -> startBtn.setStyle(
            "-fx-background-color: rgba(0, 255, 255, 0.6); -fx-text-fill: white; -fx-border-color: white; " +
            "-fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;"
        ));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(btnStyle));

        startBtn.setOnAction(e -> gameStart());

        startOverlay.getChildren().addAll(messageLabel, nameBox, diffBox, startBtn);
    }

    private void gameStart() {
        if (isRunning) return;

        startOverlay.setVisible(false);
        playerName = nameInput.getText().isEmpty() ? "Unknown" : nameInput.getText();

        // ★モード判定
        if (easyBtn.isSelected()) {
            currentMode = GameMode.EASY;
            spawnRate = 60; // 遅め
            currentTime = 60.0; // 60秒
        } else if (hardBtn.isSelected()) {
            currentMode = GameMode.HARD;
            spawnRate = 30; // 速め
            currentTime = 60.0; // 60秒
        } else {
            currentMode = GameMode.ENDLESS;
            spawnRate = 60; // 最初は普通
            currentTime = 0.0; // 時間はカウントアップ表示用などに使う
        }

        enemies.clear();
        score = 0;
        spawnCounter = 0;
        currentLife = maxLife; // ハート5個
        isRunning = true;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> update());
            }
        }, 0, 33);

        canvas.requestFocus();
    }

    private void gameOver(boolean isTimeUp) {
        isRunning = false;
        if (timer != null) timer.cancel();
        
        startOverlay.setVisible(true);
        
        if (isTimeUp) {
            // 時間切れ＝クリア扱い（または時間切れ終了）
            messageLabel.setText("TIME UP!");
            messageLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 28px; -fx-font-weight: bold;");
        } else {
            // ハート切れ＝ゲームオーバー
            messageLabel.setText("MISSION FAILED");
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 28px; -fx-font-weight: bold;");
        }
        
        drawer.drawTitle();
    }

    private void update() {
        // ★時間管理
        if (currentMode == GameMode.ENDLESS) {
            // Endlessモードは経過時間を記録（必要なら）
            currentTime += 0.033;
            
            // ★Endlessの難易度上昇ロジック
            // スコア500点ごとに敵が早く出るようになる
            // Math.max(10, ...) は「最短でも10フレームに1回」という制限（速すぎ防止）
            int baseRate = 60;
            int difficultyLevel = score / 500; 
            spawnRate = Math.max(10, baseRate - (difficultyLevel * 5));

        } else {
            // Easy/Hardはカウントダウン
            currentTime -= 0.033; // 約0.033秒減らす
            if (currentTime <= 0) {
                currentTime = 0;
                gameOver(true); // 時間切れ終了
                return;
            }
        }

        // スポーン
        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            spawnEnemy();
            spawnCounter = 0;
        }

        double currentHeight = canvas.getHeight();
        List<WordEnemy> currentEnemies = new ArrayList<>(enemies);
        
        // ★Endlessモード用のスピード計算
        double endlessSpeedMultiplier = 1.0;
        if (currentMode == GameMode.ENDLESS) {
            // スコア1000点ごとに基本スピードが0.5ずつ速くなる
            endlessSpeedMultiplier = 1.0 + (score / 1000.0) * 0.5;
        }

        for (WordEnemy e : currentEnemies) {
            // 敵に移動命令（モードに応じた速度補正をかける）
            // Easy: 普通, Hard: 1.5倍速, Endless: だんだん速く
            double baseSpeed = 2.0;
            if (currentMode == GameMode.HARD) baseSpeed = 3.0;
            
            // moveメソッドに渡すベーススピードを調整
            e.move(baseSpeed * endlessSpeedMultiplier);
            
            if (e.y > currentHeight) {
                enemies.remove(e);
                currentLife--; 
            }
        }

        if (currentLife <= 0) gameOver(false); // ライフ0で死亡

        // 描画 (現在モードと時間を渡す)
        drawer.drawGame(score, currentLife, maxLife, enemies, currentTime, currentMode);
    }

private void spawnEnemy() {
        Random rand = new Random();
        
        // ★工夫: モードによって使う単語リストを切り替える
        String[] targetList;
        
        if (currentMode == GameMode.EASY) {
            // Easyモードなら簡単な単語リスト
            targetList = GameConstants.WORDS_EASY;
        } else if (currentMode == GameMode.HARD) {
            // Hardモードなら難しい単語リスト
            targetList = GameConstants.WORDS_HARD;
        } else {
            // Endlessモードなら...混ぜて使う？ 
            // 今回は難しい方を使う、またはランダムで決めるなど工夫できます。
            // ここでは「ランダムでどちらかのリストを選ぶ」ようにしてみます。
            if (rand.nextBoolean()) {
                targetList = GameConstants.WORDS_EASY;
            } else {
                targetList = GameConstants.WORDS_HARD;
            }
        }

        // 選ばれたリストからランダムに1つ単語を選ぶ
        String word = targetList[rand.nextInt(targetList.length)];
        
        double w = canvas.getWidth();
        // 画面端ギリギリに出ないように調整
        // 長い単語だとハミ出る可能性があるので、右側の余白を少し多め(150px)に取る
        double x = rand.nextInt(Math.max(1, (int)w - 150)) + 50;
        
        // 赤い敵の出現率
        int chance = 2; 
        if (currentMode == GameMode.ENDLESS && score > 2000) chance = 4;
        
        int type = (rand.nextInt(10) < chance) ? 1 : 0;
        enemies.add(new WordEnemy(word, x, 0, type));
    }

    private void processInput(KeyCode code) {
        if (!isRunning) return;
        String key = code.toString();
        
        WordEnemy target = null;
        double maxY = -1000;

        for (WordEnemy e : enemies) {
            if (e.word.startsWith(key) && e.y > maxY) {
                maxY = e.y;
                target = e;
            }
        }

        if (target != null) {
            if (!target.damage()) {
                enemies.remove(target);
                score += target.getScore();
            }
        }
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) timer.cancel();
        super.stop();
    }
}