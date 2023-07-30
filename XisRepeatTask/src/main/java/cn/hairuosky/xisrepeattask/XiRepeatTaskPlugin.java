package cn.hairuosky.xisrepeattask;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class XiRepeatTaskPlugin extends JavaPlugin implements TabCompleter {

    private static final String PERMISSION_ALL = "xirepeattask.all";

    @Override
    public void onEnable() {
        getLogger().info("===== Xi's Repeat Task =====");
        getLogger().info("Xi's Repeat Task启动！！！");
        getLogger().info("昔式重复任务持续为您服务");
        getLogger().info("如果喜欢，记得给bbs一个好评噢！");
        getLogger().info("===== Xi's Repeat Task =====");
        XiRepeatTaskPlugin plugin = this;

        // 注册命令
        PluginCommand xrtCommand = getCommand("xirepeattask");
        if (xrtCommand != null) {
            xrtCommand.setExecutor(plugin);
            xrtCommand.setTabCompleter(plugin);
        }

        PluginCommand xirepeattaskCommand = getCommand("xirepeattask");
        if (xirepeattaskCommand != null) {
            xirepeattaskCommand.setExecutor(plugin);
            xirepeattaskCommand.setTabCompleter(plugin);
        }
        // 注册权限
        registerPermission();

        // 加载配置文件
        saveDefaultConfig();


        // 调用注册任务的方法
        registerTasks();
    }

    private void registerPermission() {
        Permission allPermission = new Permission(PERMISSION_ALL);
        Bukkit.getPluginManager().addPermission(allPermission);
    }

    private void registerTasks() {
        // 清除先前注册的全部任务
        Bukkit.getScheduler().cancelTasks(this);

        // 获取配置文件
        FileConfiguration config = getConfig();

        // 遍历任务配置
        ConfigurationSection tasksSection = config.getConfigurationSection("tasks");
        if (tasksSection != null) {
            for (String taskName : tasksSection.getKeys(false)) {
                ConfigurationSection taskSection = tasksSection.getConfigurationSection(taskName);

                // 获取任务的延迟时间和命令
                assert taskSection != null;
                int delayInSeconds = taskSection.getInt("delay");
                long delayInTicks = delayInSeconds * 20L;
                String command = taskSection.getString("command");

                // 注册任务
                new TaskRunnable(command).runTaskTimer(this, delayInTicks, delayInTicks);

                getLogger().info("完成注册任务： " + taskName);
            }
        } else {
            getLogger().warning("配置文件中貌似没有任务哦(⊙o⊙)？.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("xrt") || label.equalsIgnoreCase("xirepeattask")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    // 处理 reload 命令
                    reloadConfig();
                    registerTasks();
                    sender.sendMessage("§a插件配置已重新加载！");
                    return true;
                } else if (args[0].equalsIgnoreCase("help")) {
                    // 处理 help 命令
                    sender.sendMessage("§6======== §fXi's Repeat Task 帮助 §6========");
                    sender.sendMessage("§e/xirepeattask(xrt) reload §7- §f重新加载插件配置");
                    sender.sendMessage("§e/xirepeattask(xrt) help §7- §f显示帮助信息");
                    sender.sendMessage("§6======== §fXi's Repeat Task 帮助 §6========");
                    return true;
                }
            }

            // 如果没有匹配的子命令，则显示默认帮助信息
            sender.sendMessage("§c使用方法：/xirepeattask <reload|help>");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        String label = command.getLabel();
        if (label.equalsIgnoreCase("xrt") || label.equalsIgnoreCase("xirepeattask")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("reload");
                completions.add("help");
                return completions;
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        getLogger().info("===== Xi's Repeat Task =====");
        getLogger().info("Xi's Repeat Task关闭！！！");
        getLogger().info("昔式重复任务已经关闭");
        getLogger().info("客官再见了┭┮﹏┭┮");
        getLogger().info("===== Xi's Repeat Task =====");
        // 其他清理操作...
    }
}

class TaskRunnable extends BukkitRunnable {

    private final String command;

    public TaskRunnable(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}