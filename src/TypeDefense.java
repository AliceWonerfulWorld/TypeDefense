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
    
    // プレイヤー設定
    private String playerName = "Agent";
    private boolean isEasyMode = true;

    // UI部品
    private Button titleStartBtn; // 最初のスタートボタン
    private VBox startOverlay;    // 設定パネル
    private TextField nameInput;
    private RadioButton easyBtn, hardBtn;
    private Label messageLabel; 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        // 1. キャンバス（背景）
        canvas = new Canvas(GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        drawer = new GameDrawer(canvas);
        
        canvas.widthProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });
        canvas.heightProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });

        // 2. ★最初の「MISSION START」ボタンを作成
        createTitleButton();
        
        // 3. ★設定パネルを作成（最初は隠しておく）
        createStartOverlay();
        startOverlay.setVisible(false); // ← ここで非表示にする！

        // 重ね順: キャンバス < ボタン < パネル
        root.getChildren().addAll(titleStartBtn, startOverlay);

        Scene scene = new Scene(root, GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        scene.setOnKeyPressed(e -> processInput(e.getCode()));

        stage.setScene(scene);
        stage.setTitle("TypeDefense");
        stage.show();

        Platform.runLater(() -> drawer.drawTitle());
    }

    // ★追加: タイトルボタンを作るメソッド
    private void createTitleButton() {
        titleStartBtn = new Button("MISSION START");
        
        // サイバー風のボタンスタイル
        String btnStyle = 
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Consolas'; " +
            "-fx-background-color: rgba(0, 0, 0, 0.5); " + 
            "-fx-text-fill: cyan; " +
            "-fx-border-color: cyan; " +
            "-fx-border-width: 2px; " +
            "-fx-cursor: hand;";
            
        titleStartBtn.setStyle(btnStyle);
        
        // ホバー時の光るエフェクト
        titleStartBtn.setOnMouseEntered(e -> {
            titleStartBtn.setStyle(
                btnStyle + "-fx-background-color: rgba(0, 255, 255, 0.3); -fx-effect: dropshadow(three-pass-box, cyan, 20, 0.5, 0, 0);"
            );
        });
        titleStartBtn.setOnMouseExited(e -> titleStartBtn.setStyle(btnStyle));

        // ★クリック時の動作
        titleStartBtn.setOnAction(e -> {
            titleStartBtn.setVisible(false); // ボタンを隠す
            startOverlay.setVisible(true);   // パネルを出す
            
            // パネルのメッセージを初期状態にする
            messageLabel.setText("SYSTEM READY");
            messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        });
    }

    // 設定パネルを作るメソッド
    private void createStartOverlay() {
        startOverlay = new VBox(20);
        startOverlay.setAlignment(Pos.CENTER);
        startOverlay.setMaxSize(400, 350);
        startOverlay.setPadding(new Insets(30));

        startOverlay.setStyle(
            "-fx-background-color: rgba(0, 20, 40, 0.9);" + 
            "-fx-border-color: cyan;" +
            "-fx-border-width: 2px;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, cyan, 20, 0.5, 0, 0);"
        );

        messageLabel = new Label("SYSTEM READY");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");

        VBox nameBox = new VBox(5);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label("AGENT NAME:");
        nameLbl.setStyle("-fx-text-fill: cyan; -fx-font-family: 'Consolas';");
        
        nameInput = new TextField("Agent");
        nameInput.setStyle(
            "-fx-background-color: black;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: gray;" +
            "-fx-font-family: 'Consolas';"
        );
        nameBox.getChildren().addAll(nameLbl, nameInput);

        VBox diffBox = new VBox(5);
        diffBox.setAlignment(Pos.CENTER_LEFT);
        Label diffLbl = new Label("MISSION LEVEL:");
        diffLbl.setStyle("-fx-text-fill: cyan; -fx-font-family: 'Consolas';");
        
        HBox radios = new HBox(20);
        ToggleGroup group = new ToggleGroup();
        easyBtn = new RadioButton("EASY");
        easyBtn.setToggleGroup(group);
        easyBtn.setSelected(true);
        easyBtn.setStyle("-fx-text-fill: white;");

        hardBtn = new RadioButton("HARD");
        hardBtn.setToggleGroup(group);
        hardBtn.setStyle("-fx-text-fill: white;");

        radios.getChildren().addAll(easyBtn, hardBtn);
        diffBox.getChildren().addAll(diffLbl, radios);

        Button startBtn = new Button("INITIATE MISSION");
        startBtn.setPrefWidth(200);
        startBtn.setPrefHeight(40);
        String btnStyle = 
            "-fx-background-color: rgba(0, 255, 255, 0.2);" +
            "-fx-text-fill: cyan;" +
            "-fx-border-color: cyan;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;";
        startBtn.setStyle(btnStyle);

        startBtn.setOnMouseEntered(e -> startBtn.setStyle(
            "-fx-background-color: rgba(0, 255, 255, 0.6);" + 
            "-fx-text-fill: white;" +
            "-fx-border-color: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;"
        ));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(btnStyle));

        startBtn.setOnAction(e -> gameStart());

        startOverlay.getChildren().addAll(messageLabel, nameBox, diffBox, startBtn);
    }

    private void gameStart() {
        if (isRunning) return;

        startOverlay.setVisible(false);

        playerName = nameInput.getText().isEmpty() ? "Unknown" : nameInput.getText();
        isEasyMode = easyBtn.isSelected();
        spawnRate = isEasyMode ? 60 : 30;

        enemies.clear();
        score = 0;
        spawnCounter = 0;
        currentLife = maxLife;
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

    private void gameOver() {
        isRunning = false;
        if (timer != null) timer.cancel();
        
        // ゲームオーバー時はパネルを再表示
        startOverlay.setVisible(true);
        messageLabel.setText("MISSION FAILED");
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        
        drawer.drawTitle();
    }

    private void update() {
        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            spawnEnemy();
            spawnCounter = 0;
        }

        double currentHeight = canvas.getHeight();
        List<WordEnemy> currentEnemies = new ArrayList<>(enemies);
        
        for (WordEnemy e : currentEnemies) {
            e.move(2.0);
            if (e.y > currentHeight) {
                enemies.remove(e);
                currentLife--; 
            }
        }

        if (currentLife <= 0) gameOver();

        drawer.drawGame(score, currentLife, maxLife, enemies);
    }

   private void spawnEnemy() {
        Random rand = new Random();
        String word = GameConstants.WORDS[rand.nextInt(GameConstants.WORDS.length)];

        double w = canvas.getWidth();
        double x = rand.nextInt(Math.max(1, (int)w - 100)) + 50;

        int type = (rand.nextInt(10) < 2) ? 1 : 0;
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
                // ★変更: 敵の種類に応じたスコアを加算 (100 or 300)
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