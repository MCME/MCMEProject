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
public class ProjectFinish extends ProjectCommand {

    public ProjectFinish(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Sets a project as finished");
        setUsageDescription(" <ProjectName> : Use this command to set a project as finished");
    }

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {

                if (PluginData.getProjectsAll().get(args[0]).getStatus().equals(ProjectStatus.FINISHED)) {

                    sendProjectError(cs);

                } else {

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                             
                                Mcproject.getPluginInstance().getUpdateFinish().setString(1, ProjectStatus.FINISHED.toString());
                                Mcproject.getPluginInstance().getUpdateFinish().setLong(2, System.currentTimeMillis());
                                Mcproject.getPluginInstance().getUpdateFinish().setLong(3, System.currentTimeMillis());
                                Mcproject.getPluginInstance().getUpdateFinish().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                Mcproject.getPluginInstance().getUpdateFinish().executeUpdate();
                                PluginData.loadProjects();

                                bungee.sendReload(pl, "projects");

                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    sendDone(cs);

                }
            }

        } else {

            sendNoProject(cs);

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendProjectError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project has already been set as finished");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Set as finished!");
    }

}
