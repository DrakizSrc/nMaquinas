package me.dkz.plugins.nmaquinas.managers;

import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.models.Fuel;
import me.dkz.plugins.nmaquinas.models.Machine;
import me.dkz.plugins.nmaquinas.models.WMachine;
import me.dkz.plugins.nmaquinas.utils.MachineUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MachineManager {
    public Main plugin;
    public ArrayList<Machine> _loadedMachines = new ArrayList<>();
    public ArrayList<WMachine> _inWMachines = new ArrayList<>();
    public ArrayList<Fuel> _loadedFuel = new ArrayList<>();
    private int erros = 0;
    public MachineManager(Main plugin){
        this.plugin = plugin;
    }

    public void saveMachine(WMachine machine){
        try {
            String data = machine.getOwer()
                    +";"+ machine.getId()
                    +";"+ machine.getLocation().getWorld().getName()
                    +";"+ (int) machine.getLocation().getX()
                    +";"+ (int) machine.getLocation().getY()
                    +";"+ (int) machine.getLocation().getZ();

            PreparedStatement stm = plugin.sqLite.getConnection().prepareStatement("INSERT INTO `maquinas` values (?, ?, ?, ?, ?, ?)");
            stm.setString(1, data);
            stm.setString(2, String.valueOf(machine.getFuel()));
            stm.setString(  3, String.valueOf(machine.getItens()));
            stm.setString(4, String.valueOf(machine.useHologram));
            stm.setInt(5, machine.getAmount());
            stm.setInt(6, machine.get_level());
            stm.execute();
            _inWMachines.add(machine);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }




    public void removeMachine(WMachine machine){
        try {
            String data = machine.getOwer()
                    +";"+ machine.getId()
                    +";"+ machine.getLocation().getWorld().getName()
                    +";"+ (int)machine.getLocation().getX()
                    +";"+ (int)machine.getLocation().getY()
                    +";"+ (int)machine.getLocation().getZ();
            PreparedStatement stm = plugin.sqLite.getConnection().prepareStatement("DELETE FROM `maquinas` WHERE `data` = ?");
            stm.setString(1, data);
            stm.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void setupWorldMachines(){
        try {

            // Obtendo informações das maquinas salvas no SQLite
            PreparedStatement dataSQL = plugin.sqLite.getConnection().prepareStatement("SELECT `data` FROM `maquinas`");
            ResultSet rs = dataSQL.executeQuery();
            while (rs.next()){
                String dataString = rs.getString("data");
                Location machineLocation = new Location(Bukkit.getWorld(dataString.split(";")[2]),
                        Integer.valueOf(dataString.split(";")[3]), Integer.valueOf(dataString.split(";")[4]), Integer.valueOf(dataString.split(";")[5]));
                if(machineLocation.getBlock().getType().equals(Material.AIR)) return;
                Block machineBlock = machineLocation.getBlock();
                machineBlock.setMetadata("Dono", new FixedMetadataValue(plugin, dataString.split(";")[0]));
                machineBlock.setMetadata("Tipo", new FixedMetadataValue(plugin, dataString.split(";")[1]));

                // Salvando as maquinas em uma array para acesso futuro
                for(Machine machine : _loadedMachines) {
                    if(machine.getId().equals(dataString.split(";")[1])) {
                        WMachine wMachine = new WMachine(machine.getName());
                        wMachine.setLocation(machineLocation);
                        wMachine.setIcon(machine.icon);
                        wMachine.setBlock(machine.getBlock());
                        wMachine.setOwer(dataString.split(";")[0]);
                        wMachine.setFuelType(machine.getFuelType());
                        wMachine.setDrops(machine.getDrop());
                        wMachine.setDropsValue(machine.getDropValue());
                        wMachine.setDelay(machine.getDelay());
                        wMachine.setPrice(machine.getPrice());
                        wMachine.set_consumption(machine.get_consumption());
                        wMachine.set_drops(machine.get_drops());
                        wMachine.set_velocity(machine.get_velocity());
                        wMachine.setId(machine.getId());
                        wMachine.setTime(machine.getTime());
                        wMachine.set_maxLevel(machine.get_maxLevel());


                        PreparedStatement fuelSTM = plugin.sqLite.getConnection().prepareStatement("SELECT `fuel` FROM `maquinas` where `data` = ?");
                        fuelSTM.setString(1, dataString);
                        ResultSet rs2 = fuelSTM.executeQuery();
                        wMachine.setFuel(Integer.parseInt(rs2.getString("fuel")));

                        PreparedStatement itemsSTM = plugin.sqLite.getConnection().prepareStatement("SELECT `items` FROM `maquinas` where `data` = ?");
                        itemsSTM.setString(1, dataString);
                        ResultSet rs3 = itemsSTM.executeQuery();
                        wMachine.setItens(Integer.parseInt(rs3.getString("items")));

                        PreparedStatement holoSTM = plugin.sqLite.getConnection().prepareStatement("SELECT `holo` FROM `maquinas` where `data` = ?");
                        holoSTM.setString(1, dataString);
                        ResultSet rs4 = holoSTM.executeQuery();
                        wMachine.setUseHologram(Boolean.parseBoolean(rs4.getString("holo")));

                        PreparedStatement amountSTM = plugin.sqLite.getConnection().prepareStatement("SELECT `amount` FROM `maquinas` where `data` = ?");
                        amountSTM.setString(1, dataString);
                        ResultSet rs5 = amountSTM.executeQuery();
                        wMachine.setAmount(rs5.getInt("amount"));
                        machineBlock.setMetadata("Quantidade", new FixedMetadataValue(plugin, wMachine.getAmount()));

                        PreparedStatement levelSTM = plugin.sqLite.getConnection().prepareStatement("SELECT `level` FROM `maquinas` where `data` = ?");
                        levelSTM.setString(1, dataString);
                        ResultSet rs6 = levelSTM.executeQuery();
                        wMachine.set_level(rs6.getInt("level"));
                        machineBlock.setMetadata("Nivel", new FixedMetadataValue(plugin, wMachine.get_level()));
                        wMachine.set_updateValue((int) (wMachine.get_level() * machine.get_updatePrice()));
                        this._inWMachines.add(wMachine);
                        wMachine.createHologram();
                        if(machine.getBlock().getType() != Material.SKULL_ITEM) wMachine.getLocation().getBlock().setType(wMachine.getBlock().getType());
                        wMachine.setSellValue(wMachine.getItens() * wMachine.getDropValue());
                    }
                }
            }
            plugin.getLogger().log(Level.INFO, "{0} Maquina(s) foram carregadas nos mundos", _inWMachines.size());

        } catch (Exception throwables) {
            throwables.printStackTrace();
            erros++;
        }

    }
    public void setupMachines(){
        _buildMachine();
        plugin.getLogger().log(Level.INFO, "{0} Maquina(s) foram registradas com sucesso.", _loadedMachines.size());
        plugin.getLogger().log(Level.INFO, "{0} Erro(s) foram registrados.", erros);
    }





    public void  _buildMachine(){
        try {
            plugin.getConfig().getConfigurationSection("Maquinas").getKeys(false).forEach(maq -> {
                        Machine machine = new Machine(plugin.getConfig().getString("Maquinas." + maq + ".Nome"));
                        machine.setDescription((ArrayList<String>) plugin.getConfig().getStringList("Maquinas." + maq + ".Descrição"));
                        machine.setTime(plugin.getConfig().getInt("Maquinas." + maq + ".Tempo"));
                        machine.setId(String.valueOf(maq));
                        machine.setDelay(plugin.getConfig().getInt("Maquinas." + maq + ".Delay"));
                        machine.setFuelType(plugin.getConfig().getString("Maquinas." + maq + ".Combustivel"));
                        machine.setDropsValue(plugin.getConfig().getInt("Maquinas." + maq + ".Valor"));
                        machine.setIcon(new ItemStack(Material.getMaterial(plugin.getConfig().getString("Maquinas." + maq + ".Icone"))));
                if(Main.getMain.getConfig().getString("Maquinas."+machine.getId()+".Bloco").equalsIgnoreCase("HEAD")){
//                    machine.setBlock(MachineUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTU5ZmI5NTdiNzM2MzBiMTczYzIyNTJkNTZkODI5NzVmZGYwZThkZjVjMGMzZjNhNTE0MjU4NWExYjM3MDA5MyJ9fX0=", "Drakiz"));
                      machine.setBlock(MachineUtils.createCustomSkull(1, machine.getName(), plugin.getConfig().getString("Maquinas."+maq+".Url")));
                }else{
                        machine.setBlock(new ItemStack(Material.getMaterial(plugin.getConfig().getString("Maquinas." + maq + ".Bloco"))));
                    }
                machine.setDrops(new ItemStack(Material.getMaterial(plugin.getConfig().getString("Maquinas." + maq + ".Drop"))));
                machine.set_velocity(plugin.getConfig().getDouble("Maquinas."+maq+".Niveis.Atributos.Velocidade"));
                machine.set_drops(plugin.getConfig().getDouble("Maquinas."+maq+".Niveis.Atributos.Drops"));
                machine.set_consumption(plugin.getConfig().getDouble("Maquinas."+maq+".Niveis.Atributos.Consumo"));
                machine.set_updatePrice(plugin.getConfig().getDouble("Maquinas."+maq+".Niveis.Custo"));
                machine.set_maxLevel(plugin.getConfig().getInt("Maquinas."+maq+".Niveis.Máximo"));
                machine.setMaxStack(plugin.getConfig().getInt("Maquinas."+maq+".Stack-Máximo"));
                machine.setPrice(plugin.getConfig().getDouble("Maquinas."+maq+".Preço"));
                _loadedMachines.add(machine);














            });
        }catch (Exception ex){
            erros++;
            ex.printStackTrace();
        }
    }

    public void setupFuel(){
        plugin.getConfig().getConfigurationSection("Combustiveis").getKeys(false).forEach(id ->{
            Fuel fuel = new Fuel(plugin.getConfig().getString("Combustiveis."+id+".Nome").replaceAll("&", "§"));
            fuel.setIcon(new ItemStack(Material.getMaterial(plugin.getConfig().getString("Combustiveis."+id+".Icone"))));
            fuel.setDescription((ArrayList<String>) plugin.getConfig().getStringList("Combustiveis."+id+".Descrição").stream().map(str -> str.replaceAll("&", "§")).collect(Collectors.toList()));
            fuel.setType(plugin.getConfig().getString("Combustiveis."+id+".Tipo"));
            fuel.setAmount(plugin.getConfig().getInt("Combustiveis."+id+".Quantidade"));
            fuel.setValue(plugin.getConfig().getInt("Combustiveis."+id+".Preço"));
            fuel.setId(id);
            if(!_loadedFuel.contains(fuel)) {
                _loadedFuel.add(fuel);
            }
        });
    }

    public void update(WMachine machine) {

        String data = machine.getOwer()
                +";"+ machine.getId()
                +";"+ machine.getLocation().getWorld().getName()
                +";"+ (int)machine.getLocation().getX()
                +";"+ (int)machine.getLocation().getY()
                +";"+ (int)machine.getLocation().getZ();

        try {
            PreparedStatement _fuel = plugin.sqLite.getConnection().prepareStatement("UPDATE `maquinas` SET `fuel` = ? WHERE `data` = ?");
            _fuel.setString(1, String.valueOf(machine.getFuel()));
            _fuel.setString(2, data);
            _fuel.executeUpdate();
            _fuel.close();

            PreparedStatement _items = plugin.sqLite.getConnection().prepareStatement("UPDATE `maquinas` SET `items` = ? WHERE `data` = ?");
            _items.setString(1, String.valueOf(machine.getItens()));
            _items.setString(2, data);
            _items.executeUpdate();
            _items.close();

            PreparedStatement _holo = plugin.sqLite.getConnection().prepareStatement("UPDATE `maquinas` SET `holo` = ? WHERE `data` = ?");
            _holo.setString(1, String.valueOf(machine.getUseHologram()));
            _holo.setString(2, data);
            _holo.executeUpdate();
            _holo.close();

            PreparedStatement _amount = plugin.sqLite.getConnection().prepareStatement("UPDATE `maquinas` SET `amount` = ? WHERE `data` = ?");
            _amount.setInt(1, machine.getAmount());
            _amount.setString(2, data);
            _amount.executeUpdate();
            _amount.close();

            PreparedStatement _level = plugin.sqLite.getConnection().prepareStatement("UPDATE `maquinas` SET `level` = ? WHERE `data` = ?");
            _level.setInt(1, machine.get_level());
            _level.setString(2, data);
            _level.executeUpdate();
            _level.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
