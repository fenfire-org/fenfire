/*
PoolManager.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 */
/*
 * Written by Tuomas J. Lukka
 */

package org.fenfire.spanimages.gl;
import org.fenfire.spanimages.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.gl.*;
import org.nongnu.libvob.gl.virtualtexture.*;
import org.nongnu.libvob.memory.*;
import org.nongnu.libvob.util.ThreadBackground;
import java.util.*;

public class PoolManager implements GL.StatsCallback {
    public static boolean dbg = false;
    private static void p(String s) { 
	System.out.println("PoolManager:: "+s); }

    /** Missingpixels below this will not be considered.
     */
    public double PIXELLIMIT = 40;

    private WindowAnimation.BackgroundProcess backgroundProcessUpdate = null;
    public void setBackgroundProcessUpdate(WindowAnimation.BackgroundProcess update) {
	backgroundProcessUpdate = update;
    }

    private static PoolManager instance;
    /** PoolManager is a singleton class; get (maybe create) the instance.
     */
    public static PoolManager getInstance() {
	if(instance == null)
	    instance = new PoolManager();
	return instance;
    }

    // public int POOLSIZE = 64 * 1024 * 1024;

    public float DICELENGTH = 1;
    public float DICELENGTH2 = 1;
    public int DICEDEPTH = 10;

    public int MIN_ACTIVES = 20; // minimum number to retain actively

    Set activeSet = Collections.synchronizedSet(new HashSet());

    class MySIV extends AbstractVob implements SpanImageVob {
	float w, h;
	float sx0, sx1;
	float sy0, sy1;
	GLRen.FixedPaperQuad quad;
	SingleImage img; // We don't want to GC it before the SIV!
	public float getWidth() { return w; };
	public float getHeight() { return h; };

	public void render(java.awt.Graphics g, boolean fast, RenderInfo info1, RenderInfo info2) {
	}

	public int putGL(VobScene vs, int cs1) {
	    return quad.putGL(vs, cs1);
	}

	public float getRealX(float spanx) {
	    return w * (spanx-sx0) / (sx1-sx0);
	}
	public float getRealY(float spany) {
	    return h * (spany-sy0) / (sy1-sy0);
	}
	public int getSpanX(float vobx) {
	    return (int)(vobx * (sx1-sx0) / w + sx0);
	}
	public int getSpanY(float voby) {
	    return (int)(voby * (sy1-sy0) / h + sy0);
	}
    }

    IndirectMipzipManager indirectMipzipManager;

    int[][] allocationSchemes = new int[][] {
	{
	    // Suitable for GF4Go, 64MB
	    4, 4, 12, 18, 30, 60,
	    100, 200, 0, 0, 0, 0
	}, {
	    //  Suitable for GF FX 5600, 128MB
	    8, 8, 15, 20, 40, 80,
	    200, 0, 0, 0, 0, 0
	}
    };

    int[] allocations;
    VirtualTexture[][] slotContents;

    private PoolManager() {

	int scheme = Integer.parseInt(System.getProperty("fenfire.poolalloc", "0"));

	allocations = allocationSchemes[scheme];


	indirectMipzipManager = new NonDeletingIndirectMipzipManager();
	indirectMipzipManager.init("COMPRESSED_RGB_S3TC_DXT1_EXT", 
			2048, 2048);
	indirectMipzipManager.setDefaultTexParameters(new String[] {
	    "TEXTURE_MIN_FILTER", "LINEAR_MIPMAP_LINEAR",
	    "TEXTURE_MAG_FILTER", "LINEAR",
	    "TEXTURE_MAX_ANISOTROPY_EXT", "10"
	});
	indirectMipzipManager.setAllocations(allocations);
	slotContents = new VirtualTexture[allocations.length][];
	for(int i=0; i<allocations.length; i++)
	    slotContents[i] = new VirtualTexture[allocations[i]];

	bgThread.setDaemon(true);
	bgThread.setPriority(Thread.MIN_PRIORITY);
	bgThread.start();
    }

