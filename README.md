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

/gamble stats

Displays gambling stats

permission: litecoin.commands.gamble



PlaceHolders

%litecoin% -> balance of the target player

%litecoin_#% -> Name of the player at balance rank # where # is a whole number 

%litecoin_#_bal -> Balance of the player at balance rank # where # is a whole number

%litegamble_wins% -> Gambling wins for target player

%litegamble_wins_name% -> Gambling wins for player with name 'name'

%litegamble_losses% -> Gambling losses for target player

%litegamble_losses_name% -> Gambling losses for player with name 'name'

%litegamble_total% -> Total gambles made by target player

%litegamble_total_name% -> Total gamble made by player with name 'name'

%litegamble_profits% -> Gambling profits for target player

%litegamble_profits_name% -> Gambling profits for player with name 'name'

%litegamble_win-rate% -> Gambling win-rate for target player

%litegamble_win-rate% -> Gambling win-rate for player with name 'name'


