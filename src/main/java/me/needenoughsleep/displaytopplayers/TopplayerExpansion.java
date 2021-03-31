package me.needenoughsleep.displaytopplayers;

import javafx.util.Pair;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TopplayerExpansion extends PlaceholderExpansion {
    private final DisplayTopPlayers plugin;
    private final HashMap<String, List<Pair<String, Integer>>> data = new HashMap<>();

    public TopplayerExpansion(DisplayTopPlayers plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "topplayer";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){

        // %topplayer_obj:1_<name|score>%

        String obj, action;
        int place;

        try {
            obj = identifier.split(":")[0];
            action = identifier.split(":")[1].split("_")[1];
            try {
                place = Integer.parseInt(identifier.split(":")[1].split("_")[0]);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        if (!data.containsKey(obj)){
            Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(obj);
            if (objective == null) {
                return null;
            }
            Set<String> entries = Bukkit.getScoreboardManager().getMainScoreboard().getEntries();
            List<Pair<String, Integer>> list = new ArrayList<>();
            for (String entry : entries) {
                Integer score = objective.getScore(entry).getScore();
                list.add(new Pair<>(entry, score));
            }
            list.sort(Comparator.comparingInt((Pair<String, Integer> e) -> -e.getValue()));
            data.put(obj, list);
        }

        if (place <= data.get(obj).size()) {
            if (action.equalsIgnoreCase("name"))
                return data.get(obj).get(place - 1).getKey();
            if (action.equalsIgnoreCase("score"))
                return Integer.toString(data.get(obj).get(place - 1).getValue());
            return null;
        }
        else {
            return "";
        }
    }

    public void update() {
        Set<String> entries = Bukkit.getScoreboardManager().getMainScoreboard().getEntries();
        Iterator<Map.Entry<String, List<Pair<String, Integer>>>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Pair<String, Integer>>> entry = iterator.next();
            Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(entry.getKey());
            if (objective != null) {
                entry.getValue().clear();
                for (String entry_ : entries) {
                    entry.getValue().add(new Pair<>(entry_, objective.getScore(entry_).getScore()));
                }
                entry.getValue().sort(Comparator.comparingInt((Pair<String, Integer> e) -> -e.getValue()));
            }
            else {
                iterator.remove();
            }
        }
    }

    public void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::update,
                0L, plugin.getConfig().getLong("period"));
    }
}
