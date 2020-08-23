/*
 * Copyright (C) 2020 MCME (Fraspace5)
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
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectReload extends ProjectCommand {

    public ProjectReload(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Reload some data");
        setUsageDescription(" all|projects|map|regions|warps: Reload");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        Player pl = (Player) cs;
        switch (args[0]) {
            case "all":
                pl.sendMessage(ChatColor.GREEN + "Wait for the reload");
                try {
                    PluginData.loadProjects();
                    PluginData.loadRegions();
                    pl.sendMessage(ChatColor.GREEN + "Reload Completed for " + ChatColor.BLUE + "Projects");
                    pl.sendMessage(ChatColor.GREEN + "Reload Completed for " + ChatColor.BLUE + "Regions");

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            PluginData.loadWarps();
                            PluginData.loadAllDynmap();

                            pl.sendMessage(ChatColor.GREEN + "Reload Completed for " + ChatColor.BLUE + "Warps");
                            pl.sendMessage(ChatColor.GREEN + "Reload Completed for " + ChatColor.BLUE + "Map");
                            pl.sendMessage(ChatColor.GREEN + "Reload Completed, no errors found");

                        }

                    }.runTaskLater(Mcproject.getPluginInstance(), 20L);

                } catch (IllegalArgumentException | IllegalStateException e) {
                    pl.sendMessage(ChatColor.RED + "Errors Found - " + e.getClass().getName());
                }
                break;
            case "projects":

                pl.sendMessage(ChatColor.GREEN + "Wait for the reload");

                try {
                    PluginData.loadProjects();
                    pl.sendMessage(ChatColor.GREEN + "Reload Completed");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    pl.sendMessage(ChatColor.RED + "Errors Found - " + e.getClass().getName());
                }

                break;
            case "map":
                pl.sendMessage(ChatColor.GREEN + "Wait for the reload");

                try {
                    PluginData.loadAllDynmap();
                    pl.sendMessage(ChatColor.GREEN + "Reload Completed");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    pl.sendMessage(ChatColor.RED + "Errors Found - " + e.getClass().getName());
                }
                break;
            case "regions":
                pl.sendMessage(ChatColor.GREEN + "Wait for the reload");

                try {
                    PluginData.loadRegions();
                    pl.sendMessage(ChatColor.GREEN + "Reload Completed");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    pl.sendMessage(ChatColor.RED + "Errors Found - " + e.getClass().getName());
                }
                break;
            case "warps":
                pl.sendMessage(ChatColor.GREEN + "Wait for the reload");

                try {
                    PluginData.loadWarps();
                    pl.sendMessage(ChatColor.GREEN + "Reload Completed");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    pl.sendMessage(ChatColor.RED + "Errors Found - " + e.getClass().getName());
                }
                break;

            default:
                pl.sendMessage(ChatColor.RED + "You need to say what you want to reload...");

                break;

        }

    }

}
