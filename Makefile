all: java

PRIVATE_POOL=himalia.it.jyu.fi:~fenfire-pools/fenfire-priv
PRIVATE_DIR=../fenfire-priv


TEST=org/

LDLIB=LD_LIBRARY_PATH=/usr/lib:../libvob/src/jni:${JAVAHOME}/jre/lib/i386:${JAVAHOME}/jre/lib/i386/client

FFCLASSPATH=../depends/cryptix-jce-provider.jar:./build:../navidoc/CLASSES:../storm/CLASSES:../alph/CLASSES:../libvob/CLASSES:../depends/jython.jar:../depends/jena.jar:../depends/icu4j.jar:../depends/je.jar:../depends/png.jar:../depends/gnowsis/gnoclient.jar:../depends/gnowsis/xmlrpc-1.2-b1.jar:../depends/rio.jar:../depends/xom-1.0b7.jar:../depends/xerces.jar:../depends/javolution.jar
      # ../depends/libvob.jar

PYTHONPATH=-Dpython.path=../depends/jythonlib.jar:../depends/jython.jar:../depends/pythonlib.jar:.:../libvob:../alph
      #-Dpython.cachedir=$(PWD)/.

JAVA ?= java -Xincgc -Xnoclassgc
#JAVA = ../libvob/src/jwrapper/build/jwrapper -Xincgc -Xnoclassgc

JAVAC ?= javac

java:
	#python ../libvob/metacode/rj2java.py org/fenfire/view/BuoyConnectorLob.rj org/fenfire/view/BuoyConnectorLob.java
	mkdir -p build
	$(JAVAC) -d build -classpath $(FFCLASSPATH):$(CLASSPATH) `find org -name '*.java'`

applet_java:
	$(JAVAC) -d build -classpath $(FFCLASSPATH):$(CLASSPATH) FenApplet.java

clean:
	rm -Rf build

JAVACMD=$(JAVA) -cp $(FFCLASSPATH):$(CLASSPATH) $(PYTHONPATH) 

RUNTEST=$(LDLIB) $(JAVACMD) org.python.util.jython org/fenfire/test/test.py 

test:
	$(RUNTEST) -Dvob.api=$(API) $(DBG) $(TEST)

testbugs:
	$(RUNTEST) -Dvob.api=$(API) -f \* $(DBG) $(TEST)

DEMO?=org/fenfire/demo/buoyoing.py

runjython:
	$(LDLIB) $(JAVACMD) org.python.util.jython $(DBG)

rundemo:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py $(DBG) $(DEMO)

API?=gl
POOLDIR?=../fenfire-priv/
SETTINGS?=../fenfire-priv/
BIN?=fenpdf10.py
FILE?=myFenfire/mygraph.rdf
WINDOWSIZE?=1600x1200
run:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py -Dfenpdf.file=$(FILE) $(DBG) org/fenfire/bin/$(BIN)

run_edit:
	$(LDLIB) $(JAVACMD) $(DBG) org.fenfire.fenedit.Editor

run_notebook:
	$(LDLIB) $(JAVACMD) $(DBG) org.fenfire.demo.RDFNotebook

run_fiction:
	$(LDLIB) $(JAVACMD) $(DBG) org.fenfire.fenfiction.FenFiction

FEEDS='http://tbray.org/ongoing/ongoing.rss' 'http://dannyayers.com/index.rdf' 'http://captsolo.net/info/xmlsrv/rdf.php?blog=2' 'http://planet.classpath.org/rss10.xml'

run_feed:
	$(LDLIB) $(JAVACMD) $(DBG) org.fenfire.fenfeed.FenFeed $(FEEDS)

run_f:
	$(LDLIB) $(JAVACMD) -Dvob.api=$(API) -Dfenfire.settings.dir=$(SETTINGS) -Dstorm.pooldir=$(POOLDIR) $(DBG) org.fenfire.view.management.FServer

run_literature:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py -Dfenpdf.pool=../fenfire-priv/ -Dfenpdf.file=../spaces/literature.rdf -Dvob.windowsize=$(WINDOWSIZE) $(DBG) org/fenfire/bin/$(BIN)

run_fenpdfdemo:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py -Dfenpdf.file=$(FILE) -Dfenpdf.demo=1 $(DBG) org/fenfire/bin/$(BIN)

run_mmdemo:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py -Dfenmm.file="../spaces/mmdemo.rdf" $(DBG) org/fenfire/demo/mm.py

run_textmm:
	$(JAVACMD) -Dfenmm.file="$(FILE)" org.python.util.jython org/fenfire/fenmm/textmm.py

run_java:
	$(JAVACMD) `echo $(CLASS) | sed 's/\//./g;'` $(ARGS)


test_fenpdfdemo:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py -Dfenpdf.file=$(FILE) -Dfenpdf.demo=1 -DtestFenPDF=1 $(DBG) org/fenfire/bin/$(BIN)

test_fenpdfdemo:
	$(LDLIB) $(JAVACMD) org.python.util.jython ../libvob/rundemo.py -Dfenpdf.file=$(FILE) -Dfenpdf.demo=1 -DtestFenPDF=1 $(DBG) org/fenfire/bin/$(BIN)


