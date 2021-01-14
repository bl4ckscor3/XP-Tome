package bl4ckscor3.mod.xptome;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid=XPTome.MODID, value=Side.CLIENT)
public class ClientReg
{
	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(XPTome.XP_BOOK, 0, new ModelResourceLocation(new ResourceLocation(XPTome.MODID, ItemOldXPTome.NAME), "inventory"));
		ModelLoader.setCustomModelResourceLocation(XPTome.XP_TOME, 0, new ModelResourceLocation(new ResourceLocation(XPTome.MODID, ItemXPTome.NAME), "inventory"));
	}
}
