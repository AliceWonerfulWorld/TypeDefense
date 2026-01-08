import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;


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
        Button titleButton = uiManager.createTitleButton();
        StackPane.setAlignment(titleButton, javafx.geometry.Pos.CENTER);
        StackPane.setMargin(titleButton, new javafx.geometry.Insets(250, 0, 0, 0));
        
        root.getChildren().add(titleButton);
        root.getChildren().add(uiManager.createStartOverlay());
        root.getChildren().add(uiManager.createGameOverOverlay());
        root.getChildren().add(uiManager.createPauseOverlay());

        // コールバックの設定
        uiManager.setOnGameStart(() -> startGame());
        uiManager.setOnRetry(() -> retryGame());
        uiManager.setOnBackToTitle(() -> backToTitle());
        uiManager.setOnResume(() -> resumeGame());
        gameManager.setOnGameOver(() -> handleGameOver());

        // シーンの設定
        Scene scene = new Scene(root, GameConstants.INITIAL_WIDTH, GameConstants.INITIAL_HEIGHT);
        scene.setOnKeyPressed(e -> processInput(e));

        stage.setScene(scene);
        stage.setTitle("TypeDefense");
        stage.show();

        Platform.runLater(() -> drawer.drawTitle());
    }
    
   
    // ゲーム開始時処理
    private void startGame() {
        uiManager.hideStartOverlay();
        uiManager.getTitleStartBtn().setVisible(false);
        GameMode mode = uiManager.getSelectedMode();
        gameManager.startGame(mode);
        canvas.requestFocus();
    }
    
    // ゲームオーバー処理
    private void handleGameOver() {
        int finalScore = gameManager.getScore();
        boolean isTimeUp = gameManager.isTimeUp();
        
        uiManager.showGameOver(finalScore, isTimeUp);
        drawer.drawTitle();
    }
    
    // リトライ処理
    private void retryGame() {
        // 実行中のゲームを停止してから新しいゲームを開始
        if (gameManager.isRunning()) {
            gameManager.stopGame();
        }
        
        if (gameManager.isPaused()) {
            uiManager.hidePause();
        } else {
            uiManager.hideGameOver();
        }
        
        GameMode mode = uiManager.getSelectedMode();
        gameManager.startGame(mode);
        canvas.requestFocus();
    }
    
    // タイトルに戻る処理
    private void backToTitle() {
        if (gameManager.isPaused()) {
            gameManager.stopGame();
            uiManager.hidePause();
        } else {
            uiManager.hideGameOver();
        }
        uiManager.showTitleButton();
        drawer.drawTitle();
    }
    
    // ポーズ処理
    private void pauseGame() {
        gameManager.pauseGame();
        uiManager.showPause();
    }
    
    // 再開処理
    private void resumeGame() {
        uiManager.hidePause();
        gameManager.resumeGame();
        canvas.requestFocus();
    }

    private void processInput(KeyEvent event) {
        KeyCode code = event.getCode();
        
        // ESCキーでポーズ/再開
        if (code == KeyCode.ESCAPE) {
            if (gameManager.isRunning() && !gameManager.isPaused()) {
                pauseGame();
            } else if (gameManager.isPaused()) {
                resumeGame();
            }
            return;
        }
        
        // 実際に押されたキーの文字を取得
        String text = event.getText().toUpperCase();
        if (!text.isEmpty()) {
            gameManager.processInput(text);
        }
    }

    
    @Override
    public void stop() throws Exception {
        gameManager.stopGame();
        super.stop();
    }
}