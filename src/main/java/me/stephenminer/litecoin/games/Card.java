package me.stephenminer.litecoin.games;

public class Card {
    private final Suit suit;
    private final int value;
    private boolean faceDown;

    /**
     *
     * @param suit suit of the card, Clubs, Hearts, Diamonds, Spades
     * @param value 1 or 14 will represent the Ace, -1 the black joker, -2 the color joker
     *  11 = Jack, 12 = Queen, 13 = King
     */
    public Card(Suit suit, int value){
        this.suit = suit;
        this.value = value;
    }




    public String display(){
        if (faceDown) return "face-down-card";
        if (value > 1 && value <= 10) return value + " of " + suit.name().toLowerCase();
        else{
            switch (value){
                case -2: return "color joker";
                case -1: return "black joker";
                case 1:
                case 14:
                    return "ace of " + suit.name().toLowerCase();
                case 11: return "jack of " + suit.name().toLowerCase();
                case 12: return "queen of " + suit.name().toLowerCase();
                case 13: return "king of " + suit.name().toLowerCase();
            }
        }
        return "N/A";
    }

    public int value(){ return value; }
    public Suit suit(){ return suit; }
    public boolean faceDown(){ return faceDown; }
    public void setFaceDown(boolean faceDown){ this.faceDown = faceDown; }
    public void flip(){ this.faceDown = !faceDown; }







    public enum Suit{
        CLUBS,
        SPADES,
        HEARTS,
        DIAMONDS
    }
}
