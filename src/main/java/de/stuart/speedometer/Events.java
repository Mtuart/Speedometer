package de.stuart.speedometer;

import es.pollitoyeye.vehicles.VehiclesMain;
import es.pollitoyeye.vehicles.enums.VehicleType;
import es.pollitoyeye.vehicles.events.VehicleEnterEvent;
import es.pollitoyeye.vehicles.events.VehicleExitEvent;
import es.pollitoyeye.vehicles.interfaces.Vehicle;
import es.pollitoyeye.vehicles.interfaces.VehicleSubType;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Events implements Listener {
        
    static String bar;
    static String fuelLeft;
    static String fuelSpent;
    static String speedC;
    static String barSymbol;
    static String fuelbars;

    static int fuel;
    static int amount;
    static double finalspeed;
    
    private static Main plugin = Main.instance;

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent vehicleEnterEvent) {
        HashmapManager.inVehicle.put(vehicleEnterEvent.getPlayer().getName(), "true");

        ArmorStand armorStand = vehicleEnterEvent.getMainArmorStand();
        VehicleSubType vehicleSubType = vehicleEnterEvent.getVehicleSubType();

        showFuelSpeedBar(vehicleEnterEvent.getPlayer(), armorStand, vehicleSubType, vehicleEnterEvent.getVehicleType());
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent vehicleExitEvent) {
        HashmapManager.inVehicle.remove(vehicleExitEvent.getPlayer().getName());
    }
    
    @EventHandler
    public void onVehicleMove(EntityMoveEvent event){
        if(event.getEntity().getType().equals(EntityType.ARMOR_STAND)){
            ArmorStand stand = (ArmorStand) event.getEntity();
            HashMap<Player, Vehicle> map = VehiclesMain.getPlugin().playerVehicles;
            if(stand.getCustomName().contains(";")) {
                String[] split = stand.getCustomName().split(";");
                for (Map.Entry<Player, Vehicle> entry : map.entrySet()) {
                    Player player = entry.getKey();
                    Vehicle vehicle = entry.getValue();
                    if(player.getUniqueId().toString().equalsIgnoreCase(split[2])){
                        if(vehicle.getMainStand().getCustomName().contains(split[3])){
                            Location start = event.getFrom();
                            Location end = event.getTo().clone();
                            
                            start.setY(0);
                            end.setY(0);
                            
                            finalspeed = start.distance(end) * 20;
                        }
                    }
                }
            }
        }
    }

    public static void showFuelSpeedBar(Player player, ArmorStand armorStand, VehicleSubType vehicleSubType, VehicleType vehicleType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !HashmapManager.inVehicle.containsKey(player.getName())) {
                    cancel();
                }
                Vehicle vehicle = VehiclesMain.getPlugin().getPlayerVehicle(player);
                
                fuelLeft = plugin.getConfig().getString("Config.fuelLeftColor");
                fuelSpent = plugin.getConfig().getString("Config.fuelSpentColor");
                speedC = plugin.getConfig().getString("Config.speedColor");
                amount = plugin.getConfig().getInt("Config.barAmount");
                barSymbol = plugin.getConfig().getString("Config.barSymbol");
                
                if (vehicle != null) {
                    if(vehicleType.getUsesFuel() ) {
                        fuel = (int) vehicle.getFuel();
                        bar = plugin.getConfig().getString("Config.fuelbarDesign");
                        fuelbars = getFuelBar(fuel, vehicleSubType.getFuelCapacity(), amount, barSymbol, fuelLeft, fuelSpent);
                        assert bar != null;
                        bar = bar.replace("{fuelbars}", fuelbars);
                    } else{
                        bar = plugin.getConfig().getString("Config.speedOnlyBar");
                    }
                    assert bar != null;
                    bar = bar.replace("{speed}", speedC + String.format("%,.0f", finalspeed));
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', bar));
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

    public static String getFuelBar(int current, int max, int totalBars, String symbol, String fuelLeft, String fuelSpent) {
        float percent = (float) current / max;
        int fuelbar = (int) (totalBars * percent);

        return StringUtils.repeat("" + fuelLeft + symbol, fuelbar)
                + StringUtils.repeat("" + fuelSpent  + symbol, totalBars - fuelbar);
    }
}
