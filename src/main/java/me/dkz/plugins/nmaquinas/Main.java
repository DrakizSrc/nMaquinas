package me.dkz.plugins.nmaquinas;


import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import me.dkz.plugins.nmaquinas.commands.FCommand;
import me.dkz.plugins.nmaquinas.commands.MCommand;
import me.dkz.plugins.nmaquinas.dao.SQLite;
import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import me.dkz.plugins.nmaquinas.listener.MachineListener;
import me.dkz.plugins.nmaquinas.menu.MachineDrops;
import me.dkz.plugins.nmaquinas.menu.MenuManager;
import me.dkz.plugins.nmaquinas.listener.ChatShopEvent;
import me.dkz.plugins.nmaquinas.languages.LanguageManager;
import me.dkz.plugins.nmaquinas.managers.MachineManager;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public String prefix = "§b[nMaquinas] ";
    public MachineManager manager;
    public static Main getMain;
    public SQLite sqLite;
    private StopWatch stopWatch = new StopWatch();
    public Economy economy;
    public LanguageManager languageManager;
    public boolean _useHologram = true;
    public boolean _sellMachine = false;
    public MenuManager menuManager;


    @Override
    public void onLoad() {
        stopWatch.start();
    }




    @Override
    public void onEnable() {
        getMain = this;

        // Setup hologram system
        if(!getServer().getPluginManager().isPluginEnabled("HolographicDisplays")){
            _useHologram = false;
            getLogger().info("HolographicDisplays não encontrado, maquinas não usarão hologramas.");
        }


        // Setup configuration files
        saveDefaultConfig();
        saveResource("languages/pt-BR.yml", false);
        saveResource("menus/Maquina.yml", false);

        // Setup machines system
        _sellMachine = getConfig().getBoolean("Informações.Maquinas.Vender");
        if (VaultAPI.setupEconomy()) {
            System.out.println(getConfig().getString("Configurações.Linguagem"));
            if(new File(getDataFolder()+"/languages/"+getConfig().getString("Configurações.Linguagem")+".yml").exists()) {

                languageManager = new LanguageManager(getConfig().getString("Configurações.Linguagem"));
                sqLite = new SQLite(this);
                manager = new MachineManager(this);
                manager.setupMachines();
                manager.setupFuel();
                menuManager = new MenuManager(this);
                getCommand("maquinas").setExecutor(new MCommand());
                getCommand("combustivel").setExecutor(new FCommand());
                getCommand("drops").setExecutor(this::onCommand);
                getServer().getPluginManager().registerEvents(new MachineListener(), this);
                getServer().getPluginManager().registerEvents(new ChatShopEvent(), this);
                manager.setupWorldMachines();
                economy = VaultAPI.economy;
                InventoryManager.enable(this);

                getLogger().log(Level.INFO, "Plugin ativado com sucesso em {0}ms", stopWatch.getTime());
            }else{
                getLogger().severe("O arquivo de linguagem escolhido não existe.");
                getServer().getPluginManager().disablePlugin(this);
                stopWatch.stop();
                return;

            }
        } else {
            getLogger().severe("Nenhum plugin de economia encontrado, desligando..");
            getServer().getPluginManager().disablePlugin(this);
            stopWatch.stop();
            return;
        }

        prefix = languageManager.getStringMessage("PLUGIN-PREFIX");
        stopWatch.stop();

    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(getConfig().getBoolean("Informações.Maquinas.Armazem")) {
                if (sender.hasPermission("nmaquinas.drops")) {
                    MachineDrops machineDrops = new MachineDrops().init();
                    Player player = (Player) sender;
                    machineDrops.openInventory(player);
                } else {
                    sender.sendMessage(languageManager.getStringMessage("SEM-PERMISSÃO"));
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        if(sqLite != null)
        sqLite.close();
    }




}
