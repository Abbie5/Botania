/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [18/12/2015, 02:06:56 (GMT)]
 */
package vazkii.botania.client.render.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.lib.LibObfuscation;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class SkyblockSkyRenderer extends IRenderHandler {

	private static final ResourceLocation textureSkybox = new ResourceLocation(LibResources.MISC_SKYBOX);
	private static final ResourceLocation textureMoonPhases = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation textureSun = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation[] planetTextures = new ResourceLocation[] {
    	new ResourceLocation(LibResources.MISC_PLANET + "0.png"),
    	new ResourceLocation(LibResources.MISC_PLANET + "1.png"),
    	new ResourceLocation(LibResources.MISC_PLANET + "2.png"),
    	new ResourceLocation(LibResources.MISC_PLANET + "3.png"),
    	new ResourceLocation(LibResources.MISC_PLANET + "4.png"),
    	new ResourceLocation(LibResources.MISC_PLANET + "5.png")
    };
    
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		boolean test = false;
		if(test)
			return;
		
		int glSkyList = ReflectionHelper.getPrivateValue(RenderGlobal.class, mc.renderGlobal, LibObfuscation.GL_SKY_LIST);
		int glSkyList2 = ReflectionHelper.getPrivateValue(RenderGlobal.class, mc.renderGlobal, LibObfuscation.GL_SKY_LIST2); // Horizon like. We don't have it here
		int starGLCallList = ReflectionHelper.getPrivateValue(RenderGlobal.class, mc.renderGlobal, LibObfuscation.STAR_GL_CALL_LIST);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Vec3 vec3 = world.getSkyColor(mc.renderViewEntity, partialTicks);
        float f1 = (float)vec3.xCoord;
        float f2 = (float)vec3.yCoord;
        float f3 = (float)vec3.zCoord;
        float f6;
        
        int startFade = 25;
    	float insideVoid = Math.max(0F, ((float) (startFade - mc.thePlayer.posY) / startFade));
    	f1 = Math.max(0F, f1 - insideVoid);
    	f2 = Math.max(0F, f2 - insideVoid);
    	f3 = Math.max(0F, f3 - insideVoid);

        Tessellator tessellator1 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glColor3f(f1, f2, f3);
        GL11.glCallList(glSkyList);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
        float f7;
        float f8;
        float f9;
        float f10;

        // === Sunset
        if(afloat != null) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            f6 = afloat[0];
            f7 = afloat[1];
            f8 = afloat[2];
            float f11;

            tessellator1.startDrawing(6);
            tessellator1.setColorRGBA_F(f6, f7, f8, afloat[3] * (1F - insideVoid));
            tessellator1.addVertex(0.0D, 100.0D, 0.0D);
            byte b0 = 16;
            tessellator1.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);

            for(int j = 0; j <= b0; ++j) {
                f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
                float f12 = MathHelper.sin(f11);
                float f13 = MathHelper.cos(f11);
                tessellator1.addVertex((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3]));
            }

            tessellator1.draw();
            GL11.glPopMatrix();
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPushMatrix();
        f6 = Math.max(0.2F, 1.0F - world.getRainStrength(partialTicks)) * (1F - insideVoid);
        f7 = 0.0F;
        f8 = 0.0F;
        f9 = 0.0F;
        
        GL11.glTranslatef(f7, f8, f9);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        
        float celAng = world.getCelestialAngle(partialTicks);
        float effCelAng = celAng;
        if(celAng > 0.5)
        	effCelAng = 0.5F - (celAng - 0.5F);
        
        // === Planets
        f10 = 20F;
        float a = Math.max(0F, effCelAng - 0.3F) * f6;

        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, a * 4);
        GL11.glRotatef(90F, 0.5F, 0.5F, 0.0F);
        for(int p = 0; p < planetTextures.length; p++) {
            mc.renderEngine.bindTexture(planetTextures[p]);
            drawObject(tessellator1, f10);
            
            switch(p) {
            case 0:
            	GL11.glRotatef(70F, 1F, 0F, 0F);
            	f10 = 12F;
            	break;
            case 1: 
            	GL11.glRotatef(120F, 0F, 0F, 1F);
            	f10 = 15F;
            	break;
            case 2:
            	GL11.glRotatef(80F, 1F, 0F, 1F);
            	f10 = 25F;
            	break;
            case 3:
            	GL11.glRotatef(100F, 0F, 0F, 1F);
            	f10 = 10F;
            	break;
            case 4:
            	GL11.glRotatef(-60F, 1F, 0F, 0.5F);
            	f10 = 40F;
            }
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        
        // === Rays
        mc.renderEngine.bindTexture(textureSkybox); 
        
        if(a > 0) {
            f10 = 20F;
            GL11.glPushMatrix();
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            GL11.glTranslatef(0F, -1F, 0F);
            GL11.glColor4f(1F, 1F, 1F, a);
            GL11.glRotatef(220F, 1F, 0F, 0F);
            int angles = 90;
    		float s = 3F;
    		float m = 1F;
    		float y = 2F;
    		float y0 = 0F;
    		float uPer = 1F / 360F;
    		float anglePer = 360F / angles;
    		double fuzzPer = (Math.PI * 10) / angles;
    		float rotSpeed = 1F;
    		
    		for(int p = 0; p < 3; p++) {
    			float baseAngle = rotSpeed * (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks);
    			GL11.glRotatef((ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.25F * rotSpeed, 0F, 1F, 0F);
    			
    	        tessellator1.startDrawingQuads();
    			for(int i = 0; i < angles; i++) {
    				int j = i;
    				if(i % 2 == 0)
    					j--;
    				
    				float ang = j * anglePer + baseAngle;
    				double xp = Math.cos(ang * Math.PI / 180F) * f10;
    				double zp = Math.sin(ang * Math.PI / 180F) * f10;
    				double yo = Math.sin(fuzzPer * j) * 1;
    				
    				float ut = ang * uPer;
    				if(i % 2 == 0) {
    					tessellator1.addVertexWithUV(xp, yo + y0 + y, zp, ut, 1F);
        				tessellator1.addVertexWithUV(xp, yo + y0, zp, ut, 0);   
    				} else {
        				tessellator1.addVertexWithUV(xp, yo + y0, zp, ut, 0);
        				tessellator1.addVertexWithUV(xp, yo + y0 + y, zp, ut, 1F);
    				}

    			}
    			tessellator1.draw();

    			switch(p) {
    				case 0:
    					GL11.glRotatef(20F, 1F, 0F, 0F);
    					GL11.glColor4f(1F, 0.4F, 0.4F, a);
    					fuzzPer = (Math.PI * 14) / angles;
    					rotSpeed = 0.2F;
    					break;
    				case 1:
    					GL11.glRotatef(50F, 1F, 0F, 0F);
    					GL11.glColor4f(0.4F, 1F, 0.7F, a);
    					fuzzPer = (Math.PI * 6) / angles;
    					rotSpeed = 2F;
    					break;
    			}
    		}
    		
    		GL11.glPopMatrix();
        }

        GL11.glColor4f(1F, 1F, 1F, 1F - insideVoid);
        
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);
        // === Sun	
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        f10 = 60.0F;
        mc.renderEngine.bindTexture(textureSun); 
        drawObject(tessellator1, f10);
        
        // === Moon
        f10 = 60.0F;
        mc.renderEngine.bindTexture(textureMoonPhases);
        int k = world.getMoonPhase();
        int l = k % 4;
        int i1 = k / 4 % 2;
        float f14 = (float)(l + 0) / 4.0F;
        float f15 = (float)(i1 + 0) / 2.0F;
        float f16 = (float)(l + 1) / 4.0F;
        float f17 = (float)(i1 + 1) / 2.0F;
        tessellator1.startDrawingQuads();
        tessellator1.addVertexWithUV((double)(-f10), -100.0D, (double)f10, (double)f16, (double)f17);
        tessellator1.addVertexWithUV((double)f10, -100.0D, (double)f10, (double)f14, (double)f17);
        tessellator1.addVertexWithUV((double)f10, -100.0D, (double)(-f10), (double)f14, (double)f15);
        tessellator1.addVertexWithUV((double)(-f10), -100.0D, (double)(-f10), (double)f16, (double)f15);
        tessellator1.draw();
        
        // === Stars
        float t = (ClientTickHandler.ticksInGame + partialTicks + 2000) * 0.005F;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        GL11.glPushMatrix();
        GL11.glRotatef(t * 3, 0F, 1F, 0F);
		GL11.glColor4f(1F, 1F, 1F, f6);
		GL11.glCallList(starGLCallList);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
        GL11.glRotatef(t, 0F, 1F, 0F);
        GL11.glColor4f(0.5F, 1F, 1F, f6);
		GL11.glCallList(starGLCallList);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
        GL11.glRotatef(t * 2, 0F, 1F, 0F);
        GL11.glColor4f(1F, 0.75F, 0.75F, f6);
        GL11.glCallList(starGLCallList);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glRotatef(t * 3, 0F, 0F, 1F);
		GL11.glColor4f(1F, 1F, 1F, 0.25F * f6);
		GL11.glCallList(starGLCallList);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
        GL11.glRotatef(t, 0F, 0F, 1F);
        GL11.glColor4f(0.5F, 1F, 1F, 0.25F * f6);
		GL11.glCallList(starGLCallList);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
        GL11.glRotatef(t * 2, 0F, 0F, 1F);
        GL11.glColor4f(1F, 0.75F, 0.75F, 0.25F * f6);
        GL11.glCallList(starGLCallList);
        GL11.glPopMatrix();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
	}
	
	private void drawObject(Tessellator tess, float f10) {
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)(-f10), 100.0D, (double)(-f10), 0.0D, 0.0D);
        tess.addVertexWithUV((double)f10, 100.0D, (double)(-f10), 1.0D, 0.0D);
        tess.addVertexWithUV((double)f10, 100.0D, (double)f10, 1.0D, 1.0D);
        tess.addVertexWithUV((double)(-f10), 100.0D, (double)f10, 0.0D, 1.0D);
        tess.draw();
	}

}