    /** Make a SpanImageVob whose SingleImage's texture allocation will 
     * be tracked by this object.
     * @param i The image
     * @param p The paper object which uses the texture in i
     * @param texgen The texgen matrix used in the paper object - needed 
     * 			to calculate the scale factor the the mipmap selection
     * @param w,h The width and height to use for the FixedPaperQuad. Always starts
     * 		  at origin.
     */
    public SpanImageVob makeVob(
	    SingleImage i, 
	    Paper p, 
	    float[] texgen,
	    float w, float h,
	    float sx0, float sy0,
	    float sx1, float sy1) {

	// Calculate the area ratio of texgen
	float mult = Math.abs(texgen[0] * texgen[5] - texgen[1] * texgen[4]);
	// However, this is not enough, as most of the textures
	// are fairly anisotropic. 
	// We'll assume the texture is as wide as it is tall and calculate
	// a correcting factor.
	// The larger this factor is, the more LOD will be loaded.
	float anisoCorrection = 4;

	MySIV siv = new MySIV();
	siv.w = w;
	siv.h = h;
	siv.img = i;

	siv.sx0 = sx0; siv.sx1 = sx1; siv.sy0 = sy0; siv.sy1 = sy1;

	activeSet.add(i);

	siv.quad = GLRen.createFixedPaperQuad(
		    p, 0, 0, w, h, 0,
		    DICELENGTH, DICELENGTH2, DICEDEPTH,
		    i.accum, mult / anisoCorrection);
	if(dbg) p("Made quad: "+i.accum+" "+mult+" "+anisoCorrection+" || "+w+" "+h);
	return siv;
    }

    public void call(Object obj) {
	if(dbg) p("stats call "+obj);
	if(obj == null) return; // might get GC'd
	SingleImage singleImage = (SingleImage) obj;
	singleImage.readTexAccum();
	activeSet.add(singleImage);
    }


    Comparator sortPriority = new Comparator() {
	public int compare(Object o1, Object o2) {
	    SingleImage s1 = (SingleImage)o1;
	    SingleImage s2 = (SingleImage)o2;
	    int div = 1;
	    for(int i=0; i<s1.missingPixels.length; i++) {
		double m1 = s1.missingPixels[i];
		double m2 = s2.missingPixels[i];
		if(m1 * div < PIXELLIMIT && m2  * div< PIXELLIMIT) {
		    div *= 4;
		    continue;
		}
		if(m1 > m2) return -1;
		return 1;
	    }
	    return 0;
	}
    };

    Thread bgThread = new Thread() {
	public void run() {
	    while(true) {
		try {
		    // Slow down when debugging
		    Thread.sleep(dbg ? 10000 : 200);
		    updateAllocs();
		} catch(Exception e) {
		    e.printStackTrace();
		    System.err.println("Alloc exception "+e);
		}
	    }
	}
    };


    /** (For tests): A set of singleimages that are to be locked at
     * maximum resolution.
     */
    public List locked = null;

    synchronized public void lock(SingleImage image) throws java.io.IOException {
	if(locked == null) 
	    locked = new ArrayList();
	
	locked.add(image);
	if(locked.size() > slotContents[0].length)
	    throw new Error("Locking too many");
	for(int level=0; level<slotContents.length; level++) {
	    for(int i=0; i<slotContents[level].length; i++) 
		slotContents[level][i] = null;
	}
	for(int i=0; i<locked.size(); i++) 
	    slotContents[0][i] = ((SingleImage)locked.get(i)).virtualTexture;

	indirectMipzipManager.setSlotContents_synchronously(slotContents);
    }

    synchronized public void unlock() {
	locked = null;
    }


    SingleImage[] templ = new SingleImage[0];

