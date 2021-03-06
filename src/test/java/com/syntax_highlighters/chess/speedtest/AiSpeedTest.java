package com.syntax_highlighters.chess.speedtest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.syntax_highlighters.chess.game.ChessGame;
import com.syntax_highlighters.chess.ai.AiDifficulty;

import org.junit.jupiter.api.Test;

/**
 * Tests pertaining to the behavior of the AI.
 */
class AiSpeedTest {
    private static final int MOVES_TO_TEST = 20;

    @Test
    void easyAICompletesWithinOneSecond() {
        final long allowedTime = 1000; // Milliseconds.
        long time = aiSpeedTest(AiDifficulty.Easy);
        System.out.println("Longest move took " + time + "ms.");
        assertTrue(time < allowedTime, "The easy AI is too slow (" + time + " >= " + allowedTime + ")");
    }

    @Test
    void mediumAICompletesWithinThreeSeconds() {
        final long allowedTime = 3000; // Milliseconds.
        long time = aiSpeedTest(AiDifficulty.Medium);
        System.out.println("Longest move took " + time + "ms.");
        assertTrue(time < allowedTime, "The medium AI is too slow (" + time + " >= " + allowedTime + ")");
    }

    @Test
    void hardAICompletesWithinThreeSeconds() {
        final long allowedTime = 3000; // Milliseconds.
        long time = aiSpeedTest(AiDifficulty.Hard);
        System.out.println("Longest move took " + time + "ms.");
        assertTrue(time < allowedTime, "The hard AI is too slow (" + time + " >= " + allowedTime + ")");
    }

    /**
     * Tests an ai and returns the slowest move.
     * @param ai The AI difficulty to test with.
     * @return The longest time spent on a move in milliseconds.
     */
    private long aiSpeedTest(AiDifficulty ai) {
        long time = 0;
        ChessGame game = new ChessGame(ai, ai);
        for (int i = 0; i < MOVES_TO_TEST; i++) {
            long start = System.nanoTime();
            game.PerformAIMove();
            long end = System.nanoTime();
            long duration = (end - start) / 1_000_000;
            time = Math.max(time, duration);
        }
        return time;
    }
}
