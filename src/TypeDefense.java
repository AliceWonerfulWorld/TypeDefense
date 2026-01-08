import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

/**
 * TypeDefense - メインアプリケーションクラス
 * 各マネージャークラスを統合し、ゲームを起動
 */
public class TypeDefense extends Application {

    // モード定義
    public enum GameMode {
        EASY, HARD, ENDLESS
    }

    private Canvas canvas;
    private GameDrawer drawer;
    private GameManager gameManager;
    private UIManager uiManager; 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        // Canvasの初期化
        canvas = new Canvas(GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        // 各マネージャーの初期化
        drawer = new GameDrawer(canvas);
        gameManager = new GameManager(canvas, drawer);
        uiManager = new UIManager();
        
        // リサイズ時の処理
        canvas.widthProperty().addListener(e -> {
            if (!gameManager.isRunning()) drawer.drawTitle();
        });
        canvas.heightProperty().addListener(e -> {
            if (!gameManager.isRunning()) drawer.drawTitle();
        });

        // UI要素の作成
        root.getChildren().add(uiManager.createTitleButton());
        root.getChildren().add(uiManager.createStartOverlay());

        // コールバックの設定
        uiManager.setOnGameStart(() -> startGame());
        gameManager.setOnGameOver(() -> handleGameOver());

        // シーンの設定
        Scene scene = new Scene(root, GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        scene.setOnKeyPressed(e -> processInput(e.getCode()));

        stage.setScene(scene);
        stage.setTitle("TypeDefense - Refactored Version");
        stage.show();

        Platform.runLater(() -> drawer.drawTitle());
    }
    
    /**
     * ゲーム開始処理
     */
    private void startGame() {
        uiManager.hideStartOverlay();
        GameMode mode = uiManager.getSelectedMode();
        gameManager.startGame(mode);
        canvas.requestFocus();
    }
    
    /**
     * ゲームオーバー処理
     */
    private void handleGameOver() {
        uiManager.showStartOverlay();
        
        if (gameManager.isTimeUp()) {
            uiManager.setLargeMessage("TIME UP!", "lime");
        } else {
            uiManager.setLargeMessage("MISSION FAILED", "red");
        }
        
        drawer.drawTitle();
    }
    
    /**
     * キー入力処理
     */
    private void processInput(KeyCode code) {
        gameManager.processInput(code.toString());
    }

    
    @Override
    public void stop() throws Exception {
        gameManager.stopGame();
        super.stop();
    }
}