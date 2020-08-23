/*
 Copyright (C) 2020 MCME (Fraspace5)
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
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.thegaffer.storage.JobDatabase;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectDetails extends ProjectCommand {

    public ProjectDetails(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Show details and statistics about a project");
        setUsageDescription(" <ProjectName> : Show statistics and other informations about a project");
    }

    private static final List<String> pers = new ArrayList<>();

    private static final List<String> jobs = new ArrayList<>();

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        final Player pl = (Player) cs;

        if (PluginData.getProjectsAll().containsKey(args[0])) {

            new BukkitRunnable() {

                @Override
                public void run() {

                    try {

                        Mcproject.getPluginInstance().getSelectNewsDataId().setString(1, pl.getUniqueId().toString());
                        Mcproject.getPluginInstance().getSelectNewsDataId().setString(2, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                        final ResultSet r = Mcproject.getPluginInstance().getSelectNewsDataId().executeQuery();

                        if (!r.first()) {
                            Mcproject.getPluginInstance().getInsertNewsData().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                            Mcproject.getPluginInstance().getInsertNewsData().setString(2, pl.getUniqueId().toString());
                            Mcproject.getPluginInstance().getInsertNewsData().executeUpdate();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }.runTaskAsynchronously(Mcproject.getPluginInstance());

            if (pl.hasPermission("project.manager") || pl.hasPermission("project.owner")) {

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            ProjectData pr = PluginData.getProjectsAll().get(args[0]);

                            Mcproject.getPluginInstance().getSelectPeopleDataID().setString(1, pr.getIdproject().toString());
                            final ResultSet r2 = Mcproject.getPluginInstance().getSelectPeopleDataID().executeQuery();

                            Long r = (pr.getTime() - System.currentTimeMillis());
                            Long f = (System.currentTimeMillis() - pr.getUpdated());
                            //milliseconds

                            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                            String ps = Bukkit.getOfflinePlayer(pr.getHead()).getName();
                            switch (pr.getStatus()) {
                                case FINISHED:
                                    if (pr.isMain()) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server" + "\n");
                                    }
                                    message.addSimple(ChatColor.BOLD + "PROJECT: " + pr.getName() + " (Finished) ");
                                    message.addTooltipped(ChatColor.AQUA + "[...]" + "\n", ChatColor.GREEN + "Closed " + accTime(f) + " ago");
                                    message.addSimple(ChatColor.RED.BOLD + "Leader: " + ChatColor.DARK_PURPLE + ps);
                                    if (pr.getAssistants().size() > 0) {
                                        message.addSimple("\n" + ChatColor.BOLD + "Assistants: " + ChatColor.DARK_PURPLE + assistantsList(pr.getAssistants()) + "\n");
                                    }
                                    if (!pr.getDescription().equals(" ")) {
                                        message.addSimple(ChatColor.AQUA + pr.getDescription() + "\n");
                                    }

                                    message.addSimple(ChatColor.GOLD + "+--------------------+" + "\n"
                                            + ChatColor.GREEN + "-Current percentage: " + pr.getPercentage().toString() + "%" + "\n"
                                            + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.getMinutes() / 60) + "\n"
                                            + ChatColor.GREEN + "-Blocks P/B: " + pr.getBlocks() + "\n"
                                            + ChatColor.GOLD + "+--------------------+"
                                    );
                                    break;
                                case HIDDEN:
                                    if (pr.isMain()) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server" + "\n");
                                    }
                                    message.addSimple(ChatColor.BOLD + "PROJECT: " + pr.getName() + " (Hidden) ");
                                    message.addTooltipped(ChatColor.AQUA + "[...]" + "\n", ChatColor.GREEN + "Updated " + accTime(f) + " ago");
                                    message.addSimple(ChatColor.RED.BOLD + "Leader: " + ChatColor.DARK_PURPLE + ps);
                                    if (pr.getAssistants().size() > 0) {
                                        message.addSimple("\n" + ChatColor.BOLD + "Assistants: " + ChatColor.DARK_PURPLE + assistantsList(pr.getAssistants()) + "\n");
                                    }
                                    if (!pr.getDescription().equals(" ")) {
                                        message.addSimple(ChatColor.AQUA + pr.getDescription() + "\n");
                                    }
                                    message.addSimple(ChatColor.GOLD + "+--------------------+" + "\n"
                                            + ChatColor.GREEN + "-Current percentage: " + pr.getPercentage().toString() + "%" + "\n"
                                            + ChatColor.GREEN + "-Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.getMinutes() / 60) + "\n"
                                            + ChatColor.GREEN + "-People that works on: " + people(r2) + "\n"
                                            + ChatColor.GREEN + "-Blocks P/B: " + pr.getBlocks() + "\n"
                                            + ChatColor.GOLD + "+--------------------+");
                                    break;
                                default:
                                    if (pr.isMain()) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server" + "\n");
                                    }
                                    message.addSimple(ChatColor.BOLD + "PROJECT: " + pr.getName() + " ");
                                    message.addTooltipped(ChatColor.AQUA + "[...]" + "\n", ChatColor.GREEN + "Updated " + accTime(f) + " ago");
                                    message.addSimple(ChatColor.RED.BOLD + "Leader: " + ChatColor.DARK_PURPLE + ps);
                                    if (pr.getAssistants().size() > 0) {
                                        message.addSimple("\n" + ChatColor.BOLD + "Assistants: " + ChatColor.DARK_PURPLE + assistantsList(pr.getAssistants()) + "\n");
                                    }
                                    if (!pr.getDescription().equals(" ")) {
                                        message.addSimple(ChatColor.AQUA + pr.getDescription() + "\n");
                                    }
                                    message.addSimple(ChatColor.GOLD + "+--------------------+" + "\n"
                                            + ChatColor.GREEN + "-Current percentage: " + pr.getPercentage().toString() + "%" + "\n"
                                            + ChatColor.GREEN + "-Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.getMinutes() / 60) + "\n"
                                            + ChatColor.GREEN + "-People that works on: " + people(r2) + "\n"
                                            + ChatColor.GREEN + "-Blocks P/B: " + pr.getBlocks() + "\n"
                                            + ChatColor.GOLD + "+--------------------+"
                                    );
                                    break;
                            }

                            if (!pr.getStatus().equals(ProjectStatus.FINISHED)) {

                                jj(pr);
                                if (!jobs.isEmpty()) {
                                    message.addSimple(ChatColor.AQUA + "\n" + "Jobs hosted last month: " + "\n" + job());

                                    message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                                } else {
                                    message.addSimple(ChatColor.AQUA + "\n" + "No jobs hosted last month");
                                    message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                                }

                                if (PluginData.getRegionsReadable().containsKey(PluginData.getProjectsAll().get(args[0]).getIdproject())) {

                                    for (String region : PluginData.getRegionsReadable().get(PluginData.getProjectsAll().get(args[0]).getIdproject())) {

                                        message.addSimple("\n" + ChatColor.AQUA + region.toUpperCase() + ": ");
                                        if (PluginData.getWarps().containsKey(PluginData.getRegions().get(region).getIdr())) {
                                            message.addClickable(ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + "Click to teleport", "/project warp " + pr.getName() + " " + region).setRunDirect();

                                        } else {
                                            message.addSimple(ChatColor.RED + "No warp available for this region");

                                        }

                                    }
                                }
                            }

                            if (!pr.getLink().equalsIgnoreCase("Nothing")) {
                                message.addFancy("\n" + ChatColor.LIGHT_PURPLE + "-Forum Thread", pr.getLink(), "Click to go on the forum");
                            }

                            message.send(pl);
                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            } else if (PluginData.getProjectsAll().get(args[0]).getStatus() == ProjectStatus.SHOWED) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            ProjectData pr = PluginData.getProjectsAll().get(args[0]);

                            Mcproject.getPluginInstance().getSelectPeopleDataID().setString(1, pr.getIdproject().toString());
                            final ResultSet r2 = Mcproject.getPluginInstance().getSelectPeopleDataID().executeQuery();

                            Long r = (pr.getTime() - System.currentTimeMillis());
                            Long f = (System.currentTimeMillis() - pr.getUpdated());
                            //milliseconds

                            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                            String ps = Bukkit.getOfflinePlayer(pr.getHead()).getName();
                            if (pr.isMain()) {
                                message.addSimple(ChatColor.DARK_RED + "Main Project of the Server" + "\n");
                                message.addSimple(ChatColor.BOLD + "Project name: " + pr.getName() + " ");
                                message.addTooltipped(ChatColor.AQUA + "[...]" + "\n", ChatColor.GREEN + "Updated " + accTime(f) + " ago");
                                message.addSimple(ChatColor.RED.BOLD + "Leader: " + ChatColor.DARK_PURPLE + ps);
                                if (pr.getAssistants().size() > 0) {
                                    message.addSimple("\n" + ChatColor.BOLD + "Assistants: " + ChatColor.DARK_PURPLE + assistantsList(pr.getAssistants()) + "\n");
                                }
                                if (!pr.getDescription().equals(" ")) {
                                    message.addSimple(ChatColor.AQUA + pr.getDescription() + "\n");
                                }
                                message.addSimple(ChatColor.GOLD + "+--------------------+" + "\n"
                                        + ChatColor.GREEN + "-Current percentage: " + pr.getPercentage().toString() + "%" + "\n"
                                        + ChatColor.GREEN + "-Extimated Time: " + time(r) + "\n"
                                        + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.getMinutes() / 60) + "\n"
                                        + ChatColor.GREEN + "-People that works on: " + people(r2) + "\n"
                                        + ChatColor.GREEN + "-Blocks P/B: " + pr.getBlocks() + "\n"
                                        + ChatColor.GOLD + "+--------------------+"
                                );
                            } else {

                                message.addSimple(ChatColor.BOLD + "Project name: " + pr.getName() + " ");
                                message.addTooltipped(ChatColor.AQUA + "[...]" + "\n", ChatColor.GREEN + "Updated " + accTime(f) + " ago");
                                message.addSimple(ChatColor.RED.BOLD + "Leader: " + ChatColor.DARK_PURPLE + ps);
                                if (pr.getAssistants().size() > 0) {
                                    message.addSimple("\n" + ChatColor.BOLD + "Assistants: " + ChatColor.DARK_PURPLE + assistantsList(pr.getAssistants()) + "\n");
                                }
                                if (!pr.getDescription().equals(" ")) {
                                    message.addSimple(ChatColor.AQUA + pr.getDescription() + "\n");
                                }
                                message.addSimple(ChatColor.GOLD + "+--------------------+" + "\n"
                                        + ChatColor.GREEN + "-Current percentage: " + pr.getPercentage().toString() + "%" + "\n"
                                        + ChatColor.GREEN + "-Extimated Time: " + time(r) + "\n"
                                        + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.getMinutes() / 60) + "\n"
                                        + ChatColor.GREEN + "-People that works on: " + people(r2) + "\n"
                                        + ChatColor.GREEN + "-Blocks P/B: " + pr.getBlocks() + "\n"
                                        + ChatColor.GOLD + "+--------------------+"
                                );
                            }
                            jj(pr);

                            if (!jobs.isEmpty()) {
                                message.addSimple(ChatColor.AQUA + "\n" + "Jobs hosted last month: " + "\n" + job());

                                message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                            } else {
                                message.addSimple(ChatColor.RED + "\n" + "No jobs hosted last month");
                                message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                            }

                            for (String region : PluginData.getRegionsReadable().get(PluginData.getProjectsAll().get(args[0]).getIdproject())) {

                                message.addSimple("\n" + ChatColor.AQUA + region.toUpperCase() + ": ");
                                if (PluginData.getWarps().containsKey(PluginData.getRegions().get(region).getIdr())) {
                                    message.addClickable(ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + "Click to teleport", "/project warp " + pr.getName() + " " + region).setRunDirect();

                                } else {
                                    message.addSimple(ChatColor.RED + "No warp available for this region");

                                }

                            }
                            if (!pr.getLink().equalsIgnoreCase("Nothing")) {
                                message.addFancy("\n" + ChatColor.LIGHT_PURPLE + "-Forum Thread", pr.getLink(), "Click to go on the forum");
                            }

                            message.send(pl);

                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            } else {
                sendProjectError(cs);
            }

        } else {

            sendNoProject(cs);

        }

    }

    private static String people(ResultSet r) throws NullPointerException, SQLException {
        pers.clear();

        if (r.first()) {

            do {

                if (r.getLong("blocks") > 10) {

                    OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(r.getString("player_uuid")));

                    pers.add(p.getName());
                }

            } while (r.next());

        }

        return String.valueOf(pers.size());

    }

    private static String assistantsList(List<UUID> r) throws NullPointerException, SQLException {

        StringBuilder builder = new StringBuilder();

        r.forEach((value) -> {
            int index = r.size() - 1;
            OfflinePlayer off = Bukkit.getOfflinePlayer(value);
            UUID val = r.get(index);
            if (!value.equals(val)) {

                builder.append(off.getName()).append(",");
            } else {
                builder.append(off.getName()).append(" ");
            }
        });

        return builder.toString();

    }

    private static String job() throws NullPointerException {

        StringBuilder builder = new StringBuilder();

        jobs.forEach((value) -> {
            int index = jobs.size() - 1;

            String val = jobs.get(index);
            if (!value.equals(val)) {

                builder.append(value).append(",");
            } else {
                builder.append(value).append(" ");
            }
        });

        return builder.toString();

    }

    private static void jj(ProjectData pr) {
        jobs.clear();

        for (String value : pr.getJobs()) {
            if (JobDatabase.getActiveJobs().containsKey(value)) {

                if (JobDatabase.getActiveJobs().get(value).isPaused()) {
                    jobs.add(value);
                } else if (JobDatabase.getActiveJobs().get(value).isPrivate()) {

                } else {
                    jobs.add(value);
                }
            } else if (JobDatabase.getInactiveJobs().containsKey(value)) {
                if (System.currentTimeMillis() - JobDatabase.getInactiveJobs().get(value).getEndTime() < 2678400000L) {
                    jobs.add(value);
                }
            }

        }
    }

    private static String time(Long seconds) {

        Double d = (double) seconds / 86400000.0;
        Integer days = exactTruncation(d);
        if (days < 7 && days > 0) {

            return days + " days";

        } else if (days >= 7 && days <= 28) {

            Double w = days / 7.0;
            Integer weeks = exactTruncation(w);
            return weeks + " weeks";

        } else if (days > 28 && days < 31) {

            Double y = days - 28.0;
            return "4 weeks and " + y + " days";

        } else if (days >= 31 && days <= 341) {

            Double m = days / 31.0;
            Integer months = exactTruncation(m);
            Integer month2 = truncationForDifect(m);
            Double s = months * 31.0;
            Double rd = days - s;
            Double s2 = month2 * 31.0;
            Double rd2 = days - s2;
            if (months == month2) {
                if (rd != 0 && rd > 0) {

                    return months + " months and " + rd + " days";

                } else {

                    return months + " months";
                }
            } else {
                if (rd2 != 0 && rd2 > 0) {

                    return month2 + " months and " + rd2 + " days";

                } else {

                    return month2 + " months";
                }
            }

        } else if (days > 341 && days < 365) {

            Double y = days - 341.0;

            return "11 months and " + y + " days";

        } else if (days >= 365) {

            Double y = days / 365.0;
            Integer years = exactTruncation(y);

            Double ys = days - (years * 365.0);

            if (ys > 0) {
                return years + " years and " + ys + " days";
            } else {
                return years + " years";
            }

        } else {

            return "N/A";

        }

    }

    private static String accTime(Long seconds) {

        Double d = (double) seconds / 86400000.0;
        Integer days = exactTruncation(d);
        if (days < 7 && days > 0) {

            return days + " days";

        } else if (days >= 7 && days <= 28) {

            Double w = days / 7.0;
            Integer weeks = exactTruncation(w);
            return weeks + " weeks";

        } else if (days > 28 && days < 31) {

            Double y = days - 28.0;
            return "4 weeks and " + y + " days";

        } else if (days >= 31 && days <= 341) {

            Double m = days / 31.0;
            Integer months = exactTruncation(m);
            Double rd = days - (months * 31.0);
            if (rd != 0) {

                return months + " months and " + rd + " days";
            } else {

                return months + " months";
            }

        } else if (days > 341 && days < 365) {

            Double y = days - 341.0;

            return "11 months and " + y + " days";
        } else if (days >= 365) {

            Double y = days / 365.0;
            Integer years = exactTruncation(y);
            Double ys = days - (years * 365.0);

            return years + " years and " + ys + " days";

        } else if (days == 0) {

            Double h = seconds / 3600000.0;
            Integer hour = exactTruncation(h);
            if (hour > 0) {
                Double m = hour * 3600000.0;
                Double mm = seconds - m;
                Double m3 = mm / 60000.0;
                Integer minutes = exactTruncation(m3);

                if (minutes > 0) {

                    return hour + " hours and " + minutes + " minutes";

                } else {

                    return hour + " hours";
                }

            } else {
                Double m = seconds / 60000.0;
                Integer min = exactTruncation(m);

                return min + " minutes";
            }

        } else {

            return "N/A";

        }

    }

    private static Integer exactTruncation(Double number) {

        int i = (int) Math.round(number);
        Double decimalPart = number - i;
        Double middle = 0.50;
        if (decimalPart.compareTo(middle) >= 0) {
            return i + 1;
        } else {
            return i;
        }

    }

    private static Integer truncationForDifect(Double number) {

        int i = (int) Math.round(number);

        return i;

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendProjectError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is hidden or it has been marked as finished!");
    }

}
