package net.eltown.economy;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.SneakyThrows;
import net.eltown.economy.commands.crypto.WalletCommand;
import net.eltown.economy.commands.economy.*;
import net.eltown.economy.commands.shops.SetShopPriceCommand;
import net.eltown.economy.components.crypto.api.CryptoAPI;
import net.eltown.economy.components.economy.api.API;
import net.eltown.economy.components.economy.language.Language;
import net.eltown.economy.components.economy.provider.Provider;
import net.eltown.economy.components.forms.FormListener;
import net.eltown.economy.components.shops.api.ShopAPI;
import net.eltown.economy.components.tinyrabbit.TinyRabbit;
import net.eltown.economy.listener.PlayerListener;

import java.text.DecimalFormat;

@Getter
public class Economy extends PluginBase {

    @Getter
    private static net.eltown.economy.components.economy.api.API API;
    @Getter
    private static CryptoAPI cryptoAPI;
    @Getter
    private static ShopAPI shopAPI;

    private double defaultMoney;
    private String monetaryUnit;
    private DecimalFormat moneyFormat;
    private DecimalFormat cryptoFormat;
    private TinyRabbit rabbit;

    private Provider provider;

    @SneakyThrows
    @Override
    public void onLoad() {
        this.saveDefaultConfig();

        Language.init(this);

        this.moneyFormat = new DecimalFormat();
        this.moneyFormat.setMaximumFractionDigits(2);
        this.cryptoFormat = new DecimalFormat();
        this.cryptoFormat.setMaximumFractionDigits(4); // 0.0001
        this.rabbit = new TinyRabbit("localhost", "Economy/Server");

        this.provider = new Provider(this);
        API = new API(this, provider);

        cryptoAPI = new CryptoAPI(this);
        shopAPI = new ShopAPI(this);
    }

    @Override
    public void onEnable() {
        final Config config = this.getConfig();

        this.getLogger().info("§aStarting Economy...");

        this.defaultMoney = config.getDouble("DefaultMoney");
        this.monetaryUnit = config.getString("MonetaryUnit");

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.registerCommands(config);

        this.getLogger().info("§aDone.");
    }

    public void registerCommands(Config config) {
        CommandMap cmd = getServer().getCommandMap();

        cmd.register("economy", new MoneyCommand(this, config.getSection("Commands.Money")));
        cmd.register("economy", new SetMoneyCommand(this, config.getSection("Commands.Setmoney")));
        cmd.register("economy", new AddMoneyCommand(this, config.getSection("Commands.Addmoney")));
        cmd.register("economy", new ReduceMoneyCommand(this, config.getSection("Commands.Reducemoney")));
        cmd.register("economy", new PayCommand(this, config.getSection("Commands.Pay")));
        cmd.register("economy", new TopMoneyCommand(this, config.getSection("Commands.Topmoney")));
        cmd.register("economy", new LecoReloadCommand(this, config.getSection("Commands.Lecoreload")));
        cmd.register("economy", new WalletCommand(this));
        cmd.register("economy", new SetShopPriceCommand(this));
    }

    @Override
    public void onDisable() {
        this.provider.close();
    }

    public void reload() {
        Language.init(this);
    }
}
