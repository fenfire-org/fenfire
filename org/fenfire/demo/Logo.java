// (c): Matti J. Katila

package org.fenfire.demo;
import org.fenfire.swamp.*;
import org.fenfire.spanimages.*;
import org.nongnu.libvob.impl.*;
import org.nongnu.libvob.lob.*;
import java.awt.Color;

public class Logo extends NewLobMain {
    static private void p(String s) { System.out.println("Logo:: "+s); }

    
    protected Object node;
    public Logo(Color bg, String file) {
	super(bg);
	node = Nodes.get("file://"+file);
    }

    public Lob createLob() {
	ImagePool.init(null, windowAnim);
	ImagePool.flush();
	
	Lob lob;
	lob = ImagePool.fullImage(node, 0);

        lob = Lobs.key(lob, node);
	//lob = Lobs.align(lob, .5f, .5f);
	return lob;

    }

    public static void main(String[] argv) {
	Logo l = new Logo(new Color(1, 1, .8f), argv[0]);
	l.start();
    }


}
