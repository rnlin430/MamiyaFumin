package com.github.rnlin;

import java.util.UUID;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static com.github.rnlin.MamiyaFumin.FUMIN_BESTSCORE_KEY;

// このクラスのインスタンスは一定時間毎に プレイヤーのTIME_SINCE_REST統計値を scorelistにkey(uuid)とセットでメモリ上に格納します。
// このクラスのインスタンスは一定時間毎に、プレイヤーのスコアとベストスコアを比較しベストスコアを更新しメモリ上に格納します。
public class ScoreUpdate extends BukkitRunnable {

	MamiyaFumin plugin;

	public ScoreUpdate(MamiyaFumin p) {
		plugin = p;
	}

	@Override
	public void run() {
		plugin.playerList = plugin.getServer().getOnlinePlayers();
		// プレイヤーリストにいる（現在参加中のプレイヤー）一人一人の現在の統計値をscorelistに格納
		for (Player player : plugin.playerList) {
			int temp = player.getStatistic(Statistic.TIME_SINCE_REST) / MamiyaFumin.magnification;
			UUID uuid = player.getUniqueId();
			// Integer scoredata = MamiyaFumin.scoreList.get(uuid);
			Integer new_scoredata = new Integer(temp);
			MamiyaFumin.scoreList.put(uuid, new_scoredata);
		}
		// プレイヤーリストにいる一人一人の現在の統計値を監視しベストスコアが更新されているかscoreBestlistに格納
		// （負荷軽減のため全てのプレイヤ―はPlugin読み込み時に格納）
		for (Player player : plugin.playerList) {
			UUID uuid = player.getUniqueId();
			int bestscore = Math.max(
					plugin.cumulativePlayerscoreConfig.getInt(uuid.toString() + FUMIN_BESTSCORE_KEY),
					MamiyaFumin.scoreList.get(uuid));
			MamiyaFumin.scoreBestlist.put(uuid, bestscore);
		}

		// トータルスコアはディスク上の値が古い可能性があるのでメモリ上に展開しない
		// （ディスクからの読み込みは起動時のみ、書き込みはプラグインアンロード時のみ）
	}
}
