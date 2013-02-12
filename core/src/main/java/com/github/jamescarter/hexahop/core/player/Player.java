package com.github.jamescarter.hexahop.core.player;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;

import com.github.jamescarter.hexahop.core.level.Location;

import playn.core.Image;
import playn.core.gl.ImageLayerGL;

public class Player extends ImageLayerGL {
	private static final Image playerImage = assets().getImage("images/player.png");
	private Direction direction = Direction.SOUTH;
	private Position position = Position.STANDING;
	private Location location;

	public Player(Location location) {
		super(graphics().ctx(), playerImage);

		this.location = location;

		setWidth(65);
		setHeight(80);

		setImage();

		setPosition();
	}

	private void setImage() {
		setImage(playerImage.subImage(direction.x(), position.y(), 65, 80));
	}

	private void setPosition() {
		setTranslation(getGridColPosition(), getGridRowPosition());
	}

	private int getGridColPosition() {
		return location.col() * 46;
	}

	private int getGridRowPosition() {
		if (location.col() % 2 == 0) {
			return (location.row() * 36) - 18;
		} else {
			return (location.row() * 36);
		}
	}

	public Direction getDirection(float selectedX, float selectedY) {
		// modify player co-ordinates to center of player image
		float playerX = tx() + 30;
		float playerY = ty() + 42;

		boolean isNorthOrSouthOnly = (Math.abs(selectedX - playerX) < 25) ? true : false;
		boolean isNorth = (selectedY < playerY) ? true : false;
		boolean isEast = (selectedX > playerX) ? true : false;

		if (isNorthOrSouthOnly) {
			if (isNorth) {
				return Direction.NORTH;
			} else {
				return Direction.SOUTH;
			}
		} else {
			if (isNorth) {
				if (isEast) {
					return Direction.NORTH_EAST;
				} else {
					return Direction.NORTH_WEST;
				}
			} else {
				if (isEast) {
					return Direction.SOUTH_EAST;
				} else {
					return Direction.SOUTH_WEST;
				}
			}
		}
	}

	public void move(Direction direction) {
		this.direction = direction;

		location.move(direction);

		jumpTo(location, false);
	}

	public void jumpTo(Location newLocation, boolean isUndo) {
		if (isUndo) {
			//TODO: work out the direction the player should be facing
		}

		this.location = newLocation;

		setImage();
		setPosition();
	}

	public Location location() {
		return location;
	}
}
