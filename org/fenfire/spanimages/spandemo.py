# 
# Copyright (c) 2004, Matti J. Katila
# This file is part of Fenfire.
# 
# Fenfire is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# Fenfire is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General
# Public License along with Fenfire; if not, write to the Free
# Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
# MA  02111-1307  USA
# 



from org import fenfire as ff
from org.nongnu import alph, storm
import org.nongnu.libvob as lvob
import vob

vs = w.createVobScene()
vs.put(background((.8, .4, .9)))
w.renderStill(vs, 0)


pool = storm.impl.TransientPool(java.util.HashSet())
myalph = alph.impl.StormAlph(pool)

sc_img = myalph.addFile(java.io.File("testdata/test.png"), 'image/png')
sc_img = myalph.addFile(java.io.File("ff_bg.png"), 'image/png')

chgAfterKeyEvent = 0

class Scene:
    def __init__(self):
        self.state = 1
        self.spim = ff.spanimages.SpanImageFactory.getDefaultInstance()
        self.anim = None
        self.initialized = 0
    def scene(self, vs):
        print 'scene called'
        if not self.initialized:
            if isinstance(lvob.GraphicsAPI.getInstance(), lvob.impl.gl.GLAPI):
                poolMan = ff.spanimages.gl.PoolManager.getInstance()
                poolMan.setBackgroundProcessUpdate(vs.anim.getInstance())
            else:
                ff.spanimages.fuzzybear.FuzzySpanImageFactory.setComponent(w.getFrame())
            self.anim = vs.anim
            self.initialized = 1

	vs.put( background((0.1,0.3,0.4)))
        Vob = self.spim.f(sc_img.getCurrent())
        print Vob
        if self.state:
            cs = vs.orthoCS(0, "foo",0,
                            vs.size.width/3, vs.size.height/3,
                            0.8, 0.7)
            cs2 = vs.orthoCS(0, "bar",0,
                            vs.size.width/7, vs.size.height/7,
                            0.9, 0.9)

        else:
            cs = vs.orthoCS(0, "foo",0, 0,0,
                            vs.size.width/Vob.getWidth(),
                            vs.size.height/Vob.getHeight())
            cs2 = vs.orthoCS(0, "bar",0,
                            vs.size.width/7 * 5, vs.size.height/7 *5,
                            0.9, 0.9)
            print  vs.size.height, Vob.getHeight()
        vs.put(Vob, cs)
        vs.put(Vob, cs2)

    def key(self, k):
        if self.anim == None: return 

        print k

        if k.startswith('Ctrl-'):
            self.state = not self.state
            self.anim.animate()
            print 'argh'
            return
        else:
            self.anim.rerender()
            print 'rerender'
            return 
        
        if k == 'Control_L':
            self.state = not self.state
            self.anim.animate()
            print 'anim'
        elif k == 'Control_R':
            self.anim.switchVS()
            print 'switch'
        else:
            self.state = not self.state
            self.anim.rerender()
            print 'state changed'
