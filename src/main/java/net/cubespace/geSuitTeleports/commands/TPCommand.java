package net.cubespace.geSuitTeleports.commands;


import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuitTeleports.managers.TeleportsManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {

        if ( !( sender instanceof Player ) ) {

            /* Console Commands */

            if (args.length < 2 || args.length > 6) {
                return false;
            }

            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(geSuitTeleports.invalid_offline + args[0]);
                return true;
            }

            // tp Player1 Player2
            if ( args.length == 2 ) {
                // Send Player1 to Player2
                Player p2 = Bukkit.getPlayer(args[1]);
                if (p2 == null) {
                    sender.sendMessage(geSuitTeleports.invalid_offline + args[1]);
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + p2.getName());
                p.saveData();
                p.teleport( p2 );
                return true;
            }

            // tp Player X Y Z
            if ( args.length == 4 ) {
                // Send player to specified coordinates
                // Supports coordinates relative to the player
                if (!validCoordinates(sender, args, 1, true)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3]);
                p.saveData();
                p.teleport( new Location(
                        p.getWorld(),
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch() ) );
                return true;
            }

            // tp Player X Y Z World
            if ( args.length == 5 ) {
                // Send player to the given coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3] + geSuitTeleports.in_world + args[4]);
                p.saveData();
                TeleportsManager.teleportToLocation(
                        p.getName(),  // Player to teleport
                        "",           // Server
                        args[4],      // World
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        Double.valueOf(args[3]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch());
                return true;
            }

            // tp Player X Y Z World Server
            if ( args.length == 6 ) {
                // Send player to the given coordinates of the given server and world
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3] + geSuitTeleports.in_world + args[4] + geSuitTeleports.on_server + args[5]);
                p.saveData();
                TeleportsManager.teleportToLocation(
                        p.getName(),  // Player to teleport
                        args[5],      // Server
                        args[4],      // World
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        Double.valueOf(args[3]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch());
                return true;
            }

            return false;
        }

        /* Player Commands */

        if (args.length < 1 || args.length > 6) {
            return false;
        }

        // tp Player
        if ( args.length == 1 ) {
            // Teleport yourself to another player
            // Do not validate target username since may not be on this server
            Player p = Bukkit.getPlayer(sender.getName());
            p.saveData();
            TeleportsManager.teleportToPlayer( sender, p.getName(), args[0] );
            return true;

        }

        // tp Player1 Player2
        if ( args.length == 2 ) {
            // Send Player1 to Player2
            // Do not validate target username since may not be on this server

            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(geSuitTeleports.invalid_offline + args[0]);
                return true;
            }

            p.saveData();
            TeleportsManager.teleportToPlayer( sender, p.getName(), args[1] );
            return true;
        }

        // tp X Y Z
        if ( args.length == 3 ) {
            // Teleport yourself to the specified coordinates (of this world)
            // Supports relative coordinates
            if (!validCoordinates(sender, args, 0, true)) {
                return true;
            }
            Player p = Bukkit.getPlayer(sender.getName());
            p.saveData();
            TeleportsManager.teleportToLocation(
                    p.getName(),                 // Player to teleport
                    "",                          // Server
                    p.getWorld().getName(),      // World
                    getCoordinate(p.getLocation().getX(), args[0]),
                    getCoordinate(p.getLocation().getY(), args[1]),
                    getCoordinate(p.getLocation().getZ(), args[2]),
                    p.getLocation().getYaw(),
                    p.getLocation().getPitch() );
            return true;
        }

        // tp Player X Y Z
        // tp X Y Z World
        if ( args.length == 4) {
            Player p = Bukkit.getPlayer(sender.getName());
            Player p2 = Bukkit.getPlayer( args[0] );
            if ( p2 != null ) {
                // Teleport another player to the given coordinates (of this world)
                // Supports coordinates relative to the sender
                if (!validCoordinates(sender, args, 1, true)) {
                    return true;
                }
                p2.saveData();
                TeleportsManager.teleportToLocation(
                        p2.getName(),                // Player to teleport
                        "",                          // Server
                        p.getWorld().getName(),      // World
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        p2.getLocation().getYaw(),
                        p2.getLocation().getPitch() );
            } else {
                // Teleport yourself to the specified coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 0, false)) {
                    return true;
                }
                p.saveData();
                TeleportsManager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        "",                          // Server
                        args[3],                     // World
                        Double.valueOf(args[0]),
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch());
            }
            return true;
        }

        // tp Player X Y Z World
        // tp Server World X Y Z
        if ( args.length == 5 ) {
            Player p = Bukkit.getPlayer(sender.getName());
            Player p2 = Bukkit.getPlayer( args[0] );
            if ( p2 != null ) {
                // Teleport another player to the given coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                p2.saveData();
                TeleportsManager.teleportToLocation(
                        p2.getName(),                // Player to teleport
                        "",                          // Server
                        args[4],                     // World
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        Double.valueOf(args[3]),
                        p2.getLocation().getYaw(),
                        p2.getLocation().getPitch());
            } else {
                // Teleport yourself to the given coordinates on the given server and world
                // Cannot use relative coordinates since potentially switching server or world
                if (!validCoordinates(sender, args, 2, false)) {
                    return true;
                }
                p.saveData();
                TeleportsManager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        args[0],                     // Server
                        args[1],                     // World
                        Double.valueOf(args[2]),
                        Double.valueOf(args[3]),
                        Double.valueOf(args[4]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch());
            }
            return true;
        }

        // tp Player X Y Z World Server
        if ( args.length == 6 ) {
            Player p2 = Bukkit.getPlayer( args[0] );
            if (p2 == null) {
                sender.sendMessage(geSuitTeleports.invalid_offline + args[0]);
                return true;
            }

            // Teleport another player to the given coordinates of the given server and world
            // Cannot use relative coordinates since potentially switching server or world
            if (!validCoordinates(sender, args, 1, false)) {
                return true;
            }
            p2.saveData();
            TeleportsManager.teleportToLocation(
                    p2.getName(),                // Player to teleport
                    args[5],                     // Server
                    args[4],                     // World
                    Double.valueOf(args[1]),
                    Double.valueOf(args[2]),
                    Double.valueOf(args[3]),
                    p2.getLocation().getYaw(),
                    p2.getLocation().getPitch());
        }

        return false;
    }

    private double getCoordinate(double startingCoordinate, String coordValue) {

        if (coordValue.startsWith("~")) {
            // Return a relative coordinate
            if (coordValue.length() > 1)
                return startingCoordinate + Double.valueOf(coordValue.substring(1));
            else
                return startingCoordinate;
        }

        // Return an absolute coordinate
        return Double.valueOf(coordValue);
    }

    private boolean validCoordinates(CommandSender sender, String[] args, int startIndex, boolean allowRelative) {

        return validCoordinate(sender, allowRelative, "X", args[startIndex]) &&
                validCoordinate(sender, allowRelative, "Y", args[startIndex + 1]) &&
                validCoordinate(sender, allowRelative, "Z", args[startIndex + 2]);
    }

    private boolean validCoordinate(CommandSender sender, boolean allowRelative, String coordName, String coordValue) {

        if (coordValue.startsWith("~")) {
            if (!allowRelative) {
                sender.sendMessage(geSuitTeleports.relative_coords_not_valid);
                return false;
            }

            if (coordValue.length() == 1)
                return true;
            else
                coordValue = coordValue.substring(1);
        }

        if (!NumberUtils.isNumber(coordValue)) {
            if (coordName.equals("X"))
                sender.sendMessage(geSuitTeleports.invalid_x_coordinate_or_player + coordValue);
            else
                sender.sendMessage(geSuitTeleports.invalid + coordName + geSuitTeleports.coordinate + coordValue);
            return false;
        }

        return true;
    }

}
