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
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class TypeDefense extends Application {

    private Canvas canvas;
    private GameDrawer drawer; // ★描画担当クラス
    private Timer timer;
    
    private List<WordEnemy> enemies = new ArrayList<>();
    private int score = 0;
    private int spawnCounter = 0;
    private int spawnRate = 60;

    // UI部品
    private TextField nameField;
    private Button startButton;
    private RadioButton easyBtn, hardBtn;
    private ProgressBar lifeBar;
    private VBox topContainer;
    
    private boolean isRunning = false;
    private int maxLife = 5;
    private int currentLife;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // --- UIの構築 ---
        topContainer = new VBox();
        MenuBar menuBar = createMenuBar();
        HBox controls = createControlPanel();
        
        topContainer.getChildren().addAll(menuBar, controls);
        root.setTop(topContainer);

        // --- キャンバスの構築 ---
        // サイズは GameConstants から取得
        canvas = new Canvas(GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT - 50);
        root.setCenter(canvas);
        
        // リサイズ対応（ウィンドウサイズに合わせてキャンバスを伸縮）
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty().subtract(topContainer.heightProperty()));

        // ★描画担当の作成 (GameDrawerにお任せ)
        drawer = new GameDrawer(canvas);

        // リサイズ時の再描画
        canvas.widthProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });
        canvas.heightProperty().addListener(e -> { if(!isRunning) drawer.drawTitle(); });

        Scene scene = new Scene(root, GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        scene.setOnKeyPressed(e -> processInput(e.getCode()));

        stage.setScene(scene);
        stage.setTitle("TypeDefense (Refactored)");
        stage.show();

        // 最初の描画
        Platform.runLater(() -> drawer.drawTitle());
    }

    // メニューバー作成（コード整理のため分離）
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("ファイル");
        MenuItem exitItem = new MenuItem("終了");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    // 操作パネル作成（コード整理のため分離）
    private HBox createControlPanel() {
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("-fx-background-color: #eee;");

        Label nameLabel = new Label("名前:");
        nameField = new TextField();
        nameField.setPromptText("プレイヤー名");
        nameField.setPrefWidth(100);

        ToggleGroup group = new ToggleGroup();
        easyBtn = new RadioButton("Easy");
        easyBtn.setToggleGroup(group);
        easyBtn.setSelected(true);
        hardBtn = new RadioButton("Hard");
        hardBtn.setToggleGroup(group);

        startButton = new Button("ゲーム開始");
        startButton.setOnAction(e -> gameStart());

        Label hpLabel = new Label("HP:");
        hpLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lifeBar = new ProgressBar(1.0);
        lifeBar.setPrefWidth(100);
        lifeBar.setStyle("-fx-accent: red;");

        controls.getChildren().addAll(nameLabel, nameField, easyBtn, hardBtn, startButton, hpLabel, lifeBar);
        return controls;
    }

    private void gameStart() {
        if (isRunning) return;

        enemies.clear();
        score = 0;
        spawnCounter = 0;
        currentLife = maxLife;
        lifeBar.setProgress(1.0);
        isRunning = true;

        setControlsDisabled(true);

        spawnRate = easyBtn.isSelected() ? 60 : 30;

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
        setControlsDisabled(false);

        String name = nameField.getText().isEmpty() ? "名無し" : nameField.getText();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ゲームオーバー");
        alert.setHeaderText("お疲れさまでした！");
        alert.setContentText(name + "さんのスコアは " + score + " 点です！");
        alert.show();

        drawer.drawTitle(); // ★描画担当にお願いする
    }

    private void update() {
        // 1. スポーン処理
        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            spawnEnemy();
            spawnCounter = 0;
        }

        // 2. 移動処理
        double currentHeight = canvas.getHeight();
        List<WordEnemy> currentEnemies = new ArrayList<>(enemies);
        
        for (WordEnemy e : currentEnemies) {
            e.move(2.0);
            if (e.y > currentHeight) {
                enemies.remove(e);
                damagePlayer();
            }
        }

        if (currentLife <= 0) gameOver();

        // 3. 描画処理 (★ここが超スッキリ！)
        drawer.drawGame(score, enemies);
    }

    private void spawnEnemy() {
        Random rand = new Random();
        // ★定数クラスから単語リストを使う
        String word = GameConstants.WORDS[rand.nextInt(GameConstants.WORDS.length)];
        
        double w = canvas.getWidth();
        // 画面幅に応じたランダム位置
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

    private void damagePlayer() {
        currentLife--;
        lifeBar.setProgress((double)currentLife / maxLife);
    }

    private void setControlsDisabled(boolean disable) {
        nameField.setDisable(disable);
        easyBtn.setDisable(disable);
        hardBtn.setDisable(disable);
        startButton.setDisable(disable);
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) timer.cancel();
        super.stop();
    }
}