VERSION=snapshot-`date -I`
textmm_jar: JARNAME=textmm-$(VERSION).jar
textmm_jar: mainclass=org.fenfire.fenmm.TextMain
textmm_jar: excludelibs=libvob
textmm_jar:
	./jarbuilder.sh "$(CLASSPATH)" "$(PYTHONPATH)" "$(VERSION)" "$(JARNAME)" "$(mainclass)" "$(excludelibs)"

#applet_jar: mainclass="org.fenfire.FenApplet.class"
applet_jar: mainclass="FenApplet.class"
applet_jar: 
	./jarbuilder.sh "$(FFCLASSPATH)" "$(PYTHONPATH)" "$(VERSION)" "applet.jar" "$(mainclass)" "huugabuugabongaloo"


BENCH=org.fenfire.swamp.bench.graph

bench:
	$(LDLIB) $(JAVACMD) -Dvob.api=$(API) org.python.util.jython ../libvob/runbench.py $(BENCH)

copyrighted::
	python ../fenfire/metacode/copyrighter.py Fenfire

.PHONY: docs

##########################################################################
# General documentation targets
all-docs:
	make -C "../callgl/" docxx
	make -C "../glmosaictext/" docxx
	make -C "../libvob/" docxx java-doc
	make -C "../fenfire/" java-doc
	make -C "../loom/" java-doc
	make -C "../navidoc/" java-doc
	make -C "../storm/" java-doc
	make -C "../alph/" java-doc

#	make -C "../navidoc/" html $(DBG) RST="$$(find ../ -name "*.rst"|xargs)"
#	make -C "../navidoc/" imagemap $(DBG) RST="$$(find ../ -name "*.gen.html"|xargs)"
	make -C "../navidoc/" html $(DBG) RST="../"
	make -C "../navidoc/" imagemap $(DBG) HTML="../"

docs:   java-doc navidoc navilink

DOCPKGS= -subpackages org
#DOCPKGS= org.fenfire\
#	 org.fenfire.index\
#	 org.fenfire.index.impl\
#	 org.fenfire.util\
#	 org.fenfire.view\
#	 org.fenfire.view.buoy\
#	 org.fenfire.swamp\
#	 org.fenfire.swamp.impl

JAVADOCOPTS=-use -version -author -windowtitle "Fenfire Java API"
java-doc:
	find . -name '*.class' | xargs rm -f # Don't let javadoc see these
	rm -Rf docs/javadoc
	mkdir -p docs/javadoc
	javadoc $(JAVADOCOPTS) -d docs/javadoc -sourcepath . $(DOCPKGS)
##########################################################################
# Navidoc documentation targets
navidoc: # Compiles reST into HTML
	make -C "../navidoc/" html DBG="$(DBG)" RST="../fenfire/docs/"

navilink: # Bi-directional linking using imagemaps
	make -C "../navidoc/" imagemap HTML="../fenfire/docs/"

naviloop: # Compiles, links, loops
	make -C "../navidoc/" html-loop DBG="--imagemap $(DBG)" RST="../fenfire/$(RST)"

peg: # Creates a new PEG, uses python for quick use
	make -C "../navidoc/" new-peg PEGDIR="../fenfire/docs/pegboard"

pegs:   # Compiles only pegboard
	make -C "../navidoc/" html DBG="$(DBG)" RST="../fenfire/docs/pegboard/"

html: # Compiles reST into HTML, directories are processed recursively
	make -C "../navidoc/" html DBG="$(DBG)" RST="../fenfire/$(RST)"

html-loop: # Loop version for quick recompiling
	make -C "../navidoc/" html-loop DBG="$(DBG)" RST="../fenfire/$(RST)"

latex: # Compiles reST into LaTeX, directories are processed recursively
	make -C "../navidoc/" latex DBG="$(DBG)" RST="../fenfire/$(RST)"

latex-loop: # Loop version for quick recompiling
	make -C "../navidoc/" latex-loop DBG="$(DBG)" RST="../fenfire/$(RST)"

######
# TAGS
TAGS: TAGS_tla

TAGS_cvs:
	if [ -f "../TAGS" ]; then rm ../TAGS; fi
	cd .. && find fenfire libvob alph callgl glmosaictext alph storm navidoc -type d -name CVS -exec sed -e '/^[^/]/d' -e 's:^\(/[^/]*\).*:{}\1:' -e 's:/CVS/:/:' {}/Entries \;|xargs etags --append

TAGS_tla:
	if [ -f "../TAGS" ]; then rm ../TAGS; fi
	cd .. && for dir in fenfire libvob alph callgl glmosaictext alph storm navidoc; do tla inventory --source $$dir; done|xargs etags --append

tags:
	ctags -R metacode docs lava org


##################
# Storm pool rsync

# If you need to tell SSH your username at himalia, add to ~/.ssh/config:
# Host himalia.it.jyu.fi
#     User yourname

update-private:
	mkdir -p $(PRIVATE_DIR)
	rsync -rtvzu $(PRIVATE_POOL)/ $(PRIVATE_DIR)/

commit-private:
	rsync -rtvzu --ignore-existing $(PRIVATE_DIR)/ $(PRIVATE_POOL)/
