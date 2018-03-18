# MemeMana

MemeMana is a plugin that gives players Mana for logging in, which they can then spend on Exile/Prison pearl upkeep.

## What is Mana?

Mana is a virtual resource you get for playing on the server.
If you log in multiple days in a row, you get a login streak, meaning you get more Mana each day.
If you stop logging in, your streak will go down over time.
This means that logging in each day will give you the most Mana, but if you miss a day, it's not that big of a deal.

## Why would I want Mana?

Mana allows you to fuel Exile and/or Prison Pearls.
Each unit of Mana refills a single pearl by a certain percent.
The the pearl's health decays over time, so you must refill it with Mana every so often to keep it from getting to 0% and disappearing.

You also need a significant amount of Mana to upgrade an Exile pearl to a Prison pearl.

## How I use Mana?

When you log in, you will get a message telling you how much Mana (if any) you got for logging in.
You can check your total amount of Mana at any time by using `/manashow` or `/mns` for short.
To refill a pearl, hold it in your hand and type `/ep refill` to refill it as much as possible, or `/ep refill Amount` to refill it using up to `Amount` Mana.

To upgrade an Exile pearl to a Prison pearl, hold it in your hand, then use `/ep upgrade`.

## I don't have vault access. How do I give my Mana to the vault owner?

Type `/manatransfer Someone Amount` or `/mnt` (or `/mngive` or `/mng`) to transfer `Amount` Mana to the player or NameLayer group `Someone`.
If someone transfers Mana to a group you have the `MEMEMANA_WITHDRAW` permission on, use `/manawithdraw Group Amount` to move the Mana from the NameLayer group to your personal account.
Then you can use `/ep refill` (see above) to refill pearls.

## Mana Decay

If you don't use your Mana to refuel pearls, it will eventually decay, meaning it disappears completely.
This mechanic exists to solve the "coal problem," which is where vault owners have big enough stockpiles of coal that it doesn't really cost them anything to fuel pearls.
If the pearl fuel (Mana) decays over time, then they can never have more than `decayTime * activePlayers * manaPerPlayerPerDay` units of mana stockpiled.
This means that vault owners can only keep a certain number of players pearled indefinitely, or they will run out of Mana once their (limited) stockpile runs out.
The exact amount of players that can be kept permanently depends on how many active players give their Mana to the vault owner.
It is possible to set the ratio so that, for example, one active player's Mana is enough to keep one player permanently Prison pearled.

## Physical Mana

Your virtual Mana can be materialized into physical Mana, but **beware: physical Mana can't refuel pearls, and it can't be converted back into virtual Mana**.
Physical Mana is currently useless, but it might have a use in the future, like converting it to XP, or using it as an instant payment system.
That said, you can get physical Mana by typing `/manamaterialize` or `/mnmat` and then clicking on Mana to materialize it.

## I use multiple accounts. Do I get extra mana for logging in to each account?

No. You get mana per-user, not per-account.
If you use multiple accounts, they will share mana, and you will get mana exactly as often as you would normally, no matter how many accounts you use.
This uses [AltManager](https://github.com/CivClassic/AltManager), which uses [BanStick](https://github.com/CivClassic/BanStick) associations.

## I don't want to make a spreadsheet of all my login times. How do I check when my Mana will decay?

Hovering over the Mana in your `/manamaterialize` GUI will give you detailed information about when it will decay.

## I'm pearled and very very salty about it, and I want someone to blame. How do I figure out who is sponsoring my pearl?

You can do `/manafuellog` or `/mnfl` to list all the times anyone has refueled or upgraded your pearl, including the account which originally recieved the mana for logging in.

## I'm an admin and I want to investigate someone's Mana without typing SQL queries. What do?

`/manainspect <Player or Group>` to see their `/manamaterialize` thingy and their `/mns`, or `/manaviewtranslog <Player or Group>` or `/mnvtl` to see their transaction log.

## I'm an admin and I want to verify that MemeMana is working correctly without waiting for the login timer. What do?

`/manasimulatelogin` to simulate logging in, skipping the timer. If you just want some mana to play with, use `/manaincrease Player Amount`. You can also reset someone's streak using `/manareset Player`.
