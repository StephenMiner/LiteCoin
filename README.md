Commands:

/pay [target-player-name] [balance] 

permission: litecoin.commands.pay

Gives the target player the specified amount of litecoins and takes that same number from the sender's balance

/bal
permission: litecoin.commands.bal.self
Displays the senders balance

/bal [target-player-name]

permission: litecoin.commands.bal.others

Displays the balance of the target play

/givecoin [target-player-name] [to-give]

permission: litecoin.commands.give

Gives the target player the amount of LiteCoins specified, manifesting them from nothing

/setbal [player] [balance]

permission: litecoin.commands.setbal

Sets the balance of the target player to the specified amount

/gamble [amount]

/gamble all

Gambles the amount specified, if you win you will gain the amount you already had back and an additional amount equal to what you bet. if you lose, you lose the amount bet.

permission: litecoin.commands.gamble
