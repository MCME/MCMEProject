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
package com.mcme.mcmeproject.listener;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcmiddleearth.thegaffer.events.JobEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.mcmiddleearth.thegaffer.events.JobStartEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class JobListener implements Listener {

    @EventHandler
    public void onJobStart(JobStartEvent e) {

        String jobname = e.getJobName();
        String project = e.getJobProject();
        if (!project.equalsIgnoreCase("nothing")) {
            if (!PluginData.getProjectsAll().get(project).getJobs().contains(jobname)) {

                final List<String> jobs = PluginData.getProjectsAll().get(project).getJobs();

                jobs.add(jobname);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {
                            Mcproject.getPluginInstance().getUpdateWithoutUpdated().setString(1, "jobs");
                            Mcproject.getPluginInstance().getUpdateWithoutUpdated().setString(2, serialize(jobs));
                            Mcproject.getPluginInstance().getUpdateWithoutUpdated().setString(3, PluginData.getProjectsAll().get(project).getIdproject().toString());
                            Mcproject.getPluginInstance().getUpdateWithoutUpdated().executeUpdate();
                            PluginData.loadProjects();
                        } catch (SQLException ex) {
                            Logger.getLogger(JobListener.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            }

        }

    }

    /**
     *
     * @EventHandler public void onJobEnd(JobEndEvent e) {
     *
     * String jobname = e.getJobName(); String project = e.getJobProject(); if
     * (!project.equalsIgnoreCase("nothing")) { if
     * (PluginData.getProjectsAll().get(project).getJobs().contains(jobname)) {
     *
     * final List<String> jobs =
     * PluginData.getProjectsAll().get(project).getJobs();
     *
     * jobs.remove(jobname);
     *
     * new BukkitRunnable() {
     *
     * @Override public void run() { try { String stat = "UPDATE
     * mcmeproject_project_data SET jobs = '" + serialize(jobs) + "' WHERE
     * idproject = '" +
     * PluginData.getProjectsAll().get(project).getIdproject().toString() + "'
     * ;"; Statement statm =
     * Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
     * statm.setQueryTimeout(10); statm.executeUpdate(stat);
     * PluginData.loadProjects(); } catch (SQLException ex) {
     * Logger.getLogger(JobListener.class.getName()).log(Level.SEVERE, null,
     * ex); } }
     *
     * }.runTaskAsynchronously(Mcproject.getPluginInstance());
     *
     * }
     * }
     *
     * }
     *
     */
    private String serialize(List<String> intlist) {

        StringBuilder builder = new StringBuilder();

        intlist.forEach((s) -> {
            builder.append(s + ";");
        });

        return builder.toString();

    }

}
