package com.song.factory;

import com.song.selector.Selector;

public abstract class Factory {
	public abstract Selector createSelector(String name);
}
