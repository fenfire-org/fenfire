/*
ViewModelsLoader.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
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
 * Written by Matti J. Katila
 */

package org.fenfire.modules.init;
import org.fenfire.view.management.*;
import org.fenfire.util.*;
import org.fenfire.vocab.*;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.cloudberry.*;
import org.nongnu.storm.*;
import org.nongnu.storm.references.*;

import org.python.util.*;
import org.python.core.*;

import java.util.*;

/** Load probability model of different view models into memory.
 */
public class ViewModelsLoader implements FServer.RequestHandler {

    

    public void handleRequest(Object req, Applitude app) {
    }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("view models graph"))
	    if (o instanceof StormGraph[]) o[0] = models;
	if (req.equals("view models set"))
	    if (o instanceof StormGraph[]) o[0] = views;
    }

    private Set views;
    private StormGraph models;
    private IndexedPool pool;
    private FServer f;
    public ViewModelsLoader(FServer f) throws Exception {
	
	this.f = f;
	// get private pool  ...  get settings ptr ..

	if (f.environment.createRequest("view models", this))
	    throw new Error("Settings loader already inited!");


	// private storm pool is just "storm"
	IndexedPool[] po = new IndexedPool[1];
	f.environment.request("storm", (Object[]) po, null);
	if (po[0] == null) throw new Error("pool undefined!");
	pool = po[0];

	PointerSigner[] p = new PointerSigner[1];
	f.environment.request(StormLoader.SIGNER, (Object[]) p, null);
	if (p[0] == null) throw new Error("block signer undefined!");
	PointerSigner signer = p[0];
	PointerId ptr = signer.newPointer();


	QuadsGraph[] tmp = new QuadsGraph[1];
	f.environment.request("global graph", tmp, null);
	
	models = new StormGraph(tmp[0], pool, ptr);
	createModels();

	views = new HashSet();
	views.add(FenMM);
	views.add(PaperCanvas);
    }


    // move to approriate place, i.e., vocab module.

    static public final Object PaperCanvas = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#PaperCanvas");
    static public final Object FenMM = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#FenMM");
    static public final Object FenPDF = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#FenPDF");
    static public final Object FenTwine = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#FenTwine");

    static public final Object modelType = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#modelType");
    static public final Object probability = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#probability");


    static public final Object MayBe = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#MayBe");
    static public final Object MayNotBe = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#MayNotBe");
    static public final Object ShallBe = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#ShallBe");
    static public final Object ShallNotBe = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#ShallNotBe");
    static public final Object MustBe = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#MustBe");
    static public final Object MustNotBe = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#MustNotBe");

    static public final Object AnyResource = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#AnyResource");
    static public final Object resource = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/ViewModels#resource");
    

    static public final Object Fenmm = Nodes.get(
	"http://fenfire.org/rdf-v/2004/09/09/fenmm#MindMap");


    private void createModels() {
	
	/* Paper or canvas view
	 */
	// node -- RDF.type --> CANVAS2D.Canvas
	models.add(PaperCanvas, modelType, RDF.object);
	models.add(PaperCanvas, probability, ShallBe);
	models.add(PaperCanvas, RDF.predicate, RDF.type);
	models.add(PaperCanvas, resource, CANVAS2D.Canvas);

	// node -- CANVAS2D.contains --> node
	models.add(PaperCanvas, modelType, RDF.subject);
	models.add(PaperCanvas, probability, MayBe);
	models.add(PaperCanvas, RDF.predicate, CANVAS2D.contains);
	models.add(PaperCanvas, resource, AnyResource);


	/* FenMM view
	 */
	// node -- RDF.type --> CANVAS2D.Canvas
	models.add(FenMM, modelType, RDF.object);
	models.add(FenMM, probability, ShallBe);
	models.add(FenMM, RDF.predicate, RDF.type);
	models.add(FenMM, resource, Fenmm);

    }




}
