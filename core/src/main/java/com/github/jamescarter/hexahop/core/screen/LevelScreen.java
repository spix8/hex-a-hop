package com.github.jamescarter.hexahop.core.screen;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;


import java.util.ArrayList;
import java.util.List;

import com.github.jamescarter.hexahop.core.HexAHop;
import com.github.jamescarter.hexahop.core.callback.MapLoadCallback;
import com.github.jamescarter.hexahop.core.grid.GridLoader;
import com.github.jamescarter.hexahop.core.grid.LevelTileGrid;
import com.github.jamescarter.hexahop.core.grid.TileGrid;
import com.github.jamescarter.hexahop.core.input.LevelKeyboard;
import com.github.jamescarter.hexahop.core.input.LevelMouse;
import com.github.jamescarter.hexahop.core.input.LevelTouch;
import com.github.jamescarter.hexahop.core.json.StateJson;
import com.github.jamescarter.hexahop.core.level.Location;
import com.github.jamescarter.hexahop.core.player.Direction;
import com.github.jamescarter.hexahop.core.player.Player;
import com.github.jamescarter.hexahop.core.tile.Tile;

import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;

public class LevelScreen extends GridLoader {
	private final ImageLayer bgLayer = graphics().createImageLayer(assets().getImage("images/gradient.png"));
	private Location levelLocation;
	private List<Location> moveList = new ArrayList<Location>();
	private LevelTileGrid levelTileGrid = new LevelTileGrid(this);
	private int par;
	private Player player;
	private boolean drownedInPlace;

	public LevelScreen(Location location, String levelJsonString) {
		this.levelLocation = location;

		StateJson<Tile> levelJson = new StateJson<Tile>(
			Tile.class,
			levelTileGrid,
			anim,
			PlayN.json().parse(levelJsonString),
			"level-" + location.col() + "x" + location.row(),
			true
		);

		par = levelJson.par();
		player = new Player(this, levelJson.start());
		moveList.add(levelJson.start());

		stepOnTile(player.location(), Direction.SOUTH);
	}

	public int par() {
		return par;
	}

	@Override
	public void wasShown() {
		getGridLayer().add(player);

		getGridLayer().setTranslation(
			(((640 * HexAHop.scaleY) - ((getTileGrid().cols() * 46) * HexAHop.scaleY)) / 2) - 18,
			((480 - (getTileGrid().rows() * 36)) / 2) - 36
		);

		bgLayer.addListener(new LevelMouse(this));
		bgLayer.addListener(new LevelTouch(this));
		PlayN.keyboard().setListener(new LevelKeyboard(this));
	}

	public void move(Direction direction) {
		if (player.visible() && levelTileGrid.canMove(player.location(), direction)) {
			finishAnimation();

			moveList.add(player.location().clone());

			levelTileGrid.statusTileAt(player.location()).stepOff();

			player.move(direction);
		}
	}

	/**
	 * Undo the last move.

	 * @return true if there are more moves to undo, otherwise false.
	 */
	public boolean undo() {
		if (moveList.size() <= 1) {
			return false;
		}

		Location location = moveList.remove(moveList.size() - 1);

		if (drownedInPlace || player.visible()) {
			Tile statusTile = levelTileGrid.statusTileAt(player.location());

			if (statusTile != null) {
				statusTile.undo();
			}

			drownedInPlace = false;
		}

		if (!player.visible()) {
			player.setVisible(true);
		}

		player.jumpTo(location, true);

		levelTileGrid.statusTileAt(player.location()).undo();

		return true;
	}

	public void stepOnTile(Location location, Direction direction) {
		Tile statusTile = levelTileGrid.statusTileAt(location);

		if (statusTile == null || !statusTile.isActive()) {
			player.setVisible(false);
		} else {
			Location endLocation = statusTile.stepOn(direction);

			if (endLocation == null) {
				if (!levelTileGrid.statusTileAt(location).isActive()) {
					player.setVisible(false);
					drownedInPlace = true;
				}
			} else {
				player.jumpTo(endLocation, false);
			}
		}
	}

	public void finishAnimation() {
		for (int i=1; i<=4; i++) {
			anim.update(500 * i);
		}
	}

	public Location player() {
		return player.location();
	}

	public void complete() {
		assets().getText("levels/map.json", new MapLoadCallback(levelLocation, moveList.size() - 1 <= par));
	}

	public void backToMenu() {
		assets().getText("levels/map.json", new MapLoadCallback());
	}

	@Override
	public TileGrid<Tile> getTileGrid() {
		return levelTileGrid;
	}

	@Override
	public Layer getBackgroundLayer() {
		return bgLayer;
	}
}