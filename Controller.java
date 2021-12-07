package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
                                                                                                                        // creating the playground
    private static final int Columns = 7;                                                                               // this static final will be constant throughout the programme
    private static final int Rows = 6;
    private static final int Circle_diameter = 80;
    private static final String disc_color1 = "#24303E";
    private static final String disc_color2 = "#4CAA88";

    private static String Player_One = "Player One";                                                                    // this values will be changed when needed it will be replaced by player name
    private static String Player_Two = "Player Two";

    private boolean isPlayerOneTurn = true;                                                                             //so as app start only player will be allowed to play first then will be turn of player 2

    private Disc[][] insertedDiscArray = new Disc[Rows][Columns];                                                       // for structural changes ie inserting the disc

    @FXML
    public GridPane rootGridPane;                                                                                       // make sure that rootGridPane here should be same fx Id that i have defined in scene builder

    @FXML
    public Pane insertedDiscPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField playerOneTextField, playerTwoTextField;

    @FXML
    public Button setNamesButton;

    private boolean isAllowedToInsert = true;                                                                           // Flag to avoid the same colour disc added multiple times

    public void createPlayground()
    {
        Shape rectangleWithHoles = createGameStructuralGrid();                                                          // calling the method

        rootGridPane.add(rectangleWithHoles, 0, 1);                                                               // adding this playground to pane 2 with '0' column and '1' as row index
        // now call this method createPlayground() in Main.java

        List<Rectangle> rectangleList = createClickableColumn();
        for (Rectangle rectangle: rectangleList) {
            rootGridPane.add(rectangle, 0, 1);
        }

        setNamesButton.setOnAction(event ->{
            Player_One = playerOneTextField.getText();
            Player_Two = playerTwoTextField.getText();
            playerNameLabel.setText(isPlayerOneTurn? Player_One : Player_Two);
        });
    }

    private Shape createGameStructuralGrid()
    {
        Shape rectangleWithHoles = new Rectangle((Columns + 1) * Circle_diameter, (Rows + 1) * Circle_diameter);       // v : width and v1 : height
        // here we added 1 to get the extra margin at our Pane 2

        for(int row=0; row<Rows; row++)
        {
            for(int col=0; col<Columns; col++)
            {
                Circle circle = new Circle();
                circle.setRadius(Circle_diameter / 2);
                circle.setCenterX(Circle_diameter / 2);
                circle.setCenterY(Circle_diameter / 2);
                circle.setSmooth(true);                                                                                 // this will make the edges of our circle smooth

                circle.setTranslateY(row * (Circle_diameter + 5) + (Circle_diameter/4));     // cutting circles along Y axis and we added +5 to give the spacing in between holes
                circle.setTranslateX(col * (Circle_diameter + 5) + (Circle_diameter/4));     // cutting circles along X axis and we divided by 4 to give space between top and left corner of pane

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);                                             // white colored rectangle is created over the pane 2 and on which circles will be cut
        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumn()
    {
        List<Rectangle> rectangleList = new ArrayList<>();                                   // that will hold all the rectangle objects created we created a list of Rectangle
        for(int col=0; col<Columns; col++)
        {
            Rectangle rectangle = new Rectangle(Circle_diameter, (Rows + 1) * Circle_diameter);                      // v = width, v1 = height
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (Circle_diameter + 5) + (Circle_diameter/4));                                 // to make that rectangle aligned on the circle

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));                        // creating a Hover efect on rectangles
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert = false;                                                                          // stop the user to enter the same disc multiple times
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });// to enable the click events

            rectangleList.add(rectangle);                                                                               // adding all the rectangles generated to the rectangle list
        }

        return rectangleList;                                                                                           // call it in createPlayground()
    }

    private void insertDisc(Disc disc, int column){

        int row = Rows -1;                                                                                              // because the index is always smaller than the maximum value
        while(row >= 0)
        {
            if(getDiscIfPresent(row, column) == null)
                break;
            row--;                                                                                                      // else condition
        }
        if(row<0) // ie if full we cannot insert any more disc
            return ;

        insertedDiscArray[row][column] = disc;                                                                          // for structural change : for developers
        insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(column * (Circle_diameter + 5) + (Circle_diameter/4));

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);                 // to make disc fall from top to bottom
        translateTransition.setToY(row * (Circle_diameter + 5) + (Circle_diameter/4));

        translateTransition.setOnFinished(event -> {                                                                     // when the animation finishes
            isAllowedToInsert = true;                                                                                   // finally when disc is dropped allow the next player to insert the disc
            if(gameEnded(currentRow, column)){
                gameOver();
                return;                                                                                                 // this return is must to stop further iteration after one player has won the game ie second player cant continue game ie game has ended
            }

            isPlayerOneTurn = !isPlayerOneTurn;                                                                         // after player one now comes player two turn
            playerNameLabel.setText(isPlayerOneTurn? Player_One : Player_Two);                                          // setting Player names on text field when thier turn comes
        });
        translateTransition.play();
    }

    private boolean gameEnded(int row, int column)
    {

        // Vertical Points
        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3, row+3)                                              // That means (-3, +3) will have a range = -3, -2, -1, 0, 1, 2, 3 : Range of row values is from : 0, 1, 2, 3, 4, 5
                .mapToObj(r -> new Point2D(r, column))                                                                  // this statement will give list of Point2D obj : here r is parameter that will change and column remain const
                .collect(Collectors.toList());

        // Horizontal Points
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3, column+3)                                      // to generalise we used That means (-3, +3) will have a range = -3, -2, -1, 0, 1, 2, 3
                .mapToObj(col -> new Point2D(row, col))                                                                 // here the value of row will be constant and column value will be a parameter that will change
                .collect(Collectors.toList());

        // Diaginal point 1
        Point2D startPoint1 = new Point2D(row-3, column+3);                  // to find the starting point
        List<Point2D> Diagonal1Points = IntStream.rangeClosed(0, 6).mapToObj(i-> startPoint1.add(i, -i)).collect(Collectors.toList());

        // Diaginal point 2
        Point2D startPoint2 = new Point2D(row-3, column-3);                  // to find the starting point
        List<Point2D> Diagonal2Points = IntStream.rangeClosed(0, 6).mapToObj(i-> startPoint2.add(i, i)).collect(Collectors.toList());


        // Possible Combinations :
        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) || checkCombinations(Diagonal1Points) || checkCombinations(Diagonal2Points);
        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;
        for (Point2D point: points)
        {
            int rowIndexForArray = (int) point.getX();
            int colIndexForArray = (int) point.getY();

            // now lets find if there is any disc present at this IndexArray
            Disc disc = getDiscIfPresent(rowIndexForArray, colIndexForArray);
            if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn)                                                 // if the last inserted disc belong to the current player
            {
                chain++;
                if(chain == 4)
                {
                    return true;
                }
            }
            else{                                                                                                       // Remember this else statement is for outer if-else not for the inner if-else otherwise no ouput
                chain = 0;                                                                                              // ie if last inserted disc is not of currnt playes so else ie start fresh
            }
        }
        return false;                                                                                                   // ie if we have not got any combination throughout return false
    }

    private Disc getDiscIfPresent(int row, int column){                                                                 // to prevent ArrayIndexOutOfBoundException
        if(row >= Rows || row < 0 || column >= Columns || column < 0)                                                   // if row or column index is invalid
            return null;

        return insertedDiscArray[row][column];
    }

    private void gameOver()
    {
        String winner = isPlayerOneTurn ? Player_One : Player_Two;                                                      // the one who played the last turn will be the winner
        System.out.println("Winner is " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four Game");
        alert.setHeaderText("The Winner Is " + winner);
        alert.setContentText("Want To Play Again");

        ButtonType yesbtn = new ButtonType("YES");
        ButtonType nobtn = new ButtonType("NO, Exit");
        alert.getButtonTypes().setAll(yesbtn, nobtn);                                                                   // adding this two buttons to the alert dialogue

        Platform.runLater( () -> {                                                                                      // this code is written under Platform() is written just to remove showandwait() exception in output
            Optional<ButtonType> btnClicked =  alert.showAndWait();                                                     // this simply returns which button is actually clicked
            if(btnClicked.isPresent() && btnClicked.get() == yesbtn){
                // user has entered YES btn or reset the game
                resetgame();
            }else{
                //user has chosen No btn or exit the game
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetgame() {
        insertedDiscPane.getChildren().clear();                                                                         // Remove all the inserted disc from the pane

        for(int row=0; row<insertedDiscArray.length; row++)                                                             // structurally make all the elements of the insertedDiscArray[][] to null
        {
            for(int col =0; col<insertedDiscArray[row].length; col++)
            {
                insertedDiscArray[row][col] = null;
            }
        }
        isPlayerOneTurn = true;                                                                                         // after reseting of game player 1 should start playing
        playerNameLabel.setText("PLAYER_ONE");
        createPlayground();                                                                                             // prepare a fresh playground
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){                                                                           // constructor
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(Circle_diameter/2);
            setFill(isPlayerOneMove? Color.valueOf(disc_color1):Color.valueOf(disc_color2));                            // since each player has different value
            setCenterX(Circle_diameter/2);
            setCenterY(Circle_diameter/2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}