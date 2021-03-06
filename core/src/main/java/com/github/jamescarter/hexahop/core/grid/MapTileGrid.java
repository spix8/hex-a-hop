package com.github.jamescarter.hexahop.core.grid;

import com.github.jamescarter.hexahop.core.level.Location;
import com.github.jamescarter.hexahop.core.tile.MapStatusTile;

public class MapTileGrid extends TileGrid<Integer> {
	public void unlockConnected(Location levelLocation, boolean par) {
		MapStatusTile statusTile = (MapStatusTile)statusTileAt(levelLocation);

		if (par) {
			statusTile.perfect();
		} else {
			statusTile.complete();
		}

		for (Location location : baseConnectedTo(levelLocation)) {
			if (statusTileAt(location) == null) {
				// Ignore the "join" tiles
				if (baseAt(location) > 0) {
					setStatusTileAt(location, new MapStatusTile(location));
				}
			}
		}
	}
}
