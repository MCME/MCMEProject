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
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcme.mcmeproject.util.bungee;
import com.mcme.mcmeproject.util.utils;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectCreate extends ProjectCommand {

    public ProjectCreate(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Create a new project");
        setUsageDescription(" <ProjectName>: Create a new project");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        final Player pl = (Player) cs;
        if (!PluginData.getProjectsAll().containsKey(args[0])) {

            new BukkitRunnable() {

                @Override
                public void run() {

                    try {

                        Mcproject.getPluginInstance().getInsertProject().setString(1, utils.createId().toString());
                        Mcproject.getPluginInstance().getInsertProject().setString(2, args[0]);
                        Mcproject.getPluginInstance().getInsertProject().setString(3, pl.getUniqueId().toString());
                        Mcproject.getPluginInstance().getInsertProject().setLong(4, System.currentTimeMillis());
                        Mcproject.getPluginInstance().getInsertProject().setLong(5, System.currentTimeMillis());
                        Mcproject.getPluginInstance().getInsertProject().setLong(6, System.currentTimeMillis());
                        Mcproject.getPluginInstance().getInsertProject().setString(7, ProjectStatus.HIDDEN.name().toUpperCase());

                        Mcproject.getPluginInstance().getInsertProject().executeUpdate();
                        sendCreated(cs, args[0]);
                        PluginData.loadProjects();

                        bungee.sendReload(pl, "projects");

                    } catch (SQLException ex) {
                        Logger.getLogger(ProjectCreate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }.runTaskAsynchronously(Mcproject.getPluginInstance());

        } else {

            sendAlreadyProject(cs);

        }

    }

    private void sendAlreadyProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project already exists");
    }

    private void sendCreated(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "New project " + name + " created! Add new information, type /project help");
    }

}
