/*   
HTTPUpdater.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
 *
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
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.fenfeed.http;
import org.nongnu.storm.util.CopyUtil;
import java.io.IOException;
import java.util.*;

/** A Runnable keeping a set of resources updated by reloading them 
 *  in regular intervals. Usual application: keep a set of feeds updated.
 *  <p>
 *  Create an instance, add the initial list of feeds, then call 
 *  <code>start()</code>.
 *  <p>
 *  XXX don't check all feeds at once if they have the same update interval
 */
public final class HTTPUpdater implements Runnable {
    public interface UpdateListener {
	void startUpdate(HTTPResource resource);
	void changed(HTTPResource resource);
	void unchanged(HTTPResource resource);
	void updateFailed(HTTPResource resource, IOException error);
    }

    private class Feed implements Runnable {
	private HTTPResource resource;
	private int loadInterval;  // in millis
	private long lastRead;     // in millis

	private boolean updateInProgress = false;

	private Feed(String uri, int loadInterval)
	    throws IOException {

	    this.resource = new HTTPResource(uri, context);
	    this.loadInterval = loadInterval;
	    this.lastRead = resource.lastRead().getTime();
	}

	/** Reload if necessary */
	private void check() {
	    if(updateInProgress) return;

	    long now = System.currentTimeMillis();
	    if(lastRead+loadInterval <= now)
		new Thread(this).start();
	}

	public void run() {
	    if(updateInProgress)
		return;

	    updateInProgress = true;

	    startUpdate(resource);

	    boolean changed;
	    try {
		changed = resource.reload(false);
	    } catch(IOException e) {
		updateFailed(resource, e);
		return;
	    }

	    updateInProgress = false;

	    if(changed)
		changed(resource);
	    else
		unchanged(resource);
	}
    }

    private HTTPContext context;
    private Map feeds = Collections.synchronizedMap(new LinkedHashMap());
    private Set listeners = Collections.synchronizedSet(new LinkedHashSet());

    public HTTPUpdater(HTTPContext context) {
	this.context = context;
    }

    public void start() {
	new Thread(this).start();
    }

    public HTTPResource add(String uri, int loadIntervalMinutes) 
	throws IOException {

	int loadInterval = loadIntervalMinutes * 60 * 1000;
	Feed feed = new Feed(uri, loadInterval);
	feeds.put(uri, feed);
	return feed.resource;
    }

    public void remove(String uri) {
	feeds.remove(uri);
    }

    public void addUpdateListener(UpdateListener listener) {
	listeners.add(listener);
    }
    public void removeUpdateListener(UpdateListener listener) {
	listeners.remove(listener);
    }

    public void run() {
	while(true) {
	    synchronized(feeds) {
		for(Iterator i=feeds.values().iterator(); i.hasNext();) {
		    Feed f = (Feed)i.next();
		    f.check();
		}
	    }

	    try {
		synchronized(this) {
		    wait(60 * 1000); // check every minute -- it's cheap
		}
	    } catch(InterruptedException e) {
	    }
	}
    }

    private void startUpdate(HTTPResource resource) {
	synchronized(listeners) {
	    for(Iterator i=listeners.iterator(); i.hasNext();)
		((UpdateListener)i.next()).startUpdate(resource);
	}
    }
    private void changed(HTTPResource resource) {
	synchronized(listeners) {
	    for(Iterator i=listeners.iterator(); i.hasNext();)
		((UpdateListener)i.next()).changed(resource);
	}
    }
    private void unchanged(HTTPResource resource) {
	synchronized(listeners) {
	    for(Iterator i=listeners.iterator(); i.hasNext();)
		((UpdateListener)i.next()).unchanged(resource);
	}
    }
    private void updateFailed(HTTPResource resource, IOException error) {
	synchronized(listeners) {
	    for(Iterator i=listeners.iterator(); i.hasNext();)
		((UpdateListener)i.next()).updateFailed(resource, error);
	}
    }

    /** Parameters: "uri1 loadInterval1 uri2 loadInterval2 ..."
     */
    public static void main(String[] args) throws Exception {
	HTTPResource.dbg = true;
	
	HTTPContext context = new HTTPContext();
	HTTPUpdater upd = new HTTPUpdater(context);

	upd.addUpdateListener(new UpdateListener() {
		public void startUpdate(HTTPResource r) {
		    System.out.println("start update: "+r.getURI());
		}
		public void changed(HTTPResource r) {
		    System.out.println("changed: "+r.getURI());
		}
		public void unchanged(HTTPResource r) {
		    System.out.println("unchanged: "+r.getURI());
		}
		public void updateFailed(HTTPResource r, IOException e) {
		    e.printStackTrace();
		    System.out.println("update failed: "+r.getURI());
		    System.out.println("(see exception above)");
		}
	    });

	for(int i=0; i<args.length; i+=2) {
	    upd.add(args[i], Integer.parseInt(args[i+1])); 
	}

	upd.start();
    }
}
