package com.syntax_highlighters.chess.chesspiece;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.syntax_highlighters.chess.Board;
import com.syntax_highlighters.chess.Color;
import com.syntax_highlighters.chess.move.Move;
import com.syntax_highlighters.chess.Position;

/**
 * Bishop chess piece.
 */
public class ChessPieceBishop extends AbstractChessPiece {
    /**
     * Create a bishop at the given position with the given color.
     *
     * @param pos The position to place the bishop at
     * @param color The color of the piece
     */
    public ChessPieceBishop(Position pos, Color color) {
        super(pos, color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toChessNotation() {
        return "B";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getPositionScoreTable() {
        return new int[]{
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   5,   5,  10,  10,   5,   5, -10,
            -10,   0,  10,  10,  10,  10,   0, -10,
            -10,  10,  10,  10,  10,  10,  10, -10,
            -10,   5,   0,   0,   0,   0,   5, -10,
            -20, -10, -10, -10, -10, -10, -10, -20,
        };
    }

    /**
     * Return all possible moves the bishop can make.
     *
     * A bishop can move any number of steps in any diagonal direction, but only
     * as long as the path is not blocked by a piece. If it is blocked by an
     * enemy piece, the bishop may capture that piece but go no further.
     *
     * @param board The current state of the board
     * @return A List of all the possible moves the piece can make
     */
    @Override
    public List<Move> allPossibleMoves(Board board) {
        List<Move> ne = movesInDirection(1, 1, board);
        List<Move> nw = movesInDirection(-1, 1, board);
        List<Move> se = movesInDirection(-1, -1, board);
        List<Move> sw = movesInDirection(1, -1, board);
        return Stream.of(ne, nw, se, sw)
                .flatMap(Collection::stream)
                .filter(m -> board.moveDoesntPutKingInCheck(m, color))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IChessPiece copy() {
        return new ChessPieceBishop(this.getPosition(), this.getColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPieceScore() {
        return 330;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssetName() {
        return "bishop_white.png";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean threatens(Position p, Board b) {
        return threatenDirection(1, 1, p, b)
                || threatenDirection(1, -1, p, b)
                || threatenDirection(-1, -1, p, b)
                || threatenDirection(-1, 1, p, b);
    }
}
