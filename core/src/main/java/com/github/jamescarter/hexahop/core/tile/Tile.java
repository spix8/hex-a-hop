package com.github.jamescarter.hexahop.core.tile;

import static playn.core.PlayN.graphics;

import com.github.jamescarter.hexahop.core.grid.TileGrid;
import com.github.jamescarter.hexahop.core.level.Location;
import com.github.jamescarter.hexahop.core.player.Direction;

import playn.core.gl.ImageLayerGL;

public abstract class Tile extends ImageLayerGL {
	private int id;
	private Location location;
	private boolean active = true;

	public Tile(Location location, TileImage ti) {
		super(graphics().ctx(), ti.getImage());

		this.id = ti.id();
		this.location = location;
	}

	/**
	 * Get the current TileImage id that's being displayed.
	 * 
	 * @return
	 */
	public int id() {
		return id;
	}

	/**
	 * Performed when a tile has been stepped on by the player.
	 * 
	 * @param direction The direction the player is heading.
	 * @return The location the player should be moved to as an affect of stepping on this tile.
	 */
	public Location stepOn(Direction direction) {
		return null;
	}

	/**
	 * Performed when the player steps off the tile.
	 * 
	 * This will not be triggered if the stepOn() event returns a new location for the user to move to.
	 * 
	 */
	public void stepOff() {
		
	}

	/**
	 * Undo the last state change.
	 * 
	 */
	public void undo() {
		
	}

	public boolean isWall() {
		return false;
	}

	/**
	 * Return if the tile is activate or nto.
	 * 
	 * @return true if the tile is active and in play, false if it is not.
	 */
	public final boolean isActive() {
		return active;
	}

	/**
	 * Get the current location of this tile.
	 * 
	 * @return the tile location
	 */
	public Location getLocation() {
		return location;
	}

	public void setTileImage(TileImage ti) {
		setImage(ti.getImage());

		id = ti.id();
	}

	/**
	 * Deactivate this tile. It will not be visible on the grid.
	 * 
	 */
	public void deactiate() {
		active = false;
		setVisible(false);
	}

	/**
	 * Activate this tile. It will be visible on the grid.
	 * 
	 */
	public void activate() {
		active = true;
		setVisible(true);
	}

	public static Tile newTile(TileGrid<?> tileGrid, Location location, int id) {
		switch(id) {
			case 0:
				return null;
			case 1:
				return new StoneTile(location, false);
			case 2:
				return new CollapsableTile(tileGrid, location, false);
			case 3:
				return new CollapsableTile(tileGrid, location, true);
			case 4:
				return new TrampolineTile(tileGrid, location);
			case 6:
				return new StoneTile(location, true);
			case 7:
				return new Collapsable2Tile(tileGrid, location, false);
			case 8:
				return new Collapsable2Tile(tileGrid, location, true);
			case 80:
			case 81:
			case 82:
			case 83:
				return new MapStatusTile(location);
		}

		return null;
	}
}