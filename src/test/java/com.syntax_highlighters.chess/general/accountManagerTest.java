package com.syntax_highlighters.chess.general;

import com.syntax_highlighters.chess.Account;
import com.syntax_highlighters.chess.AccountManager;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests concerning the behavior of the AccountManager class.
 */
class accountManagerTest {

    @Test
    void newAccountManagerHasNoAccounts() {
        String file = "newAccountManagerHasNoAccounts.db";
        new File(file).deleteOnExit();
        AccountManager am = new AccountManager(file);
        assertEquals(0, am.accountSize());
    }

    @Test
    void accountManagerCanRetrieveAccountByName() {
        String file = "accountManagerCanRetrieveAccountByName.db";
        new File(file).deleteOnExit();
        AccountManager am = new AccountManager(file);
        Account a = new Account("Alice");
        Account b = new Account("Bob");
        
        am.addAccount(a);
        am.addAccount(b);
        
        assertEquals(a, am.getAccount("Alice"));
        assertEquals(b, am.getAccount("Bob"));
    }

    @Test
    void accountManagerDoesNotAddAccountWithSameNameTwice() {
        String file = "accountManagerDoesNotAddAccountWithSameNameTwice.db";
        new File(file).deleteOnExit();
        AccountManager am = new AccountManager(file);
        Account a1 = new Account("Alice");
        Account a2 = new Account("Alice");

        am.addAccount(a1);
        am.addAccount(a2);

        assertEquals(1, am.accountSize());
    }

    @Test
    void savedAccountStoresCorrectData() {
        String file = "savedAccountStoresCorrectData.db";
        new File(file).deleteOnExit();
        String name = "Alice";
        int wins = 10;
        int losses = 20;
        int rating = 1250;
        {
            AccountManager am = new AccountManager(file);
            Account ac = new Account(name, wins, losses, rating);
            assertEquals(name, ac.getName());
            assertEquals(wins, ac.getWinCount());
            assertEquals(losses, ac.getLossCount());
            assertEquals(rating, ac.getRating());
            am.addAccount(ac);
            am.save();
        }
        {
            AccountManager am = new AccountManager(file);
            Account ac = am.getAccount(name);
            assertEquals(name, ac.getName());
            assertEquals(wins, ac.getWinCount());
            assertEquals(losses, ac.getLossCount());
            assertEquals(rating, ac.getRating());
        }
    }

    @Test
    void accountManagerUpdatesScoreCorrectlyForSamePlayerWinningTwice() {
        String file = "savedAccountStoresCorrectData.db";
        new File(file).deleteOnExit();
        AccountManager am = new AccountManager(file);
        Account a = new Account("Alice"); // initial rating: 1000
        Account b = new Account("Bob");   // initial rating: 1000
        am.addAccount(a);
        am.addAccount(b);
        am.updateRating(a, b);
        // a.getRating() = (1000 + 400*1) / 1 = 1400
        // b.getRating() = (1000 - 400*1) / 1 = 600
        am.updateRating(a, b);
        // a.getRating() = ((1000 + 600) + 400*2) / 2 = 1200
        //                           ^ new rating of opponent
        // b.getRating() = ((1000 + 1400) - 400*2) / 2 = 800
        //                           ^ new rating of opponent
        assertEquals(1200, a.getRating());
        assertEquals(800, b.getRating());
        // This is based on the formula from Wikipedia:
        // rating = (<sum of opponent ratings> + 400*(wins - losses)) / <games played>
        // https://en.wikipedia.org/wiki/Elo_rating_system#Performance_rating
    }

    // TODO: add test to ensure draws don't affect the score wrong
    // TODO: add tests to ensure unrated wins/losses don't affect the score wrong
}
