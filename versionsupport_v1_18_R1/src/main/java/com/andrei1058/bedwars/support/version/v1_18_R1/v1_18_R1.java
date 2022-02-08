/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.andrei1058.bedwars.support.version.v1_18_R1;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.shop.ShopHolo;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.TeamColor;
import com.andrei1058.bedwars.api.entity.Despawnable;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.language.Messages;
import com.andrei1058.bedwars.api.server.VersionSupport;
import com.andrei1058.bedwars.support.version.common.VersionCommon;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3fa;
import net.minecraft.SharedConstants;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBase;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftFireball;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class v1_18_R1 extends VersionSupport {

    private static final UUID chatUUID = new UUID(0L, 0L);

    public v1_18_R1(Plugin plugin, String name) {
        super(plugin, name);
        loadDefaultEffects();
    }

    @Override
    public void registerVersionListeners() {
        new VersionCommon(this);
    }

    @Override
    public void registerCommand(String name, Command clasa) {
        ((CraftServer) getPlugin().getServer()).getCommandMap().register(name, clasa);
    }

    @Override
    public String getTag(org.bukkit.inventory.ItemStack itemStack, String key) {
        ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = i.s();
        return tag == null ? null : tag.e(key) ? tag.l(key) : null;
    }

    @Override
    public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        p.sendTitle(title == null ? " " : title, subtitle == null ? " " : subtitle, fadeIn, stay, fadeOut);
    }

    public void spawnSilverfish(Location loc, ITeam bedWarsTeam, double speed, double health, int despawn, double damage) {
        new Despawnable(Silverfish.spawn(loc, bedWarsTeam, speed, health, despawn, damage), bedWarsTeam, despawn,
                Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME, PlayerKillEvent.PlayerKillCause.SILVERFISH_FINAL_KILL, PlayerKillEvent.PlayerKillCause.SILVERFISH);
    }

    @Override
    public void spawnIronGolem(Location loc, ITeam bedWarsTeam, double speed, double health, int despawn) {
        new Despawnable(IGolem.spawn(loc, bedWarsTeam, speed, health, despawn), bedWarsTeam, despawn, Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME,
                PlayerKillEvent.PlayerKillCause.IRON_GOLEM_FINAL_KILL, PlayerKillEvent.PlayerKillCause.IRON_GOLEM);
    }

    @Override
    public void playAction(Player p, String text) {
        CraftPlayer cPlayer = (CraftPlayer) p;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.c, chatUUID);
        cPlayer.getHandle().b.a(ppoc);
    }

    @Override
    public boolean isBukkitCommandRegistered(String name) {
        return ((CraftServer) getPlugin().getServer()).getCommandMap().getCommand(name) != null;
    }

    @Override
    public org.bukkit.inventory.ItemStack getItemInHand(Player p) {
        return p.getInventory().getItemInMainHand();
    }

    @Override
    public void hideEntity(Entity e, Player p) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(e.getEntityId());
        ((CraftPlayer) p).getHandle().b.a(packet);

    }

    @Override
    public void minusAmount(Player p, org.bukkit.inventory.ItemStack i, int amount) {
        if (i.getAmount() - amount <= 0) {
            if (p.getInventory().getItemInOffHand().equals(i)) {
                p.getInventory().setItemInOffHand(null);
            } else {
                p.getInventory().removeItem(i);
            }
            return;
        }
        i.setAmount(i.getAmount() - amount);
        p.updateInventory();
    }

    @Override
    public void setSource(TNTPrimed tnt, Player owner) {
        EntityLiving nmsEntityLiving = (((CraftLivingEntity) owner).getHandle());
        EntityTNTPrimed nmsTNT = (((CraftTNTPrimed) tnt).getHandle());
        try {
            //noinspection JavaReflectionMemberAccess
            Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(nmsTNT, nmsEntityLiving);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isArmor(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).c() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).c() instanceof ItemArmor;
    }

    @Override
    public boolean isTool(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).c() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).c() instanceof ItemTool;
    }

    @Override
    public boolean isSword(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).c() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).c() instanceof ItemSword;
    }

    @Override
    public boolean isAxe(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack).c() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).c() instanceof ItemAxe;
    }

    @Override
    public boolean isBow(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).c() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).c() instanceof ItemBow;
    }

    @Override
    public boolean isProjectile(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).E() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).E() instanceof IProjectile;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void registerEntities() {
        //noinspection deprecation
        Map<String, Type<?>> types = (Map<String, Type<?>>) DataConverterRegistry.a().getSchema(
                DataFixUtils.makeKey(SharedConstants.b().getWorldVersion())
        ).findChoiceType(DataConverterTypes.q).types();

        types.put("minecraft:bwsilverfish", types.get("minecraft:silverfish"));
        EntityTypes.Builder.a(Silverfish::new, EnumCreatureType.a).a("bwsilverfish");

        types.put("minecraft:bwgolem", types.get("minecraft:iron_golem"));
        EntityTypes.Builder.a(IGolem::new, EnumCreatureType.a).a("bwgolem");
    }

    @Override
    public void spawnShop(Location loc, String name1, List<Player> players, IArena arena) {
        Location l = loc.clone();

        if (l.getWorld() == null) return;
        Villager vlg = (Villager) l.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        vlg.setAI(false);
        vlg.setRemoveWhenFarAway(false);
        vlg.setCollidable(false);
        vlg.setInvulnerable(true);
        vlg.setSilent(true);

        for (Player p : players) {
            String[] nume = Language.getMsg(p, name1).split(",");
            if (nume.length == 1) {
                ArmorStand a = createArmorStand(nume[0], l.clone().add(0, 1.85, 0));
                new ShopHolo(Language.getPlayerLanguage(p).getIso(), a, null, l, arena);
            } else {
                ArmorStand a = createArmorStand(nume[0], l.clone().add(0, 2.1, 0));
                ArmorStand b = createArmorStand(nume[1], l.clone().add(0, 1.85, 0));
                new ShopHolo(Language.getPlayerLanguage(p).getIso(), a, b, l, arena);
            }
        }
        for (ShopHolo sh : ShopHolo.getShopHolo()) {
            if (sh.getA() == arena) {
                sh.update();
            }
        }
    }

    @Override
    public double getDamage(org.bukkit.inventory.ItemStack i) {
        ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound compound = (nmsStack.s() != null) ? nmsStack.s() : new NBTTagCompound();
        return compound.k("generic.attackDamage");
    }

    private static ArmorStand createArmorStand(String name, Location loc) {
        if (loc == null) return null;
        if (loc.getWorld() == null) return null;
        ArmorStand a = loc.getWorld().spawn(loc, ArmorStand.class);
        a.setGravity(false);
        a.setVisible(false);
        a.setCustomNameVisible(true);
        a.setCustomName(name);
        return a;
    }

    @Override
    public void voidKill(Player p) {
        ((CraftPlayer) p).getHandle().a(DamageSource.m, 1000);
    }

    @Override
    public void hideArmor(Player victim, Player receiver) {
        List<Pair<EnumItemSlot, ItemStack>> items = new ArrayList<>();
        List<Pair<EnumItemSlot, ItemStack>> hands = new ArrayList<>();
        hands.add(new Pair<>(EnumItemSlot.a, new ItemStack(Item.b(0))));
        hands.add(new Pair<>(EnumItemSlot.b, new ItemStack(Item.b(0))));

        items.add(new Pair<>(EnumItemSlot.f, new ItemStack(Item.b(0))));
        items.add(new Pair<>(EnumItemSlot.e, new ItemStack(Item.b(0))));
        items.add(new Pair<>(EnumItemSlot.d, new ItemStack(Item.b(0))));
        items.add(new Pair<>(EnumItemSlot.c, new ItemStack(Item.b(0))));
        PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(victim.getEntityId(), items);
        PacketPlayOutEntityEquipment packet2 = new PacketPlayOutEntityEquipment(victim.getEntityId(), hands);
        EntityPlayer pc = ((CraftPlayer) receiver).getHandle();
        if (victim != receiver) {
            pc.b.a(packet2);
        }
        pc.b.a(packet1);
    }

    @Override
    public void showArmor(Player victim, Player receiver) {
        List<Pair<EnumItemSlot, ItemStack>> items = new ArrayList<>();
        List<Pair<EnumItemSlot, ItemStack>> hands = new ArrayList<>();

        hands.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(victim.getInventory().getItemInMainHand())));
        hands.add(new Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(victim.getInventory().getItemInOffHand())));

        items.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(victim.getInventory().getHelmet())));
        items.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(victim.getInventory().getChestplate())));
        items.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(victim.getInventory().getLeggings())));
        items.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(victim.getInventory().getBoots())));
        PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(victim.getEntityId(), items);
        PacketPlayOutEntityEquipment packet2 = new PacketPlayOutEntityEquipment(victim.getEntityId(), hands);
        EntityPlayer pc = ((CraftPlayer) receiver).getHandle();
        if (victim != receiver) {
            pc.b.a(packet2);
        }
        pc.b.a(packet1);
    }

    @Override
    public void spawnDragon(Location l, ITeam bwt) {
        if (l == null || l.getWorld() == null) {
            getPlugin().getLogger().log(Level.WARNING, "Could not spawn Dragon. Location is null");
            return;
        }
        EnderDragon ed = (EnderDragon) l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
        ed.setPhase(EnderDragon.Phase.CIRCLING);
    }

    @Override
    public void colorBed(ITeam bwt) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockState bed = bwt.getBed().clone().add(x, 0, z).getBlock().getState();
                if (bed instanceof Bed) {
                    bed.setType(bwt.getColor().bedMaterial());
                    bed.update();
                }
            }
        }
    }

    @Override
    public void registerTntWhitelist() {
        try {
            Field field = BlockBase.class.getDeclaredField("aI");
            field.setAccessible(true);
            field.set(Blocks.eq, 12f);
            field.set(Blocks.au, 300f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBlockTeamColor(Block block, TeamColor teamColor) {
        if (block.getType().toString().contains("STAINED_GLASS") || block.getType().toString().equals("GLASS")) {
            block.setType(teamColor.glassMaterial());
        } else if (block.getType().toString().contains("_TERRACOTTA")) {
            block.setType(teamColor.glazedTerracottaMaterial());
        } else if (block.getType().toString().contains("_WOOL")) {
            block.setType(teamColor.woolMaterial());
        }
    }

    @Override
    public void setCollide(Player p, IArena a, boolean value) {
        p.setCollidable(value);
        if (a == null) return;
        a.updateSpectatorCollideRule(p, value);
    }

    @Override
    public org.bukkit.inventory.ItemStack addCustomData(org.bukkit.inventory.ItemStack i, String data) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.s();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.c(tag);
        }

        tag.a("BedWars1058", data);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public org.bukkit.inventory.ItemStack setTag(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
        ItemStack is = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = is.s();
        if (tag == null) {
            tag = new NBTTagCompound();
            is.c(tag);
        }

        tag.a(key, value);
        return CraftItemStack.asBukkitCopy(is);
    }

    @Override
    public boolean isCustomBedWarsItem(org.bukkit.inventory.ItemStack i) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.s();
        if (tag == null) return false;
        return tag.e("BedWars1058");
    }

    @Override
    public String getCustomData(org.bukkit.inventory.ItemStack i) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.s();
        if (tag == null) return "";
        return tag.l("BedWars1058");
    }

    @Override
    public org.bukkit.inventory.ItemStack colourItem(org.bukkit.inventory.ItemStack itemStack, ITeam bedWarsTeam) {
        if (itemStack == null) return null;
        String type = itemStack.getType().toString();
        if (type.contains("_BED")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().bedMaterial(), itemStack.getAmount());
        } else if (type.contains("_STAINED_GLASS_PANE")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().glassPaneMaterial(), itemStack.getAmount());
        } else if (type.contains("STAINED_GLASS") || type.equals("GLASS")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().glassMaterial(), itemStack.getAmount());
        } else if (type.contains("_TERRACOTTA")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().glazedTerracottaMaterial(), itemStack.getAmount());
        } else if (type.contains("_WOOL")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().woolMaterial(), itemStack.getAmount());
        }
        return itemStack;
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(String material, int amount, short data) {
        org.bukkit.inventory.ItemStack i;
        try {
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.valueOf(material), amount);
        } catch (Exception ex) {
            getPlugin().getLogger().log(Level.WARNING, material + " is not a valid " + getName() + " material!");
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BEDROCK);
        }
        return i;
    }

    @Override
    public void teamCollideRule(Team team) {
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setCanSeeFriendlyInvisibles(true);
    }

    @Override
    public org.bukkit.Material materialFireball() {
        return org.bukkit.Material.FIRE_CHARGE;
    }

    @Override
    public org.bukkit.Material materialPlayerHead() {
        return org.bukkit.Material.PLAYER_HEAD;
    }

    @Override
    public org.bukkit.Material materialSnowball() {
        return org.bukkit.Material.SNOWBALL;
    }

    @Override
    public org.bukkit.Material materialGoldenHelmet() {
        return org.bukkit.Material.GOLDEN_HELMET;
    }

    @Override
    public org.bukkit.Material materialGoldenChestPlate() {
        return org.bukkit.Material.GOLDEN_CHESTPLATE;
    }

    @Override
    public org.bukkit.Material materialGoldenLeggings() {
        return org.bukkit.Material.GOLDEN_LEGGINGS;
    }

    @Override
    public org.bukkit.Material materialCake() {
        return org.bukkit.Material.CAKE;
    }

    @Override
    public org.bukkit.Material materialCraftingTable() {
        return org.bukkit.Material.CRAFTING_TABLE;
    }

    @Override
    public org.bukkit.Material materialEnchantingTable() {
        return org.bukkit.Material.ENCHANTING_TABLE;
    }

    @Override
    public org.bukkit.Material woolMaterial() {
        return org.bukkit.Material.WHITE_WOOL;
    }

    @Override
    public String getShopUpgradeIdentifier(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = i.s();
        return tag == null ? "null" : tag.e("tierIdentifier") ? tag.l("tierIdentifier") : "null";
    }

    @Override
    public org.bukkit.inventory.ItemStack setShopUpgradeIdentifier(org.bukkit.inventory.ItemStack itemStack, String identifier) {
        ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = i.s();
        if (tag == null) {
            tag = new NBTTagCompound();
            i.c(tag);
        }
        tag.a("tierIdentifier", identifier);
        return CraftItemStack.asBukkitCopy(i);
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerHead(Player player, org.bukkit.inventory.ItemStack copyTagFrom) {
        org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(materialPlayerHead());

        if (copyTagFrom != null) {
            ItemStack i = CraftItemStack.asNMSCopy(head);
            i.c(CraftItemStack.asNMSCopy(copyTagFrom).s());
            head = CraftItemStack.asBukkitCopy(i);
        }

//        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
//        FIXME: current hotfix will get rate limited! how the hell do we set head texture now?
//        wtf is this: SkullOwner:{Id:[I;-1344581477,-1919271229,-1306015584,-647763423],Name:"andrei1058"}
//        Field profileField;
//        try {
//            //noinspection ConstantConditions
//            profileField = headMeta.getClass().getDeclaredField("profile");
//            profileField.setAccessible(true);
//            profileField.set(headMeta, ((CraftPlayer) player).getProfile());
//        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
//            e1.printStackTrace();
//        }
//        assert headMeta != null;
//        headMeta.setOwningPlayer(player);
//        head.setItemMeta(headMeta);

        return head;
    }

    @Override
    public void sendPlayerSpawnPackets(Player respawned, IArena arena) {
        if (respawned == null) return;
        if (arena == null) return;
        if (!arena.isPlayer(respawned)) return;

        // if method was used when the player was still in re-spawning screen
        if (arena.getRespawnSessions().containsKey(respawned)) return;

        EntityPlayer entityPlayer = ((CraftPlayer) respawned).getHandle();
        PacketPlayOutNamedEntitySpawn show = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        PacketPlayOutEntityVelocity playerVelocity = new PacketPlayOutEntityVelocity(entityPlayer);
        PacketPlayOutEntityHeadRotation head = new PacketPlayOutEntityHeadRotation(entityPlayer, getCompressedAngle(entityPlayer.getBukkitYaw()));

        List<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.a, entityPlayer.b(EnumItemSlot.a)));
        list.add(new Pair<>(EnumItemSlot.b, entityPlayer.b(EnumItemSlot.b)));
        list.add(new Pair<>(EnumItemSlot.f, entityPlayer.b(EnumItemSlot.f)));
        list.add(new Pair<>(EnumItemSlot.e, entityPlayer.b(EnumItemSlot.e)));
        list.add(new Pair<>(EnumItemSlot.d, entityPlayer.b(EnumItemSlot.d)));
        list.add(new Pair<>(EnumItemSlot.c, entityPlayer.b(EnumItemSlot.c)));


        for (Player p : arena.getPlayers()) {
            if (p == null) continue;
            if (p.equals(respawned)) continue;
            // if p is in re-spawning screen continue
            if (arena.getRespawnSessions().containsKey(p)) continue;

            EntityPlayer boundTo = ((CraftPlayer) p).getHandle();
            if (p.getWorld().equals(respawned.getWorld())) {
                if (respawned.getLocation().distance(p.getLocation()) <= arena.getRenderDistance()) {

                    // send respawned player to regular players
                    boundTo.b.a(show);
                    boundTo.b.a(head);
                    boundTo.b.a(playerVelocity);
                    boundTo.b.a(new PacketPlayOutEntityEquipment(respawned.getEntityId(), list));

                    // send nearby players to respawned player
                    // if the player has invisibility hide armor
                    if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        hideArmor(p, respawned);
                    } else {
                        PacketPlayOutNamedEntitySpawn show2 = new PacketPlayOutNamedEntitySpawn(boundTo);
                        PacketPlayOutEntityVelocity playerVelocity2 = new PacketPlayOutEntityVelocity(boundTo);
                        PacketPlayOutEntityHeadRotation head2 = new PacketPlayOutEntityHeadRotation(boundTo, getCompressedAngle(boundTo.getBukkitYaw()));
                        entityPlayer.b.a(show2);
                        entityPlayer.b.a(playerVelocity2);
                        entityPlayer.b.a(head2);
                        showArmor(p, respawned);
                    }
                }
            }
        }

        for (Player spectator : arena.getSpectators()) {
            if (spectator == null) continue;
            if (spectator.equals(respawned)) continue;
            EntityPlayer boundTo = ((CraftPlayer) spectator).getHandle();
            respawned.hidePlayer(getPlugin(), spectator);
            if (spectator.getWorld().equals(respawned.getWorld())) {
                if (respawned.getLocation().distance(spectator.getLocation()) <= arena.getRenderDistance()) {

                    // send respawned player to spectator
                    boundTo.b.a(show);
                    boundTo.b.a(playerVelocity);
                    boundTo.b.a(new PacketPlayOutEntityEquipment(respawned.getEntityId(), list));
                    boundTo.b.a(new PacketPlayOutEntityHeadRotation(entityPlayer, getCompressedAngle(entityPlayer.getBukkitYaw())));
                }
            }
        }
    }

    @Override
    public String getInventoryName(InventoryEvent e) {
        return e.getView().getTitle();
    }

    @Override
    public void setUnbreakable(ItemMeta itemMeta) {
        itemMeta.setUnbreakable(true);
    }

    @Override
    public String getMainLevel() {
        //noinspection deprecation
        return ((DedicatedServer) MinecraftServer.getServer()).z.a().p;
    }

    @Override
    public int getVersion() {
        return 8;
    }

    @Override
    public void setJoinSignBackground(BlockState b, org.bukkit.Material material) {
        if (b.getBlockData() instanceof WallSign) {
            b.getBlock().getRelative(((WallSign) b.getBlockData()).getFacing().getOppositeFace()).setType(material);
        }
    }

    @Override
    public void spigotShowPlayer(Player victim, Player receiver) {
        receiver.showPlayer(getPlugin(), victim);
    }

    @Override
    public void spigotHidePlayer(Player victim, Player receiver) {
        receiver.hidePlayer(getPlugin(), victim);
    }

    @Override
    public Fireball setFireballDirection(Fireball fireball, Vector vector) {
        EntityFireball fb = ((CraftFireball) fireball).getHandle();
        fb.b = vector.getX() * 0.1D;
        fb.c = vector.getY() * 0.1D;
        fb.d = vector.getZ() * 0.1D;
        return (Fireball) fb.getBukkitEntity();
    }

    @Override
    public void playRedStoneDot(Player player) {
        Color color = Color.RED;
        PacketPlayOutWorldParticles particlePacket = new PacketPlayOutWorldParticles(new ParticleParamRedstone(new Vector3fa((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()), (float) 1),
                true, player.getLocation().getX(), player.getLocation().getY() + 2.6, player.getLocation().getZ(), 0, 0, 0, 0, 0);
        for (Player inWorld : player.getWorld().getPlayers()) {
            if (inWorld.equals(player)) continue;
            ((CraftPlayer) inWorld).getHandle().b.a(particlePacket);
        }
    }

    @Override
    public void clearArrowsFromPlayerBody(Player player) {
        ((CraftLivingEntity)player).getHandle().ai().b(new DataWatcherObject<>(12, DataWatcherRegistry.b),-1);
    }
}