<<<<<<< HEAD
# [![CodeFactor](https://www.codefactor.io/repository/github/noslowdwn/voidfall/badge)](https://www.codefactor.io/repository/github/noslowdwn/voidfall) VoidFall 1.3
The plugin provides actions for players and allows to execute it if somebody triggered it!

## Features
 - Many triggers to execute actions
   - If a player reached cords
   - If a player entered/leaved the region (requires <a href="https://www.spigotmc.org/resources/worldguard-events.65176/">**WGEvents**</a>)
   - if a player joined/quited the server, or died 
 - Execute actions if a player triggered it
 - Specify that only one action will be executed randomly!
 - Also, can be enabled debug mode to view any errors in console
 - And provided updates checker for your comfort
 - Optimized (90% code runs async and config doesn't cals every time)
 - You can execute many actions when players reaching the selected cords
   - Command from console 
   - Command from player 
   - Send message
   - Send message to all online players
   - Play sound
   - Show a message in above hotbar (In actionbar)
   - Show a message on display (In Title/Subtitle)
   - Give an effect
   - Teleport to specified location
   - Change gamemode
 - Enable or disable updates checker


## Commands and Permissions
 - /voidfall - reload plugin configuration
 - voidfall.reload - allows you to use the reload command


## Download links
- en
   - <a href="https://modrinth.com/plugin/voidfall">**modrinth.com**</a>
- ru
   - <a href="https://spigotmc.ru/resources/voidfall.2239/">**spigotmc.ru**</a>
   - <a href="https://black-minecraft.com/resources/voidfall.5648/">**black-minecraft.com**</a>


### Triggers
 - [CONSOLE] - execute a command as the console
 - [PLAYER] - execute a command as the player (Do not write any /)
 - [TITLE] - display a message to the player on the screen (title) (Usage title;subtitle;fadeIn;stay;fadeOut)
 - [ACTIONBAR] - display a message to the player above the hotbar (In the action bar)
 - [MESSAGE] - send a message to the player in chat
 - [PLAY_SOUND] - play a sound to the player (Usage SOUND;VOLUME;PITCH (Volume and pitch are optional))
 - [PLAY_SOUND_ALL] - play a sound to all players (Usage SOUND;VOLUME;PITCH (Volume and pitch are optional))
 - [EFFECT] - give the player a potion effect (Usage EFFECT;STRENGTH;DURATION (Strength and duration are optional))
 - [BROADCAST] - send a message to all players online
 - [TELEPORT] - teleport the player (Usage WORLD;X;Y;Z;YAW;PITCH ("yaw" and "pitch" are optional))
   - You can also use ~ to get player position for value (For example '[TELEPORT] ~;~;~100;~;~;~' 
     will teleport player in the same world, with the same x and z cord, but 100 blocks above, and also with the same yaw and pitch) 
 - [GAMEMODE] - set the player's game mode (0 or survival | 1 or creative | 2 or adventure | 3 or spectator)