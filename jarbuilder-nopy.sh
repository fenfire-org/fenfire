#!/bin/bash
set -e
CLASSPATH="$1"
VERSION="$2"
JARNAME="$3"
MAINCLASS="$4"
EXCLUDELIBS="$5"

excludepattern="$EXCLUDELIBS"

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
echo "Main-Class: $MAINCLASS"
echo "Main-Class: $MAINCLASS" > $jardir/manifest

cd $jardir
echo "Gathering files from CLASSPATH"
for name in $(echo $CLASSPATH|tr : '\n'|egrep -v "$excludepattern"); do
#for name in $(echo $CLASSPATH|tr : '\n'); do
    case $name in 
	*.jar) 
	    echo "unpack $name"
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

cd "$basedir"

echo 'Including *LICENSE* and *README*'
cp *LICENSE* $jardir
# cp $(GZZ_DEPENDS)/{jython,python,yaml}.license $(jardir)
cp *README* $jardir

cd $jardir

#echo "remove META-INF copied from jar files"
rm -rf META-INF/
echo "Creating the jar"
jar cfm $JARNAME manifest *

cd "$basedir"

mv $jardir/$JARNAME .
#rm -rf $jardir

echo "Done."