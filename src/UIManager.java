import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * UI要素を管理するクラス
 * タイトルボタンとスタートオーバーレイの生成・管理を担当
 */
public class UIManager {
    
    private Button titleStartBtn;
    private VBox startOverlay;
    private VBox gameOverOverlay;
    private VBox pauseOverlay;
    private TextField nameInput;
    private RadioButton easyBtn, hardBtn, endlessBtn;
    private Label messageLabel;
    private Label scoreLabel;
    
    // コールバック
    private Runnable onStartButtonClick;
    private Runnable onGameStart;
    private Runnable onRetry;
    private Runnable onBackToTitle;
    private Runnable onResume;
    
    /**
     * タイトルボタンを作成
     */
    public Button createTitleButton() {
        titleStartBtn = new Button("GAME START");
        
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
            setMessage("SYSTEM READY", "white");
            if (onStartButtonClick != null) {
                onStartButtonClick.run();
            }
        });
        
        return titleStartBtn;
    }
    
    /**
     * スタートオーバーレイを作成
     */
    public VBox createStartOverlay() {
        startOverlay = new VBox(20);
        startOverlay.setAlignment(Pos.CENTER);
        startOverlay.setMaxSize(450, 400);
        startOverlay.setPadding(new Insets(30));
        
        startOverlay.setStyle(
            "-fx-background-color: rgba(0, 20, 40, 0.9); -fx-border-color: cyan; -fx-border-width: 2px; " +
            "-fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, cyan, 20, 0.5, 0, 0);"
        );
        
        // メッセージラベル
        messageLabel = new Label("SYSTEM READY");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        
        // 名前入力欄
        VBox nameBox = createNameInput();
        
        // モード選択
        VBox modeBox = createModeSelection();
        
        // スタートボタン
        Button startBtn = createGameStartButton();
        
        startOverlay.getChildren().addAll(messageLabel, nameBox, modeBox, startBtn);
        startOverlay.setVisible(false);
        
        return startOverlay;
    }
    
    /**
     * 名前入力欄を作成
     */
    private VBox createNameInput() {
        VBox nameBox = new VBox(5);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLbl = new Label("PLAYER NAME:");
        nameLbl.setStyle("-fx-text-fill: cyan; -fx-font-family: 'Consolas';");
        
        nameInput = new TextField("hogehoge");
        nameInput.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: gray; -fx-font-family: 'Consolas';");
        
        nameBox.getChildren().addAll(nameLbl, nameInput);
        return nameBox;
    }
    
    /**
     * モード選択欄を作成
     */
    private VBox createModeSelection() {
        VBox diffBox = new VBox(5);
        diffBox.setAlignment(Pos.CENTER_LEFT);
        
        Label diffLbl = new Label("DIFFICULTY LEVEL:");
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
        
        endlessBtn = new RadioButton("ENDLESS");
        endlessBtn.setToggleGroup(group);
        endlessBtn.setStyle("-fx-text-fill: magenta; -fx-font-weight: bold;");
        
        radios.getChildren().addAll(easyBtn, hardBtn, endlessBtn);
        diffBox.getChildren().addAll(diffLbl, radios);
        
        return diffBox;
    }
    
    /**
     * ゲーム開始ボタンを作成
     */
    private Button createGameStartButton() {
        Button startBtn = new Button("GAME START");
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
        
        startBtn.setOnAction(e -> {
            if (onGameStart != null) {
                onGameStart.run();
            }
        });
        
        return startBtn;
    }
    
    /**
     * メッセージを設定
     */
    public void setMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
    }
    
    /**
     * メッセージを大きく表示
     */
    public void setLargeMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
    }
    
    // Setters for callbacks
    public void setOnStartButtonClick(Runnable callback) {
        this.onStartButtonClick = callback;
    }
    
    public void setOnGameStart(Runnable callback) {
        this.onGameStart = callback;
    }
    
    // Getters
    public Button getTitleStartBtn() {
        return titleStartBtn;
    }
    
    public VBox getStartOverlay() {
        return startOverlay;
    }
    
    public String getPlayerName() {
        String name = nameInput.getText();
        return name.isEmpty() ? "Unknown" : name;
    }
    
    public TypeDefense.GameMode getSelectedMode() {
        if (easyBtn.isSelected()) {
            return TypeDefense.GameMode.EASY;
        } else if (hardBtn.isSelected()) {
            return TypeDefense.GameMode.HARD;
        } else {
            return TypeDefense.GameMode.ENDLESS;
        }
    }
    
    public void showStartOverlay() {
        startOverlay.setVisible(true);
    }
    
    public void hideStartOverlay() {
        startOverlay.setVisible(false);
    }
    
    public void showTitleButton() {
        titleStartBtn.setVisible(true);
    }
    
    /**
     * ゲームオーバーオーバーレイを作成
     */
    public VBox createGameOverOverlay() {
        gameOverOverlay = new VBox(25);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setMaxSize(500, 450);
        gameOverOverlay.setPadding(new Insets(40));
        
        gameOverOverlay.setStyle(
            "-fx-background-color: rgba(0, 20, 40, 0.95); -fx-border-color: cyan; -fx-border-width: 3px; " +
            "-fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, cyan, 25, 0.6, 0, 0);"
        );
        
        // メッセージラベル
        Label titleLabel = new Label("TIME UP!");
        titleLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        
        // スコア表示
        scoreLabel = new Label("FINAL SCORE: 00000");
        scoreLabel.setStyle("-fx-text-fill: cyan; -fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        
        // ボタンコンテナ
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        // リトライボタン
        Button retryBtn = createStyledButton("RETRY", "rgba(0, 255, 0, 0.2)", "lime");
        retryBtn.setOnAction(e -> {
            if (onRetry != null) {
                onRetry.run();
            }
        });
        
        // タイトルに戻るボタン
        Button titleBtn = createStyledButton("TITLE", "rgba(255, 165, 0, 0.2)", "orange");
        titleBtn.setOnAction(e -> {
            if (onBackToTitle != null) {
                onBackToTitle.run();
            }
        });
        
        buttonBox.getChildren().addAll(retryBtn, titleBtn);
        
        gameOverOverlay.getChildren().addAll(titleLabel, scoreLabel, buttonBox);
        gameOverOverlay.setVisible(false);
        
        return gameOverOverlay;
    }
    
    /**
     * スタイル付きボタンを作成
     */
    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
        
        String btnStyle = 
            "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; " +
            "-fx-border-color: " + textColor + "; -fx-border-width: 2px; " +
            "-fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand; -fx-font-family: 'Consolas';";
        btn.setStyle(btnStyle);
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + textColor + "; -fx-text-fill: black; " +
            "-fx-border-color: white; -fx-border-width: 2px; " +
            "-fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand; -fx-font-family: 'Consolas'; " +
            "-fx-effect: dropshadow(three-pass-box, " + textColor + ", 15, 0.7, 0, 0);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(btnStyle));
        
        return btn;
    }
    
    /**
     * ゲームオーバー画面を表示
     */
    public void showGameOver(int score, boolean isTimeUp) {
        // メッセージを設定
        Label titleLabel = (Label) gameOverOverlay.getChildren().get(0);
        if (isTimeUp) {
            titleLabel.setText("TIME UP!");
            titleLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        } else {
            titleLabel.setText("MISSION FAILED");
            titleLabel.setStyle("-fx-text-fill: red; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        }
        
        // スコアを設定
        scoreLabel.setText("FINAL SCORE: " + String.format("%05d", score));
        
        gameOverOverlay.setVisible(true);
    }
    
    /**
     * ゲームオーバー画面を非表示
     */
    public void hideGameOver() {
        gameOverOverlay.setVisible(false);
    }
    
    // コールバック設定
    public void setOnRetry(Runnable callback) {
        this.onRetry = callback;
    }
    
    public void setOnBackToTitle(Runnable callback) {
        this.onBackToTitle = callback;
    }
    
    /**
     * ポーズメニューを作成
     */
    public VBox createPauseOverlay() {
        pauseOverlay = new VBox(20);
        pauseOverlay.setAlignment(Pos.CENTER);
        pauseOverlay.setMaxSize(400, 350);
        pauseOverlay.setPadding(new Insets(30));
        
        pauseOverlay.setStyle(
            "-fx-background-color: rgba(0, 20, 40, 0.95); -fx-border-color: yellow; -fx-border-width: 3px; " +
            "-fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, yellow, 25, 0.6, 0, 0);"
        );
        
        // タイトル
        Label titleLabel = new Label("PAUSED");
        titleLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        
        // ボタンコンテナ
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        // 続けるボタン
        Button resumeBtn = createStyledButton("RESUME", "rgba(0, 255, 0, 0.2)", "lime");
        resumeBtn.setPrefWidth(250);
        resumeBtn.setOnAction(e -> {
            if (onResume != null) {
                onResume.run();
            }
        });
        
        // リスタートボタン
        Button restartBtn = createStyledButton("RESTART", "rgba(0, 255, 255, 0.2)", "cyan");
        restartBtn.setPrefWidth(250);
        restartBtn.setOnAction(e -> {
            if (onRetry != null) {
                onRetry.run();
            }
        });
        
        // タイトルに戻るボタン
        Button titleBtn = createStyledButton("BACK TO TITLE", "rgba(255, 165, 0, 0.2)", "orange");
        titleBtn.setPrefWidth(250);
        titleBtn.setOnAction(e -> {
            if (onBackToTitle != null) {
                onBackToTitle.run();
            }
        });
        
        buttonBox.getChildren().addAll(resumeBtn, restartBtn, titleBtn);
        
        pauseOverlay.getChildren().addAll(titleLabel, buttonBox);
        pauseOverlay.setVisible(false);
        
        return pauseOverlay;
    }
    
    /**
     * ポーズメニューを表示
     */
    public void showPause() {
        pauseOverlay.setVisible(true);
    }
    
    /**
     * ポーズメニューを非表示
     */
    public void hidePause() {
        pauseOverlay.setVisible(false);
    }
    
    /**
     * ポーズ中かどうか
     */
    public boolean isPauseVisible() {
        return pauseOverlay.isVisible();
    }
    
    /**
     * 続けるコールバックを設定
     */
    public void setOnResume(Runnable callback) {
        this.onResume = callback;
    }
}
