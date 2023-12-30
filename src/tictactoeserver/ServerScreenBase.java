package tictactoeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.xml.bind.DatatypeConverter;
import org.apache.derby.jdbc.ClientDriver;
import static tictactoeserver.PlayerHandler.playersConnections;
import static tictactoeserver.ServerConnection.con;

public class ServerScreenBase extends Pane {

    protected final Label labelTitle;
    protected final Label lablOfflineUsers;
    protected final Label lablOnlineUsers;
    protected final Button btnStartStop;
    protected final Line line;
    protected final Line line0;
    protected final Line line1;
    protected final PieChart pieChart;
    protected final Label noOnlineLabel;
    protected final Label noOfflineLabel;
    protected final Button buttonExit;
    protected final Button buttonMinimize;
    ServerConnection serviceThread;
    boolean isRunning;
    boolean started;
    ObservableList<PieChart.Data> pieChartData;

    public ServerScreenBase() {

        labelTitle = new Label();
        lablOfflineUsers = new Label();
        lablOnlineUsers = new Label();
        btnStartStop = new Button();
        line = new Line();
        line0 = new Line();
        line1 = new Line();
        pieChart = new PieChart();
        noOnlineLabel = new Label();
        noOfflineLabel = new Label();
        buttonExit = new Button();
        buttonMinimize = new Button();
        serviceThread = new ServerConnection();
        isRunning = false;
        started = false;

        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(600.0);
        setPrefWidth(800.0);
        getStyleClass().add("backgroundColor");
        getStylesheets().add("/css/style.css");

        labelTitle.setLayoutX(28.0);
        labelTitle.setLayoutY(14.0);
        labelTitle.setText("Online Users");
        labelTitle.setTextFill(javafx.scene.paint.Color.WHITE);
        labelTitle.setFont(new Font("Segoe UI Bold", 40.0));

        lablOfflineUsers.setLayoutX(529.0);
        lablOfflineUsers.setLayoutY(423.0);
        lablOfflineUsers.setText("Offline Users");
        lablOfflineUsers.setTextFill(javafx.scene.paint.Color.WHITE);
        lablOfflineUsers.setFont(new Font("Segoe UI Bold", 25.0));

        lablOnlineUsers.setLayoutX(159.0);
        lablOnlineUsers.setLayoutY(423.0);
        lablOnlineUsers.setText("Online Users");
        lablOnlineUsers.setTextFill(javafx.scene.paint.Color.WHITE);
        lablOnlineUsers.setFont(new Font("Segoe UI Bold", 25.0));
        lablOnlineUsers.setOnMouseClicked((e) -> {
            System.out.println(playersConnections.size() + "this is the size of the array");
            System.out.println(playersConnections.get(0).toString());
            System.out.println(playersConnections.get(1).toString());

            Game game = new Game(playersConnections.get(0), playersConnections.get(1));
        });

        btnStartStop.setLayoutX(249.0);
        btnStartStop.setLayoutY(491.0);
        btnStartStop.setMnemonicParsing(false);
        btnStartStop.setPrefHeight(51.0);
        btnStartStop.setPrefWidth(280.0);
        btnStartStop.setStyle("-fx-background-color: #CF8A9B; -fx-border-color: #CF8A9B; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-width: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        btnStartStop.setText("Start Service");
        btnStartStop.setTextFill(javafx.scene.paint.Color.WHITE);
        btnStartStop.setFont(new Font("Segoe UI Bold", 25.0));

        line.setEndX(89.0);
        line.setLayoutX(171.0);
        line.setLayoutY(76.0);
        line.setStartX(-138.0);
        line.setStroke(javafx.scene.paint.Color.valueOf("#8a559b"));
        line.setStrokeWidth(7.0);

        line0.setEndX(51.66455078125);
        line0.setEndY(-2.0);
        line0.setLayoutX(253.0);
        line0.setLayoutY(457.0);
        line0.setStartX(-117.0);
        line0.setStartY(-2.0);
        line0.setStroke(javafx.scene.paint.Color.valueOf("#8a559b"));
        line0.setStrokeWidth(5.0);

        line1.setEndX(70.705078125);
        line1.setEndY(-2.0);
        line1.setLayoutX(607.0);
        line1.setLayoutY(457.0);
        line1.setStartX(-102.0);
        line1.setStartY(-2.0);
        line1.setStroke(javafx.scene.paint.Color.valueOf("#db4f7e"));
        line1.setStrokeWidth(5.0);

        pieChart.setLayoutX(192.0);
        pieChart.setLayoutY(88.0);
        pieChart.setPrefHeight(325.0);
        pieChart.setPrefWidth(395.0);

        noOnlineLabel.setLayoutX(134.0);
        noOnlineLabel.setLayoutY(423.0);
        noOnlineLabel.setText("0");
        noOnlineLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        noOnlineLabel.setFont(new Font("Segoe UI Bold", 25.0));

        noOfflineLabel.setLayoutX(503.0);
        noOfflineLabel.setLayoutY(423.0);
        noOfflineLabel.setText("0");
        noOfflineLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        noOfflineLabel.setFont(new Font("Segoe UI Bold", 25.0));

        buttonExit.setLayoutX(747.0);
        buttonExit.setLayoutY(14.0);
        buttonExit.setMnemonicParsing(false);
        buttonExit.setStyle("-fx-background-radius: 30; -fx-background-color: e8ccd5;");
        buttonExit.setText("X");
        buttonExit.setFont(new Font("Gill Sans MT Bold Italic", 19.0));
        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });

