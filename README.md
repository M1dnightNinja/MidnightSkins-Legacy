# MidnightSkins
A library for Spigot plugins that allows for changing players' skins and names. Supports 1.8 or higher. Tested on 1.15.2

## Installation
MidnightSkins is hosted on the public Maven repository ```mdmc.ddns.net/maven```. Instructions for installing with 
Gradle is as follows:

### Gradle
Add the following to your ```build.gradle``` file:
```
repositories {
    maven {
        url 'https://mdmc.ddns.net/maven'
    }
}

dependencies {
    compile 'me.m1dnightninja:midnightskins:1.0'
}
```
After that, you need to tell Gradle to copy MidnightSkins into your plugin's jar file. To accomplish this, you need
to add a new configuration and a new build task. These can be whatever you want, but this overview will use the names
```jarDependencies``` and ```jarWithDependencies``` for the configuration and the build task, respectively. The code is
as follows:

```
configurations {
    jarDependencies
    jarDependencies.transitive = false
}

task jarWithDependencies(type: Jar) {
    from { configurations.jarDependencies.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
```

## Usage

##### Enabling the Library
Start by enabling the library. None of the methods will do anything until you enable it.
```
MidnightSkins.getInstance().enable();
```
##### Obtaining a Skin
There are multiple ways to obtain a skin. The two most common are getting it from the player directly, or getting it from Mojang's servers.

You can get a skin from a player by calling the `getCurrentSkin()` method. If you have applied a custom skin to the player already, this method will return their custom skin. If you want their original skin, call `getOriginalSkin()` instead.
```
MidnightSkins.getInstance().getCurrentSkin(player);
```
```
MidnightSkins.getInstance().getOriginalSkin(player);
```

You can get a skin from Mojang by calling the `getSkinFromWeb()` method. Pass in a player's UUID as the only argument. Keep in mind that running this method synchronously will cause the server to hang until the HTTP request is responded to or times out.
```
MidnightSkins.getInstance().getSkinFromWeb(uuid);
```
All of these methods will return a `Skin` Object. From that, you can get the UUID, Base64, and Signed Base64 values of the skin. You can also set a player's skin to that skin.

##### Setting a Player's Skin
You can easily set a player's skin to a `Skin` Object by calling the `setPlayerSkin()` method. It needs a `Player` and a `Skin` as arguments. Keep in mind that nobody (including the player) will be able to see the updated skin until you call the `updatePlayer()` method.
```
MidnightSkins.getInstance().setPlayerSkin(player,skin);
```

##### Dealing with Names
You can set a player's name with the `setPlayerName()` method. It needs a `Player` and a `String` as arguments. You must also call `updatePlayer()` before anybody will be able to see the change.
```
MidnightSkins.getInstance().setPlayerName(player,name);
```
There are also a few methods for obtaining a player's name. The `getCurrentName()` Method will return the name currently applied to the player. Their custom name if they have one, or their original name if they don't. The `getOriginalName()` method will return the name a player logged in with.
```
MidnightSkins.getInstance().getCurrentName(player);
```
```
MidnightSkins.getInstance().getOriginalName(player);
```

##### Resetting
You can reset a player's name or skin to default by calling `resetPlayerName()` or `resetPlayerSkin()` respectively. You can also call `resetPlayer()` to reset both at once.
```
MidnightSkins.getInstance().resetPlayerName(player);
```
```
MidnightSkins.getInstance().resetPlayerSkin(player);
```
```
MidnightSkins.getInstance().resetPlayer(player);
```

##### Updating Players
After you change a player's name or skin, you will need to call `updatePlayer()` before anybody on the server (including them) can see the changes.
```
MidnightSkins.getInstance().updatePlayer(player);
```

##### Event API
Whenever a skin is changed, a `PlayerSkinChangeEvent` is called, containing the affected Player, the previous skin, and the newly updated skin.
Whenever a name is changed, a `PlayerNameChangeEvent` is called, containing the affected Player, the previous name, and the newly updated name.
Both of these events are called as soon as the name or skin are set, not when the player is updated. When a player is updated, a `PlayerAppearanceUpdatedEvent` is called, containing the affected player, the new name, and the new skin. 
All of these events can be intercepted just like any other event.
```
@EventHandler
public void onUpdated(PlayerAppearanceUpdatedEvent event) {
    // Do whatever
}
```
##### PlaceholderAPI Integration
| Placeholder | Description
| ------------|---------------|
| %midnightskins_current_name% | Returns a player's custom name if they have one, or their original name if not. |
| %midnightskins_original_name% | Returns a player's original name. |
| %midnightskins_current_skin_base64% | Returns the Base64 value of the skin currently applied to the player. |
| %midnightskins_current_skin_signedBase64% | Returns the Signed Base64 value of the skin currently applied to the player. |
| %midnightskins_current_skin_uuid% | Returns the UUID of the skin currently applied to the player. |
| %midnightskins_original_skin_base64% | Returns the Base64 value of the skin the player logged in with. |
| %midnightskins_original_skin_signedBase64% | Returns the Signed Base64 value of the skin the player logged in with. |
| %midnightskins_original_skin_uuid% | Returns the UUID of the skin the player logged in with. |

## Troubleshooting

If two plugins use this library with different versions, it can cause conflicts. In such a case, download the latest 'standalone' version from the releases tab and install  it as a plugin on your server.