    Set bumped = new HashSet();
    synchronized public void updateAllocs() {

	if(locked != null) return;

	SingleImage[] actives = (SingleImage[])activeSet.toArray(templ);
	if(dbg) p("UpdateAllocs start "+actives.length+" "+this);

	long time = System.currentTimeMillis();
	for(int i=0; i<actives.length; i++) {
	    actives[i].updateTime(time);
	}
	Arrays.sort(actives, sortPriority);

	bumped.clear(); 
	boolean yetUpdated = false;
	int level=0; int ind = 0;
	for(int i=0; level < slotContents.length &&
		     i<actives.length; i++) {
	    if(dbg) p("Level 1 "+i+" "+level+" "+ind+" "+actives[i].virtualTexture);
	    int minLevel = 0;
	    int div = 1;
	    for(; minLevel < actives[i].missingPixels.length; minLevel ++) {
		if(actives[i].missingPixels[minLevel] * div >= PIXELLIMIT) break;
		div *= 4;
	    }
	    if(minLevel >= actives[i].missingPixels.length) {
		activeSet.remove(actives[i]);
		continue;
	    }
	    // If it's going to a lower level, go there. 
	    // Leave all intermediate things there.
	    if(minLevel > level) {
		level = minLevel;
		ind = 0;
		while(level < slotContents.length &&
		      ind >= slotContents[level].length) {
		    ind = 0;
		    level++;
		}
		if(level >= slotContents.length) break;
	    }
	    int prevLevel = indirectMipzipManager.getSlotLevel(
			actives[i].virtualTexture);
	    if(dbg) p("Previous level: "+prevLevel+", currentLevel: "+bumped.contains(actives[i].virtualTexture));
	    // If it's on a higher level, let it be
	    if(prevLevel >= 0 && ! bumped.contains(actives[i].virtualTexture)) {
		if(prevLevel < level)
		    continue;
		// Make sure it doesn't occur twice
		// XXX This may be too slow
		for(int k = 0; k < slotContents[prevLevel].length; k++) {
		    if(slotContents[prevLevel][k] == actives[i].virtualTexture)
			slotContents[prevLevel][k] = null;
		}
	    }

	    // If we have something changed on screen we 
	    // should not be quiet about it!
	    if (prevLevel < 0 || prevLevel > level) {
		if (dbg) p("need update!");
		if (!yetUpdated && backgroundProcessUpdate != null) {
		    if (dbg) p("let's update!");
		    backgroundProcessUpdate.chg();
		}
		yetUpdated = true;
	    }

	    bumped.add(slotContents[level][ind]);
	    slotContents[level][ind] = actives[i].virtualTexture;

	    ind++;
	    while(level < slotContents.length &&
		  ind >= slotContents[level].length) {
		// if(dbg) p("LevelIn1 "+level);
		ind = 0;
		level++;
	    }
	}

	while(level < slotContents.length) {
	    slotContents[level][ind] = null;
	    ind++;
	    while(level < slotContents.length &&
		  ind >= slotContents[level].length) {
		ind = 0;
		level++;
	    }
	}
	if(dbg) {
	    p("UpdateAllocs finish "+this);
	    Set had = new HashSet();
	    for(int lev = 0; lev < slotContents.length; lev++) {
		p("Level "+lev);
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i<slotContents[lev].length; i++) {
		    if(slotContents[lev][i] == null) continue;
		    if(had.contains(slotContents[lev][i]))
			throw new Error("SlotContents already! "+slotContents[lev][i]);
		    had.add(slotContents[lev][i]);
		    buf.append(" "+slotContents[lev][i]);
		}
		p(""+buf);
	    }
	}
	indirectMipzipManager.setSlotContents(slotContents);
	if(dbg) p("Did setSlotContents");

	/*
	int left = POOLSIZE;
	for(int i=0; i<actives.length; i++) {
	    if(locked.contains(actives[i])) {
		actives[i].loader.setGoalBaseLevel(0,
				ThreadBackground.getDefaultInstance(),
				1);
		continue;
	    }
	    int l = 0;
	    // if(dbg) p("Loop: "+i+" "+actives.length);
	    for(; l < actives[i].missingPixels.length; l++) {
		// if(dbg) p("l "+l+"  miss: "+actives[i].missingPixels[l]);
		if(actives[i].missingPixels[l] > .15 * actives[i].nPixels()) {
		    l--; 
		    break;
		}
	    }

	    while(
		    actives[i].loader.getMemory(l) > left &&
		    l < actives[i].loader.getNLevels()-1)
		l++;

	    left -= actives[i].loader.getMemory(l);
	    if(dbg) p("Setgoal: "+actives[i]+" "+l);
	    actives[i].loader.setGoalBaseLevel(l,
			    ThreadBackground.getDefaultInstance(),
			    (float)(10 + .1 * i));
	    if(i >= MIN_ACTIVES && l == actives[i].loader.getNLevels()-1)
		activeSet.remove(actives[i]);
	}
	if(dbg) p("Memory left: "+left);
	*/
    }

}


