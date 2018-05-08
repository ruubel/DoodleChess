package com.syntax_highlighters.chess;

import java.util.List;
import com.syntax_highlighters.chess.entities.IChessPiece;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.syntax_highlighters.chess.entities.Color;

/**
 * Custom game mode with sjadam rules.
 *
 * https://github.com/JonasTriki/sjadam-js
 * www.sjadam.no
 */
public class SjadamGame extends AbstractGame {
    private List<IChessPiece> lastMovePieces;
    private List<Position> jumpedFromPositions = new ArrayList<>();

    public SjadamGame() {
        this.board = new Board();
        this.board.setupNewGame();
        lastMovePieces = getPieces();
    }

    private boolean lastMoveTookEnemy(){
        if(lastMovePieces.size() != getPieces().size())
            return true;
        return false;
    }

    private boolean lastMoveWasSameColour(){
        Move m = board.getLastMove();
        if (m == null) return false;
        Color col = m.getColor(board);
        if(col == nextPlayerColor)
            return true;
        return false;
    }

    private IChessPiece lastPiece(){
        return board.getAtPosition(board.getLastMove().newPos);
    }

    private boolean lastJumpedPieceWasEnemy() {
        if (getLastJumpedPosition() == null) return false;
        Move m = board.getLastMove();
        Position mid = m.oldPos.stepsToPosition(m.newPos).get(1);
        return board.getAtPosition(mid).getColor() == nextPlayerColor.opponentColor();
    }
    
    private Position getLastJumpedPosition() {
        if (jumpedFromPositions.size() == 0) return null;
        return jumpedFromPositions.get(jumpedFromPositions.size()-1);
    }


    /**
     * Get a list of all the possible moves that can be made during this turn.
     *
     * Takes care of any restrictions put in place due to stateful changes in
     * the SjadamGame.
     *
     * @return A list of all the possible moves that can be made by the current
     * player
     */
    @Override
    public List<Move> allPossibleMoves() {
        List<IChessPiece> pieces = getPieces().stream()
            .filter(p -> p.getColor() == nextPlayerColor())
            .filter(p -> getLastJumpedPosition() == null || p == lastPiece())
            .collect(Collectors.toList());
        List<Move> allMoves = pieces.stream()
            .flatMap(p -> p.allPossibleMoves(getBoard()).stream())
            .collect(Collectors.toList());

        for (IChessPiece piece : pieces) {
            List<Move> sjadamJumps = getSjadamJumps(piece);
            Position oldPos = getLastJumpedPosition();
            if (!lastJumpedPieceWasEnemy() || oldPos == null) {
                allMoves.addAll(sjadamJumps);
            }
            else {
                allMoves.add(new Move(piece.getPosition(), oldPos, board));
            }
        }
        return allMoves;
    }

    /**
     * Helper method: get all the possible jumps the piece can make.
     *
     * Does not do any validity checks.
     *
     * @param piece The piece to get jumps for
     * @return All jumps the piece can make
     */
    private List<Move> getSjadamJumps(IChessPiece piece) {
        List<Move> moves = new ArrayList<>();
        tryAddSjadamMove(piece, p -> p.south(1), moves);
        tryAddSjadamMove(piece, p -> p.north(1), moves);
        tryAddSjadamMove(piece, p -> p.west(1), moves);
        tryAddSjadamMove(piece, p -> p.east(1), moves);
        tryAddSjadamMove(piece, p -> p.northwest(1), moves);
        tryAddSjadamMove(piece, p -> p.northeast(1), moves);
        tryAddSjadamMove(piece, p -> p.southwest(1), moves);
        tryAddSjadamMove(piece, p -> p.southeast(1), moves);
        return moves;
    }

    /**
     * Helper method: add move jumping over piece if possible.
     */
    private void tryAddSjadamMove(IChessPiece piece, Position.Manipulator dir, List<Move> list) {
        Position p = piece.getPosition();
        Position next = dir.transform(p);
        Position next2 = dir.transform(next);
        if (!isOnBoard(next2)) return;
        if (board.isOccupied(next) && ! board.isOccupied(next2))
            list.add(new Move(p, next2, board));
    }

    /**
     * Get a list of all possible moves a given piece can make this turn.
     *
     * Takes care of any restrictions put in place due to stateful changes in
     * the SjadamGame.
     *
     * @return A list of the possible moves that can be made by the current
     * player using this piece.
     */
    @Override
    public List<Move> allPossibleMoves(IChessPiece piece) {
        return allPossibleMoves().stream()
            .filter(m -> m.getPiece(board) == piece)
            .collect(Collectors.toList());
    }

    /**
     * Validate and perform a move.
     * @param from Coordinate from
     * @param to Coordinate to
     * @return If the move was valid and performed.
     */
    public List<Move> performMove(Position from, Position to) {
        IChessPiece piece = getPieceAtPosition(from);
        if (piece == null) return new ArrayList<>();
        List<Move> movesToPos = allPossibleMoves(piece).stream()
            .filter(m -> m.getPosition().equals(to))
            .collect(Collectors.toList());
        
        if (movesToPos.size() == 1) {
            Move move = movesToPos.get(0);
            performMove(move);
        }
        return movesToPos;
    }

    @Override
    public void performMove(Move move) {
        Color col = nextPlayerColor();
        IChessPiece piece = move.getPiece(board);
        boolean endTurn = piece.canMoveTo(move.newPos, board);
        if (endTurn) { // this is a regular chess move
            col = col.opponentColor(); // end turn
        }
        super.performMove(move);
        
        Position oldPos = getLastJumpedPosition();
        if (endTurn || !jumpedFromPositions.isEmpty()
                && move.newPos.equals(jumpedFromPositions.get(0))) {
            jumpedFromPositions = new ArrayList<>(); // clear jumped positions
        }
        else if (move.newPos.equals(oldPos)) {
            // drop last position from list of jumped positions
            jumpedFromPositions.remove(jumpedFromPositions.size()-1);
        }
        else {
            // put old piece position in list of jumped positions
            jumpedFromPositions.add(move.oldPos);
        }
        nextPlayerColor = col; // set next player color to the correct color
    }

    @Override
    public boolean canMoveTo(IChessPiece piece, Position pos) {
        return allPossibleMoves(piece).stream().anyMatch(m -> m.getPosition().equals(pos));
    }

    @Override
    public SjadamGame copy() {
        throw new RuntimeException("Copy not implemented for sjadam");
    }

    public void endTurn() {
        nextPlayerColor = nextPlayerColor.opponentColor();
    }
    
    public boolean hasJumped() {
        return getLastJumpedPosition() != null;
    }
}
