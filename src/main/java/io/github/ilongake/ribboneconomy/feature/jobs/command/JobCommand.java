package io.github.ilongake.ribboneconomy.feature.jobs.command;

import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.JobType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand implements CommandExecutor {

    private final JobManager jobManager;

    public JobCommand(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用できます。");
            return true;
        }

        // /job
        if (args.length == 0) {

            JobType job = jobManager.getJob(
                    player.getUniqueId()
            );

            player.sendMessage(
                    "§6現在の職業: §e"
                            + job.getDisplayName()
            );

            player.sendMessage(
                    "§7職業を変更するには"
            );

            player.sendMessage(
                    "§a/job miner"
            );

            player.sendMessage(
                    "§a/job farmer"
            );

            player.sendMessage(
                    "§a/job hunter"
            );

            player.sendMessage(
                    "§c/job leave §7で職業を辞められます。"
            );

            return true;
        }

        String jobName = args[0].toLowerCase();

        switch (jobName) {

            case "miner":

                jobManager.setJob(
                        player.getUniqueId(),
                        JobType.MINER
                );

                player.sendMessage(
                        "§a職業を「採掘師」に変更しました！"
                );

                break;

            case "farmer":

                jobManager.setJob(
                        player.getUniqueId(),
                        JobType.FARMER
                );

                player.sendMessage(
                        "§a職業を「農家」に変更しました！"
                );

                break;

            case "hunter":

                jobManager.setJob(
                        player.getUniqueId(),
                        JobType.HUNTER
                );

                player.sendMessage(
                        "§a職業を「狩人」に変更しました！"
                );

                break;

            case "leave":

                jobManager.leaveJob(
                        player.getUniqueId()
                );

                player.sendMessage(
                        "§c職業を辞めました。"
                );

                break;

            default:

                player.sendMessage(
                        "§cその職業は存在しません。"
                );

                player.sendMessage(
                        "§7使用可能: miner / farmer / hunter"
                );

                break;
        }

        return true;
    }
}