# 
# Copyright (c) 2003, Tuomas J. Lukka
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


import vob, java
from org.fenfire.spanimages import gl as spi
from org.fenfire.spanimages.gl import papermakers
from org import fenfire as ff
import org

pool = org.nongnu.storm.impl.TransientPool(java.util.HashSet())
myalph = org.nongnu.alph.impl.StormAlph(pool)
sc = myalph.addFile(java.io.File('../alph/testdata/test1.pdf'), 'application/pdf')

def benchScene(vs, 
	paper = 0,
	nquads = 100
	):
    span = sc.getPage(0)
    poolManager = spi.PoolManager.getInstance()
    scrollimager = spi.PageScrollBlockImager()
    img = scrollimager.getSingleImage(span, poolManager)
    try:
	poolManager.lock(img)
	
	spanImageFactory = spi.DefaultSpanImageFactory(scrollimager)

	w = vob.putil.demowindow.w
	if paper == 0:
	    spanImageFactory.paperMaker = papermakers.white()
	elif paper == 1:
	    spanImageFactory.paperMaker = papermakers.fancyBlend()
	elif paper == 2:
	    spanImageFactory.paperMaker = papermakers.fancyHalo()
	elif paper == 3:
	    spanImageFactory.paperMaker = papermakers.fancyBlur()
	elif paper == 4:
	    spanImageFactory.paperMaker = papermakers.nvFancyBlur()
	else: assert 0==1, paper

	layout = ff.view.PageSpanLayout(
			sc.getPage(0),
			spanImageFactory)

	vs.map.put(vob.vobs.SolidBackdropVob(java.awt.Color.yellow))

	for i in range(0, nquads):
	    cs = vs.orthoCS(0,"A", 50-i, 1, 1, 1, 1)
	    layout.place(vs, cs)
    finally:
	poolManager.unlock()

    return (paper, nquads)

args = {
    "nquads" : (1, 2, 4, 8, 16),
    # "paper" : (0, 1, 2, 3, 4)
    "paper" : (3, 4)
}
