package com.phoen1x.storagedelight.impl;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

public class StorageDelightPatch implements ModInitializer {
	public static final String MOD_ID = "storagedelight";

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of(MOD_ID, "block"));
	}
}