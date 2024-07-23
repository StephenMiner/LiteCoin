package me.stephenminer.litecoin.games;

import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.games.gui.BlackJackGui;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BlackJack {
    private final LiteCoin plugin;
    private final Player player;
    private int wager;
    private final Random random;

    private BlackJackGui gui;
    private boolean ended;

    private List<Card> cards, playerCards;
    private List<Card> dealerHand;

    public BlackJack(Player player, int wager){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
        this.player = player;
        this.wager = wager;
        playerCards = new ArrayList<>();
        dealerHand = new ArrayList<>();
        random = new Random();
        initDeck();
        gui = new BlackJackGui(this);
        gui.showPlayer(player);

    }

    public void startGame(){
        shuffleDeck(random.nextInt(9) + 9);
        dealDealer();
        Result result = dealPlayer();
        gui.updateInventory();
        if (result == Result.WIN){
            ended = true;
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "What the sigma, you got a Blackjack!");
            player.playSound(player.getLocation(),Sound.LEVEL_UP,2,1);
           // BlackJackCmd.activeGames.remove(player.getUniqueId());
          //  player.closeInventory();
        }
    }

    private void initDeck(){
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()){
            for (int i = 2; i <= 14; i++){
                Card card = new Card(suit, i);
                cards.add(card);
            }
        }
        System.out.println(cards.size());
    }

    /**
     * Gives dealer starting cards with one face down
     */
    public void dealDealer(){
        dealerHand.add(drawCard());
        Card card = drawCard();
        card.setFaceDown(true);
        dealerHand.add(card);
        gui.updateInventory();
    }

    /**
     * Simulates the play the dealer makes after serving the player
     * They will keep drawing hands until the best value of their cards is at least 17
     * @return The Game result for the player, WIN if the dealer BUSTS,
     * LOSE if the dealer is closer to 21 than them or WIN if they are closer than the dealer
     */
    public Result dealersPlay(){
        int value = bestValue(true);
        dealerHand.forEach(card->card.setFaceDown(false));
        while (value < 17){
            dealerHand.add(drawCard());
            value = bestValue(true);
        }
        ended = true;
        gui.updateInventory();
        if  (value > 21){
            payout(Result.WIN,false);
            return Result.WIN;
        }else if(value == bestValue(false)){
            return Result.PUSH;
        }else if (playerWin()){
            payout(Result.WIN,false);
            return Result.WIN;
        }else {
            payout(Result.LOSE,false);
            return Result.LOSE;
        }
    }

    public Result dealPlayer(){
        playerCards.add(drawCard());
        playerCards.add(drawCard());
        gui.updateInventory();
        if (bestValue(false) == 21) {
            payout(Result.WIN, true);
            ended = true;
            gui.updateInventory();
            return Result.WIN;
        }
        return Result.CONTINUE;
    }

    public void shuffleDeck(int shuffles){
        for (int i = 0; i < shuffles; i++){
            Collections.shuffle(cards, random);
        }
    }

    public Result hitPlayer(){
        playerCards.add(drawCard());
        player.playSound(player.getLocation(), Sound.DIG_SNOW,1,1);
        gui.updateInventory();
        if (bestValue(false) == 21) {
            ended = true;
            payout(Result.WIN,false);
            gui.updateInventory();
            return Result.WIN;
        }
        else if (bust()) {
            ended = true;
            payout(Result.LOSE,false);
            gui.updateInventory();
            return Result.BUST;
        }
        else return Result.CONTINUE;
    }

    /**
     * The player taking the "stay" action. Automatically starts the dealer's play
     * @return
     */
    public Result stayPlayer(){
        ended = true;
        gui.updateInventory();
        return dealersPlay();
    }

    public void randomInsert(Card card){
        int slot = random.nextInt(cards.size());
        cards.add(slot, card);
    }

    public Card drawCard(){
        if (cards.isEmpty()) return null;
        return cards.remove(cards.size()-1);
    }


    public int bestValue(boolean dealer){
        int value = 0;
        Card[] copy;
        if (dealer) copy = dealerHand.toArray(new Card[0]);
        else copy = playerCards.toArray(new Card[0]);
        Arrays.sort(copy, Comparator.comparingInt(Card::value));
        for (Card card : copy){
            //Aces should be counted last
            if (card.value() == 14){
                if (value + 11 > 21) value++;
                else value += 11;
            }else if (card.value() >= 10) value += 10;
            else value += card.value();

        }
        return value;
    }

    public void payout(Result result, boolean natural){
        if (result == Result.BUST || result == Result.LOSE){
            plugin.incrementBalance(player,-1 * wager);
        }else if (result == Result.WIN){
            int payout = natural ? (int) (wager * 1.5) : wager;
            plugin.incrementBalance(player,payout);
        }
    }

    public void restart(){
        for (Card card : playerCards){
            card.setFaceDown(false);
            randomInsert(card);
        }
        for (Card card : dealerHand){
            card.setFaceDown(false);
            randomInsert(card);
        }
        playerCards.clear();
        dealerHand.clear();
        ended = false;
        startGame();
    }


    private boolean bust(){ return bestValue(false) > 21; }

    private boolean playerWin(){ return 21 - bestValue(true) > 21 - bestValue(false); }

    public List<Card> dealerHand(){ return dealerHand; }

    public List<Card> playerHand(){ return playerCards; }

    public BlackJackGui gui(){ return gui; }

    public boolean ended(){ return ended; }

    public void setEnded(boolean ended){ this.ended = ended; }

    public int wager(){ return wager; }


    public enum Result{
        BUST,
        CONTINUE,
        WIN,
        LOSE,
        PUSH
    }
}
