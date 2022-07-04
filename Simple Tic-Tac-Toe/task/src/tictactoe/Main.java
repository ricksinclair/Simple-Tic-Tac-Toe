package tictactoe;


import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    static int numX;
    static int numO;
    static int numPlaceholders;
    static List<String> winnersList;
    static List<String> boardState;

    public static void main( String[] args ) {
        resetScoreboard ();
        startGame ();
    }

    private static void resetScoreboard() {
        numX = 0;
        numO = 0;
        numPlaceholders = 0;
        winnersList = new ArrayList<> ();
    }

    private static void startGame() {
        boardState = new ArrayList<> ();
        Scanner scanner = new Scanner ( System.in );
        System.out.println ( "Enter cells: " );
        char[] userInput = scanner.nextLine ().toCharArray ();
        processGameBoard ( "         ".toCharArray () );
    }

    private static void processGameBoard( char[] userInput ) {


        if ( userInput.length == 9 ) {
            boardState = new ArrayList<> ();
            CharBuffer.wrap ( userInput ).chars ().forEachOrdered ( c -> {
                boardState.add ( String.valueOf ( (char) c ) );
            } );
            //Below formatting indexes (human number scheme) 1-9 NOT 0-8 !!


            System.out.println ( generateGameBoard ( userInput ) );
            generateWinnerList ( userInput );
            printResultText ();
        } else {
            System.out.println ( "Invalid input" );
        }
    }

    private static void printResultText() {
        System.out.println ( "Winners: " + winnersList );

        if ( isGameInvalid () ) {
            System.out.println ( "Impossible" );
            return;
        }

        if ( !hasWinner ( winnersList ) && numPlaceholders == 0 ) {
            System.out.println ( "Draw" );
            return;
        }

        if ( hasOnlyOneWinner ( winnersList ) ) {
            System.out.println ( winnersList.get ( 0 ) + " wins" );
        } else {
            System.out.println ( "Game not finished" );
            handleSecondMove ();
        }
    }


    private static void takeAccountIfCurrentCharacterIsBlankSpace( char[] gameBoard, int index ) {
        //increments numPlaceholders if there is a "_" or " ";
        if ( gameBoard[index] == '_' || gameBoard[index] == ' ' ) {
            numPlaceholders++;
        }

    }

    private static boolean isGameInvalid() {
        if ( isGameStateImpossibleByDifference () ) {
            return true;
        }
        if ( hasWinner ( winnersList ) ) {
            return !hasOnlyOneWinner ( winnersList );

        }

        return false;
    }


    private static StringBuilder generateGameBoard( char[] gameBoard ) {
        StringBuilder builder = new StringBuilder ();
        builder.append ( "---------\n" );
        Integer[] leftSideIndexes = new Integer[]{1, 4, 7};
        Integer[] rightSideIndexes = new Integer[]{3, 6, 9};

        List<Integer> leftList = Arrays.asList ( leftSideIndexes );
        List<Integer> rightList = Arrays.asList ( rightSideIndexes );
        resetScoreboard ();
        //Loop through gameBoard
        for ( int i = 0; i < gameBoard.length; i++ ) {
            if ( builder.length () == 39 ) {
                builder.append ( "\n---------" );

                return builder;

            }

            addCurrentPieceToCount ( gameBoard, i );
            //Add Left Side formatting
            addLeftSideFormatting ( builder, leftList, i );

            //Add element with leading space
            addElementWithLeadingSpace ( gameBoard[i], builder );

            //Add Right Side formatting
            addRightSideFormatting ( builder, rightList, i );
            //take account of blank spaces on play field
            takeAccountIfCurrentCharacterIsBlankSpace ( gameBoard, i );
            //Add Piece to boardState
            addPieceToBoardState ( gameBoard[i] );

        }
//        builder.delete ( 39, builder.toString ().length () );
        builder.append ( "\n---------" );

        return builder;

    }

    private static void addPieceToBoardState( char c ) {
        boardState.add ( String.valueOf ( c ) );

    }

    private static void addRightSideFormatting( StringBuilder builder, List<Integer> rightList, int i ) {
        if ( convertIndexToHumanNumberScheme ( i ) == 9 ) {
            builder.append ( " |" );
        } else if ( rightList.contains ( convertIndexToHumanNumberScheme ( i ) ) ) {
            builder.append ( " |\n" );
        }
    }

    private static int convertIndexToHumanNumberScheme( int i ) {
        return i + 1;
    }

    private static void addElementWithLeadingSpace( char c, StringBuilder builder ) {
        builder.append ( " " ).append ( c );

    }

    private static void addLeftSideFormatting( StringBuilder builder, List<Integer> leftList, int i ) {
        if ( leftList.contains ( convertIndexToHumanNumberScheme ( i ) ) ) {
            builder.append ( "|" );
        }
    }

    private static void addCurrentPieceToCount( char[] gameBoard, int i ) {
        //Add current piece to count

        if ( gameBoard[i] == 'X' ) {
            numX++;
        } else if ( gameBoard[i] == 'O' ) {
            numO++;
        }
    }

    private static boolean isGameStateImpossibleByDifference() {
//  print numX and numO
        return numX - numO > 1 || numO - numX > 1;

    }


    //create method hasWinner that checks if winnersList has a size greater than 0
    private static boolean hasWinner( List<String> winnersList ) {
        return winnersList.size () > 0;
    }


    //function to look through winnersList and determine if all strings  are the same
    private static boolean hasOnlyOneWinner( List<String> winnersList ) {
        if ( hasWinner ( winnersList ) ) {
            String firstString = winnersList.get ( 0 );
            AtomicBoolean returnValue = new AtomicBoolean ( true );
            //refactor for loop to use lambda expression
            winnersList.forEach ( string -> {
                if ( !string.equals ( firstString ) ) {
                    returnValue.set ( false );
                }
            } );

            return returnValue.get ();
        } else return false;
    }

    private static void handleSecondMove() {

        Scanner scanner = new Scanner ( System.in );
        System.out.println ( "Enter the coordinates: " );
        String userInput = scanner.nextLine ();
        boolean matches = userInput.matches ( "^\\d\\s\\d$" );
        boolean nonNumeric = userInput.matches ( "^[a-zA-Z]*$" );
        //This is a check to see if the user input is a valid coordinate
        if ( matches ) {
            int x = Integer.parseInt ( userInput.split ( "" )[0] );
            int y = Integer.parseInt ( userInput.split ( "" )[2] );
            if ( isMoveOutOfRange ( x, y ) ) {
                System.out.println ( "Coordinates should be from 1 to 3!" );
                handleSecondMove ();
            }else {
                //If the input is a valid coordinate, check if the space is empty. If empty, perform move.
                switch ( x ) {
                    case 1:
                        switch ( y ) {
                            case 1:
                                checkIfSpaceIsEmptyThenExecuteMove ( 0 );
                                break;
                            case 2:
                                checkIfSpaceIsEmptyThenExecuteMove ( 1 );
                                break;
                            case 3:
                                checkIfSpaceIsEmptyThenExecuteMove ( 2 );
                                break;
                        }
                        break;

                    case 2:
                        switch ( y ) {
                            case 1:
                                checkIfSpaceIsEmptyThenExecuteMove ( 3 );
                                break;
                            case 2:
                                checkIfSpaceIsEmptyThenExecuteMove ( 4 );
                                break;
                            case 3:
                                checkIfSpaceIsEmptyThenExecuteMove ( 5 );
                                break;
                        }
                        break;
                    case 3:
                        switch ( y ) {
                            case 1:
                                checkIfSpaceIsEmptyThenExecuteMove ( 6 );
                                break;
                            case 2:
                                checkIfSpaceIsEmptyThenExecuteMove ( 7 );
                                break;
                            case 3:
                                checkIfSpaceIsEmptyThenExecuteMove ( 8 );
                                break;
                        }
                        break;
                }
            }
//            printGameBoard ( generateGameBoard ( String.join("", boardState).toCharArray() ) );
        } else if ( nonNumeric ) {
            System.out.println ( "You should enter numbers!" );
            handleSecondMove ();

        } else if ( userInput.matches ( "^[\\d\\s]*$" ) ) {
            System.out.println ( "Coordinates should be from 1 to 3!" );
            handleSecondMove ();
        }
    }

    private static boolean isMoveOutOfRange( int x, int y ) {
        return x > 3 || y > 3 || x < 1 || y < 1;
    }

    private static void  checkIfSpaceIsEmptyThenExecuteMove( int boardIndex ) {
        if ( boardState.get ( boardIndex ).equals ( " " ) ) {
            boardState.set ( boardIndex, "X" );
            System.out.println ( generateGameBoard ( String.join ( "", boardState ).toCharArray () ) );


        } else {
            System.out.println ( "This cell is occupied! Choose another one!" );
            handleSecondMove ();
        }
    }


    private static void generateWinnerList( char[] gameBoard ) {
        if ( isTopHorizontalWin ( gameBoard ) ) {
            System.out.println ( "Top Horizontal Win" );
            winnersList.add ( gameBoard[0] + "" );
        }
        if ( isMiddleHorizontalWin ( gameBoard ) ) {
            System.out.println ( "Middle Horizontal Win" );
            winnersList.add ( gameBoard[3] + "" );
        }
        if ( isBottomHorizontalWin ( gameBoard ) ) {
            System.out.println ( "Bottom Horizontal Win" );
            winnersList.add ( gameBoard[6] + "" );
        }
        if ( isLeftVerticalWin ( gameBoard ) ) {
            System.out.println ( "Left Vertical Win" );
            winnersList.add ( gameBoard[0] + "" );
        }
        if ( isMiddleVerticalWin ( gameBoard ) ) {
            System.out.println ( "Middle Vertical Win" );
            winnersList.add ( gameBoard[1] + "" );
        }
        if ( isRightVerticalWin ( gameBoard ) ) {
            System.out.println ( "Right Vertical Win" );
            winnersList.add ( gameBoard[5] + "" );
        }
        if ( isBottomLeftToTopRightDiagonalWin ( gameBoard ) ) {
            System.out.println ( "Bottom Left to Top Right Diagonal Win" );
            winnersList.add ( gameBoard[6] + "" );
        }
        if ( isTopLeftToBottomRightDiagonalWin ( gameBoard ) ) {
            System.out.println ( "Top Left to Bottom Right Diagonal Win" );
            winnersList.add ( gameBoard[0] + "" );
        }
    }

    
    /////////////
    //ABOUT THE BELOW METHODS:
    //The below methods are used to check if there is a winner. The game board is passed in as a char array.
    //There are 8 ways to win. The methods check if there is a winner in each of the 8 ways. 
    //The char array is 9 elements long. The first element is the top left corner, the last element is the bottom right corner.
    
    /////////////
    //Top Horizontal Win
    private static boolean isTopHorizontalWin( char[] gameBoard ) {
        return gameBoard[0] == gameBoard[1] && gameBoard[1] == gameBoard[2] && gameBoard[0] != '_' && gameBoard[0] != ' ';
    }

    //Middle Horizontal Win
    private static boolean isMiddleHorizontalWin( char[] gameBoard ) {
        return gameBoard[3] == gameBoard[4] && gameBoard[4] == gameBoard[5] && gameBoard[3] != '_' && gameBoard[0] != ' ';
    }

    //Bottom Horizontal Win
    private static boolean isBottomHorizontalWin( char[] gameBoard ) {
        return gameBoard[6] == gameBoard[7] && gameBoard[7] == gameBoard[8] && gameBoard[6] != '_' && gameBoard[0] != ' ';
    }

    //Left Vertical Win
    private static boolean isLeftVerticalWin( char[] gameBoard ) {
        return gameBoard[0] == gameBoard[3] && gameBoard[3] == gameBoard[6] && gameBoard[0] != '_' && gameBoard[0] != ' ';
    }

    //Middle Vertical Win
    private static boolean isMiddleVerticalWin( char[] gameBoard ) {
        return gameBoard[1] == gameBoard[4] && gameBoard[4] == gameBoard[7] && gameBoard[1] != '_' && gameBoard[0] != ' ';
    }

    // Right Vertical Win
    private static boolean isRightVerticalWin( char[] gameBoard ) {
        return gameBoard[2] == gameBoard[5] && gameBoard[5] == gameBoard[8] && gameBoard[2] != '_' && gameBoard[0] != ' ';
    }

    //BottomLeftToTopRightDiagonal Win
    private static boolean isBottomLeftToTopRightDiagonalWin( char[] gameBoard ) {
        return gameBoard[6] == gameBoard[4] && gameBoard[4] == gameBoard[2] && gameBoard[6] != '_' && gameBoard[0] != ' ';
    }

    //TopLeftToBottomRightDiagonal Win
    private static boolean isTopLeftToBottomRightDiagonalWin( char[] gameBoard ) {
        return gameBoard[0] == gameBoard[4] && gameBoard[4] == gameBoard[8] && gameBoard[0] != '_' && gameBoard[0] != ' ';
    }
}
