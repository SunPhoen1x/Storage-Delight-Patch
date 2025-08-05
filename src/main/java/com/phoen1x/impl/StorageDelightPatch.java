package com.phoen1x.impl;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageDelightPatch implements ModInitializer {
	public static final String MOD_ID = "storagedelight-patch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets("storagedelight");
		ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of("storagedelight", "block"));
	}
}