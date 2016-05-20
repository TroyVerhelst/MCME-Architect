/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.architect.specialBlockHandling;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.DevUtil;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Furnace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockListener implements Listener{
 
    @EventHandler(priority = EventPriority.HIGH)
    private void eggInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.hasBlock() 
                && event.getHand().equals(EquipmentSlot.HAND)
                && event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) {
            DevUtil.log(2,"eggInteract fired cancelled: " + event.isCancelled());
            if(event.isCancelled()) {
                return;
            }
            if(!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.DRAGON_EGG)) {
                return;
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                event.setCancelled(true);
            }
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (!(p.getGameMode().equals(GameMode.CREATIVE)
                        && PluginData.hasPermission(p, Permission.INTERACT_EGG))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void logPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType().equals(Material.LOG)
                || p.getItemInHand().getType().equals(Material.LOG_2)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.SIX_SIDED_LOGS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Six Sided")) {
                DevUtil.log(2,"logPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!PluginData.hasPermission(p, Permission.PLACE_SIX_SIDED_LOG)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                }
                Block b = event.getBlockPlaced();
                MaterialData md = p.getItemInHand().getData();
                md.setData((byte) (md.getData()+12));
                BlockState bs = b.getState();

                bs.setData(md);
                bs.update();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void giantMushroomPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType().equals(Material.HUGE_MUSHROOM_1)
                || p.getItemInHand().getType().equals(Material.HUGE_MUSHROOM_2)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.SIX_SIDED_LOGS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasEnchants()) {
                DevUtil.log(2,"giantMushroomPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!PluginData.hasPermission(p, Permission.PLACE_SIX_SIDED_LOG)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                }
                Block b = event.getBlockPlaced();
                MaterialData md = p.getItemInHand().getData();
                md.setData(MushroomBlocks.getDataValue(p.getItemInHand().getItemMeta().getDisplayName()));
                final BlockState bs = b.getState();
                bs.setData(md);
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        bs.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void pistonPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getType().equals(Material.PISTON_BASE)
                || p.getItemInHand().getType().equals(Material.PISTON_STICKY_BASE)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.PISTON_EXTENSIONS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName() && 
                (p.getItemInHand().getItemMeta().getDisplayName().startsWith("Table")
                || p.getItemInHand().getItemMeta().getDisplayName().startsWith("Wheel"))) {
                    DevUtil.log(2,"pistonPlace fired cancelled: " + event.isCancelled());
                    if(event.isCancelled()) {
                        return;
                    }
                    event.setCancelled(true);
                    if(!PluginData.hasPermission(p, Permission.PLACE_PISTON_EXTENSION)) {
                        PluginData.getMessageUtil().sendNoPermissionError(p);
                        return;
                    }
                    float yaw = p.getLocation().getYaw();
                    float pitch = p.getLocation().getPitch();
                    byte data = getPistonOrFurnaceDat(yaw, pitch, p.getItemInHand().getType());
                    Block block = event.getBlock();
                    final BlockState blockState = block.getState();
                    blockState.setType(Material.PISTON_EXTENSION);
                    blockState.setRawData(data);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            blockState.update(true, false);
                        }
                    }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }

    private static byte getPistonOrFurnaceDat(float yaw, float pitch, Material type) {
        byte dat = 0;
        while(yaw>180) yaw-=360;
        while(yaw<-180) yaw+=360;
        if (pitch  < -45) {
            dat = 0;
        } else if (pitch  > 45) {
            dat = 1;
        } else if ((yaw >= -45 && yaw < 45)) {
            dat = 2;
        } else if (yaw < -135 || yaw >= 135) {
            dat = 3;
        } else if ((yaw >= -135 && yaw < -45)) {
            dat = 4;
        } else if ((yaw >= 45 && yaw < 135)) {
            dat = 5;
        }
        if(type.equals(Material.PISTON_STICKY_BASE)) {
            dat+=8;
        }
        return dat;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void vegPlace(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getHand().equals(EquipmentSlot.OFF_HAND)
                &&(p.getItemInHand().getType().equals(Material.CARROT_ITEM)
                || p.getItemInHand().getType().equals(Material.POTATO_ITEM)
                || p.getItemInHand().getType().equals(Material.WHEAT)
                || p.getItemInHand().getType().equals(Material.MELON_SEEDS)
                || p.getItemInHand().getType().equals(Material.PUMPKIN_SEEDS)
                || p.getItemInHand().getType().equals(Material.BROWN_MUSHROOM)
                || p.getItemInHand().getType().equals(Material.CACTUS)
                || p.getItemInHand().getType().equals(Material.RED_MUSHROOM))) {
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.PLANTS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Placeable")) {
                DevUtil.log(2,"vegPlace fired cancelled: " + event.isCancelled());
                /*if(event.isCancelled()) {
                    return;
                }*/
                if(!PluginData.hasPermission(p, Permission.PLACE_PLANT)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                }
                Block b = event.getClickedBlock().getRelative(event.getBlockFace());
                BlockState bs = b.getState();
                switch(p.getItemInHand().getType()) {
                    case BROWN_MUSHROOM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.BROWN_MUSHROOM, true);
                        break;
                    case RED_MUSHROOM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.RED_MUSHROOM, true);
                        break;
                    case WHEAT:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.CROPS, true);
                        break;
                    case MELON_SEEDS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.MELON_STEM, true);
                        break;
                    case PUMPKIN_SEEDS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.PUMPKIN_STEM, true);
                       break;
                    case CARROT_ITEM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(),  
                                            Material.CARROT, true);
                       break;
                    case POTATO_ITEM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.POTATO, true);
                        break;
                    case CACTUS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.CACTUS, false);
                        break;

                }
                bs.update(true,false);
            }
        }
    }
    
    private static BlockState handleInteract(Block clickedBlock,BlockFace clickedFace, 
                                             Material materialMatch, boolean editDataValue) {
        BlockState blockState;
        if(clickedBlock.getType().equals(materialMatch) ) {
            blockState = clickedBlock.getState();
            if(editDataValue) {
                byte dataValue = (byte) (blockState.getRawData()- 1);
                if(dataValue<0) {
                    dataValue = 7;
                }
                blockState.setRawData(dataValue);
            }
        }
        else {
            blockState = clickedBlock.getRelative(clickedFace).getState();
            if(blockState.getType().equals(Material.AIR)) {
                blockState.setType(materialMatch);
                blockState.setRawData((byte)7);
            }
        }
        return blockState;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void torchPlace(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getHand().equals(EquipmentSlot.HAND)
                &&(p.getInventory().getItemInMainHand().getType().equals(Material.REDSTONE_TORCH_ON))) {
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.REDSTONE_TORCH)) {
                return;
            }
            if (p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()
                    && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().startsWith("Burning")) {
                DevUtil.log(2,"torchPlace fired cancelled: " + event.isCancelled());
                /*if(event.isCancelled()) {
                    return;
                }*/
                if(!PluginData.hasPermission(p, Permission.PLACE_TORCH)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                }
                Block b = event.getClickedBlock().getRelative(event.getBlockFace());
                BlockState bs = b.getState();
                if(bs.getType().equals(Material.AIR)) {
                    bs.setType(Material.REDSTONE_TORCH_ON);
                    bs.setRawData(getTorchDat(event.getBlockFace()));
                    bs.update(true,false);
                }
            }
        }
    }
    
    private byte getTorchDat(BlockFace face) {
        switch(face) {
            case NORTH:
                return (byte) 4;
            case SOUTH:
                return (byte) 3;
            case WEST:
                return (byte) 2;
            case EAST:
                return (byte) 1;
            default:
                return (byte) 0;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void doubleSlabPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getType().equals(Material.STEP)
                || p.getItemInHand().getType().equals(Material.WOOD_STEP)
                || p.getItemInHand().getType().equals(Material.STONE_SLAB2)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.DOUBLE_SLABS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Double")) {
                DevUtil.log(2,"doubleSlabPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!PluginData.hasPermission(p, Permission.PLACE_DOUBLE_SLAB)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                }
                Block b = event.getBlockPlaced();
                BlockState bs = b.getState();
                if(p.getItemInHand().getType().equals(Material.STONE_SLAB2)) {
                    bs.setType(Material.DOUBLE_STONE_SLAB2);
                }
                else {
                    bs.setType(Material.DOUBLE_STEP);
                }
                if(p.getItemInHand().getType().equals(Material.WOOD_STEP)) {
                    bs.setRawData((byte) 2);
                }
                else if(p.getItemInHand().getItemMeta().getDisplayName().startsWith("Double Full")) {
                    bs.setRawData((byte) 8);
                }
                else {
                    bs.setRawData(p.getItemInHand().getData().getData());
                }
                bs.update(true,false);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void doorPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Material blockType = event.getBlock().getType();
        if (isDoor(blockType)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.HALF_DOORS)) {
                return;
            }
            ItemStack item = p.getItemInHand();
            if(item.getItemMeta().getDisplayName().startsWith("Half")) {
                DevUtil.log(2,"doorPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                event.setCancelled(true);
                if(!PluginData.hasPermission(p, Permission.PLACE_HALF_DOOR)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    return;
                }
                float yaw = p.getLocation().getYaw();
                byte data = getDoorDat(yaw);

                Block block = event.getBlock();
                final BlockState blockState = block.getState();
                blockState.setRawData(data);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        blockState.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }

    @EventHandler
    public void furnaceProlongBurning(FurnaceSmeltEvent event) {
        if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BURNING_FURNACE)) {
            return;
        }
        final Block block = event.getBlock();
        final Material smelting = ((Furnace) block.getState()).getInventory().getSmelting().getType();
        new BukkitRunnable() {
            @Override
            public void run() {
                Furnace furnace = (Furnace) block.getState();
                FurnaceInventory inventory = furnace.getInventory();
                ItemStack item =inventory.getResult();
                inventory.setResult(null);
                inventory.setSmelting(new ItemStack(smelting));
                furnace.setBurnTime(Short.MAX_VALUE);
                furnace.update(true, false);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void placeFurnace(BlockPlaceEvent event) {
        final Player p = event.getPlayer();
        if(event.getBlock().getType().equals(Material.FURNACE)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BURNING_FURNACE)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Burning")) {
                DevUtil.log(2,"placeBurningFurnace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!PluginData.hasPermission(p, Permission.BURNING_FURNACE)) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                }
                Furnace furnace = (Furnace) event.getBlock().getState();
                furnace.setType(Material.BURNING_FURNACE);
                furnace.setRawData(getPistonOrFurnaceDat(p.getLocation().getYaw(),0,Material.BURNING_FURNACE));
                furnace.update(true, false);
                final Block block = event.getBlock();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Furnace furnace = (Furnace) block.getState();
                        furnace.getInventory().setSmelting(new ItemStack(Material.RAW_FISH));
                        furnace.setBurnTime(Short.MAX_VALUE);
                        furnace.update(true, false); 
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 10);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void protectFurnaceInventory(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();
        if(!(player instanceof Player)) {
            return;
        }
        Player p = (Player) player;
        if(event.getInventory() instanceof FurnaceInventory) {
            if (!PluginData.isModuleEnabled(p.getWorld(), Modules.BURNING_FURNACE)) {
                return;
            }
            DevUtil.log(2,"protectInventoryBurningFurnace fired cancelled: " + event.isCancelled());
            if(event.isCancelled()) {
                return;
            }
            if(!PluginData.hasPermission(p, Permission.BURNING_FURNACE)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                event.setCancelled(true);
            }
        }
    }
    
    private byte getDoorDat(float yaw) {
        if ((yaw >= -225 && yaw < -135)
                || (yaw >= 135 && yaw <= 225)) {
            return 3;
        } else if ((yaw >= -135 && yaw < -45)
                || (yaw >= 225 && yaw < 315)) {
            return 0;
        } else if ((yaw >= -45 && yaw < 45)
                || (yaw >= -360 && yaw < -315)
                || (yaw >= 315 && yaw <= 360)) {
            return 1;
        } else if ((yaw >= -315 && yaw < -225)
                || (yaw >= 45 && yaw < 135)) {
            return 2;
        } else {
            return 0;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void noPhysicsDoors(BlockPhysicsEvent event) {
        Material doorType = event.getBlock().getType();
        if (isDoor(doorType)) {
            DevUtil.log(2,"noPhysicsDoors fired cancelled: " + event.isCancelled());
            if(event.isCancelled()) {
                return;
            }
            if(PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.HALF_DOORS)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void noOpenHalfDoors(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player p = event.getPlayer();
            Material blockType = event.getClickedBlock().getType();
            if (isDoor(blockType)) {
                if(PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.HALF_DOORS)) {
                    DevUtil.log(2,"noOpenHalfDoors fired cancelled: " + event.isCancelled());
                    if(event.isCancelled()) {
                        return;
                    }
                    Block above = event.getClickedBlock().getRelative(BlockFace.UP);
                    if(event.getClickedBlock().getData()!=8 && !(isDoor(above.getType()) && above.getData()==8)){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isDoor(Material blockType) {
        return blockType.equals(Material.WOODEN_DOOR)
                || blockType.equals(Material.IRON_DOOR_BLOCK)
                || blockType.equals(Material.SPRUCE_DOOR)
                || blockType.equals(Material.BIRCH_DOOR)
                || blockType.equals(Material.JUNGLE_DOOR)
                || blockType.equals(Material.ACACIA_DOOR)
                || blockType.equals(Material.DARK_OAK_DOOR);
    }

}