        buttonMinimize.setLayoutX(708.0);
        buttonMinimize.setLayoutY(14.0);
        buttonMinimize.setMnemonicParsing(false);
        buttonMinimize.setPrefHeight(17.0);
        buttonMinimize.setPrefWidth(31.0);
        buttonMinimize.setStyle("-fx-background-radius: 30; -fx-background-color: e8ccd5;");
        buttonMinimize.setText("-");
        buttonMinimize.setFont(new Font("Gill Sans MT", 19.0));
        buttonMinimize.setOnAction((ActionEvent event) -> {
            Stage stage = (Stage) buttonMinimize.getScene().getWindow();
            stage.setIconified(true);
        });

        getChildren().add(labelTitle);
        getChildren().add(lablOfflineUsers);
        getChildren().add(lablOnlineUsers);
        getChildren().add(btnStartStop);
        getChildren().add(line);
        getChildren().add(line0);
        getChildren().add(line1);
        getChildren().add(pieChart);
        getChildren().add(noOnlineLabel);
        getChildren().add(noOfflineLabel);
        getChildren().add(buttonExit);
        getChildren().add(buttonMinimize);

        pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Online", 0),
                new PieChart.Data("Offline", 0));
        pieChart.setData(pieChartData);

        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #8a559b;");
        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #db4f7e;");
        pieChart.setLegendVisible(false);

        btnStartStop.setOnAction((event) -> {
            if (isRunning) {
                stopService();
                isRunning = false;
                btnStartStop.setText("Start Service");
            } else {
                startService();
                isRunning = true;
                btnStartStop.setText("Stop Service");
            }
        });

        // Schedule the task to update counts every 10 seconds
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateCountsFromDatabase, 0, 5, TimeUnit.SECONDS);
        serviceThread.startConnectionDB();
        initializeBD();
    }

    private void updateCountsFromDatabase() {
    try {
        String query = "SELECT COUNT(*) FROM PLAYER WHERE AVAILABLE = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            // Get cont of online users
            statement.setBoolean(1, true);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int onlineCount = resultSet.getInt(1);
                    updateOnlineCount(onlineCount);
                }
            }

            // Get count of offline users
            statement.setBoolean(1, false);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int offlineCount = resultSet.getInt(1);
                    updateOfflineCount(offlineCount);
                }
            }
        }
    } catch (SQLException ex) {
        System.out.println("SQLException");
    } catch (Exception e) {
        System.out.println("Exception");
    }
}


    private void updateOnlineCount(int count) {
        Platform.runLater(() -> {
            noOnlineLabel.setText(Integer.toString(count));
            pieChartData.get(0).setPieValue(count);
        });
    }

    private void updateOfflineCount(int count) {
        Platform.runLater(() -> {
            noOfflineLabel.setText(Integer.toString(count));
            pieChartData.get(1).setPieValue(count);
        });
    }

    private void startService() {
        
        if (started) {
            serviceThread.resume();
        } else {
            serviceThread.start();
            started = true;
        }
    }
    
//        ArrayList<String> responseServer = new ArrayList<>();
//                responseServer.add("closed");
//                Gson gsonServer = new GsonBuilder().create();
//                String responseServerJson = gsonServer.toJson(responseServer);
//        PlayerHandler.sendRequest(responseServerJson);

    public void stopService() {
        serviceThread.suspend();
        PlayerHandler.playersConnections.forEach(player -> {
            player.closeConnections();
        });
    }

