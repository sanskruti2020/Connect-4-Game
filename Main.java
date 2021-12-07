package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    private Controller controller;                                                        // defining the Controller as a field variable

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();                                            // since our root Node is Grid pane in scene builder
        controller = loader.getController();

        controller.createPlayground();                                                    // calling playground method from Controller.java

        MenuBar menuBar = createMenu();                                                   // calling createMenu() and passing value returned to MenuBar
        menuBar.prefWidthProperty().bind(stage.widthProperty());                          // to help menuBar cover whole width of pane

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);                        // means from parent root of gridPane we want verry first child (index=0) ie Menu Pane
        menuPane.getChildren().add(menuBar);                                             // add menuBar child to its parent Pane 1 which is menuPane

        Scene scene = new Scene(rootGridPane);                                           // gridpane will be the first container of our Scene
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setResizable(false);
        stage.show();
    }

    private MenuBar createMenu()
    {
        // File Menu and its items ----------> Menu Bar
        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetgame());                                      // to give actions to this items we need to use setOnAction() EventHandler method

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetgame());                            // calling resetgame() method from the Controller.java
        
        SeparatorMenuItem seperator = new SeparatorMenuItem();                            // this will be a seperator between Reset and Exit
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitgame());

        fileMenu.getItems().addAll(newGame, resetGame, seperator, exitGame);              // add file items to the the File Menu remember to add seperator also

        // Help Menu and its items ----------> Menu Bar
        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Connect Four");
        aboutGame.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem seperatorItem = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutme());

        helpMenu.getItems().addAll(aboutGame, seperatorItem, aboutMe);                   // add help items to the Help Menu

        MenuBar menuBar = new MenuBar();                                                  // creating a Menu Bar and add File Menu to it
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;                                                                  // add a return type as MenuBar in public class and call this createMenu() in start()
    }

    private void aboutme() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Sanskruti Anil Jaiswal");
        alert.setContentText("I Love To Code . And develop games . Connect Four is one such game ! Enjoy The Game");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four Game");
        alert.setHeaderText("How To Play ? ");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top" +
                " into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective " +
                "of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game." +
                " The first player can always win by playing the right moves.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);                      // to expand the text size of the output
        alert.show();
    }

    private void exitgame() {                                                           // simply close the application and shut down all the resources
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}