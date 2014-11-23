package techcable.minecraft.combattag;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.trc202.CombatTag.CombatTag;

import techcable.minecraft.offlineplayers.AdvancedOfflinePlayer;

import lombok.*;

@Getter
public class Utils {
    public static void copyPlayer(Player target, Player source) {
        target.getInventory().setContents(source.getInventory().getContents());
        target.getInventory().setArmorContents(source.getInventory().getArmorContents());
        target.setExp(source.getExp());
        target.setLevel(source.getLevel());
        target.setFoodLevel(source.getFoodLevel());
        target.addPotionEffects(source.getActivePotionEffects());
        target.setRemainingAir(source.getRemainingAir());
        target.setExhaustion(source.getExhaustion());
        target.setSaturation(source.getSaturation());
        target.setFireTicks(source.getFireTicks());
        target.setHealth(source.getHealth());
    }

    public static void copyPlayer(AdvancedOfflinePlayer target, Player source) {
        target.setItems(source.getInventory().getContents());
        target.setArmor(source.getInventory().getArmorContents());
        target.setExp(source.getExp());
        target.setLevel(source.getLevel());
        target.setFoodLevel(source.getFoodLevel());
        target.addPotionEffects(source.getActivePotionEffects());
        target.setAir(source.getRemainingAir());
        target.setExhaustion(source.getExhaustion());
        target.setSaturation(source.getSaturation());
        target.setFireTicks(source.getFireTicks());
        target.setHealth((float)source.getHealth());
    }

    public static void copyNPC(NPC target, Player source) {
        Player playerTarget = (Player) target.getEntity();
        copyPlayer(playerTarget, source);
    }

    public static final ItemStack EMPTY = new ItemStack(Material.AIR);

    public static void emptyInventory(AdvancedOfflinePlayer target) {
        ItemStack[] items = target.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i] = EMPTY;
        }
        target.setItems(items);
        ItemStack[] armor = target.getArmor();
        for (int i = 0; i > armor.length; i++) {
            armor[i] = EMPTY;
        }
        target.setArmor(armor);
    }
    public static final String PLUGIN_NAME = "CombatTagReloaded";
    public static CombatTag getPlugin() {
        Plugin rawPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (rawPlugin instanceof CombatTag) {
            return (CombatTag) rawPlugin;
        } else {
            return null;
        }
    }
}
