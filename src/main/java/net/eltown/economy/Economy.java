package net.eltown.economy;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.SneakyThrows;
import net.eltown.economy.commands.*;
import net.eltown.economy.components.api.API;
import net.eltown.economy.components.language.Language;
import net.eltown.economy.components.provider.Provider;
import net.eltown.economy.components.tinyrabbit.TinyRabbit;
import net.eltown.economy.listener.PlayerListener;

import java.text.DecimalFormat;

public class Economy extends PluginBase {

    @Getter
    private static net.eltown.economy.components.api.API API;

    @Getter
    private double defaultMoney;
    @Getter
    private String monetaryUnit;
    @Getter
    private DecimalFormat moneyFormat;
    @Getter
    private TinyRabbit rabbit;

    private Provider provider;

    @SneakyThrows
    @Override
    public void onLoad() {
        this.moneyFormat = new DecimalFormat();
        this.moneyFormat.setMaximumFractionDigits(2);
        this.rabbit = new TinyRabbit("localhost", "Economy/Server");
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final Config config = this.getConfig();

        Language.init(this);

        this.getLogger().info("§aStarting Economy...");

        this.defaultMoney = config.getDouble("DefaultMoney");
        this.monetaryUnit = config.getString("MonetaryUnit");

        this.provider = new Provider(this);
        API = new API(this, provider);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.registerCommands(config);

        this.getLogger().info("§aDone.");
    }

    public void registerCommands(Config config) {
        CommandMap cmd = getServer().getCommandMap();

        cmd.register("money", new MoneyCommand(this, config.getSection("Commands.Money")));
        cmd.register("setmoney", new SetMoneyCommand(this, config.getSection("Commands.Setmoney")));
        cmd.register("addmoney", new AddMoneyCommand(this, config.getSection("Commands.Addmoney")));
        cmd.register("reducemoney", new ReduceMoneyCommand(this, config.getSection("Commands.Reducemoney")));
        cmd.register("pay", new PayCommand(this, config.getSection("Commands.Pay")));
        cmd.register("topmoney", new TopMoneyCommand(this, config.getSection("Commands.Topmoney")));
        cmd.register("lecoreload", new LecoReloadCommand(this, config.getSection("Commands.Lecoreload")));
    }

    @Override
    public void onDisable() {
        this.provider.close();
    }

    public void reload() {
        Language.init(this);
    }
}
