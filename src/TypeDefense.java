import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane; // ★変更: 自由なレイアウト用
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.util.Pair; // ★追加: ダイアログの結果受け取り用
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Optional;

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
    private Button titleStartBtn; // タイトル画面の真ん中に出るボタン

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // ★重要: StackPaneを使うと「重ね合わせ」ができる
        StackPane root = new StackPane();

        // 1. キャンバス（一番奥）
        canvas = new Canvas(GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        // 2. スタートボタン（手前）
        titleStartBtn = new Button("MISSION START");
        titleStartBtn.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: rgba(0, 255, 255, 0.3); " + // 半透明の水色
            "-fx-text-fill: white; " +
            "-fx-border-color: cyan; " +
            "-fx-border-width: 2px;"
        );
        // マウスが乗った時のエフェクト（CSSみたいに記述できる）
        titleStartBtn.setOnMouseEntered(e -> titleStartBtn.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: rgba(0, 255, 255, 0.6); -fx-text-fill: white; -fx-border-color: cyan; -fx-border-width: 2px;"));
        titleStartBtn.setOnMouseExited(e -> titleStartBtn.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: rgba(0, 255, 255, 0.3); -fx-text-fill: white; -fx-border-color: cyan; -fx-border-width: 2px;"));

        // ボタンを押したら「設定ダイアログ」を開く
        titleStartBtn.setOnAction(e -> showStartDialog());
        root.getChildren().add(titleStartBtn);

        // 描画担当
        drawer = new GameDrawer(canvas);
        
        // リサイズ対応
        canvas.widthProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });
        canvas.heightProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });

        Scene scene = new Scene(root, GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        scene.setOnKeyPressed(e -> processInput(e.getCode()));

        stage.setScene(scene);
        stage.setTitle("TypeDefense");
        stage.show();

        Platform.runLater(() -> drawer.drawTitle());
    }

    // ★新機能: 設定用ダイアログを表示する
    private void showStartDialog() {
        // ダイアログの作成
        Dialog<Pair<String, Boolean>> dialog = new Dialog<>();
        dialog.setTitle("Mission Setup");
        dialog.setHeaderText("Configuring Neural Link...\n(名前と難易度を設定してください)");

        // ボタンの種類
        ButtonType loginButtonType = new ButtonType("Connect", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // レイアウト（グリッド）
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 入力部品
        TextField name = new TextField();
        name.setPromptText("Username");
        
        RadioButton easy = new RadioButton("Easy");
        RadioButton hard = new RadioButton("Hard");
        ToggleGroup group = new ToggleGroup();
        easy.setToggleGroup(group);
        hard.setToggleGroup(group);
        easy.setSelected(true);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Difficulty:"), 0, 1);
        grid.add(easy, 1, 1);
        grid.add(hard, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // 結果を変換する処理
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(name.getText(), easy.isSelected());
            }
            return null;
        });

        // 表示して待機
        Optional<Pair<String, Boolean>> result = dialog.showAndWait();
        
        // OKが押されたらゲーム開始
        result.ifPresent(pair -> {
            this.playerName = pair.getKey().isEmpty() ? "Agent" : pair.getKey();
            this.isEasyMode = pair.getValue();
            gameStart();
        });
    }

    private void gameStart() {
        if (isRunning) return;

        // ボタンを隠す
        titleStartBtn.setVisible(false);

        // 初期化
        enemies.clear();
        score = 0;
        spawnCounter = 0;
        currentLife = maxLife;
        isRunning = true;

        spawnRate = isEasyMode ? 60 : 30;

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
        
        // ボタンを再表示
        titleStartBtn.setVisible(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GAME OVER");
        alert.setHeaderText("Mission Failed");
        alert.setContentText("Agent " + playerName + "\nFinal Score: " + score);
        alert.show();

        drawer.drawTitle();
    }

    private void update() {
        // スポーン
        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            spawnEnemy();
            spawnCounter = 0;
        }

        // 移動
        double currentHeight = canvas.getHeight();
        List<WordEnemy> currentEnemies = new ArrayList<>(enemies);
        
        for (WordEnemy e : currentEnemies) {
            e.move(2.0);
            if (e.y > currentHeight) {
                enemies.remove(e);
                currentLife--; // ダメージ
            }
        }

        if (currentLife <= 0) gameOver();

        // 描画 (HP情報も渡す)
        drawer.drawGame(score, currentLife, maxLife, enemies);
    }

    private void spawnEnemy() {
        Random rand = new Random();
        String word = GameConstants.WORDS[rand.nextInt(GameConstants.WORDS.length)];
        double w = canvas.getWidth();
        double x = rand.nextInt(Math.max(1, (int)w - 100)) + 50;
        enemies.add(new WordEnemy(word, x, 0));
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
                score += 100;
            }
        }
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) timer.cancel();
        super.stop();
    }
}