//     public void startConnectionDB(){
//        try {
//            DriverManager.registerDriver(new ClientDriver());
//            con = DriverManager.getConnection("jdbc:derby://localhost:1527/TicTacToe", "root", "root");
//                    System.out.println("Database connection established successfully.");
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ServerScreenBase.class.getName()).log(Level.SEVERE, null, ex);
//                    System.err.println("Error connecting to the database: " + ex.getMessage());
//        }
//    }

    private void initializeBD() {
        try {
            PreparedStatement pst1 = ServerConnection.con.prepareStatement("UPDATE Player SET AVAILABLE=FALSE,Isplaying=FALSE");
            int res = pst1.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ServerScreenBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}





//package tictactoeserver;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.chart.PieChart;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Pane;
//import javafx.scene.shape.Line;
//import javafx.scene.text.Font;
//import javafx.stage.Stage;
//import javax.xml.bind.DatatypeConverter;
//import org.apache.derby.jdbc.ClientDriver;
//import static tictactoeserver.PlayerHandler.playersConnections;
//import static tictactoeserver.ServerConnection.con;
//
//public class ServerScreenBase extends Pane {
//
//    protected final Label labelTitle;
//    protected final Label lablOfflineUsers;
//    protected final Label lablOnlineUsers;
//    protected final Button btnStartStop;
//    protected final Line line;
//    protected final Line line0;
//    protected final Line line1;
//    protected final PieChart pieChart;  
//    protected final Label noOnlineLabel;
//    protected final Label noOfflineLabel;
//    protected final Button buttonExit;
//    protected final Button buttonMinimize;
//    ServerConnection serviceThread;
//    boolean isRunning;
//    boolean started;
//
//    public ServerScreenBase() {
//
//        labelTitle = new Label();
//        lablOfflineUsers = new Label();
//        lablOnlineUsers = new Label();
//        btnStartStop = new Button();
//        line = new Line();
//        line0 = new Line();
//        line1 = new Line();
//        pieChart = new PieChart();
//        noOnlineLabel = new Label();
//        noOfflineLabel = new Label();
//        buttonExit = new Button();
//        buttonMinimize = new Button();
//        serviceThread = new ServerConnection();
//        isRunning = false;
//        started = false;
//        
//
//        setMaxHeight(USE_PREF_SIZE);
//        setMaxWidth(USE_PREF_SIZE);
//        setMinHeight(USE_PREF_SIZE);
//        setMinWidth(USE_PREF_SIZE);
//        setPrefHeight(600.0);
//        setPrefWidth(800.0);
//        getStyleClass().add("backgroundColor");
//        getStylesheets().add("/css/style.css");
//
//        labelTitle.setLayoutX(28.0);
//        labelTitle.setLayoutY(14.0);
//        labelTitle.setText("Online Users");
//        labelTitle.setTextFill(javafx.scene.paint.Color.WHITE);
//        labelTitle.setFont(new Font("Segoe UI Bold", 40.0));
//
//        lablOfflineUsers.setLayoutX(529.0);
//        lablOfflineUsers.setLayoutY(423.0);
//        lablOfflineUsers.setText("Offline Users");
//        lablOfflineUsers.setTextFill(javafx.scene.paint.Color.WHITE);
//        lablOfflineUsers.setFont(new Font("Segoe UI Bold", 25.0));
//
//        lablOnlineUsers.setLayoutX(159.0);
//        lablOnlineUsers.setLayoutY(423.0);
//        lablOnlineUsers.setText("Online Users");
//        lablOnlineUsers.setTextFill(javafx.scene.paint.Color.WHITE);
//        lablOnlineUsers.setFont(new Font("Segoe UI Bold", 25.0));
//        lablOnlineUsers.setOnMouseClicked((e)->{
//            System.out.println(playersConnections.size()+"this is the size of the array");
//            System.out.println(playersConnections.get(0).toString());
//                        System.out.println(playersConnections.get(1).toString());
//
//            Game game = new Game(playersConnections.get(0), playersConnections.get(1));
//        });
//
//        btnStartStop.setLayoutX(249.0);
//        btnStartStop.setLayoutY(491.0);
//        btnStartStop.setMnemonicParsing(false);
//        btnStartStop.setPrefHeight(51.0);
//        btnStartStop.setPrefWidth(280.0);
//        btnStartStop.setStyle("-fx-background-color: #CF8A9B; -fx-border-color: #CF8A9B; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-width: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
//        btnStartStop.setText("Start Service");
//        btnStartStop.setTextFill(javafx.scene.paint.Color.WHITE);
//        btnStartStop.setFont(new Font("Segoe UI Bold", 25.0));
//
//        line.setEndX(89.0);
//        line.setLayoutX(171.0);
//        line.setLayoutY(76.0);
//        line.setStartX(-138.0);
//        line.setStroke(javafx.scene.paint.Color.valueOf("#8a559b"));
//        line.setStrokeWidth(7.0);
//
//        line0.setEndX(51.66455078125);
//        line0.setEndY(-2.0);
//        line0.setLayoutX(253.0);
//        line0.setLayoutY(457.0);
//        line0.setStartX(-117.0);
//        line0.setStartY(-2.0);
//        line0.setStroke(javafx.scene.paint.Color.valueOf("#8a559b"));
//        line0.setStrokeWidth(5.0);
//
//        line1.setEndX(70.705078125);
//        line1.setEndY(-2.0);
//        line1.setLayoutX(607.0);
//        line1.setLayoutY(457.0);
//        line1.setStartX(-102.0);
//        line1.setStartY(-2.0);
//        line1.setStroke(javafx.scene.paint.Color.valueOf("#db4f7e"));
//        line1.setStrokeWidth(5.0);
//
//        pieChart.setLayoutX(192.0);
//        pieChart.setLayoutY(88.0);
//        pieChart.setPrefHeight(325.0);
//        pieChart.setPrefWidth(395.0);
//
//        noOnlineLabel.setLayoutX(134.0);
//        noOnlineLabel.setLayoutY(423.0);
//        noOnlineLabel.setText("6");
//        noOnlineLabel.setTextFill(javafx.scene.paint.Color.WHITE);
//        noOnlineLabel.setFont(new Font("Segoe UI Bold", 25.0));
//
//        noOfflineLabel.setLayoutX(503.0);
//        noOfflineLabel.setLayoutY(423.0);
//        noOfflineLabel.setText("3");
//        noOfflineLabel.setTextFill(javafx.scene.paint.Color.WHITE);
//        noOfflineLabel.setFont(new Font("Segoe UI Bold", 25.0));
//
//        buttonExit.setLayoutX(747.0);
//        buttonExit.setLayoutY(14.0);
//        buttonExit.setMnemonicParsing(false);
//        buttonExit.setStyle("-fx-background-radius: 30; -fx-background-color: e8ccd5;");
//        buttonExit.setText("X");
//        buttonExit.setFont(new Font("Gill Sans MT Bold Italic", 19.0));
//        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Platform.exit();
//            }
//        });
//
//        buttonMinimize.setLayoutX(708.0);
//        buttonMinimize.setLayoutY(14.0);
//        buttonMinimize.setMnemonicParsing(false);
//        buttonMinimize.setPrefHeight(17.0);
//        buttonMinimize.setPrefWidth(31.0);
//        buttonMinimize.setStyle("-fx-background-radius: 30; -fx-background-color: e8ccd5;");
//        buttonMinimize.setText("-");
//        buttonMinimize.setFont(new Font("Gill Sans MT", 19.0));
//        buttonMinimize.setOnAction((ActionEvent event) -> {
//            Stage stage = (Stage) buttonMinimize.getScene().getWindow();
//            stage.setIconified(true);
//        });
//
//        getChildren().add(labelTitle);
//        getChildren().add(lablOfflineUsers);
//        getChildren().add(lablOnlineUsers);
//        getChildren().add(btnStartStop);
//        getChildren().add(line);
//        getChildren().add(line0);
//        getChildren().add(line1);
//        getChildren().add(pieChart);
//        getChildren().add(noOnlineLabel);
//        getChildren().add(noOfflineLabel);
//        getChildren().add(buttonExit);
//        getChildren().add(buttonMinimize);
//        
//        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
//                new PieChart.Data("Online", 6),
//                new PieChart.Data("Offline", 3));
//        pieChart.setData(pieChartData);
//        
//        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #8a559b;");
//        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #db4f7e;");
//        pieChart.setLegendVisible(false);
//        
//        btnStartStop.setOnAction((event)->{
//            if(isRunning){
//                stopService();
//                isRunning = false;
//                btnStartStop.setText("Start Service");
//            }
//            else{
//                startService();
//                isRunning = true;
//                btnStartStop.setText("Stop Service");
//            }
//        });
//        
//        
//    }
//    private void startService(){
//        serviceThread.startConnectionDB();
//        if(started){
//            serviceThread.resume();
//        }
//        else{
//            serviceThread.start();
//            started = true;
//        }
//    }
//    
//    public void stopService(){
//        serviceThread.suspend();
//        PlayerHandler.playersConnections.forEach(player -> {
//            ArrayList<String> response = new ArrayList<>();
//            response.add("shutdown");
//            Gson gson = new GsonBuilder().create();
//            String responseJSon = gson.toJson(response);
//            player.closeConnections();
//        });
//    }
//    public void startConnectionDB(){
//        try {
//            DriverManager.registerDriver(new ClientDriver());
//            con = DriverManager.getConnection("jdbc:derby://localhost:1527/DBforTicTacToe", "a", "a");
//        } catch (SQLException ex) {
//            Logger.getLogger(ServerScreenBase.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }   
//}
