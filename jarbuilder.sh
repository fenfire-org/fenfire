#!/bin/bash
set -e
CLASSPATH="$1"
PYTHONPATH="$2"
VERSION="$3"
JARNAME="$4"
MAINCLASS="$5"
EXCLUDELIBS="$6"

[ "$EXCLUDELIBS" != "" ] && EXCLUDELIBS="|$EXCLUDELIBS"

excludepattern="cryptix$EXCLUDELIBS"

jardir=$JARNAME.build

basedir="$PWD"
PYTHONDIRS=""


# for "pythonlib.jar", try "pythonlib.license" and "python.license"
getLicense() {
    dir=$(dirname "$1")
    name=$(basename "$1" .jar)
    plain=${name%lib}
    echo "'$name' '$plain'"
    [ -e "$dir/$name.license" ] && cp "$dir/$name.license" . && return 0
    [ -e "$dir/$plain.license" ] && cp "$dir/$plain.license" . && return 0
    return 0
}


rm -Rf $jardir
mkdir $jardir
echo "Main-Class: $MAINCLASS" > $jardir/manifest

cd $jardir
echo "Gathering files from CLASSPATH"
#for name in $(echo $CLASSPATH|tr : '\n'|egrep -v "$excludepattern"); do
for name in $(echo $CLASSPATH|tr : '\n'); do
    case $name in 
	*.jar) 
	    jar xf ../$name ;
	    # cat META-INF/MANIFEST.MF >>manifest.try
	    getLicense ../$name
	    echo "Jar: " $name
	    ;;
	*) 
	    echo "ARGH! $name"
	    cp -a ../$name/* . 
	    ;;
    esac
done
echo "Gathering files from PYTHONPATH"
for name in $(echo $PYTHONPATH|sed "s/-Dpython.path=//"| \
    tr : '\n'|egrep -v "^\.\$|$excludepattern"); do 
    case $name in
	*.jar)
	    jar xf ../$name
	    # cat META-INF/MANIFEST.MF >>manifest.try
	    getLicense ../$name
	    ;;
	*)
	    PYTHONDIRS="$PYTHONDIRS $name"
	    ;;
    esac
done

echo 'Gathering *.py from PYTHONPATH and .'
for dir in $PYTHONDIRS . ; do
    cd "$basedir/$dir"
    find * -name "*.py"| while read file ; do 
	mkdir -p "$basedir/$jardir/$(dirname $file)" 
	cp "$file" "$basedir/$jardir/$file"
    done
done

cd "$basedir"

echo 'Including *LICENSE* and *README*'
cp *LICENSE* $jardir
# cp $(GZZ_DEPENDS)/{jython,python,yaml}.license $(jardir)
cp *README* $jardir

cd $jardir

#echo "remove cryptix sf"
rm -r META-INF/CRYP*
echo "Creating the jar"
jar cfm $JARNAME manifest *

cd "$basedir"

mv $jardir/$JARNAME .
#rm -rf $jardir

echo "Done."