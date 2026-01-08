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
    private TextField nameInput;
    private RadioButton easyBtn, hardBtn, endlessBtn;
    private Label messageLabel;
    
    // コールバック
    private Runnable onStartButtonClick;
    private Runnable onGameStart;
    
    /**
     * タイトルボタンを作成
     */
    public Button createTitleButton() {
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
        
        Label nameLbl = new Label("AGENT NAME:");
        nameLbl.setStyle("-fx-text-fill: cyan; -fx-font-family: 'Consolas';");
        
        nameInput = new TextField("Agent");
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
}
