/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectLink extends ProjectCommand {

    public ProjectLink(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Add a link to a project");
        setUsageDescription(" <ProjectName> <link>: Set the link of the project");
    }

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            manager = false;
            head = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                                String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET link = '" + args[1] + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";
                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                PluginData.loadProjects();
                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    sendDone(cs);
                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject = '" + PluginData.getProjectsAll().get(prr).idproject.toString() + "' AND staff_uuid ='" + pl.getUniqueId().toString() + "' ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    if (r.first()) {
                        manager = true;

                    }

                    if (PluginData.projectsAll.get(prr).head.equals(pl.getUniqueId())) {
                        head = true;

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

        if (manager || head || pl.hasPermission("project.owner")) {
            return true;
        } else {
            sendNoPermission(cs);
            return false;
        }

    }

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Project link updated!");
    }
}
