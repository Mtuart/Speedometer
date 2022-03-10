package de.stuart.speedometer;

import es.pollitoyeye.vehicles.VehiclesMain;
import es.pollitoyeye.vehicles.enums.VehicleType;
import es.pollitoyeye.vehicles.events.VehicleEnterEvent;
import es.pollitoyeye.vehicles.events.VehicleExitEvent;
import es.pollitoyeye.vehicles.interfaces.Vehicle;
import es.pollitoyeye.vehicles.interfaces.VehicleSubType;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Events implements Listener {

    static String speed;
    static String speed2;
    
    static String bar;
    static String fuelLeft;
    static String fuelSpent;
    static String speedC;
    static String barSymbol;
    static String fuelbars;

    static int fuel;
    static int amount;
    
    static char barChar;

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
                    Vector vector = armorStand.getVelocity();
                    speed = String.format("%,.0f", vector.length() * 20);
                    if (!speed.equalsIgnoreCase("2")) {
                        speed2 = String.format("%,.0f", vector.length() * 20);
                    } else {
                        speed2 = "0";
                    }
                    
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
                    bar = bar.replace("{speed}", speedC + speed2);
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
